package com.own.common.utils;

import java.math.BigDecimal;

/**
 * 经纬度距离计算工具类
 * 使用Haversine公式计算地球表面两点之间的直线距离
 */
public class DistanceCalculator {

    // 地球半径（单位：千米）
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * 计算两点之间的直线距离（单位：千米）
     *
     * @param lng1 起点经度
     * @param lat1 起点纬度
     * @param lng2 终点经度
     * @param lat2 终点纬度
     * @return 距离（千米）
     */
    public static double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        // 将角度转换为弧度
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLng = Math.toRadians(lng2 - lng1);
        double deltaLat = Math.toRadians(lat2 - lat1);

        // Haversine公式
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离
        return EARTH_RADIUS * c;
    }

    /**
     * 计算两点之间的直线距离（使用BigDecimal）
     *
     * @param lng1 起点经度
     * @param lat1 起点纬度
     * @param lng2 终点经度
     * @param lat2 终点纬度
     * @return 距离（千米）
     */
    public static double calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        if (lng1 == null || lat1 == null || lng2 == null || lat2 == null) {
            return Double.MAX_VALUE;
        }
        return calculateDistance(lng1.doubleValue(), lat1.doubleValue(), lng2.doubleValue(), lat2.doubleValue());
    }

    /**
     * 根据距离和速度估算时间（分钟）
     *
     * @param distance 距离（千米）
     * @param speed    速度（千米/小时）
     * @return 时间（分钟）
     */
    public static int estimateTime(double distance, double speed) {
        if (speed <= 0) {
            return 0;
        }
        return (int) Math.ceil((distance / speed) * 60);
    }

    /**
     * 估算自驾时间（假设平均速度40km/h）
     *
     * @param distance 距离（千米）
     * @return 时间（分钟）
     */
    public static int estimateDriveTime(double distance) {
        return estimateTime(distance, 40.0);
    }

    /**
     * 估算公交时间（假设平均速度20km/h，包含等车和换乘时间）
     *
     * @param distance 距离（千米）
     * @return 时间（分钟）
     */
    public static int estimateBusTime(double distance) {
        return estimateTime(distance, 20.0);
    }

    /**
     * 估算打车时间（假设平均速度35km/h）
     *
     * @param distance 距离（千米）
     * @return 时间（分钟）
     */
    public static int estimateTaxiTime(double distance) {
        return estimateTime(distance, 35.0);
    }

    /**
     * 估算自驾费用（假设每公里1元油费+过路费）
     *
     * @param distance 距离（千米）
     * @return 费用（元）
     */
    public static double estimateDriveCost(double distance) {
        return Math.round(distance * 1.0 * 100.0) / 100.0;
    }

    /**
     * 估算打车费用（起步价10元/3km，之后每公里2元）
     *
     * @param distance 距离（千米）
     * @return 费用（元）
     */
    public static double estimateTaxiCost(double distance) {
        if (distance <= 3) {
            return 10.0;
        }
        double cost = 10 + (distance - 3) * 2;
        return Math.round(cost * 100.0) / 100.0;
    }

    /**
     * 估算公交费用（固定2元）
     *
     * @param distance 距离（千米）
     * @return 费用（元）
     */
    public static double estimateBusCost(double distance) {
        return 2.0;
    }
}
