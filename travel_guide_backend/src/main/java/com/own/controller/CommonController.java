package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.service.GeoCodeService;
import cn.y8e.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用控制器
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Autowired
    private GeoCodeService geoCodeService;

    /**
     * 地址转经纬度（地理编码）
     *
     * @param params 请求参数，包含address
     * @return 经纬度信息
     */
    @PostMapping("/geoCode")
    @IgnoreAuth
    public String geoCode(@RequestBody Map<String, String> params) {
        String address = params.get("address");
        Map<String, Object> result = geoCodeService.geoCode(address);
        return ResultUtil.successWithData(result);
    }

    /**
     * 经纬度转地址（逆地理编码）
     *
     * @param params 请求参数，包含longitude和latitude
     * @return 地址信息
     */
    @PostMapping("/reverseGeoCode")
    @IgnoreAuth
    public String reverseGeoCode(@RequestBody Map<String, Object> params) {
        Double longitude = params.get("longitude") != null ? 
            Double.valueOf(params.get("longitude").toString()) : null;
        Double latitude = params.get("latitude") != null ? 
            Double.valueOf(params.get("latitude").toString()) : null;
        Map<String, Object> result = geoCodeService.reverseGeoCode(longitude, latitude);
        return ResultUtil.successWithData(result);
    }
}
