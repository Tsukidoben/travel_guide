package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.AliPayUtil;
import com.own.common.utils.CodeUtil;
import com.own.mappers.HotelOrderMapper;
import com.own.model.HotelInfo;
import com.own.model.HotelOrder;
import com.own.model.HotelRoom;
import com.own.service.HotelInfoService;
import com.own.service.HotelOrderService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import com.own.service.HotelRoomService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.math.BigDecimal;
import java.util.*;

import com.own.common.utils.ContextUtil;

/**
 * 酒店订单表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HotelOrderServiceImpl extends ServiceImpl<HotelOrderMapper, HotelOrder> implements HotelOrderService {
    private static final String NOTIFY_API = "/hotelOrder/notify";
    private static final String RETURN_API = "/front/hotelOrderFront";

    @Override
    public String submit(HotelOrder hotelOrder) {
        if (ObjectUtil.isEmpty(hotelOrder)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(hotelOrder.getHotelId())) {
            throw new BaseException("酒店id不能为空");
        }
        if (ObjectUtil.isEmpty(hotelOrder.getRoomId())) {
            throw new BaseException("房间id不能为空");
        }
        if (ObjectUtil.isEmpty(hotelOrder.getBuyCount())) {
            throw new BaseException("购买数量不能为空");
        }

        HotelInfo hotelInfo = SpringUtil.getBean(HotelInfoService.class)
                .getById(hotelOrder.getHotelId());
        if (ObjectUtil.isEmpty(hotelInfo)) {
            throw new BaseException("酒店不存在");
        }
        HotelRoom hotelRoom = SpringUtil.getBean(HotelRoomService.class)
                .getById(hotelOrder.getRoomId());
        if (ObjectUtil.isEmpty(hotelRoom)) {
            throw new BaseException("房间不存在");
        }

        hotelOrder.setOrderState("10")
                .setHotelShot(CommonUtil.toJson(hotelInfo.setHotelDetail(null)))
                .setHotelId(hotelInfo.getId())
                .setRoomId(hotelRoom.getId())
                .setRoomShot(CommonUtil.toJson(hotelRoom))
                .setUserId(ContextUtil.getCurrentUserId())
                .setOrderCode(CodeUtil.lambdaGenerateCodeWithDate("HOTEL", 3, HotelOrder.class, HotelOrder::getOrderCode))
                .setTotalPrice(hotelRoom.getPrice().multiply(new BigDecimal(hotelOrder.getBuyCount()).multiply(new BigDecimal(hotelOrder.getDays()))));

        this.save(hotelOrder);

        return AliPayUtil.toPay("酒店预定：" + hotelInfo.getHtoelName() + "-" + hotelRoom.getRoomName() + " * " + hotelOrder.getBuyCount(),
                hotelOrder.getId(),
                hotelOrder.getTotalPrice(),
                NOTIFY_API, RETURN_API);
    }

    @Override
    public void cancel(String id) {
        HotelOrder hotelOrder = this.getById(id);
        hotelOrder.setOrderState("-1");
        this.updateById(hotelOrder);
    }

    @Override
    public String payById(String id) {
        HotelOrder hotelOrder = this.getById(id);

        HotelInfo hotelInfo = SpringUtil.getBean(HotelInfoService.class)
                .getById(hotelOrder.getHotelId());

        HotelRoom hotelRoom = SpringUtil.getBean(HotelRoomService.class)
                .getById(hotelOrder.getRoomId());

        return AliPayUtil.toPay("酒店预定：" + hotelInfo.getHtoelName() + "-" + hotelRoom.getRoomName() + " * " + hotelOrder.getBuyCount(),
                hotelOrder.getId(),
                hotelOrder.getTotalPrice(),
                NOTIFY_API, RETURN_API);
    }

    @Override
    public void delById(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if (ObjectUtil.isEmpty(delVo.getIds())) {
            throw new BaseException("请选择要删除的数据");
        }
        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<HotelOrder> listPage(QueryFilter<HotelOrder> queryFilter) {
        // 获取分页条件
        IPage<HotelOrder> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        HotelOrder params = CommonUtil.getParams(queryFilter, HotelOrder.class);
        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();

        // 根据绑定角色过滤数据
        if("3".equals(ContextUtil.getCurrentUserRole())){
            List<HotelInfo> hotelInfoList = SpringUtil.getBean(HotelInfoService.class)
                    .lambdaQuery()
                    .eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId())
                    .list();
            if(ObjectUtil.isEmpty(hotelInfoList)){
                return page;
            }
            wrapper.in(HotelOrder::getHotelId, hotelInfoList.stream().map(HotelInfo::getId).toList());
        }

        wrapper
                .eq(ObjectUtil.isNotEmpty(params.getHotelId()), HotelOrder::getHotelId, params.getHotelId())
                .eq(ObjectUtil.isNotEmpty(params.getRoomId()), HotelOrder::getRoomId, params.getRoomId())
                .eq(ObjectUtil.isNotEmpty(params.getOrderState()), HotelOrder::getOrderState, params.getOrderState())
                .like(ObjectUtil.isNotEmpty(params.getOrderCode()), HotelOrder::getOrderCode, params.getOrderCode())
        ;

        // 排序
        wrapper.orderByDesc(HotelOrder::getCreateTime);

        IPage<HotelOrder> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public HotelOrder getByIdPlus(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }
        HotelOrder entity = this.getById(id);
        if (ObjectUtil.isNotEmpty(entity)) {
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public String aliNotify(HttpServletRequest request) {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }
            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");

            // 更新订单已支付的逻辑代码
            HotelOrder orderInfo = this.baseMapper.selectById(tradeNo);
            if (ObjectUtil.isNotEmpty(orderInfo)) {
                orderInfo.setPayTime(new Date())
                        .setPayNo(alipayTradeNo)
                        .setCaptcha(CodeUtil.lambdaGenerateCode("", 8, HotelOrder.class, HotelOrder::getCaptcha))
                        .setOrderState("20");

                this.updateById(orderInfo);
            }
        }
        return "success";
    }

    @Override
    public HotelOrder getByCaptcha(String captcha) {
        HotelOrder hotelOrder = this.lambdaQuery().eq(HotelOrder::getCaptcha, captcha).one();
        if (ObjectUtil.isEmpty(hotelOrder)) {
            throw new BaseException("核销码无效");
        }
        return hotelOrder;
    }

    @Override
    public void writeOff(String captcha) {
        HotelOrder hotelOrder = this.lambdaQuery().eq(HotelOrder::getCaptcha, captcha).one();
        if (ObjectUtil.isEmpty(hotelOrder)) {
            throw new BaseException("核销码无效");
        }

        hotelOrder.setOrderState("80")
                .setWriteoffTime(new Date());

        this.updateById(hotelOrder);
    }

    @Override
    public IPage<HotelOrder> myOrder(QueryFilter<HotelOrder> queryFilter) {
        // 获取分页条件
        IPage<HotelOrder> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        HotelOrder params = CommonUtil.getParams(queryFilter, HotelOrder.class);
        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();

        if ("3".equals(ContextUtil.getCurrentUserRole())) {
            // 查询我的酒店有哪些
            List<HotelInfo> list = SpringUtil.getBean(HotelInfoService.class)
                    .lambdaQuery()
                    .eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId()).list();
            if (CollUtil.isEmpty(list)) {
                return page;
            }
            wrapper.in(HotelOrder::getHotelId, list.stream().map(HotelInfo::getId).toList());
        }

        wrapper
                .eq(ObjectUtil.isNotEmpty(params.getHotelId()), HotelOrder::getHotelId, params.getHotelId())
                .eq(HotelOrder::getUserId, ContextUtil.getCurrentUserId())
                .eq(ObjectUtil.isNotEmpty(params.getRoomId()), HotelOrder::getRoomId, params.getRoomId())
                .eq(ObjectUtil.isNotEmpty(params.getOrderState()), HotelOrder::getOrderState, params.getOrderState())
                .like(ObjectUtil.isNotEmpty(params.getOrderCode()), HotelOrder::getOrderCode, params.getOrderCode());

        // 排序
        wrapper.orderByDesc(HotelOrder::getCreateTime);

        IPage<HotelOrder> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    private void convert(List<HotelOrder> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, HotelOrder.class)
                    .done()
                    .convert();
        }
    }

    private void convert(HotelOrder entity) {
        List<HotelOrder> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
