package com.own.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.HuAiResultMapper;
import com.own.model.HuAiResult;
import com.own.service.HuAiResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HuAiResultServiceImpl extends ServiceImpl<HuAiResultMapper, HuAiResult> implements HuAiResultService {

    @Override
    public List<HuAiResult> getAiList(Integer limit) {
        if (limit == 0) {
            //不限制则为10
            limit = 10;
        }

        List<HuAiResult> list = this.lambdaQuery()
                .eq(HuAiResult::getUserId, ContextUtil.getCurrentUserId())
                .orderByDesc(HuAiResult::getCreateTime)
                .last("limit " + limit)
                .list();

        return list;
    }
}