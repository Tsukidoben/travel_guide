package com.own.common.algorithm;

import cn.hutool.core.util.ObjectUtil;
import com.own.model.AttractionInfo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路线规划算法工具类
 */
@Slf4j
public class RoutePlanningAlgorithm {

    /**
     * 地球半径（千米）
     */
    private static final int EARTH_RADIUS = 6371;

    /**
     * 计算两点间的直线距离（Haversine公式）
     *
     * @param lng1 起点经度
     * @param lat1 起点纬度
     * @param lng2 终点经度
     * @param lat2 终点纬度
     * @return 距离（千米）
     */
    public static double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 基于最近邻算法选择下一个景点
     *
     * @param currentLng     当前位置经度
     * @param currentLat     当前位置纬度
     * @param candidates     候选景点列表
     * @param visitedIds     已访问的景点ID集合
     * @param remainingBudget 剩余预算
     * @param remainingMinutes 剩余时间（分钟）
     * @param ticketPriceMap  景点ID到最低票价的映射
     * @return 选择的下一个景点，如果没有合适的则返回null
     */
    public static AttractionInfo selectNextAttraction(
            BigDecimal currentLng,
            BigDecimal currentLat,
            List<AttractionInfo> candidates,
            Set<String> visitedIds,
            BigDecimal remainingBudget,
            int remainingMinutes,
            Map<String, BigDecimal> ticketPriceMap
    ) {
        if (ObjectUtil.isEmpty(candidates)) {
            return null;
        }

        AttractionInfo bestChoice = null;
        double minDistance = Double.MAX_VALUE;

        for (AttractionInfo attraction : candidates) {
            // 跳过已访问的
            if (visitedIds.contains(attraction.getId())) {
                continue;
            }

            // 检查经纬度是否存在
            if (attraction.getLongitude() == null || attraction.getLatitude() == null) {
                continue;
            }

            // 检查预算
            BigDecimal ticketPrice = ticketPriceMap.getOrDefault(attraction.getId(), BigDecimal.ZERO);
            if (ticketPrice.compareTo(remainingBudget) > 0) {
                continue;
            }

            // 检查时间
            Integer playHour = attraction.getPlayHour();
            if (playHour != null && playHour > remainingMinutes) {
                continue;
            }

            // 计算距离
            double distance = calculateDistance(
                    currentLng.doubleValue(), currentLat.doubleValue(),
                    attraction.getLongitude().doubleValue(),
                    attraction.getLatitude().doubleValue()
            );

            // 选择最近的
            if (distance < minDistance) {
                minDistance = distance;
                bestChoice = attraction;
            }
        }

        return bestChoice;
    }

    /**
     * 筛选候选景点
     *
     * @param allAttractions   所有景点
     * @param preferenceTypes  偏好分类ID列表
     * @param startLng         起点经度
     * @param startLat         起点纬度
     * @param maxDistance      最大距离（千米）
     * @return 筛选后的候选景点
     */
    public static List<AttractionInfo> filterCandidates(
            List<AttractionInfo> allAttractions,
            List<String> preferenceTypes,
            BigDecimal startLng,
            BigDecimal startLat,
            double maxDistance
    ) {
        if (ObjectUtil.isEmpty(allAttractions)) {
            return new ArrayList<>();
        }

        return allAttractions.stream()
                .filter(attraction -> {
                    // 必须有经纬度
                    if (attraction.getLongitude() == null || attraction.getLatitude() == null) {
                        return false;
                    }

                    // 按偏好分类过滤
                    if (ObjectUtil.isNotEmpty(preferenceTypes) && ObjectUtil.isNotEmpty(attraction.getTypeId())) {
                        boolean matched = false;
                        String typeId = attraction.getTypeId();
                        
                        for (String prefType : preferenceTypes) {
                            // 策略1: 精确匹配
                            if (typeId.equals(prefType)) {
                                matched = true;
                                break;
                            }
                            
                            // 策略2: 如果偏好类型是短数字(1-3位)，尝试匹配ID末尾
                            if (prefType.length() <= 3 && typeId.length() > 3) {
                                String suffix = typeId.substring(typeId.length() - prefType.length());
                                if (suffix.equals(prefType)) {
                                    matched = true;
                                    break;
                                }
                            }
                        }
                        
                        if (!matched) {
                            return false;
                        }
                    }

                    // 按距离过滤
                    double distance = calculateDistance(
                            startLng.doubleValue(), startLat.doubleValue(),
                            attraction.getLongitude().doubleValue(),
                            attraction.getLatitude().doubleValue()
                    );

                    return distance <= maxDistance;
                })
                .collect(Collectors.toList());
    }

    /**
     * 预算分配算法 - 按天数平均分配
     *
     * @param days        总天数
     * @param totalBudget 总预算
     * @return 每天的预算分配
     */
    public static Map<Integer, BigDecimal> allocateBudget(int days, BigDecimal totalBudget) {
        Map<Integer, BigDecimal> dailyBudget = new HashMap<>();
        if (days <= 0 || totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) <= 0) {
            return dailyBudget;
        }

        BigDecimal dailyAmount = totalBudget.divide(new BigDecimal(days), 2, BigDecimal.ROUND_HALF_UP);
        for (int i = 1; i <= days; i++) {
            dailyBudget.put(i, dailyAmount);
        }

        return dailyBudget;
    }

    /**
     * 计算顺路指数（1-5星）
     *
     * @param distance 距离（千米）
     * @return 顺路指数
     */
    public static int calculateConvenienceIndex(double distance) {
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
}
