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

        // 公交
        Map<String, Object> busInfo = buildEstimatedBusInfo();
        result.put("bus", busInfo);

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

            log.info("调用高德公交API：origin={}, destination={}", origin, destination);
            String response = HttpUtil.get(AMAP_TRANSIT_URL, params);
            log.info("高德公交API响应(前500字符): {}", response.length() > 500 ? response.substring(0, 500) + "..." : response);

            // 解析响应数据
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            String status = jsonResponse.getStr("status");
            
            if (!"1".equals(status)) {
                log.warn("高德公交API调用失败：{}，使用估算数据", jsonResponse.getStr("info"));
                return buildEstimatedBusInfo();
            }

            JSONObject routeResult = jsonResponse.getJSONObject("route");
            if (routeResult == null) {
                log.warn("高德公交API返回数据格式错误，使用估算数据");
                return buildEstimatedBusInfo();
            }

            JSONArray transits = routeResult.getJSONArray("transits");
            if (transits == null || transits.isEmpty()) {
                log.warn("高德公交API未返回路线数据，使用估算数据");
                return buildEstimatedBusInfo();
            }

            // 获取第一条路线（最优路线）
            JSONObject firstTransit = transits.getJSONObject(0);
            
            // 耗时（秒转分钟）
            int duration = firstTransit.getInt("duration", 0);
            int durationMinutes = (int) Math.ceil(duration / 60.0);
            
            // 距离（米转千米）
            double distance = firstTransit.getDouble("distance", 0.0) / 1000.0;
            
            // 费用（元）
            double cost = firstTransit.getDouble("cost", 2.0);
            if (cost <= 0) {
                cost = 2.0;  // 默认2元
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
                log.info("成功提取公交polyline，长度: {}", polyline.length());
            } catch (Exception e) {
                log.warn("提取公交polyline失败", e);
                busInfo.put("polyline", "");
            }
            
            log.info("高德公交API返回 - 距离: {}km, 耗时: {}分钟, 费用: {}元", 
                    distance, durationMinutes, cost);
            
        } catch (Exception e) {
            log.error("调用高德公交API异常，使用估算数据", e);
            return buildEstimatedBusInfo();
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
                JSONObject segment = segments.getJSONObject(i);
                
                // 提取公交站数
                JSONObject bus = segment.getJSONObject("bus");
                if (bus != null) {
                    JSONArray buslines = bus.getJSONArray("buslines");
                    if (buslines != null && !buslines.isEmpty()) {
                        for (int j = 0; j < buslines.size(); j++) {
                            JSONObject busline = buslines.getJSONObject(j);
                            int stationCount = busline.getInt("via_stops_count", 0);
                            totalStations += stationCount;
                        }
                    }
                }
                
                // 提取步行距离
                JSONObject walking = segment.getJSONObject("walking");
                if (walking != null) {
                    double walkDist = walking.getDouble("distance", 0.0);
                    totalWalkDistance += walkDist;
                }
            }
        }
        
        detail.put("busStationCount", totalStations);
        detail.put("walkDistance", Math.round(totalWalkDistance));
        
        return detail;
    }

    /**
     * 构建估算的公交信息（备用方案）
     */
    private Map<String, Object> buildEstimatedBusInfo() {
        Map<String, Object> busInfo = new HashMap<>();
        busInfo.put("time", 30);  // 估算30分钟
        busInfo.put("cost", 2.0);  // 固定2元
        busInfo.put("busStationCount", 0);  // 无站数信息
        busInfo.put("walkDistance", 0);  // 无步行距离
        busInfo.put("description", "乘坐公交线路，可能需要换乘1-2次");
        busInfo.put("polyline", "");  // 无路线数据
        busInfo.put("steps", new ArrayList<>());  // 无步骤信息
        return busInfo;
    }

    /**
     * 构建公交路线描述
     */
    private String buildBusRouteDescription(JSONObject transit) {
        StringBuilder description = new StringBuilder();
        JSONArray segments = transit.getJSONArray("segments");
        
        if (segments != null && !segments.isEmpty()) {
            // 取前2个关键步骤作为描述
            int maxSegments = Math.min(segments.size(), 2);
            for (int i = 0; i < maxSegments; i++) {
                JSONObject segment = segments.getJSONObject(i);
                JSONObject bus = segment.getJSONObject("bus");
                
                if (bus != null) {
                    String busName = bus.getStr("busline_name", "");
                    if (ObjectUtil.isNotEmpty(busName)) {
                        if (description.length() > 0) {
                            description.append("→");
                        }
                        description.append(busName);
                    }
                }
                
                // 如果有步行部分
                JSONObject walking = segment.getJSONObject("walking");
                if (walking != null) {
                    String instruction = walking.getStr("instruction", "");
                    if (ObjectUtil.isNotEmpty(instruction) && description.length() > 0) {
                        description.append("→").append("步行");
                    }
                }
            }
            if (segments.size() > 2) {
                description.append("...");
            }
        } else {
            description.append("乘坐公交线路，可能需要换乘1-2次");
        }
        
        return description.toString();
    }

    /**
     * 提取公交路线坐标串 polyline
     */
    private String extractBusPolyline(JSONObject transit) {
        StringBuilder polyline = new StringBuilder();
        JSONArray segments = transit.getJSONArray("segments");
        
        if (segments != null && !segments.isEmpty()) {
            for (int i = 0; i < segments.size(); i++) {
                JSONObject segment = segments.getJSONObject(i);
                
                // 提取公交路段的polyline
                JSONObject bus = segment.getJSONObject("bus");
                if (bus != null) {
                    JSONArray buslines = bus.getJSONArray("buslines");
                    if (buslines != null && !buslines.isEmpty()) {
                        for (int j = 0; j < buslines.size(); j++) {
                            JSONObject busline = buslines.getJSONObject(j);
                            String stepPolyline = busline.getStr("polyline", "");
                            
                            if (ObjectUtil.isNotEmpty(stepPolyline)) {
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
        
        return polyline.toString();
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
     * 构建公交结构化步骤
     */
    private List<Map<String, Object>> buildBusStructuredSteps(JSONObject transit) {
        List<Map<String, Object>> steps = new ArrayList<>();
        JSONArray segments = transit.getJSONArray("segments");
        
        if (segments != null && !segments.isEmpty()) {
            int stepNo = 1;
            for (int i = 0; i < segments.size(); i++) {
                JSONObject segment = segments.getJSONObject(i);
                
                // 步行部分
                JSONObject walking = segment.getJSONObject("walking");
                if (walking != null) {
                    String instruction = walking.getStr("instruction", "步行");
                    double distance = walking.getDouble("distance", 0.0);
                    
                    Map<String, Object> stepInfo = new HashMap<>();
                    stepInfo.put("stepNo", stepNo++);
                    stepInfo.put("instruction", instruction);
                    stepInfo.put("distance", distance);
                    stepInfo.put("type", "walking");
                    
                    steps.add(stepInfo);
                }
                
                // 公交部分
                JSONObject bus = segment.getJSONObject("bus");
                if (bus != null) {
                    JSONArray buslines = bus.getJSONArray("buslines");
                    if (buslines != null && !buslines.isEmpty()) {
                        for (int j = 0; j < buslines.size(); j++) {
                            JSONObject busline = buslines.getJSONObject(j);
                            String busName = busline.getStr("name", "");
                            int viaStops = busline.getInt("via_stops_count", 0);
                            double distance = busline.getDouble("distance", 0.0);
                            
                            Map<String, Object> stepInfo = new HashMap<>();
                            stepInfo.put("stepNo", stepNo++);
                            stepInfo.put("instruction", busName + "（乘坐" + viaStops + "站）");
                            stepInfo.put("distance", distance);
                            stepInfo.put("type", "bus");
                            stepInfo.put("busName", busName);
                            stepInfo.put("stationCount", viaStops);
                            
                            steps.add(stepInfo);
                        }
                    }
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
}
