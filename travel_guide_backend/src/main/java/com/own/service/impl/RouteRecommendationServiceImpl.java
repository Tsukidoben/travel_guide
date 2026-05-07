package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.algorithm.RoutePlanningAlgorithm;
import com.own.common.utils.CommonUtil;
import com.own.common.utils.ContextUtil;
import com.own.mappers.*;
import com.own.model.*;
import com.own.service.AttractionInfoService;
import com.own.service.FoodInfoService;
import com.own.service.HotelInfoService;
import com.own.service.HotelRoomService;
import com.own.service.RouteRecommendationService;
import com.own.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路线推荐服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RouteRecommendationServiceImpl extends ServiceImpl<RouteRecommendationMapper, RouteRecommendation>
        implements RouteRecommendationService {

    @Value("${amap.key:}")
    private String amapKey;
    
    private static final String AMAP_DRIVING_URL = "https://restapi.amap.com/v3/direction/driving";
    private static final String AMAP_TRANSIT_URL = "https://restapi.amap.com/v3/direction/transit/integrated";

    @Autowired
    private AttractionInfoService attractionInfoService;

    @Autowired
    private HotelInfoService hotelInfoService;

    @Autowired
    private HotelRoomService hotelRoomService;

    @Autowired
    private FoodInfoService foodInfoService;

    @Autowired
    private RouteRecommendationDetailMapper detailMapper;

    @Autowired
    private TicketInfoMapper ticketInfoMapper;

    @Autowired
    private FoodShopMapper foodShopMapper;

    @Autowired
    private FoodInfoMapper foodInfoMapper;

    @Autowired
    private AttractionTypeMapper attractionTypeMapper;

    @Autowired
    private com.own.service.GeoCodeService geoCodeService;

    @Autowired
    private com.own.mappers.RouteGenerateCountMapper generateCountMapper;

    /**
     * 每日最大生成次数
     */
    @Value("${route.recommendation.max-daily-generate:5}")
    private int maxDailyGenerate;

    /**
     * 每天可用时间（分钟）：8:00-22:00 = 14小时
     */
    private static final int DAILY_AVAILABLE_MINUTES = 840;

    /**
     * 景点搜索范围（千米）
     */
    private static final double ATTRACTION_SEARCH_RADIUS = 50.0;

    /**
     * 酒店搜索范围（千米）
     */
    private static final double HOTEL_SEARCH_RADIUS = 50.0; // 放宽到50km，覆盖整个贵阳市范围

    /**
     * 检查每日生成次数限制
     */
    private void checkDailyGenerateLimit() {
        String userId = ContextUtil.getCurrentUserId();
        if (ObjectUtil.isEmpty(userId)) {
            log.warn("用户未登录，跳过每日生成次数检查");
            return;
        }
        
        LocalDate today = LocalDate.now();
        RouteGenerateCount record = generateCountMapper.selectOne(
                new LambdaQueryWrapper<RouteGenerateCount>()
                        .eq(RouteGenerateCount::getUserId, userId)
                        .eq(RouteGenerateCount::getGenerateDate, today)
        );
        
        int todayCount = record != null ? record.getCount() : 0;
        
        if (todayCount >= maxDailyGenerate) {
            throw new BaseException("今日生成次数已用完（" + maxDailyGenerate + "次/天），请明天再试");
        }
        
        log.info("用户 {} 今日已生成 {} 次，剩余 {} 次", userId, todayCount, maxDailyGenerate - todayCount);
    }

    /**
     * 增加每日生成次数
     */
    private void incrementDailyGenerateCount() {
        String userId = ContextUtil.getCurrentUserId();
        if (ObjectUtil.isEmpty(userId)) {
            log.warn("用户未登录，跳过增加生成次数");
            return;
        }
        
        LocalDate today = LocalDate.now();
        
        RouteGenerateCount record = generateCountMapper.selectOne(
                new LambdaQueryWrapper<RouteGenerateCount>()
                        .eq(RouteGenerateCount::getUserId, userId)
                        .eq(RouteGenerateCount::getGenerateDate, today)
        );
        
        if (record == null) {
            // 首次生成
            record = new RouteGenerateCount();
            record.setUserId(userId);
            record.setGenerateDate(today);
            record.setCount(1);
            generateCountMapper.insert(record);
            log.info("用户 {} 今日首次生成，计数初始化为 1", userId);
        } else {
            // 增加计数
            record.setCount(record.getCount() + 1);
            generateCountMapper.updateById(record);
            log.info("用户 {} 今日生成次数增加到 {}", userId, record.getCount());
        }
    }

    @Override
    public RouteRecommendationResponse generateRecommendation(RouteRecommendationRequest request) {
        // 0. 检查每日生成次数限制
        checkDailyGenerateLimit();
        
        // 1. 参数校验
        validateRequest(request);

        // 2. 解析起点位置
        BigDecimal startLng = request.getLongitude();
        BigDecimal startLat = request.getLatitude();
        String startAddress = request.getAddress();

        // 如果经纬度为空但提供了地址，则进行地理编码
        if ((startLng == null || startLat == null) && ObjectUtil.isNotEmpty(startAddress)) {
            Map<String, Object> geoResult = geoCodeService.geoCode(startAddress);
            if (ObjectUtil.isNotEmpty(geoResult) && geoResult.containsKey("longitude") && geoResult.containsKey("latitude")) {
                startLng = new BigDecimal(geoResult.get("longitude").toString());
                startLat = new BigDecimal(geoResult.get("latitude").toString());
                log.info("地址 '{}' 解析为经纬度: {}, {}", startAddress, startLng, startLat);
            } else {
                throw new BaseException("地址解析失败，请检查地址是否正确或提供经纬度");
            }
        }

        if (startLng == null || startLat == null) {
            throw new BaseException("请提供起点位置（经纬度或地址）");
        }

        // 3. 加载候选数据
        List<AttractionInfo> allAttractions = attractionInfoService.list();
        List<HotelInfo> allHotels = hotelInfoService.list();

        // 4. 筛选候选景点
        List<AttractionInfo> candidates = RoutePlanningAlgorithm.filterCandidates(
                allAttractions,
                request.getPreferenceTypes(),
                startLng,
                startLat,
                ATTRACTION_SEARCH_RADIUS
        );

        if (CollUtil.isEmpty(candidates)) {
            throw new BaseException("未找到符合条件的景点，请调整搜索条件");
        }

        // 5. 预算分配
        Map<Integer, BigDecimal> dailyBudget = RoutePlanningAlgorithm.allocateBudget(
                request.getDays(),
                request.getBudget() != null ? request.getBudget() : new BigDecimal("999999")
        );

        // 6. 生成每日行程
        List<ItineraryDay> itinerary = new ArrayList<>();
        Set<String> visitedAttractionIds = new HashSet<>();
        Set<Long> visitedFoodIds = new HashSet<>();  // 新增：记录已推荐的小吃ID
        BigDecimal currentLng = startLng;
        BigDecimal currentLat = startLat;
        String previousLocationName = startAddress;  // 记录前一天的位置名称（用于下一天的起点）
        int totalAttractions = 0;
        int totalHotels = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalDuration = 0;

        String transportType = ObjectUtil.isNotEmpty(request.getTransportPreference())
                ? request.getTransportPreference() : "drive";
        
        // 计算每天的目标景点数量，确保景点均匀分配
        int totalCandidateCount = candidates.size();
        int attractionsPerDay = Math.max(2, totalCandidateCount / request.getDays()); // 每天至少2个景点
        int remainingDays = request.getDays();
        int remainingAttractions = totalCandidateCount;

        for (int day = 1; day <= request.getDays(); day++) {
            // 计算当天应该安排的景点数量
            int todayTargetAttractions;
            if (day == request.getDays()) {
                // 最后一天安排剩余所有景点
                todayTargetAttractions = remainingAttractions;
            } else {
                // 计算剩余天数每天平均景点数，并预留一些给后续天数
                int avgRemaining = remainingAttractions / remainingDays;
                todayTargetAttractions = Math.min(attractionsPerDay, Math.max(2, avgRemaining + 1));
            }
            
            ItineraryDay itineraryDay = planSingleDay(
                    day,
                    dailyBudget.getOrDefault(day, BigDecimal.ZERO),
                    currentLng,
                    currentLat,
                    candidates,
                    visitedAttractionIds,
                    allHotels,
                    transportType,
                    new HashSet<>(),  // 传入空的小吃ID集合
                    todayTargetAttractions,  // 每天景点数量限制
                    day == 1 ? startAddress : previousLocationName  // 第一天用起点地址，后续用前一天的位置
            );
            
            // 更新剩余景点数
            long dayAttractionCount = itineraryDay.getItems().stream()
                    .filter(item -> "attraction".equals(item.getType()))
                    .count();
            remainingAttractions -= dayAttractionCount;
            remainingDays--;

            itinerary.add(itineraryDay);

            // 统计总数
            long dayAttractions = itineraryDay.getItems().stream()
                    .filter(item -> "attraction".equals(item.getType()))
                    .count();
            long dayHotels = itineraryDay.getItems().stream()
                    .filter(item -> "hotel".equals(item.getType()))
                    .count();

            totalAttractions += dayAttractions;
            totalHotels += dayHotels;

            // 统计费用、距离、时长
            for (ItineraryItem item : itineraryDay.getItems()) {
                // 累加费用（门票+小吃+酒店+交通）
                if (item.getTicketPrice() != null) {
                    totalCost = totalCost.add(item.getTicketPrice());
                }
                if (item.getFoodPrice() != null) {
                    totalCost = totalCost.add(item.getFoodPrice());
                }
                if (item.getRoomPrice() != null) {
                    totalCost = totalCost.add(item.getRoomPrice());
                }
                if (item.getCost() != null) {
                    totalCost = totalCost.add(item.getCost());
                }
                
                // 累加距离
                if (item.getDistance() != null) {
                    totalDistance = totalDistance.add(item.getDistance());
                }
                
                // 累加时长
                if (item.getDuration() != null) {
                    totalDuration += item.getDuration();
                }
                if (item.getPlayDuration() != null) {
                    totalDuration += item.getPlayDuration();
                }
            }

            // 更新当前位置为当天最后一个景点或酒店的位置
            Optional<ItineraryItem> lastItem = itineraryDay.getItems().stream()
                    .filter(item -> item.getLongitude() != null && item.getLatitude() != null)
                    .reduce((first, second) -> second);

            if (lastItem.isPresent()) {
                currentLng = lastItem.get().getLongitude();
                currentLat = lastItem.get().getLatitude();
                // 更新前一天的位置名称（用于下一天的起点）
                if ("hotel".equals(lastItem.get().getType())) {
                    previousLocationName = lastItem.get().getHotelName();
                } else if ("attraction".equals(lastItem.get().getType())) {
                    previousLocationName = lastItem.get().getAttractionName();
                } else if ("food".equals(lastItem.get().getType())) {
                    previousLocationName = lastItem.get().getShopName();
                }
            }
        }

        // 7. 不自动保存，直接构建响应
        RouteRecommendationResponse response = buildResponse(null, itinerary);
        
        // 8. 生成成功后，增加计数
        incrementDailyGenerateCount();

        return response;
    }

    /**
     * 规划单天行程
     */
    private ItineraryDay planSingleDay(
            int dayNum,
            BigDecimal dayBudget,
            BigDecimal startLng,
            BigDecimal startLat,
            List<AttractionInfo> candidates,
            Set<String> visitedIds,
            List<HotelInfo> allHotels,
            String transportType,
            Set<Long> visitedFoodIds,  // 记录已推荐的小吃ID
            int maxAttractionsPerDay,   // 每天最大景点数量
            String startAddress         // 起点地址（仅第一天使用）
    ) {
        ItineraryDay itineraryDay = new ItineraryDay();
        itineraryDay.setDayNum(dayNum);
        itineraryDay.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        List<ItineraryItem> items = new ArrayList<>();
        BigDecimal remainingBudget = dayBudget;
        int remainingMinutes = DAILY_AVAILABLE_MINUTES;
        BigDecimal currentLng = startLng;
        BigDecimal currentLat = startLat;
        int sortOrder = 1;

        // 获取景点ID到票价的映射
        Map<String, BigDecimal> ticketPriceMap = buildTicketPriceMap();
        
        // 统计当天已安排的景点数量
        int todayAttractionCount = 0;

        // 选择景点序列（增加景点数量限制）
        while (remainingMinutes > 60 && 
               remainingBudget.compareTo(BigDecimal.valueOf(50)) > 0 && 
               todayAttractionCount < maxAttractionsPerDay) {
            AttractionInfo nextAttraction = RoutePlanningAlgorithm.selectNextAttraction(
                    currentLng, currentLat,
                    candidates, visitedIds,
                    remainingBudget, remainingMinutes,
                    ticketPriceMap
            );

            if (nextAttraction == null) {
                break;
            }

            // 添加交通路线（包括第一个景点，从起点或上一天终点出发）
            // 获取上一站名称
            String fromName;
            if (items.isEmpty()) {
                // 第一个景点：使用传入的起点地址
                fromName = startAddress != null ? startAddress : "起点";
            } else {
                // 后续景点：从上一个景点或小吃出发
                fromName = "起点";
                for (int i = items.size() - 1; i >= 0; i--) {
                    ItineraryItem prevItem = items.get(i);
                    if ("attraction".equals(prevItem.getType())) {
                        fromName = prevItem.getAttractionName();
                        break;
                    } else if ("food".equals(prevItem.getType())) {
                        fromName = prevItem.getShopName();
                        break;
                    }
                }
            }
            
            ItineraryItem routeItem = createRouteItem(
                    currentLng, currentLat,
                    nextAttraction.getLongitude(), nextAttraction.getLatitude(),
                    fromName,
                    nextAttraction.getAttractionName(),
                    transportType,
                    sortOrder++
            );
            items.add(routeItem);

            // 添加景点
            ItineraryItem attractionItem = createAttractionItem(nextAttraction, sortOrder++);
            items.add(attractionItem);
            visitedIds.add(nextAttraction.getId());
            todayAttractionCount++;  // 增加当天景点计数

            // 更新状态
            BigDecimal ticketPrice = ticketPriceMap.getOrDefault(nextAttraction.getId(), BigDecimal.ZERO);
            remainingBudget = remainingBudget.subtract(ticketPrice);
            remainingMinutes -= nextAttraction.getPlayHour() != null ? nextAttraction.getPlayHour() : 120;
            currentLng = nextAttraction.getLongitude();
            currentLat = nextAttraction.getLatitude();

            // 在景点附近推荐小吃（传入已访问的小吃ID集合）
            ItineraryItem foodItem = recommendNearbyFood(
                    nextAttraction.getLongitude(),
                    nextAttraction.getLatitude(),
                    sortOrder++,
                    visitedFoodIds  // 传入已访问的小吃ID
            );
            if (foodItem != null) {
                // 添加从景点到小吃的交通路线
                ItineraryItem routeToFood = createRouteItem(
                        nextAttraction.getLongitude(),
                        nextAttraction.getLatitude(),
                        foodItem.getLongitude(),
                        foodItem.getLatitude(),
                        nextAttraction.getAttractionName(),
                        foodItem.getShopName(),
                        transportType,
                        sortOrder++
                );
                items.add(routeToFood);
                
                items.add(foodItem);
                remainingBudget = remainingBudget.subtract(foodItem.getFoodPrice() != null
                        ? foodItem.getFoodPrice() : BigDecimal.ZERO);
                // 记录已推荐的小吃ID
                if (foodItem.getFoodInfoId() != null) {
                    visitedFoodIds.add(foodItem.getFoodInfoId());
                }
                // 更新当前位置为小吃店铺
                currentLng = foodItem.getLongitude();
                currentLat = foodItem.getLatitude();
            }
        }

        // 推荐住宿（每天都需）
        if (dayNum >= 1) { // 第1天及以后都需要住宿
            // 在推荐酒店前，先添加从最后一站到酒店的交通路线
            ItineraryItem hotelItem = recommendHotel(
                    currentLng, currentLat,
                    remainingBudget,
                    allHotels,
                    sortOrder++
            );
            if (hotelItem != null) {
                // 如果有前一站（景点或小吃），添加交通路线到酒店
                if (!items.isEmpty()) {
                    String fromName = "起点";
                    for (int i = items.size() - 1; i >= 0; i--) {
                        ItineraryItem prevItem = items.get(i);
                        if ("attraction".equals(prevItem.getType())) {
                            fromName = prevItem.getAttractionName();
                            break;
                        } else if ("food".equals(prevItem.getType())) {
                            fromName = prevItem.getShopName();
                            break;
                        }
                    }
                    
                    ItineraryItem routeToHotel = createRouteItem(
                            currentLng, currentLat,
                            hotelItem.getLongitude(), hotelItem.getLatitude(),
                            fromName,
                            hotelItem.getHotelName(),
                            transportType,
                            sortOrder++
                    );
                    items.add(routeToHotel);
                }
                
                items.add(hotelItem);
            } else {
                log.warn("第{}天未找到合适的酒店，当前位置：({},{})，剩余预算：{}", 
                        dayNum, currentLng, currentLat, remainingBudget);
            }
        }

        itineraryDay.setItems(items);
        return itineraryDay;
    }

    /**
     * 智能推荐交通方式
     * 
     * @param distance 距离（千米）
     * @param userPreference 用户偏好
     * @return 推荐的交通方式
     */
    private String recommendTransportType(double distance, String userPreference) {
        // 根据用户偏好进行智能推荐
        switch (userPreference) {
            case "walking":
            case "walk":
                // 步行模式：2km 以内步行，超过 2km 自动切换
                if (distance <= 2.0) {
                    log.info("步行模式，距离 {} km <= 2km，推荐步行", distance);
                    return "walking";
                } else {
                    log.info("步行模式，距离 {} km > 2km，自动切换为公交", distance);
                    return "bus";  // 超过 2km 自动切换为公交
                }
                
            case "drive":
                // 驾车模式：全部驾车（车必须跟着人走）
                log.info("驾车模式，距离 {} km，推荐驾车", distance);
                return "drive";
                
            case "bus":
                // 公交模式：1km 以内步行，超过 1km 公交
                if (distance <= 1.0) {
                    log.info("公交模式，距离 {} km <= 1km，推荐步行", distance);
                    return "walking";
                } else {
                    log.info("公交模式，距离 {} km > 1km，推荐公交", distance);
                    return "bus";
                }
                
            case "taxi":
                // 打车模式：1km 以内步行，超过 1km 打车
                if (distance <= 1.0) {
                    log.info("打车模式，距离 {} km <= 1km，推荐步行", distance);
                    return "walking";
                } else {
                    log.info("打车模式，距离 {} km > 1km，推荐打车", distance);
                    return "taxi";
                }
                
            default:
                // 默认模式：根据距离智能推荐
                if (distance <= 1.0) {
                    return "walking";
                } else if (distance <= 5.0) {
                    return "bus";
                } else if (distance <= 15.0) {
                    return "taxi";
                } else {
                    return "drive";
                }
        }
    }

    /**
     * 创建景点行程项
     */
    private ItineraryItem createAttractionItem(AttractionInfo attraction, int sortOrder) {
        ItineraryItem item = new ItineraryItem();
        item.setType("attraction");
        item.setSortOrder(sortOrder);
        item.setAttractionId(attraction.getId());
        item.setAttractionName(attraction.getAttractionName());
        item.setPlayDuration(attraction.getPlayHour());
        item.setImages(attraction.getAttractionPic());
        item.setDescription(attraction.getAttractionDesc());
        item.setLongitude(attraction.getLongitude());
        item.setLatitude(attraction.getLatitude());

        // 获取最低票价
        BigDecimal minPrice = getMinTicketPrice(attraction.getId());
        item.setTicketPrice(minPrice);

        return item;
    }

    /**
     * 创建路线行程项（调用高德API获取真实数据）
     */
    private ItineraryItem createRouteItem(
            BigDecimal fromLng, BigDecimal fromLat,
            BigDecimal toLng, BigDecimal toLat,
            String fromName,
            String toName,
            String transportType,
            int sortOrder
    ) {
        // 计算距离
        double distance = RoutePlanningAlgorithm.calculateDistance(
                fromLng.doubleValue(), fromLat.doubleValue(),
                toLng.doubleValue(), toLat.doubleValue()
        );
        
        // 智能推荐交通方式
        String recommendedTransport = recommendTransportType(distance, transportType);
        log.info("从 {} 到 {}，距离 {} km，推荐交通方式：{}", fromName, toName, distance, recommendedTransport);
        
        ItineraryItem item = new ItineraryItem();
        item.setType("route");
        item.setSortOrder(sortOrder);
        item.setFromLocation(fromName);
        item.setToLocation(toName);
        item.setTransportType(recommendedTransport);

        // 调用高德 API 获取真实路线数据
        try {
            String origin = fromLng + "," + fromLat;
            String destination = toLng + "," + toLat;
            
            int duration;
            BigDecimal cost;
            
            if ("bus".equals(recommendedTransport)) {
                // 调用高德公交 API
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
                            distance = firstTransit.getDouble("distance", 0.0) / 1000.0; // 米转千米
                            int durationSeconds = firstTransit.getInt("duration", 0);
                            duration = (int) Math.ceil(durationSeconds / 60.0); // 秒转分钟
                            
                            // 获取公交费用
                            Object costObj = firstTransit.get("cost");
                            if (costObj != null) {
                                try {
                                    double busCost = costObj instanceof Number ? 
                                        ((Number) costObj).doubleValue() : 
                                        Double.parseDouble(costObj.toString());
                                    cost = new BigDecimal(String.format("%.2f", busCost > 0 ? busCost : 2.0));
                                } catch (Exception e) {
                                    cost = new BigDecimal("2.00");
                                }
                            } else {
                                cost = new BigDecimal("2.00");
                            }
                        } else {
                            // 没有公交路线，使用估算
                            distance = RoutePlanningAlgorithm.calculateDistance(
                                    fromLng.doubleValue(), fromLat.doubleValue(),
                                    toLng.doubleValue(), toLat.doubleValue()
                            );
                            duration = (int) (distance / 20 * 60) + 10;
                            cost = new BigDecimal("2.00");
                        }
                    } else {
                        throw new Exception("API返回数据格式错误");
                    }
                } else {
                    throw new Exception("API调用失败: " + jsonResponse.getStr("info"));
                }
            } else if ("walking".equals(recommendedTransport) || "walk".equals(recommendedTransport)) {
                // 步行：调用高德步行 API
                Map<String, Object> params = new HashMap<>();
                params.put("key", amapKey);
                params.put("origin", origin);
                params.put("destination", destination);
                params.put("output", "json");
                
                String response = HttpUtil.get("https://restapi.amap.com/v3/direction/walking", params);
                JSONObject jsonResponse = JSONUtil.parseObj(response);
                
                if ("1".equals(jsonResponse.getStr("status"))) {
                    JSONObject routeResult = jsonResponse.getJSONObject("route");
                    if (routeResult != null) {
                        JSONArray paths = routeResult.getJSONArray("paths");
                        if (paths != null && !paths.isEmpty()) {
                            JSONObject firstPath = paths.getJSONObject(0);
                            distance = firstPath.getDouble("distance", 0.0) / 1000.0; // 米转千米
                            int durationSeconds = firstPath.getInt("duration", 0);
                            duration = (int) Math.ceil(durationSeconds / 60.0); // 秒转分钟
                            
                            // 验证步行时间的合理性：步行速度应该在 3-6 km/h 之间
                            double walkingSpeed = distance / (duration / 60.0); // km/h
                            if (walkingSpeed > 6.0 || walkingSpeed < 3.0) {
                                log.warn("步行时间不合理：距离 {} km, 时间 {} 分钟，计算速度 {} km/h，使用估算值", 
                                    distance, duration, walkingSpeed);
                                // 使用估算值
                                duration = (int) (distance / 5 * 60); // 按 5km/h 估算
                            }
                            
                            // 如果步行距离超过 3km，给出警告
                            if (distance > 3.0) {
                                log.warn("步行距离过长（{} km），建议改为公交或打车", distance);
                            }
                            
                            cost = BigDecimal.ZERO; // 步行免费
                        } else {
                            throw new Exception("未找到步行路线");
                        }
                    } else {
                        throw new Exception("API返回数据格式错误");
                    }
                } else {
                    throw new Exception("API调用失败: " + jsonResponse.getStr("info"));
                }
            } else if ("drive".equals(recommendedTransport) || "taxi".equals(recommendedTransport)) {
                // 调用高德驾车 API
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
                            distance = firstPath.getDouble("distance", 0.0) / 1000.0; // 米转千米
                            int durationSeconds = firstPath.getInt("duration", 0);
                            duration = (int) Math.ceil(durationSeconds / 60.0); // 秒转分钟
                            
                            // 计算费用
                            if ("drive".equals(recommendedTransport)) {
                                cost = new BigDecimal(String.format("%.2f", distance * 0.6));
                            } else {
                                // 打车费用
                                cost = new BigDecimal(String.format("%.2f", 10 + distance * 1.8));
                            }
                        } else {
                            throw new Exception("未找到驾车路线");
                        }
                    } else {
                        throw new Exception("API返回数据格式错误");
                    }
                } else {
                    throw new Exception("API调用失败: " + jsonResponse.getStr("info"));
                }
            } else {
                // 未知交通方式，使用估算
                distance = RoutePlanningAlgorithm.calculateDistance(
                        fromLng.doubleValue(), fromLat.doubleValue(),
                        toLng.doubleValue(), toLat.doubleValue()
                );
                duration = (int) (distance / 30 * 60);
                cost = BigDecimal.ZERO;
            }
            
            item.setDistance(new BigDecimal(String.format("%.2f", distance)));
            item.setDuration(duration);
            item.setCost(cost);
            
            // 设置起点和终点坐标
            item.setFromLongitude(fromLng);
            item.setFromLatitude(fromLat);
            item.setToLongitude(toLng);
            item.setToLatitude(toLat);
            
        } catch (Exception e) {
            log.warn("调用高德API失败，使用估算值: {}", e.getMessage());
            // 出错时使用估算值
            distance = RoutePlanningAlgorithm.calculateDistance(
                    fromLng.doubleValue(), fromLat.doubleValue(),
                    toLng.doubleValue(), toLat.doubleValue()
            );
            item.setDistance(new BigDecimal(String.format("%.2f", distance)));
            
            int duration;
            BigDecimal cost;
            switch (recommendedTransport) {
                case "drive":
                    duration = (int) (distance / 40 * 60);
                    cost = new BigDecimal(String.format("%.2f", distance * 0.6));
                    break;
                case "taxi":
                    duration = (int) (distance / 35 * 60);
                    cost = new BigDecimal(String.format("%.2f", 10 + distance * 1.8));
                    break;
                case "bus":
                    duration = (int) (distance / 20 * 60) + 10;
                    cost = new BigDecimal("2.00");
                    break;
                case "walking":
                case "walk":
                    duration = (int) (distance / 5 * 60); // 步行速度约 5km/h
                    cost = BigDecimal.ZERO;
                    break;
                default:
                    duration = (int) (distance / 30 * 60);
                    cost = BigDecimal.ZERO;
            }
            item.setDuration(duration);
            item.setCost(cost);
            
            // 设置起点和终点坐标
            item.setFromLongitude(fromLng);
            item.setFromLatitude(fromLat);
            item.setToLongitude(toLng);
            item.setToLatitude(toLat);
        }

        return item;
    }

    /**
     * 推荐附近小吃（避免重复推荐）
     */
    private ItineraryItem recommendNearbyFood(
            BigDecimal lng, BigDecimal lat, 
            int sortOrder,
            Set<Long> visitedFoodIds  // 新增：已访问的小吃ID集合
    ) {
        try {
            // 查询所有营业中的小吃店铺
            List<FoodShop> foodShops = foodShopMapper.selectList(
                    new LambdaQueryWrapper<FoodShop>()
                            .eq(FoodShop::getBusinessStatus, "营业中")
            );

            if (ObjectUtil.isEmpty(foodShops)) {
                return null;
            }

            // 找到距离最近且有未推荐小吃的店铺
            FoodShop nearestShop = null;
            double minDistance = Double.MAX_VALUE;

            for (FoodShop shop : foodShops) {
                if (shop.getLongitude() == null || shop.getLatitude() == null) {
                    continue;
                }

                double distance = RoutePlanningAlgorithm.calculateDistance(
                        lng.doubleValue(), lat.doubleValue(),
                        shop.getLongitude().doubleValue(), shop.getLatitude().doubleValue()
                );

                // 如果距离超过3km，跳过
                if (distance > 3.0) {
                    continue;
                }

                // 获取该店铺的小吃信息
                List<FoodInfo> foodInfos = foodInfoMapper.selectList(
                        new LambdaQueryWrapper<FoodInfo>()
                                .eq(FoodInfo::getShopId, shop.getId())
                );

                if (ObjectUtil.isEmpty(foodInfos)) {
                    continue;
                }

                // 检查该店铺是否有未推荐过的小吃
                boolean hasUnvisitedFood = foodInfos.stream()
                        .anyMatch(food -> !visitedFoodIds.contains(food.getId()));

                if (!hasUnvisitedFood) {
                    continue;  // 该店铺所有小吃都已推荐过，跳过
                }

                // 找到最近的有未推荐小吃的店铺
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestShop = shop;
                }
            }

            if (nearestShop == null) {
                return null;
            }

            // 获取该店铺的小吃信息（选择未推荐过且最便宜的）
            List<FoodInfo> foodInfos = foodInfoMapper.selectList(
                    new LambdaQueryWrapper<FoodInfo>()
                            .eq(FoodInfo::getShopId, nearestShop.getId())
            );

            if (ObjectUtil.isEmpty(foodInfos)) {
                return null;
            }

            FoodInfo cheapestUnvisitedFood = foodInfos.stream()
                    .filter(food -> !visitedFoodIds.contains(food.getId()))
                    .filter(food -> food.getAvgPrice() != null)
                    .min(Comparator.comparing(FoodInfo::getAvgPrice))
                    .orElse(null);

            if (cheapestUnvisitedFood == null) {
                return null;
            }

            // 构建行程项
            ItineraryItem item = new ItineraryItem();
            item.setType("food");
            item.setSortOrder(sortOrder);
            item.setFoodShopId(nearestShop.getId());
            item.setFoodInfoId(cheapestUnvisitedFood.getId());
            item.setFoodName(cheapestUnvisitedFood.getName());
            item.setShopName(nearestShop.getName());
            item.setFoodPrice(cheapestUnvisitedFood.getAvgPrice());
            item.setDescription(cheapestUnvisitedFood.getDescription());  // 设置小吃简介
            item.setLongitude(nearestShop.getLongitude());
            item.setLatitude(nearestShop.getLatitude());

            return item;
        } catch (Exception e) {
            log.error("推荐小吃失败", e);
            return null;
        }
    }

    /**
     * 推荐酒店
     */
    private ItineraryItem recommendHotel(
            BigDecimal lng, BigDecimal lat,
            BigDecimal remainingBudget,
            List<HotelInfo> allHotels,
            int sortOrder
    ) {
        log.info("开始推荐酒店，当前位置：({}, {}), 剩余预算：{}, 酒店总数：{}", 
                lng, lat, remainingBudget, allHotels != null ? allHotels.size() : 0);
        
        if (CollUtil.isEmpty(allHotels)) {
            log.warn("酒店列表为空");
            return null;
        }

        // 查找附近的酒店
        Optional<HotelInfo> nearestHotel = allHotels.stream()
                .filter(hotel -> {
                    if (hotel.getLongitude() == null || hotel.getLatitude() == null) {
                        log.debug("酒店 {} 缺少经纬度", hotel.getHtoelName());
                        return false;
                    }
                    return true;
                })
                .filter(hotel -> {
                    double distance = RoutePlanningAlgorithm.calculateDistance(
                            lng.doubleValue(), lat.doubleValue(),
                            hotel.getLongitude().doubleValue(), hotel.getLatitude().doubleValue()
                    );
                    boolean inRange = distance <= HOTEL_SEARCH_RADIUS;
                    log.debug("酒店 {} 距离 {} km, 是否在范围内：{}", hotel.getHtoelName(), distance, inRange);
                    return inRange;
                })
                .min((h1, h2) -> {
                    double dist1 = RoutePlanningAlgorithm.calculateDistance(
                            lng.doubleValue(), lat.doubleValue(),
                            h1.getLongitude().doubleValue(), h1.getLatitude().doubleValue()
                    );
                    double dist2 = RoutePlanningAlgorithm.calculateDistance(
                            lng.doubleValue(), lat.doubleValue(),
                            h2.getLongitude().doubleValue(), h2.getLatitude().doubleValue()
                    );
                    return Double.compare(dist1, dist2);
                });

        if (!nearestHotel.isPresent()) {
            log.warn("未找到 {} km范围内的酒店", HOTEL_SEARCH_RADIUS);
            return null;
        }

        HotelInfo hotel = nearestHotel.get();
        log.info("找到最近酒店：{}", hotel.getHtoelName());

        // 获取最便宜的房间
        List<HotelRoom> rooms = hotelRoomService.getByHotelId(hotel.getId());
        log.info("酒店 {} 的房间数量：{}", hotel.getHtoelName(), rooms != null ? rooms.size() : 0);
        
        if (CollUtil.isEmpty(rooms)) {
            log.warn("酒店 {} 没有房间数据", hotel.getHtoelName());
            return null;
        }

        // 获取最便宜的房间（不再严格限制预算）
        HotelRoom cheapestRoom = rooms.stream()
                .filter(room -> room.getPrice() != null)
                .min(Comparator.comparing(HotelRoom::getPrice))
                .orElse(null);

        if (cheapestRoom == null) {
            log.warn("酒店 {} 没有可用房间", hotel.getHtoelName());
            return null;
        }
        
        // 如果房间价格超出预算，仍然推荐（给用户选择权）
        if (cheapestRoom.getPrice().compareTo(remainingBudget) > 0) {
            log.info("酒店 {} 最便宜房间 {} 元超出预算 {} 元，但仍推荐", 
                    hotel.getHtoelName(), cheapestRoom.getPrice(), remainingBudget);
        } else {
            log.info("成功推荐酒店：{}, 房间：{}, 价格：{} 元", 
                    hotel.getHtoelName(), cheapestRoom.getRoomName(), cheapestRoom.getPrice());
        }

        ItineraryItem item = new ItineraryItem();
        item.setType("hotel");
        item.setSortOrder(sortOrder);
        item.setHotelId(hotel.getId());
        item.setHotelName(hotel.getHtoelName());
        item.setRoomId(cheapestRoom.getId());
        item.setRoomName(cheapestRoom.getRoomName());
        item.setRoomPrice(cheapestRoom.getPrice());
        item.setLongitude(hotel.getLongitude());
        item.setLatitude(hotel.getLatitude());

        return item;
    }

    /**
     * 构建景点ID到最低票价的映射
     */
    private Map<String, BigDecimal> buildTicketPriceMap() {
        List<TicketInfo> allTickets = ticketInfoMapper.selectList(null);
        return allTickets.stream()
                .collect(Collectors.groupingBy(
                        TicketInfo::getAttractionId,
                        Collectors.mapping(TicketInfo::getTicketPrice, Collectors.minBy(BigDecimal::compareTo))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().orElse(BigDecimal.ZERO)
                ));
    }

    /**
     * 获取景点最低票价
     */
    private BigDecimal getMinTicketPrice(String attractionId) {
        List<TicketInfo> tickets = ticketInfoMapper.selectList(
                new LambdaQueryWrapper<TicketInfo>()
                        .eq(TicketInfo::getAttractionId, attractionId)
        );

        return tickets.stream()
                .map(TicketInfo::getTicketPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 保存推荐记录
     */
    private RouteRecommendation saveRecommendation(
            RouteRecommendationRequest request,
            List<ItineraryDay> itinerary,
            int totalAttractions,
            int totalHotels,
            BigDecimal totalCost,
            BigDecimal totalDistance,
            int totalDuration
    ) {
        RouteRecommendation recommendation = new RouteRecommendation();
        recommendation.setId(IdUtil.fastSimpleUUID());
        recommendation.setUserId(ContextUtil.getCurrentUserId());
        recommendation.setRecommendationName(generateRecommendationName(request));
        recommendation.setDays(request.getDays());
        recommendation.setBudget(request.getBudget());
        
        // 处理起点经纬度：如果前端没有传，但有地址，则进行地理编码
        BigDecimal startLng = request.getLongitude();
        BigDecimal startLat = request.getLatitude();
        String startAddress = request.getAddress();
        
        if ((startLng == null || startLat == null) && ObjectUtil.isNotEmpty(startAddress)) {
            try {
                Map<String, Object> geoResult = geoCodeService.geoCode(startAddress);
                if (ObjectUtil.isNotEmpty(geoResult) && geoResult.containsKey("longitude") && geoResult.containsKey("latitude")) {
                    startLng = new BigDecimal(geoResult.get("longitude").toString());
                    startLat = new BigDecimal(geoResult.get("latitude").toString());
                    log.info("保存行程时，地址 '{}' 解析为经纬度: {}, {}", startAddress, startLng, startLat);
                } else {
                    log.warn("保存行程时，地址 '{}' 解析失败，经纬度将为空", startAddress);
                }
            } catch (Exception e) {
                log.error("保存行程时，地址解析异常: {}", e.getMessage(), e);
            }
        }
        
        recommendation.setStartLongitude(startLng);
        recommendation.setStartLatitude(startLat);
        recommendation.setStartAddress(startAddress);
        recommendation.setPreferenceTypes(request.getPreferenceTypes() != null
                ? String.join(",", request.getPreferenceTypes()) : null);
        recommendation.setTotalAttractions(totalAttractions);
        recommendation.setTotalHotels(totalHotels);
        recommendation.setEstimatedCost(totalCost);
        recommendation.setTotalDistance(totalDistance);
        recommendation.setTotalDuration(totalDuration);
        recommendation.setStatus(1);

        this.save(recommendation);

        // 保存详情到数据库
        saveRecommendationDetails(recommendation.getId(), itinerary);

        return recommendation;
    }

    /**
     * 生成推荐方案名称
     */
    private String generateRecommendationName(RouteRecommendationRequest request) {
        // 如果用户自定义了名称，使用自定义名称
        if (request.getRecommendationName() != null && !request.getRecommendationName().trim().isEmpty()) {
            return request.getRecommendationName().trim();
        }
        // 否则使用默认名称
        return request.getDays() + "日精选路线";
    }

    /**
     * 构建响应对象
     */
    private RouteRecommendationResponse buildResponse(
            RouteRecommendation recommendation,
            List<ItineraryDay> itinerary
    ) {
        RouteRecommendationResponse response = new RouteRecommendationResponse();
        
        if (recommendation != null) {
            response.setRecommendationId(recommendation.getId());
            response.setRecommendationName(recommendation.getRecommendationName());

            RouteRecommendationResponse.RecommendationSummary summary =
                    new RouteRecommendationResponse.RecommendationSummary();
            summary.setDays(recommendation.getDays());
            summary.setBudget(recommendation.getBudget());
            summary.setEstimatedCost(recommendation.getEstimatedCost());
            summary.setTotalAttractions(recommendation.getTotalAttractions());
            summary.setTotalHotels(recommendation.getTotalHotels());
            summary.setTotalDistance(recommendation.getTotalDistance());
            summary.setTotalDuration(recommendation.getTotalDuration());

            response.setSummary(summary);
        } else {
            // 如果没有recommendation对象，从itinerary中计算统计数据
            RouteRecommendationResponse.RecommendationSummary summary = 
                    calculateSummaryFromItinerary(itinerary);
            response.setSummary(summary);
        }
        
        response.setItinerary(itinerary);

        return response;
    }

    /**
     * 从行程数据计算统计数据
     */
    private RouteRecommendationResponse.RecommendationSummary calculateSummaryFromItinerary(
            List<ItineraryDay> itinerary) {
        RouteRecommendationResponse.RecommendationSummary summary =
                new RouteRecommendationResponse.RecommendationSummary();
        
        int totalAttractions = 0;
        int totalHotels = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalDuration = 0;
        
        for (ItineraryDay day : itinerary) {
            for (ItineraryItem item : day.getItems()) {
                if ("attraction".equals(item.getType())) {
                    totalAttractions++;
                } else if ("hotel".equals(item.getType())) {
                    totalHotels++;
                }
                
                if (item.getTicketPrice() != null) {
                    totalCost = totalCost.add(item.getTicketPrice());
                }
                if (item.getFoodPrice() != null) {
                    totalCost = totalCost.add(item.getFoodPrice());
                }
                if (item.getRoomPrice() != null) {
                    totalCost = totalCost.add(item.getRoomPrice());
                }
                if (item.getCost() != null) {
                    totalCost = totalCost.add(item.getCost());
                }
                
                if (item.getDistance() != null) {
                    totalDistance = totalDistance.add(item.getDistance());
                }
                
                if (item.getDuration() != null) {
                    totalDuration += item.getDuration();
                }
                if (item.getPlayDuration() != null) {
                    totalDuration += item.getPlayDuration();
                }
            }
        }
        
        summary.setDays(itinerary.size());
        summary.setTotalAttractions(totalAttractions);
        summary.setTotalHotels(totalHotels);
        summary.setEstimatedCost(totalCost);
        summary.setTotalDistance(totalDistance);
        summary.setTotalDuration(totalDuration);
        
        return summary;
    }

    /**
     * 保存推荐详情到数据库 (JSON 优化版)
     */
    private void saveRecommendationDetails(String recommendationId, List<ItineraryDay> itinerary) {
        if (CollUtil.isEmpty(itinerary)) {
            return;
        }

        List<RouteRecommendationDetail> details = new ArrayList<>();
        int sortOrder = 1;

        for (ItineraryDay day : itinerary) {
            for (ItineraryItem item : day.getItems()) {
                RouteRecommendationDetail detail = new RouteRecommendationDetail();
                detail.setId(IdUtil.fastSimpleUUID());
                detail.setRecommendationId(recommendationId);
                detail.setDayNum(day.getDayNum());
                detail.setSortOrder(sortOrder++);

                String type = item.getType();
                detail.setItemType(type);
                com.alibaba.fastjson.JSONObject extraInfo = new com.alibaba.fastjson.JSONObject();

                if ("attraction".equals(type)) {
                    detail.setName(item.getAttractionName());
                    detail.setDuration(item.getPlayDuration());
                    detail.setCost(item.getTicketPrice());
                    detail.setLongitude(item.getLongitude());
                    detail.setLatitude(item.getLatitude());
                    
                    extraInfo.put("attractionId", item.getAttractionId());
                    extraInfo.put("description", item.getDescription());
                    extraInfo.put("images", item.getImages());
                } else if ("food".equals(type)) {
                    detail.setName(item.getShopName());
                    detail.setCost(item.getFoodPrice());
                    detail.setLongitude(item.getLongitude());
                    detail.setLatitude(item.getLatitude());
                    
                    extraInfo.put("foodShopId", item.getFoodShopId());
                    extraInfo.put("foodInfoId", item.getFoodInfoId());
                    extraInfo.put("foodName", item.getFoodName());
                    extraInfo.put("description", item.getDescription());
                } else if ("hotel".equals(type)) {
                    detail.setName(item.getHotelName());
                    detail.setCost(item.getRoomPrice());
                    detail.setLongitude(item.getLongitude());
                    detail.setLatitude(item.getLatitude());
                    
                    extraInfo.put("hotelId", item.getHotelId());
                    extraInfo.put("roomId", item.getRoomId());
                    extraInfo.put("roomName", item.getRoomName());
                } else if ("route".equals(type)) {
                    detail.setName(item.getFromLocation() + " -> " + item.getToLocation());
                    detail.setDuration(item.getDuration());
                    detail.setCost(item.getCost());
                    detail.setLongitude(item.getFromLongitude());
                    detail.setLatitude(item.getFromLatitude());
                    
                    extraInfo.put("fromLocation", item.getFromLocation());
                    extraInfo.put("toLocation", item.getToLocation());
                    extraInfo.put("transportType", item.getTransportType());
                    extraInfo.put("distance", item.getDistance());
                    extraInfo.put("polyline", item.getRoutePolyline());
                    extraInfo.put("fromLongitude", item.getFromLongitude());
                    extraInfo.put("fromLatitude", item.getFromLatitude());
                    extraInfo.put("toLongitude", item.getToLongitude());
                    extraInfo.put("toLatitude", item.getToLatitude());
                }

                detail.setExtraInfo(extraInfo);
                details.add(detail);
            }
        }

        if (CollUtil.isNotEmpty(details)) {
            detailMapper.insert(details);
            log.info("保存推荐详情成功，推荐ID：{}, 详情数量：{}", recommendationId, details.size());
        }
    }

    /**
     * 参数校验
     */
    private void validateRequest(RouteRecommendationRequest request) {
        if (request == null) {
            throw new BaseException("请求参数不能为空");
        }
        if (request.getDays() == null || request.getDays() <= 0) {
            throw new BaseException("旅行天数必须大于0");
        }
        if (request.getDays() > 15) {
            throw new BaseException("旅行天数不能超过15天");
        }
        if ((request.getLongitude() == null || request.getLatitude() == null)
                && ObjectUtil.isEmpty(request.getAddress())) {
            throw new BaseException("请提供起点位置（经纬度或地址）");
        }
    }

    @Override
    public String saveRecommendationManually(RouteRecommendationRequest request) {
        log.info("用户手动保存路线推荐");
        
        // 1. 重新生成行程（因为生成时没有保存）
        RouteRecommendationResponse response = generateRecommendation(request);
        
        if (response == null || response.getItinerary() == null || response.getItinerary().isEmpty()) {
            throw new BaseException("生成行程失败");
        }
        
        // 2. 计算统计数据
        int totalAttractions = 0;
        int totalHotels = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDistance = BigDecimal.ZERO;
        int totalDuration = 0;
        
        for (ItineraryDay day : response.getItinerary()) {
            for (ItineraryItem item : day.getItems()) {
                if ("attraction".equals(item.getType())) {
                    totalAttractions++;
                } else if ("hotel".equals(item.getType())) {
                    totalHotels++;
                }
                
                if (item.getTicketPrice() != null) {
                    totalCost = totalCost.add(item.getTicketPrice());
                }
                if (item.getFoodPrice() != null) {
                    totalCost = totalCost.add(item.getFoodPrice());
                }
                if (item.getRoomPrice() != null) {
                    totalCost = totalCost.add(item.getRoomPrice());
                }
                if (item.getCost() != null) {
                    totalCost = totalCost.add(item.getCost());
                }
                
                if (item.getDistance() != null) {
                    totalDistance = totalDistance.add(item.getDistance());
                }
                
                if (item.getDuration() != null) {
                    totalDuration += item.getDuration();
                }
                if (item.getPlayDuration() != null) {
                    totalDuration += item.getPlayDuration();
                }
            }
        }
        
        // 3. 保存到数据库
        RouteRecommendation recommendation = saveRecommendation(
                request, response.getItinerary(), totalAttractions, totalHotels,
                totalCost, totalDistance, totalDuration
        );
        
        log.info("手动保存成功，推荐ID：{}", recommendation.getId());
        return recommendation.getId();
    }

    @Override
    public IPage<RouteRecommendation> getHistory(QueryFilter<RouteRecommendation> queryFilter) {
        IPage<RouteRecommendation> page = CommonUtil.getPage(queryFilter);
        LambdaQueryWrapper<RouteRecommendation> wrapper = new LambdaQueryWrapper<>();

        // 只查询当前用户的记录
        wrapper.eq(RouteRecommendation::getUserId, ContextUtil.getCurrentUserId());
        wrapper.eq(RouteRecommendation::getStatus, 1);
        wrapper.orderByDesc(RouteRecommendation::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public RouteRecommendationResponse getDetail(String recommendationId) {
        if (ObjectUtil.isEmpty(recommendationId)) {
            throw new BaseException("推荐记录ID不能为空");
        }

        RouteRecommendation recommendation = this.getById(recommendationId);
        if (recommendation == null) {
            throw new BaseException("推荐记录不存在");
        }

        // 从数据库加载详细行程
        List<ItineraryDay> itinerary = loadItineraryFromDatabase(recommendationId);

        RouteRecommendationResponse response = new RouteRecommendationResponse();
        response.setRecommendationId(recommendation.getId());
        response.setRecommendationName(recommendation.getRecommendationName());

        RouteRecommendationResponse.RecommendationSummary summary =
                new RouteRecommendationResponse.RecommendationSummary();
        summary.setDays(recommendation.getDays());
        summary.setBudget(recommendation.getBudget());
        summary.setEstimatedCost(recommendation.getEstimatedCost());
        summary.setTotalAttractions(recommendation.getTotalAttractions());
        summary.setTotalHotels(recommendation.getTotalHotels());
        summary.setTotalDistance(recommendation.getTotalDistance());
        summary.setTotalDuration(recommendation.getTotalDuration());

        response.setSummary(summary);
        response.setItinerary(itinerary);

        return response;
    }

    /**
     * 从数据库加载行程详情
     */
    private List<ItineraryDay> loadItineraryFromDatabase(String recommendationId) {
        // 查询该推荐的所有详情
        List<RouteRecommendationDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<RouteRecommendationDetail>()
                        .eq(RouteRecommendationDetail::getRecommendationId, recommendationId)
                        .orderByAsc(RouteRecommendationDetail::getDayNum, RouteRecommendationDetail::getSortOrder)
        );

        if (CollUtil.isEmpty(details)) {
            return new ArrayList<>();
        }

        // 按天分组
        Map<Integer, List<RouteRecommendationDetail>> dayMap = details.stream()
                .collect(Collectors.groupingBy(RouteRecommendationDetail::getDayNum));

        List<ItineraryDay> itinerary = new ArrayList<>();
        for (Map.Entry<Integer, List<RouteRecommendationDetail>> entry : dayMap.entrySet()) {
            ItineraryDay day = new ItineraryDay();
            day.setDayNum(entry.getKey());

            List<ItineraryItem> items = entry.getValue().stream()
                    .map(this::convertDetailToItem)
                    .collect(Collectors.toList());

            day.setItems(items);
            itinerary.add(day);
        }

        return itinerary;
    }

    /**
     * 将数据库详情转换为行程项 (JSON 优化版)
     */
    private ItineraryItem convertDetailToItem(RouteRecommendationDetail detail) {
        ItineraryItem item = new ItineraryItem();
        String type = detail.getItemType();
        item.setType(type);
        item.setSortOrder(detail.getSortOrder());
        com.alibaba.fastjson.JSONObject extraInfo = detail.getExtraInfo();

        if (extraInfo == null) {
            extraInfo = new com.alibaba.fastjson.JSONObject();
        }

        if ("attraction".equals(type)) {
            item.setAttractionId(extraInfo.getString("attractionId"));
            item.setAttractionName(detail.getName());
            item.setPlayDuration(detail.getDuration());
            item.setTicketPrice(detail.getCost());
            item.setDescription(extraInfo.getString("description"));
            
            // 处理图片：提取 id 并拼接下载 URL
            String imagesStr = extraInfo.getString("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                try {
                    com.alibaba.fastjson.JSONArray imagesArray = com.alibaba.fastjson.JSON.parseArray(imagesStr);
                    java.util.List<String> imageUrls = new java.util.ArrayList<>();
                    for (int i = 0; i < imagesArray.size(); i++) {
                        com.alibaba.fastjson.JSONObject imgObj = imagesArray.getJSONObject(i);
                        String fileId = imgObj.getString("id");
                        if (fileId != null && !fileId.isEmpty()) {
                            // 拼接文件下载 URL
                            imageUrls.add("http://localhost:8088/file/download/" + fileId);
                        }
                    }
                    item.setImages(com.alibaba.fastjson.JSON.toJSONString(imageUrls));
                } catch (Exception e) {
                    log.warn("解析景点图片失败: {}", imagesStr);
                    item.setImages(imagesStr);
                }
            }
        } else if ("food".equals(type)) {
            item.setFoodShopId(extraInfo.getLong("foodShopId"));
            item.setFoodInfoId(extraInfo.getLong("foodInfoId"));
            item.setFoodName(extraInfo.getString("foodName"));
            item.setShopName(detail.getName());
            item.setFoodPrice(detail.getCost());
            item.setDescription(extraInfo.getString("description"));
        } else if ("hotel".equals(type)) {
            item.setHotelId(extraInfo.getString("hotelId"));
            item.setHotelName(detail.getName());
            item.setRoomId(extraInfo.getString("roomId"));
            item.setRoomName(extraInfo.getString("roomName"));
            item.setRoomPrice(detail.getCost());
        } else if ("route".equals(type)) {
            item.setFromLocation(extraInfo.getString("fromLocation"));
            item.setToLocation(extraInfo.getString("toLocation"));
            item.setTransportType(extraInfo.getString("transportType"));
            item.setDistance(extraInfo.getBigDecimal("distance"));
            item.setDuration(detail.getDuration());
            item.setCost(detail.getCost());
            item.setRoutePolyline(extraInfo.getString("polyline"));
            item.setFromLongitude(extraInfo.getBigDecimal("fromLongitude"));
            item.setFromLatitude(extraInfo.getBigDecimal("fromLatitude"));
            item.setToLongitude(extraInfo.getBigDecimal("toLongitude"));
            item.setToLatitude(extraInfo.getBigDecimal("toLatitude"));
        }

        // 设置通用坐标
        item.setLongitude(detail.getLongitude());
        item.setLatitude(detail.getLatitude());

        return item;
    }

    @Override
    public void deleteRecommendation(String recommendationId) {
        if (ObjectUtil.isEmpty(recommendationId)) {
            throw new BaseException("推荐记录ID不能为空");
        }

        RouteRecommendation recommendation = this.getById(recommendationId);
        if (recommendation == null) {
            throw new BaseException("推荐记录不存在");
        }

        // 硬删除关联的详情记录
        List<RouteRecommendationDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<RouteRecommendationDetail>()
                        .eq(RouteRecommendationDetail::getRecommendationId, recommendationId)
        );
        
        if (CollUtil.isNotEmpty(details)) {
            List<String> detailIds = details.stream()
                    .map(RouteRecommendationDetail::getId)
                    .collect(Collectors.toList());
            detailMapper.deleteBatchIds(detailIds);
            log.info("删除行程 {} 的 {} 条详情记录", recommendationId, detailIds.size());
        }
        
        // 硬删除主记录
        this.removeById(recommendationId);
        log.info("成功删除行程：{}", recommendationId);
    }

    @Override
    public int batchDeleteRecommendation(List<String> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            throw new BaseException("请选择要删除的行程");
        }

        log.info("开始批量删除行程，数量：{}", ids.size());
        
        int deleteCount = 0;
        for (String id : ids) {
            try {
                RouteRecommendation recommendation = this.getById(id);
                if (recommendation != null) {
                    // 硬删除关联的详情记录
                    List<RouteRecommendationDetail> details = detailMapper.selectList(
                            new LambdaQueryWrapper<RouteRecommendationDetail>()
                                    .eq(RouteRecommendationDetail::getRecommendationId, id)
                    );
                    
                    if (CollUtil.isNotEmpty(details)) {
                        // 使用 MyBatis-Plus 的 deleteBatchIds 方法批量删除
                        List<String> detailIds = details.stream()
                                .map(RouteRecommendationDetail::getId)
                                .collect(Collectors.toList());
                        detailMapper.deleteBatchIds(detailIds);
                        log.info("删除行程 {} 的 {} 条详情记录", id, detailIds.size());
                    }
                    
                    // 硬删除主记录
                    this.removeById(id);
                    
                    deleteCount++;
                    log.info("成功删除行程：{}", id);
                } else {
                    log.warn("行程不存在：{}", id);
                }
            } catch (Exception e) {
                log.error("删除行程失败：{}", id, e);
            }
        }
        
        log.info("批量删除完成，成功删除：{} 条", deleteCount);
        return deleteCount;
    }

    @Override
    public void favoriteRecommendation(String recommendationId) {
        // TODO: 实现收藏功能
        log.info("收藏推荐方案：{}", recommendationId);
    }
}
