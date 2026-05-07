package com.own.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.own.service.GeoCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 地理编码服务实现类
 */
@Slf4j
@Service
public class GeoCodeServiceImpl implements GeoCodeService {

    @Value("${amap.key:}")
    private String amapKey;

    private static final String GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    private static final String REVERSE_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/regeo";

    @Override
    public Map<String, Object> geoCode(String address) {
        Map<String, Object> result = new HashMap<>();

        if (ObjectUtil.isEmpty(address)) {
            log.warn("地址为空，无法进行地理编码");
            return result;
        }

        if (ObjectUtil.isEmpty(amapKey)) {
            log.error("高德地图API Key未配置");
            return result;
        }

        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("address", address);
            params.put("output", "json");

            // 调用高德地图API
            String response = HttpUtil.get(GEOCODE_URL, params);
            log.info("高德地图地理编码响应: {}", response);

            // 解析响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            String status = jsonObject.getStr("status");
            String info = jsonObject.getStr("info");

            if ("1".equals(status) && "OK".equals(info)) {
                JSONObject geocodes = jsonObject.getJSONArray("geocodes").getJSONObject(0);
                if (geocodes != null) {
                    String location = geocodes.getStr("location");
                    if (ObjectUtil.isNotEmpty(location)) {
                        String[] lngLat = location.split(",");
                        if (lngLat.length == 2) {
                            double longitude = Double.parseDouble(lngLat[0]);
                            double latitude = Double.parseDouble(lngLat[1]);

                            result.put("longitude", longitude);
                            result.put("latitude", latitude);
                            log.info("地址 '{}' 解析成功: 经度={}, 纬度={}", address, longitude, latitude);
                        }
                    }
                }
            } else {
                log.error("高德地图API返回错误: status={}, info={}", status, info);
            }
        } catch (Exception e) {
            log.error("地理编码异常: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public Map<String, Object> reverseGeoCode(Double longitude, Double latitude) {
        Map<String, Object> result = new HashMap<>();

        if (longitude == null || latitude == null) {
            log.warn("经纬度为空，无法进行逆地理编码");
            return result;
        }

        if (ObjectUtil.isEmpty(amapKey)) {
            log.error("高德地图API Key未配置");
            return result;
        }

        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("location", longitude + "," + latitude);
            params.put("output", "json");
            params.put("extensions", "base");
            params.put("poitype", ""); // 不指定POI类型，获取更通用的地址
            params.put("radius", 500); // 缩小搜索半径到500米，获取最近的地址
            params.put("roadlevel", 1); // 返回道路信息

            // 调用高德地图逆地理编码API
            String response = HttpUtil.get(REVERSE_GEOCODE_URL, params);
            log.info("高德地图逆地理编码响应: {}", response);

            // 解析响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            String status = jsonObject.getStr("status");
            String info = jsonObject.getStr("info");

            if ("1".equals(status) && "OK".equals(info)) {
                JSONObject regeocode = jsonObject.getJSONObject("regeocode");
                if (regeocode != null) {
                    String formattedAddress = regeocode.getStr("formatted_address");
                    if (ObjectUtil.isNotEmpty(formattedAddress)) {
                        result.put("address", formattedAddress);
                        result.put("formattedAddress", formattedAddress);
                        log.info("经纬度 {}, {} 解析成功: {}", longitude, latitude, formattedAddress);
                    }
                }
            } else {
                log.error("高德地图API返回错误: status={}, info={}", status, info);
            }
        } catch (Exception e) {
            log.error("逆地理编码异常: {}", e.getMessage(), e);
        }

        return result;
    }
}
