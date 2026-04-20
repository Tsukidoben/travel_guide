package com.own.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.HuAiResult;

import java.util.List;

public interface HuAiResultService extends IService<HuAiResult> {


    List<HuAiResult> getAiList(Integer limit);
}