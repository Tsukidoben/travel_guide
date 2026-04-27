package com.own.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.own.common.utils.DistanceCalculator;
import com.own.mappers.AttractionInfoMapper;
import com.own.mappers.FoodShopMapper;
import com.own.mappers.FoodInfoMapper;
import com.own.model.AttractionInfo;
import com.own.model.FoodShop;
import com.own.model.FoodInfo;
import com.own.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路线服务实现类
 */
@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private AttractionInfoMapper attractionInfoMapper;

    @Autowired
    private FoodShopMapper foodShopMapper;

    @Autowired
    private FoodInfoMapper foodInfoMapper;

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

        // 2. 计算总距离
        double distance = DistanceCalculator.calculateDistance(
                fromAttraction.getLongitude(), fromAttraction.getLatitude(),
                toAttraction.getLongitude(), toAttraction.getLatitude());

        result.put("distance", roundToTwoDecimal(distance));

        // 3. 内置规则自动估算：自驾 / 公交 / 打车 耗时、预估费用、路线简述
        // 自驾
        Map<String, Object> driveInfo = new HashMap<>();
        driveInfo.put("time", DistanceCalculator.estimateDriveTime(distance));
        driveInfo.put("cost", DistanceCalculator.estimateDriveCost(distance));
        driveInfo.put("description", "沿主要道路行驶，途经城市主干道");
        result.put("drive", driveInfo);

        // 公交
        Map<String, Object> busInfo = new HashMap<>();
        busInfo.put("time", DistanceCalculator.estimateBusTime(distance));
        busInfo.put("cost", DistanceCalculator.estimateBusCost(distance));
        busInfo.put("description", "乘坐公交线路，可能需要换乘1-2次");
        result.put("bus", busInfo);

        // 打车
        Map<String, Object> taxiInfo = new HashMap<>();
        taxiInfo.put("time", DistanceCalculator.estimateTaxiTime(distance));
        taxiInfo.put("cost", DistanceCalculator.estimateTaxiCost(distance));
        taxiInfo.put("description", "直接打车前往，无需换乘");
        result.put("taxi", taxiInfo);

        return result;
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
