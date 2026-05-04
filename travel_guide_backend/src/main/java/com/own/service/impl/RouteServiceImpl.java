package com.own.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.own.common.utils.DistanceCalculator;
import com.own.mappers.AttractionInfoMapper;
import com.own.mappers.FoodShopMapper;
import com.own.mappers.FoodInfoMapper;
import com.own.model.AttractionInfo;
import com.own.model.FoodShop;
import com.own.model.FoodInfo;
import com.own.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 路线服务实现类
 */
@Slf4j
@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private AttractionInfoMapper attractionInfoMapper;

    @Autowired
    private FoodShopMapper foodShopMapper;

    @Autowired
    private FoodInfoMapper foodInfoMapper;

    @Value("${amap.key:}")
    private String amapKey;

    private static final String AMAP_DRIVING_URL = "https://restapi.amap.com/v3/direction/driving";
    private static final String AMAP_TRANSIT_URL = "https://restapi.amap.com/v3/direction/transit/integrated";
    private static final String AMAP_WALKING_URL = "https://restapi.amap.com/v3/direction/walking";

    // 本地缓存（使用ConcurrentHashMap实现简单缓存）
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟过期
    private static final Map<String, Long> CACHE_TIMESTAMP = new ConcurrentHashMap<>();

    @Override
    public List<Map<String, Object>> getNextAttraction(String attractionId) {
        // 1. 根据景点ID查询当前景点经纬度
        if (ObjectUtil.isEmpty(attractionId)) {
            return new ArrayList<>();
        }

        AttractionInfo currentAttraction = attractionInfoMapper.selectById(attractionId);
        if (currentAttraction == null || currentAttraction.getLongitude() == null 
                || currentAttraction.getLatitude() == null) {
            return new ArrayList<>();
        }

        BigDecimal currentLng = currentAttraction.getLongitude();
        BigDecimal currentLat = currentAttraction.getLatitude();

        // 2. 查询全部景点数据，排除自身
        LambdaQueryWrapper<AttractionInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(AttractionInfo::getId, attractionId)
               .isNotNull(AttractionInfo::getLongitude)
               .isNotNull(AttractionInfo::getLatitude);
        
        List<AttractionInfo> allAttractions = attractionInfoMapper.selectList(wrapper);

        // 3. 计算各景点直线距离，由近到远排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (AttractionInfo attraction : allAttractions) {
            if (attraction.getLongitude() != null && attraction.getLatitude() != null) {
                double distance = DistanceCalculator.calculateDistance(
                        currentLng, currentLat, 
                        attraction.getLongitude(), attraction.getLatitude());
                
                Map<String, Object> item = new HashMap<>();
                item.put("id", attraction.getId());
                item.put("attractionName", attraction.getAttractionName());
                item.put("attractionPic", attraction.getAttractionPic());
                item.put("attractionPlace", attraction.getAttractionPlace());
                item.put("longitude", attraction.getLongitude());
                item.put("latitude", attraction.getLatitude());
                item.put("playHour", attraction.getPlayHour());
                item.put("distance", roundToTwoDecimal(distance));
                item.put("driveTime", DistanceCalculator.estimateDriveTime(distance));
                
                result.add(item);
            }
        }

        // 按距离升序排序
        result.sort(Comparator.comparingDouble(item -> ((Number) item.get("distance")).doubleValue()));

        // 4. 固定只返回前3条
        return result.stream().limit(3).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTrafficInfo(String fromAttractionId, String toAttractionId) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取起点、终点经纬度
        if (ObjectUtil.isEmpty(fromAttractionId) || ObjectUtil.isEmpty(toAttractionId)) {
            return result;
        }

        AttractionInfo fromAttraction = attractionInfoMapper.selectById(fromAttractionId);
        AttractionInfo toAttraction = attractionInfoMapper.selectById(toAttractionId);

        if (fromAttraction == null || toAttraction == null 
                || fromAttraction.getLongitude() == null || fromAttraction.getLatitude() == null
                || toAttraction.getLongitude() == null || toAttraction.getLatitude() == null) {
            return result;
        }

        try {
            // 2. 调用高德驾车 API
            String origin = fromAttraction.getLongitude() + "," + fromAttraction.getLatitude();
            String destination = toAttraction.getLongitude() + "," + toAttraction.getLatitude();
            
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("output", "json");
            
            String response = HttpUtil.get(AMAP_DRIVING_URL, params);
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            String status = jsonResponse.getStr("status");
            
            if (!"1".equals(status)) {
                log.warn("高德驾车 API调用失败：{}", jsonResponse.getStr("info"));
                return buildEstimatedTrafficInfo(fromAttraction, toAttraction);
            }
            
            JSONObject routeResult = jsonResponse.getJSONObject("route");
            if (routeResult == null) {
                log.warn("高德驾车 API返回数据格式错误");
                return buildEstimatedTrafficInfo(fromAttraction, toAttraction);
            }
            
            JSONArray paths = routeResult.getJSONArray("paths");
            if (paths == null || paths.isEmpty()) {
                log.warn("高德驾车 API未返回路线数据");
                return buildEstimatedTrafficInfo(fromAttraction, toAttraction);
            }
            
            // 获取第一条路线（最优路线）
            JSONObject firstPath = paths.getJSONObject(0);
            
            // 距离（米转千米）
            double distance = firstPath.getDouble("distance", 0.0) / 1000.0;
            result.put("distance", roundToTwoDecimal(distance));
            
            // 耗时（秒转分钟）
            int duration = firstPath.getInt("duration", 0);
            int durationMinutes = (int) Math.ceil(duration / 60.0);
            
            // 3. 构建自驾方案
            Map<String, Object> driveInfo = new HashMap<>();
            driveInfo.put("time", durationMinutes);
            driveInfo.put("cost", DistanceCalculator.estimateDriveCost(distance));
            driveInfo.put("description", buildRouteDescription(firstPath));
            
            // 添加结构化步骤
            List<Map<String, Object>> steps = buildStructuredSteps(firstPath);
            driveInfo.put("steps", steps);
            
            // 路线坐标串 polyline
            try {
                String polyline = extractPolyline(firstPath);
                driveInfo.put("polyline", polyline);
                log.info("成功提取polyline，长度: {}", polyline.length());
            } catch (Exception e) {
                log.warn("提取polyline失败，设置为空字符串", e);
                driveInfo.put("polyline", "");
            }
            
            result.put("drive", driveInfo);

            // 4. 调用高德公交API获取真实路线
            Map<String, Object> busInfo = getBusRouteInfo(origin, destination, fromAttraction, toAttraction);
            result.put("bus", busInfo);

            // 5. 打车方案使用驾车路线数据（打车也是走公路）
            Map<String, Object> taxiInfo = new HashMap<>();
            taxiInfo.put("time", durationMinutes);  // 使用驾车的真实耗时
            
            // 计算打车费用区间（±10%）
            double taxiCost = DistanceCalculator.estimateTaxiCost(distance);
            double taxiCostMin = Math.round(taxiCost * 0.9 * 100.0) / 100.0;
            double taxiCostMax = Math.round(taxiCost * 1.1 * 100.0) / 100.0;
            taxiInfo.put("cost", taxiCost);
            taxiInfo.put("taxiCostMin", taxiCostMin);
            taxiInfo.put("taxiCostMax", taxiCostMax);
            
            taxiInfo.put("description", driveInfo.get("description"));  // 使用驾车的路线描述
            taxiInfo.put("polyline", driveInfo.get("polyline"));  // 使用驾车的polyline
            taxiInfo.put("steps", driveInfo.get("steps"));  // 使用驾车的结构化步骤
            result.put("taxi", taxiInfo);

        } catch (Exception e) {
            log.error("调用高德地图API异常", e);
            return buildEstimatedTrafficInfo(fromAttraction, toAttraction);
        }

        return result;
    }

    /**
     * 构建估算的交通信息（备用方案）
     */
    private Map<String, Object> buildEstimatedTrafficInfo(AttractionInfo fromAttraction, AttractionInfo toAttraction) {
        Map<String, Object> result = new HashMap<>();
        
        double distance = DistanceCalculator.calculateDistance(
                fromAttraction.getLongitude(), fromAttraction.getLatitude(),
                toAttraction.getLongitude(), toAttraction.getLatitude());
        
        result.put("distance", roundToTwoDecimal(distance));
        
        // 自驾
        Map<String, Object> driveInfo = new HashMap<>();
        driveInfo.put("time", DistanceCalculator.estimateDriveTime(distance));
        driveInfo.put("cost", DistanceCalculator.estimateDriveCost(distance));
        driveInfo.put("description", "沿主要道路行驶，途经城市主干道");
        driveInfo.put("steps", new ArrayList<>());
        driveInfo.put("polyline", "");
        result.put("drive", driveInfo);

        // 公交（返回 null，表示没有找到路线）
        result.put("bus", null);

        // 打车
        Map<String, Object> taxiInfo = new HashMap<>();
        taxiInfo.put("time", DistanceCalculator.estimateTaxiTime(distance));
        double taxiCost = DistanceCalculator.estimateTaxiCost(distance);
        taxiInfo.put("cost", taxiCost);
        taxiInfo.put("taxiCostMin", Math.round(taxiCost * 0.9 * 100.0) / 100.0);
        taxiInfo.put("taxiCostMax", Math.round(taxiCost * 1.1 * 100.0) / 100.0);
        taxiInfo.put("description", "直接打车前往，无需换乘");
        taxiInfo.put("steps", new ArrayList<>());
        taxiInfo.put("polyline", "");
        result.put("taxi", taxiInfo);
        
        return result;
    }

    /**
     * 获取公交路线信息
     */
    private Map<String, Object> getBusRouteInfo(String origin, String destination, 
                                                  AttractionInfo fromAttraction, 
                                                  AttractionInfo toAttraction) {
        Map<String, Object> busInfo = new HashMap<>();
        
        try {
            // 构建公交API请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("output", "json");
            params.put("city", "贵阳");  // 设置城市，提高准确性

            // 调用公交API（静默处理）
            String response = HttpUtil.get(AMAP_TRANSIT_URL, params);

            // 解析响应数据
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            String status = jsonResponse.getStr("status");
            
            // 静默处理 API 调用失败
            if (!"1".equals(status)) {
                log.warn("高德公交 API 调用失败：{}", jsonResponse.getStr("info"));
                return null;
            }

            JSONObject routeResult = jsonResponse.getJSONObject("route");
            if (routeResult == null) {
                log.warn("高德公交 API 返回数据格式错误");
                return null;
            }

            JSONArray transits = routeResult.getJSONArray("transits");
            if (transits == null || transits.isEmpty()) {
                log.info("高德公交 API 没有找到对应路线");
                return null;
            }

            // 获取第一条路线（最优路线）
            JSONObject firstTransit = transits.getJSONObject(0);
            
            // 耗时（秒转分钟）
            int duration = firstTransit.getInt("duration", 0);
            int durationMinutes = (int) Math.ceil(duration / 60.0);
            
            // 距离（米转千米）
            double distance = firstTransit.getDouble("distance", 0.0) / 1000.0;
            
            // 费用（元）- 高德API可能返回字符串或数字，也可能不返回
            double cost = 2.0;  // 默认2元
            Object costObj = firstTransit.get("cost");
            if (costObj != null) {
                try {
                    // 尝试解析为数字
                    if (costObj instanceof Number) {
                        cost = ((Number) costObj).doubleValue();
                    } else {
                        // 如果是字符串，尝试解析
                        cost = Double.parseDouble(costObj.toString());
                    }
                    // 如果费用为0或负数，使用默认值
                    if (cost <= 0) {
                        cost = 2.0;
                    }
                } catch (NumberFormatException e) {
                    // 静默处理解析失败
                }
            }
            
            busInfo.put("time", durationMinutes);
            busInfo.put("cost", cost);
            
            // 补充公交详细信息
            Map<String, Object> busDetail = extractBusDetail(firstTransit);
            busInfo.put("busStationCount", busDetail.get("busStationCount"));
            busInfo.put("walkDistance", busDetail.get("walkDistance"));
            
            // 构建路线描述
            busInfo.put("description", buildBusRouteDescription(firstTransit));
            
            // 添加结构化步骤
            List<Map<String, Object>> steps = buildBusStructuredSteps(firstTransit);
            busInfo.put("steps", steps);
            
            // 提取polyline
            try {
                String polyline = extractBusPolyline(firstTransit);
                busInfo.put("polyline", polyline);
            } catch (Exception e) {
                busInfo.put("polyline", "");
            }
            
        } catch (Exception e) {
            log.error("获取公交路线信息异常", e);
            return null;
        }
        
        return busInfo;
    }

    /**
     * 提取公交详细信息（站数、步行距离）
     */
    private Map<String, Object> extractBusDetail(JSONObject transit) {
        Map<String, Object> detail = new HashMap<>();
        int totalStations = 0;
        double totalWalkDistance = 0.0;
        
        JSONArray segments = transit.getJSONArray("segments");
        if (segments != null && !segments.isEmpty()) {
            for (int i = 0; i < segments.size(); i++) {
                try {
                    Object segmentObj = segments.get(i);
                    
                    // 跳过非 JSONObject 类型的数据
                    if (!(segmentObj instanceof JSONObject)) {
                        continue;
                    }
                    
                    JSONObject segment = (JSONObject) segmentObj;
                    
                    // 提取公交站数
                    Object busObj = segment.get("bus");
                    if (busObj instanceof JSONObject) {
                        JSONObject bus = (JSONObject) busObj;
                        JSONArray buslines = bus.getJSONArray("buslines");
                        if (buslines != null && !buslines.isEmpty()) {
                            for (int j = 0; j < buslines.size(); j++) {
                                Object buslineObj = buslines.get(j);
                                if (buslineObj instanceof JSONObject) {
                                    JSONObject busline = (JSONObject) buslineObj;
                                    int stationCount = busline.getInt("via_stops_count", 0);
                                    totalStations += stationCount;
                                }
                            }
                        }
                    }
                    
                    // 提取步行距离
                    Object walkingObj = segment.get("walking");
                    if (walkingObj instanceof JSONObject) {
                        JSONObject walking = (JSONObject) walkingObj;
                        double walkDist = walking.getDouble("distance", 0.0);
                        totalWalkDistance += walkDist;
                    }
                } catch (Exception e) {
                    // 静默处理解析失败
                }
            }
        }
        
        detail.put("busStationCount", totalStations);
        detail.put("walkDistance", Math.round(totalWalkDistance));
        
        return detail;
    }

    /**
     * 构建公交路线描述（返回完整路线信息）
     */
    private String buildBusRouteDescription(JSONObject transit) {
        StringBuilder description = new StringBuilder();
        JSONArray segments = transit.getJSONArray("segments");
        
        if (segments != null && !segments.isEmpty()) {
            // 遍历所有步骤，返回完整路线描述
            for (int i = 0; i < segments.size(); i++) {
                try {
                    Object segmentObj = segments.get(i);
                    if (!(segmentObj instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject segment = (JSONObject) segmentObj;
                    
                    // 提取公交线路名称
                    Object busObj = segment.get("bus");
                    if (busObj instanceof JSONObject) {
                        JSONObject bus = (JSONObject) busObj;
                        JSONArray buslines = bus.getJSONArray("buslines");
                        if (buslines != null && !buslines.isEmpty()) {
                            for (int j = 0; j < buslines.size(); j++) {
                                Object buslineObj = buslines.get(j);
                                if (buslineObj instanceof JSONObject) {
                                    String busName = ((JSONObject) buslineObj).getStr("name", "");
                                    if (ObjectUtil.isNotEmpty(busName)) {
                                        if (description.length() > 0) {
                                            description.append(" > ");
                                        }
                                        description.append(busName);
                                    }
                                }
                            }
                        }
                    }
                    
                    // 如果有步行部分，也添加到描述中
                    Object walkingObj = segment.get("walking");
                    if (walkingObj instanceof JSONObject) {
                        JSONObject walking = (JSONObject) walkingObj;
                        double walkDistance = walking.getDouble("distance", 0.0);
                        if (walkDistance > 0) {
                            int walkMeters = (int) walkDistance;
                            if (description.length() > 0) {
                                description.append(" > ");
                            }
                            description.append("步行").append(walkMeters).append("米");
                        }
                    }
                } catch (Exception e) {
                    // 静默处理构建失败
                }
            }
        } else {
            description.append("乘坐公交线路，可能需要换乘1-2次");
        }
        
        return description.toString();
    }

    /**
     * 提取公交路线坐标串 polyline
     * 注意：高德公交API返回的路线是沿着实际道路的真实路线
     */
    private String extractBusPolyline(JSONObject transit) {
        StringBuilder polyline = new StringBuilder();
        JSONArray segments = transit.getJSONArray("segments");
            
        if (segments != null && !segments.isEmpty()) {
            for (int i = 0; i < segments.size(); i++) {
                try {
                    Object segmentObj = segments.get(i);
                    if (!(segmentObj instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject segment = (JSONObject) segmentObj;
                        
                    // 提取步行路段的 polyline
                    Object walkingObj = segment.get("walking");
                    if (walkingObj instanceof JSONObject) {
                        JSONObject walking = (JSONObject) walkingObj;
                        String walkPolyline = walking.getStr("polyline", "");
                        
                        // 如果 walking 没有 polyline，调用高德步行 API 获取真实路线
                        if (ObjectUtil.isEmpty(walkPolyline)) {
                            String origin = walking.getStr("origin", "");
                            String destination = walking.getStr("destination", "");
                            if (ObjectUtil.isNotEmpty(origin) && ObjectUtil.isNotEmpty(destination)) {
                                walkPolyline = getWalkingPolylineFromAPI(origin, destination);
                            }
                        }
                        
                        if (ObjectUtil.isNotEmpty(walkPolyline)) {
                            if (polyline.length() > 0) {
                                polyline.append(";");
                            }
                            polyline.append(walkPolyline);
                        }
                    }
                        
                    // 提取公交路段的 polyline
                    Object busObj = segment.get("bus");
                    if (busObj instanceof JSONObject) {
                        JSONObject bus = (JSONObject) busObj;
                        JSONArray buslines = bus.getJSONArray("buslines");
                        
                        if (buslines != null && !buslines.isEmpty()) {
                            for (int j = 0; j < buslines.size(); j++) {
                                Object buslineObj = buslines.get(j);
                                if (buslineObj instanceof JSONObject) {
                                    JSONObject busline = (JSONObject) buslineObj;
                                    String stepPolyline = busline.getStr("polyline", "");
                                    
                                    // 检查是否与上一条 polyline 重复
                                    if (ObjectUtil.isNotEmpty(stepPolyline)) {
                                        String lastPolyline = polyline.length() > 0 ? polyline.toString() : "";
                                        if (!lastPolyline.endsWith(stepPolyline)) {
                                            if (polyline.length() > 0) {
                                                polyline.append(";");
                                            }
                                            polyline.append(stepPolyline);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("提取公交 polyline segment[{}] 失败", i, e);
                }
            }
        }
            
        return polyline.toString();
    }

    /**
     * 调用高德步行 API 获取真实的步行路线坐标
     * @param origin 起点坐标（经度,纬度）
     * @param destination 终点坐标（经度,纬度）
     * @return 步行路线 polyline
     */
    private String getWalkingPolylineFromAPI(String origin, String destination) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("output", "json");
            
            String response = HttpUtil.get(AMAP_WALKING_URL, params);
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            String status = jsonResponse.getStr("status");
            
            if ("1".equals(status)) {
                JSONObject routeResult = jsonResponse.getJSONObject("route");
                if (routeResult != null) {
                    JSONArray paths = routeResult.getJSONArray("paths");
                    if (paths != null && !paths.isEmpty()) {
                        // 获取第一条步行路线
                        JSONObject firstPath = paths.getJSONObject(0);
                        String walkPolyline = firstPath.getStr("polyline", "");
                        if (ObjectUtil.isNotEmpty(walkPolyline)) {
                            return walkPolyline;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("调用高德步行 API 失败", e);
        }
        
        // 如果调用失败，返回起点和终点的直线
        return origin + ";" + destination;
    }

    /**
     * 构建结构化路线步骤（驾车/打车）
     */
    private List<Map<String, Object>> buildStructuredSteps(JSONObject path) {
        List<Map<String, Object>> steps = new ArrayList<>();
        JSONArray pathSteps = path.getJSONArray("steps");
        
        if (pathSteps != null && !pathSteps.isEmpty()) {
            for (int i = 0; i < pathSteps.size(); i++) {
                JSONObject step = pathSteps.getJSONObject(i);
                Map<String, Object> stepInfo = new HashMap<>();
                
                stepInfo.put("stepNo", i + 1);
                stepInfo.put("instruction", step.getStr("instruction", ""));
                stepInfo.put("distance", step.getDouble("distance", 0.0));
                
                steps.add(stepInfo);
            }
        }
        
        return steps;
    }

    /**
     * 构建公交结构化步骤（包含每个步骤的独立 polyline）
     */
    private List<Map<String, Object>> buildBusStructuredSteps(JSONObject transit) {
        List<Map<String, Object>> steps = new ArrayList<>();
        JSONArray segments = transit.getJSONArray("segments");
        
        if (segments != null && !segments.isEmpty()) {
            int stepNo = 1;
            for (int i = 0; i < segments.size(); i++) {
                try {
                    Object segmentObj = segments.get(i);
                    if (!(segmentObj instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject segment = (JSONObject) segmentObj;
                    
                    // 步行部分
                    Object walkingObj = segment.get("walking");
                    if (walkingObj instanceof JSONObject) {
                        JSONObject walking = (JSONObject) walkingObj;
                        String instruction = walking.getStr("instruction", "步行");
                        double distance = walking.getDouble("distance", 0.0);
                        
                        // 提取步行路段的 polyline
                        String walkPolyline = walking.getStr("polyline", "");
                        
                        // 如果 walking 没有 polyline，调用高德步行 API 获取真实路线
                        if (ObjectUtil.isEmpty(walkPolyline)) {
                            String origin = walking.getStr("origin", "");
                            String destination = walking.getStr("destination", "");
                            if (ObjectUtil.isNotEmpty(origin) && ObjectUtil.isNotEmpty(destination)) {
                                walkPolyline = getWalkingPolylineFromAPI(origin, destination);
                            }
                        }
                        
                        Map<String, Object> stepInfo = new HashMap<>();
                        stepInfo.put("stepNo", stepNo++);
                        stepInfo.put("instruction", instruction);
                        stepInfo.put("distance", distance);
                        stepInfo.put("type", "walking");
                        stepInfo.put("polyline", walkPolyline);  // 添加步行段的独立 polyline
                        
                        steps.add(stepInfo);
                    }
                    
                    // 公交部分
                    Object busObj = segment.get("bus");
                    if (busObj instanceof JSONObject) {
                        JSONObject bus = (JSONObject) busObj;
                        JSONArray buslines = bus.getJSONArray("buslines");
                        if (buslines != null && !buslines.isEmpty()) {
                            for (int j = 0; j < buslines.size(); j++) {
                                Object buslineObj = buslines.get(j);
                                if (buslineObj instanceof JSONObject) {
                                    JSONObject busline = (JSONObject) buslineObj;
                                    String busName = busline.getStr("name", "");
                                    int viaStops = busline.getInt("via_stops_count", 0);
                                    double distance = busline.getDouble("distance", 0.0);
                                    String busPolyline = busline.getStr("polyline", "");  // 公交段的独立 polyline
                                    
                                    Map<String, Object> stepInfo = new HashMap<>();
                                    stepInfo.put("stepNo", stepNo++);
                                    stepInfo.put("instruction", busName + "（乘坐" + viaStops + "站）");
                                    stepInfo.put("distance", distance);
                                    stepInfo.put("type", "bus");
                                    stepInfo.put("busName", busName);
                                    stepInfo.put("stationCount", viaStops);
                                    stepInfo.put("polyline", busPolyline);  // 添加公交段的独立 polyline
                                    
                                    steps.add(stepInfo);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("构建公交步骤 segment[{}] 失败", i, e);
                }
            }
        }
        
        return steps;
    }

    /**
     * 构建路线描述
     */
    private String buildRouteDescription(JSONObject path) {
        StringBuilder description = new StringBuilder();
        JSONArray steps = path.getJSONArray("steps");
        
        if (steps != null && !steps.isEmpty()) {
            // 取前3个关键步骤作为描述
            int maxSteps = Math.min(steps.size(), 3);
            for (int i = 0; i < maxSteps; i++) {
                JSONObject step = steps.getJSONObject(i);
                String instruction = step.getStr("instruction", "");
                if (ObjectUtil.isNotEmpty(instruction)) {
                    if (i > 0) {
                        description.append("→");
                    }
                    description.append(instruction);
                }
            }
            if (steps.size() > 3) {
                description.append("...");
            }
        } else {
            description.append("沿主要道路行驶，途经城市主干道");
        }
        
        return description.toString();
    }

    /**
     * 提取路线坐标串 polyline
     */
    private String extractPolyline(JSONObject path) {
        StringBuilder polyline = new StringBuilder();
        JSONArray steps = path.getJSONArray("steps");
        
        if (steps != null && !steps.isEmpty()) {
            for (int i = 0; i < steps.size(); i++) {
                JSONObject step = steps.getJSONObject(i);
                // 高德API返回的polyline是字符串格式，不是数组
                String stepPolyline = step.getStr("polyline", "");
                
                if (ObjectUtil.isNotEmpty(stepPolyline)) {
                    if (polyline.length() > 0) {
                        polyline.append(";");
                    }
                    polyline.append(stepPolyline);
                }
            }
        }
        
        return polyline.toString();
    }

    @Override
    public List<Map<String, Object>> getNearFoodShop(String fromAttractionId, String toAttractionId) {
        // 1. 参数验证
        if (ObjectUtil.isEmpty(fromAttractionId) || ObjectUtil.isEmpty(toAttractionId)) {
            return new ArrayList<>();
        }

        // 2. 获取起点和终点景点的经纬度
        AttractionInfo fromAttraction = attractionInfoMapper.selectById(fromAttractionId);
        AttractionInfo toAttraction = attractionInfoMapper.selectById(toAttractionId);

        if (fromAttraction == null || toAttraction == null
                || fromAttraction.getLongitude() == null || fromAttraction.getLatitude() == null
                || toAttraction.getLongitude() == null || toAttraction.getLatitude() == null) {
            return new ArrayList<>();
        }

        BigDecimal startLng = fromAttraction.getLongitude();
        BigDecimal startLat = fromAttraction.getLatitude();
        BigDecimal endLng = toAttraction.getLongitude();
        BigDecimal endLat = toAttraction.getLatitude();

        // 3. 查询所有小吃店
        LambdaQueryWrapper<FoodShop> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(FoodShop::getLongitude)
               .isNotNull(FoodShop::getLatitude);
        
        List<FoodShop> allFoodShops = foodShopMapper.selectList(wrapper);

        // 4. 计算小吃店坐标至两点路线区间距离，并获取店铺下的小吃信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (FoodShop shop : allFoodShops) {
            if (shop.getLongitude() != null && shop.getLatitude() != null) {
                // 计算小吃店到起点的距离
                double distanceToStart = DistanceCalculator.calculateDistance(
                        startLng, startLat, 
                        shop.getLongitude(), shop.getLatitude());
                
                // 计算小吃店到终点的距离
                double distanceToEnd = DistanceCalculator.calculateDistance(
                        endLng, endLat, 
                        shop.getLongitude(), shop.getLatitude());
                
                // 判断小吃店是否在路线附近（简化算法：到起点和终点的距离之和与总距离的差值在合理范围内）
                // 这里使用更简单的方法：计算小吃店到线段的最短距离
                double minDistance = Math.min(distanceToStart, distanceToEnd);
                
                // 筛选3km范围内店铺
                if (minDistance <= 3.0) {
                    // 查询该店铺下的推荐小吃（取第一个推荐小吃）
                    LambdaQueryWrapper<FoodInfo> foodWrapper = new LambdaQueryWrapper<>();
                    foodWrapper.eq(FoodInfo::getShopId, shop.getId())
                              .eq(FoodInfo::getStatus, 1) // 只查询上架的
                              .orderByDesc(FoodInfo::getIsRecommend) // 优先推荐
                              .orderByDesc(FoodInfo::getScore) // 按评分排序
                              .last("LIMIT 1"); // 只取一个
                    
                    FoodInfo foodInfo = foodInfoMapper.selectOne(foodWrapper);
                    
                    if (foodInfo != null) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", shop.getId()); // 店铺ID
                        item.put("foodId", foodInfo.getId()); // 小吃ID
                        item.put("name", foodInfo.getName()); // 小吃名称
                        item.put("images", foodInfo.getImages()); // 小吃图片
                        item.put("address", shop.getAddress()); // 店铺地址
                        item.put("avgPrice", foodInfo.getAvgPrice() != null ? foodInfo.getAvgPrice() : shop.getAvgPrice()); // 小吃价格
                        item.put("distance", roundToTwoDecimal(minDistance));
                        item.put("longitude", shop.getLongitude()); // 店铺经度
                        item.put("latitude", shop.getLatitude()); // 店铺纬度
                        
                        result.add(item);
                    }
                }
            }
        }

        // 5. 按距离升序排序，限制返回前4条
        result.sort(Comparator.comparingDouble(item -> ((Number) item.get("distance")).doubleValue()));
        return result.stream().limit(4).collect(Collectors.toList());
    }

    /**
     * 保留两位小数
     */
    private double roundToTwoDecimal(double value) {
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 缓存工具方法 - 获取缓存
     */
    @SuppressWarnings("unchecked")
    private <T> T getFromCache(String key) {
        Long timestamp = CACHE_TIMESTAMP.get(key);
        if (timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_EXPIRE_TIME) {
            return (T) CACHE.get(key);
        }
        // 缓存过期，清除
        CACHE.remove(key);
        CACHE_TIMESTAMP.remove(key);
        return null;
    }

    /**
     * 缓存工具方法 - 设置缓存
     */
    private void putToCache(String key, Object value) {
        CACHE.put(key, value);
        CACHE_TIMESTAMP.put(key, System.currentTimeMillis());
    }

    @Override
    public Map<String, Object> getTrafficInfoByType(String fromAttractionId, String toAttractionId, String transportType) {
        // 缓存key
        String cacheKey = "traffic_" + fromAttractionId + "_" + toAttractionId + "_" + transportType;
        
        // 尝试从缓存获取
        Map<String, Object> cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取交通信息: {}", cacheKey);
            return cachedResult;
        }

        // 获取完整的交通信息
        Map<String, Object> fullTrafficInfo = getTrafficInfo(fromAttractionId, toAttractionId);
        
        // 根据交通方式筛选
        if ("all".equals(transportType) || transportType == null) {
            putToCache(cacheKey, fullTrafficInfo);
            return fullTrafficInfo;
        }

        Map<String, Object> filteredResult = new HashMap<>();
        filteredResult.put("distance", fullTrafficInfo.get("distance"));
        
        switch (transportType.toLowerCase()) {
            case "drive":
                filteredResult.put("drive", fullTrafficInfo.get("drive"));
                break;
            case "bus":
                filteredResult.put("bus", fullTrafficInfo.get("bus"));
                break;
            case "taxi":
                filteredResult.put("taxi", fullTrafficInfo.get("taxi"));
                break;
            default:
                filteredResult = fullTrafficInfo;
        }

        // 存入缓存
        putToCache(cacheKey, filteredResult);
        return filteredResult;
    }

    @Override
    public Map<String, Object> getMapRouteData(String fromAttractionId, String toAttractionId, String transportType) {
        // 缓存key
        String cacheKey = "map_route_" + fromAttractionId + "_" + toAttractionId + "_" + transportType;
        
        // 尝试从缓存获取
        Map<String, Object> cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取地图路线数据: {}", cacheKey);
            return cachedResult;
        }

        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取起点、终点经纬度
            AttractionInfo fromAttraction = attractionInfoMapper.selectById(fromAttractionId);
            AttractionInfo toAttraction = attractionInfoMapper.selectById(toAttractionId);

            if (fromAttraction == null || toAttraction == null 
                    || fromAttraction.getLongitude() == null || fromAttraction.getLatitude() == null
                    || toAttraction.getLongitude() == null || toAttraction.getLatitude() == null) {
                return result;
            }

            String origin = fromAttraction.getLongitude() + "," + fromAttraction.getLatitude();
            String destination = toAttraction.getLongitude() + "," + toAttraction.getLatitude();

            // 根据不同交通方式获取路线数据
            if ("drive".equals(transportType) || "taxi".equals(transportType)) {
                // 驾车/打车路线
                Map<String, Object> params = new HashMap<>();
                params.put("key", amapKey);
                params.put("origin", origin);
                params.put("destination", destination);
                params.put("output", "json");
                
                String response = HttpUtil.get(AMAP_DRIVING_URL, params);
                JSONObject jsonResponse = JSONUtil.parseObj(response);
                
                if ("1".equals(jsonResponse.getStr("status"))) {
                    JSONObject routeResult = jsonResponse.getJSONObject("route");
                    if (routeResult != null) {
                        JSONArray paths = routeResult.getJSONArray("paths");
                        if (paths != null && !paths.isEmpty()) {
                            JSONObject firstPath = paths.getJSONObject(0);
                            
                            // 提取polyline
                            String polyline = extractPolyline(firstPath);
                            result.put("polyline", polyline);
                            
                            // 提取路线节点标记
                            List<Map<String, Object>> markers = extractDriveMarkers(firstPath, fromAttraction, toAttraction);
                            result.put("markers", markers);
                            
                            // 距离和耗时
                            double distance = firstPath.getDouble("distance", 0.0) / 1000.0;
                            int duration = firstPath.getInt("duration", 0);
                            result.put("distance", roundToTwoDecimal(distance));
                            result.put("duration", (int) Math.ceil(duration / 60.0));
                        }
                    }
                }
            } else if ("bus".equals(transportType)) {
                // 公交路线
                Map<String, Object> params = new HashMap<>();
                params.put("key", amapKey);
                params.put("origin", origin);
                params.put("destination", destination);
                params.put("output", "json");
                params.put("city", "贵阳");

                String response = HttpUtil.get(AMAP_TRANSIT_URL, params);
                JSONObject jsonResponse = JSONUtil.parseObj(response);
                
                if ("1".equals(jsonResponse.getStr("status"))) {
                    JSONObject routeResult = jsonResponse.getJSONObject("route");
                    if (routeResult != null) {
                        JSONArray transits = routeResult.getJSONArray("transits");
                        if (transits != null && !transits.isEmpty()) {
                            JSONObject firstTransit = transits.getJSONObject(0);
                            
                            // 提取polyline
                            String polyline = extractBusPolyline(firstTransit);
                            result.put("polyline", polyline);
                            
                            // 提取公交站点标记
                            List<Map<String, Object>> markers = extractBusStations(firstTransit);
                            result.put("markers", markers);
                            
                            // 距离和耗时
                            double distance = firstTransit.getDouble("distance", 0.0) / 1000.0;
                            int duration = firstTransit.getInt("duration", 0);
                            result.put("distance", roundToTwoDecimal(distance));
                            result.put("duration", (int) Math.ceil(duration / 60.0));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取地图路线数据异常", e);
        }

        // 存入缓存
        putToCache(cacheKey, result);
        return result;
    }

    /**
     * 提取驾车路线节点标记（高速口等）
     */
    private List<Map<String, Object>> extractDriveMarkers(JSONObject path, AttractionInfo fromAttraction, AttractionInfo toAttraction) {
        List<Map<String, Object>> markers = new ArrayList<>();
        
        // 添加起点标记
        Map<String, Object> startMarker = new HashMap<>();
        startMarker.put("type", "start");
        startMarker.put("name", fromAttraction.getAttractionName());
        startMarker.put("longitude", fromAttraction.getLongitude());
        startMarker.put("latitude", fromAttraction.getLatitude());
        markers.add(startMarker);
        
        // 从步骤中提取关键节点（如高速口）
        JSONArray steps = path.getJSONArray("steps");
        if (steps != null && !steps.isEmpty()) {
            for (int i = 0; i < steps.size(); i++) {
                JSONObject step = steps.getJSONObject(i);
                String instruction = step.getStr("instruction", "");
                
                // 检测是否包含高速公路相关信息
                if (instruction.contains("高速") || instruction.contains("收费站")) {
                    Map<String, Object> marker = new HashMap<>();
                    marker.put("type", "highway");
                    marker.put("name", instruction.length() > 20 ? instruction.substring(0, 20) + "..." : instruction);
                    
                    // 提取该步骤的坐标（取第一步的坐标作为标记点）
                    String polyline = step.getStr("polyline", "");
                    if (ObjectUtil.isNotEmpty(polyline)) {
                        String[] coords = polyline.split(";")[0].split(",");
                        if (coords.length == 2) {
                            try {
                                marker.put("longitude", new BigDecimal(coords[0]));
                                marker.put("latitude", new BigDecimal(coords[1]));
                            } catch (Exception e) {
                                log.warn("解析坐标失败", e);
                            }
                        }
                    }
                    
                    markers.add(marker);
                }
            }
        }
        
        // 添加终点标记
        Map<String, Object> endMarker = new HashMap<>();
        endMarker.put("type", "end");
        endMarker.put("name", toAttraction.getAttractionName());
        endMarker.put("longitude", toAttraction.getLongitude());
        endMarker.put("latitude", toAttraction.getLatitude());
        markers.add(endMarker);
        
        return markers;
    }

    /**
     * 提取公交站点标记
     */
    private List<Map<String, Object>> extractBusStations(JSONObject transit) {
        List<Map<String, Object>> markers = new ArrayList<>();
        
        JSONArray segments = transit.getJSONArray("segments");
        if (segments != null && !segments.isEmpty()) {
            for (int i = 0; i < segments.size(); i++) {
                try {
                    Object segmentObj = segments.get(i);
                    if (!(segmentObj instanceof JSONObject)) {
                        continue;
                    }
                    JSONObject segment = (JSONObject) segmentObj;
                    
                    // 提取公交部分
                    Object busObj = segment.get("bus");
                    if (busObj instanceof JSONObject) {
                        JSONObject bus = (JSONObject) busObj;
                        JSONArray buslines = bus.getJSONArray("buslines");
                        if (buslines != null && !buslines.isEmpty()) {
                            for (int j = 0; j < buslines.size(); j++) {
                                Object buslineObj = buslines.get(j);
                                if (buslineObj instanceof JSONObject) {
                                    JSONObject busline = (JSONObject) buslineObj;
                                    
                                    // 提取途经站点
                                    JSONArray viaStops = busline.getJSONArray("via_stops");
                                    if (viaStops != null && !viaStops.isEmpty()) {
                                        for (int k = 0; k < viaStops.size(); k++) {
                                            Object stopObj = viaStops.get(k);
                                            if (stopObj instanceof JSONObject) {
                                                JSONObject stop = (JSONObject) stopObj;
                                                Map<String, Object> marker = new HashMap<>();
                                                marker.put("type", "bus_station");
                                                marker.put("name", stop.getStr("name", ""));
                                                
                                                String location = stop.getStr("location", "");
                                                if (ObjectUtil.isNotEmpty(location)) {
                                                    String[] coords = location.split(",");
                                                    if (coords.length == 2) {
                                                        try {
                                                            marker.put("longitude", new BigDecimal(coords[0]));
                                                            marker.put("latitude", new BigDecimal(coords[1]));
                                                        } catch (Exception e) {
                                                            log.warn("解析公交站点坐标失败", e);
                                                        }
                                                    }
                                                }
                                                
                                                markers.add(marker);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // 静默处理提取失败
                }
            }
        }
        
        return markers;
    }

    @Override
    public Map<String, Object> getNearFoodShopWithPage(String fromAttractionId, String toAttractionId,
                                                        Integer pageNum, Integer pageSize,
                                                        String sortBy, String sortOrder,
                                                        String businessStatus) {
        // 缓存key
        String cacheKey = "food_page_" + fromAttractionId + "_" + toAttractionId + "_" + pageNum + "_" + pageSize + "_" + sortBy + "_" + sortOrder + "_" + businessStatus;
        
        // 尝试从缓存获取
        Map<String, Object> cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取分页小吃数据: {}", cacheKey);
            return cachedResult;
        }

        Map<String, Object> result = new HashMap<>();
        
        // 默认分页参数
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        
        // 获取所有沿途小吃
        List<Map<String, Object>> allFoodShops = getNearFoodShop(fromAttractionId, toAttractionId);
        
        // 补充完整信息（包括business_status, business_hours, recommend_reason等）
        List<Map<String, Object>> enrichedShops = enrichFoodShopInfo(allFoodShops);
        
        // 按营业状态筛选
        if (ObjectUtil.isNotEmpty(businessStatus)) {
            enrichedShops = enrichedShops.stream()
                .filter(shop -> businessStatus.equals(shop.get("businessStatus")))
                .collect(Collectors.toList());
        }
        
        // 排序
        if (ObjectUtil.isNotEmpty(sortBy)) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
            
            switch (sortBy) {
                case "convenienceIndex":
                    enrichedShops.sort((a, b) -> {
                        int indexA = (int) a.getOrDefault("convenienceIndex", 1);
                        int indexB = (int) b.getOrDefault("convenienceIndex", 1);
                        return isAsc ? Integer.compare(indexA, indexB) : Integer.compare(indexB, indexA);
                    });
                    break;
                case "avgPrice":
                    enrichedShops.sort((a, b) -> {
                        double priceA = ((Number) a.getOrDefault("avgPrice", 0)).doubleValue();
                        double priceB = ((Number) b.getOrDefault("avgPrice", 0)).doubleValue();
                        return isAsc ? Double.compare(priceA, priceB) : Double.compare(priceB, priceA);
                    });
                    break;
                case "distance":
                    enrichedShops.sort((a, b) -> {
                        double distA = ((Number) a.getOrDefault("distance", 0)).doubleValue();
                        double distB = ((Number) b.getOrDefault("distance", 0)).doubleValue();
                        return isAsc ? Double.compare(distA, distB) : Double.compare(distB, distA);
                    });
                    break;
                default:
                    // 默认按距离排序
                    enrichedShops.sort(Comparator.comparingDouble(item -> ((Number) item.getOrDefault("distance", 0)).doubleValue()));
            }
        } else {
            // 默认按距离排序
            enrichedShops.sort(Comparator.comparingDouble(item -> ((Number) item.getOrDefault("distance", 0)).doubleValue()));
        }
        
        // 分页
        int total = enrichedShops.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Map<String, Object>> pagedShops = new ArrayList<>();
        if (fromIndex < total) {
            pagedShops = enrichedShops.subList(fromIndex, toIndex);
        }
        
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);
        result.put("records", pagedShops);
        
        // 存入缓存
        putToCache(cacheKey, result);
        return result;
    }

    /**
     * 补充小吃店铺完整信息
     */
    private List<Map<String, Object>> enrichFoodShopInfo(List<Map<String, Object>> shops) {
        for (Map<String, Object> shop : shops) {
            Long shopId = ((Number) shop.get("id")).longValue();
            FoodShop foodShop = foodShopMapper.selectById(shopId);
            
            if (foodShop != null) {
                // 添加营业状态
                shop.put("businessStatus", ObjectUtil.isNotEmpty(foodShop.getBusinessStatus()) 
                    ? foodShop.getBusinessStatus() : "营业中");
                
                // 添加营业时间
                shop.put("businessHours", foodShop.getBusinessHours());
                
                // 计算顺路指数
                Object distanceObj = shop.get("distance");
                if (distanceObj != null) {
                    double distance = ((Number) distanceObj).doubleValue();
                    int convenienceIndex = calculateConvenienceIndex(distance);
                    shop.put("convenienceIndex", convenienceIndex);
                }
            }
            
            // 添加推荐理由（从小吃信息中获取）
            Long foodId = shop.get("foodId") != null ? ((Number) shop.get("foodId")).longValue() : null;
            if (foodId != null) {
                FoodInfo foodInfo = foodInfoMapper.selectById(foodId);
                if (foodInfo != null) {
                    shop.put("recommendReason", foodInfo.getRecommendReason());
                    // 更新distance_to_route到数据库
                    if (shop.get("distance") != null) {
                        foodInfo.setDistanceToRoute(new BigDecimal(((Number) shop.get("distance")).doubleValue()));
                        foodInfoMapper.updateById(foodInfo);
                    }
                }
            }
        }
        return shops;
    }

    /**
     * 计算顺路指数
     */
    private int calculateConvenienceIndex(double distance) {
        if (distance <= 0.5) {
            return 5;
        } else if (distance <= 1.0) {
            return 4;
        } else if (distance <= 2.0) {
            return 3;
        } else if (distance <= 3.0) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public List<Map<String, Object>> getFoodMarkers(String fromAttractionId, String toAttractionId) {
        // 缓存key
        String cacheKey = "food_markers_" + fromAttractionId + "_" + toAttractionId;
        
        // 尝试从缓存获取
        List<Map<String, Object>> cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取小吃标记数据: {}", cacheKey);
            return cachedResult;
        }

        // 获取所有沿途小吃（不限制数量）
        List<Map<String, Object>> allFoodShops = getNearFoodShop(fromAttractionId, toAttractionId);
        
        // 转换为标记数据格式
        List<Map<String, Object>> markers = new ArrayList<>();
        for (Map<String, Object> shop : allFoodShops) {
            Map<String, Object> marker = new HashMap<>();
            marker.put("id", shop.get("id"));
            marker.put("foodId", shop.get("foodId"));
            marker.put("name", shop.get("name"));
            marker.put("longitude", shop.get("longitude"));
            marker.put("latitude", shop.get("latitude"));
            marker.put("avgPrice", shop.get("avgPrice"));
            marker.put("distance", shop.get("distance"));
            
            // 计算顺路指数
            if (shop.get("distance") != null) {
                double distance = ((Number) shop.get("distance")).doubleValue();
                marker.put("convenienceIndex", calculateConvenienceIndex(distance));
            }
            
            markers.add(marker);
        }
        
        // 存入缓存
        putToCache(cacheKey, markers);
        return markers;
    }
}
