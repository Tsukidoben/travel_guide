package com.own.service;

import java.util.Map;

/**
 * 地理编码服务接口
 */
public interface GeoCodeService {

    /**
     * 地址转经纬度（地理编码）
     *
     * @param address 地址
     * @return 包含经纬度的Map，格式：{longitude: xxx, latitude: xxx}
     */
    Map<String, Object> geoCode(String address);
}
