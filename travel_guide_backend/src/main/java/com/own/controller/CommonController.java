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
}
