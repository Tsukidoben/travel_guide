package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.AttractionOrder;
import com.own.model.HotelOrder;
import com.own.service.HotelOrderService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 酒店订单表
 * 控制层
 */
@RestController
@RequestMapping("/hotelOrder")
public class HotelOrderController {

    @Autowired
    private HotelOrderService hotelOrderService;

    /**
     * 提交订单
     */
    @PostMapping("submit")
    public String submit(@RequestBody HotelOrder hotelOrder) {
        return ResultUtil.successWithData(hotelOrderService.submit(hotelOrder));
    }

    /**
     * 根据id获取跳转支付
     */
    @PostMapping("payById/{id}")
    public String payById(@PathVariable("id") String id) {
        return ResultUtil.<String>successWithData(hotelOrderService.payById(id));
    }

    /**
     * 取消订单
     */
    @PostMapping("cancel/{id}")
    public String cancel(@PathVariable("id") String id) {
        hotelOrderService.cancel(id);
        return ResultUtil.success("订单取消成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        hotelOrderService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        hotelOrderService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<HotelOrder> queryFilter) {
        return ResultUtil.<HotelOrder>returnPages(hotelOrderService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<HotelOrder>successWithData(hotelOrderService.getByIdPlus(id));
    }

    /**
     * 沙箱支付回调接口
     */
    @RequestMapping("notify")
    @IgnoreAuth
    public String payNotify(HttpServletRequest request){
        return ResultUtil.<String>successWithData(hotelOrderService.aliNotify(request));
    }

    /**
     * 根据核销码查询详情
     */
    @PostMapping("getByCaptcha/{captcha}")
    public String getByCaptcha(@PathVariable("captcha") String captcha) {
        return ResultUtil.<HotelOrder>successWithData(hotelOrderService.getByCaptcha(captcha));
    }

    /**
     * 确认核销
     */
    @PostMapping("writeOff/{captcha}")
    public String writeOff(@PathVariable("captcha") String captcha) {
        hotelOrderService.writeOff(captcha);
        return ResultUtil.success("核销成功");
    }

    /**
     * 酒店订单（我的）
     */
    @PostMapping("myOrder")
    public String myOrder(@RequestBody(required = false) QueryFilter<HotelOrder> queryFilter) {
        return ResultUtil.<HotelOrder>returnPages(hotelOrderService.myOrder(queryFilter));
    }
}
