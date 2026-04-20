package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.AliPayUtil;
import com.own.common.utils.CodeUtil;
import com.own.mappers.AttractionOrderMapper;
import com.own.model.AttractionInfo;
import com.own.model.AttractionOrder;
import com.own.model.TicketInfo;
import com.own.service.AttractionInfoService;
import com.own.service.AttractionOrderService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import com.own.service.TicketInfoService;
import jakarta.servlet.http.HttpServletRequest;
import kotlin.jvm.internal.Lambda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.math.BigDecimal;
import java.util.*;

import com.own.common.utils.ContextUtil;

/**
 * 景点订单表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttractionOrderServiceImpl extends ServiceImpl<AttractionOrderMapper, AttractionOrder> implements AttractionOrderService {
    private static final String NOTIFY_API = "/attractionOrder/notify";
    private static final String RETURN_API = "/front/ticketOrder";

    @Override
    public String submit(AttractionOrder attractionOrder) {
        if (ObjectUtil.isEmpty(attractionOrder)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(attractionOrder.getAttractionId())) {
            throw new BaseException("所属景点不能为空");
        }
        if (ObjectUtil.isEmpty(attractionOrder.getTicketId())) {
            throw new BaseException("所属门票不能为空");
        }
        if (ObjectUtil.isEmpty(attractionOrder.getBuyCount())) {
            throw new BaseException("购买数量不能为空");
        }

        // 判断景点是否存在
        AttractionInfo attractionInfo = SpringUtil.getBean(AttractionInfoService.class).getById(attractionOrder.getAttractionId());
        if (ObjectUtil.isEmpty(attractionInfo)) {
            throw new BaseException("景点不存在");
        }
        TicketInfo ticketInfo = SpringUtil.getBean(TicketInfoService.class).getById(attractionOrder.getTicketId());
        if (ObjectUtil.isEmpty(ticketInfo)) {
            throw new BaseException("门票不存在");
        }

        attractionOrder.setOrderCode(CodeUtil.lambdaGenerateCodeWithDate("TICKET", 3, AttractionOrder.class, AttractionOrder::getOrderCode))
                .setAttractionId(attractionInfo.getId())
                .setAttractionShot(CommonUtil.toJson(attractionInfo.setAttractionDetail(null)))
                .setTicketId(ticketInfo.getId())
                .setTicketShot(CommonUtil.toJson(ticketInfo))
                .setUserId(ContextUtil.getCurrentUserId())
                .setOrderState("10")
                .setTotalPrice(ticketInfo.getTicketPrice().multiply(new BigDecimal(attractionOrder.getBuyCount())));

        this.save(attractionOrder);

        return AliPayUtil.toPay("门票购买：" + attractionInfo.getAttractionName() + "-" + ticketInfo.getTicketName() + " * " + attractionOrder.getBuyCount(),
                attractionOrder.getId(), attractionOrder.getTotalPrice(), NOTIFY_API, RETURN_API);
    }

    @Override
    public String payById(String id) {
        AttractionOrder attractionOrder = this.getById(id);

        AttractionInfo attractionInfo = SpringUtil.getBean(AttractionInfoService.class).getById(attractionOrder.getAttractionId());
        TicketInfo ticketInfo = SpringUtil.getBean(TicketInfoService.class).getById(attractionOrder.getTicketId());

        return AliPayUtil.toPay("门票购买：" + attractionInfo.getAttractionName() + "-" + ticketInfo.getTicketName() + " * " + attractionOrder.getBuyCount(),
                attractionOrder.getId(), attractionOrder.getTotalPrice(), NOTIFY_API, RETURN_API);
    }

    @Override
    public void cancel(String id) {
        AttractionOrder attractionOrder = this.getById(id);
        attractionOrder.setOrderState("-1");
        this.updateById(attractionOrder);
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
    public IPage<AttractionOrder> listPage(QueryFilter<AttractionOrder> queryFilter) {
        // 获取分页条件
        IPage<AttractionOrder> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        AttractionOrder params = CommonUtil.getParams(queryFilter, AttractionOrder.class);
        LambdaQueryWrapper<AttractionOrder> wrapper = new LambdaQueryWrapper<>();

        // 根据绑定角色过滤数据
        wrapper.eq("2".equals(ContextUtil.getCurrentUserRole()), AttractionOrder::getUserId, ContextUtil.getCurrentUserId());

        wrapper
                .eq(ObjectUtil.isNotEmpty(params.getAttractionId()), AttractionOrder::getAttractionId, params.getAttractionId())
                .eq(ObjectUtil.isNotEmpty(params.getTicketId()), AttractionOrder::getTicketId, params.getTicketId())
                .eq(ObjectUtil.isNotEmpty(params.getOrderState()), AttractionOrder::getOrderState, params.getOrderState())
                .like(ObjectUtil.isNotEmpty(params.getOrderCode()), AttractionOrder::getOrderCode, params.getOrderCode())
        ;

        // 排序
        wrapper.orderByDesc(AttractionOrder::getCreateTime);

        IPage<AttractionOrder> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public AttractionOrder getByIdPlus(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BaseException("请选择要查看的数据");
        }
        AttractionOrder entity = this.getById(id);
        if (ObjectUtil.isNotEmpty(entity)) {
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public AttractionOrder getByCaptcha(String captcha) {
        AttractionOrder attractionOrder = this.lambdaQuery().eq(AttractionOrder::getCaptcha, captcha).one();
        if (ObjectUtil.isEmpty(attractionOrder)) {
            throw new BaseException("核销码无效");
        }
        return attractionOrder;
    }

    @Override
    public void writeOff(String captcha) {
        AttractionOrder attractionOrder = this.lambdaQuery().eq(AttractionOrder::getCaptcha, captcha).one();
        if (ObjectUtil.isEmpty(attractionOrder)) {
            throw new BaseException("核销码无效");
        }

        attractionOrder.setOrderState("80")
                .setWriteoffTime(new Date());

        this.updateById(attractionOrder);
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
            AttractionOrder orderInfo = this.baseMapper.selectById(tradeNo);
            if (ObjectUtil.isNotEmpty(orderInfo)) {
                orderInfo.setPayTime(new Date())
                        .setPayNo(alipayTradeNo)
                        .setCaptcha(CodeUtil.lambdaGenerateCode("", 8, AttractionOrder.class, AttractionOrder::getCaptcha))
                        .setOrderState("20");

                this.updateById(orderInfo);
            }
        }
        return "success";
    }

    @Override
    public IPage<AttractionOrder> myOrder(QueryFilter<AttractionOrder> queryFilter) {
        // 获取分页条件
        IPage<AttractionOrder> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        AttractionOrder params = CommonUtil.getParams(queryFilter, AttractionOrder.class);
        LambdaQueryWrapper<AttractionOrder> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .eq(ObjectUtil.isNotEmpty(params.getAttractionId()), AttractionOrder::getAttractionId, params.getAttractionId())
                .eq(AttractionOrder::getUserId, ContextUtil.getCurrentUserId())
                .eq(ObjectUtil.isNotEmpty(params.getTicketId()), AttractionOrder::getTicketId, params.getTicketId())
                .eq(ObjectUtil.isNotEmpty(params.getOrderState()), AttractionOrder::getOrderState, params.getOrderState())
                .like(ObjectUtil.isNotEmpty(params.getOrderCode()), AttractionOrder::getOrderCode, params.getOrderCode());

        // 排序
        wrapper.orderByDesc(AttractionOrder::getCreateTime);

        IPage<AttractionOrder> resp = this.page(page, wrapper);
        if (ObjectUtil.isNotEmpty(resp.getRecords())) {
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    private void convert(List<AttractionOrder> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, AttractionOrder.class)
                    .done()
                    .convert();
        }
    }

    private void convert(AttractionOrder entity) {
        List<AttractionOrder> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
