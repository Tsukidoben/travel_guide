package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.HotelInfoMapper;
import com.own.model.AttractionInfo;
import com.own.model.HotelInfo;
import com.own.service.HotelInfoService;
import com.own.model.vo.DelVo;
import com.own.common.utils.CommonUtil;
import cn.y8e.common.utils.convert.ConvertUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.vo.QueryFilter;
import kotlin.jvm.internal.Lambda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.ArrayList;

/**
 * 酒店管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HotelInfoServiceImpl extends ServiceImpl<HotelInfoMapper, HotelInfo> implements HotelInfoService {

    @Override
    public void saveOrUpdatePlus(HotelInfo hotelInfo) {
        if (ObjectUtil.isEmpty(hotelInfo)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(hotelInfo.getHtoelPic())) {
            throw new BaseException("酒店图片不能为空");
        }
        if (ObjectUtil.isEmpty(hotelInfo.getHtoelName())) {
            throw new BaseException("酒店名称不能为空");
        }
        if (ObjectUtil.isEmpty(hotelInfo.getHotelDesc())) {
            throw new BaseException("酒店介绍不能为空");
        }
        if (ObjectUtil.isEmpty(hotelInfo.getHotelDetail())) {
            throw new BaseException("酒店描述不能为空");
        }
        if (ObjectUtil.isEmpty(hotelInfo.getHotelService())) {
            throw new BaseException("酒店服务不能为空");
        }
        if(ObjectUtil.isEmpty(hotelInfo.getId())){
            this.save(hotelInfo);
        }else{
            this.updateById(hotelInfo);
        }
    }

    @Override
    public void delById(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }
        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<HotelInfo> listPage(QueryFilter<HotelInfo> queryFilter) {
        // 获取分页条件
        IPage<HotelInfo> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        HotelInfo params = CommonUtil.getParams(queryFilter, HotelInfo.class);
        LambdaQueryWrapper<HotelInfo> wrapper = new LambdaQueryWrapper<>();

        if("3".equals(ContextUtil.getCurrentUserRole())){
            wrapper.eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId());
        }

        // 排序
        wrapper.orderByDesc(HotelInfo::getCreateTime);

        // 关键字模糊搜索
        if(ObjectUtil.isNotEmpty(params.getKeyword())){
            wrapper.and(w ->
                        w.like(HotelInfo::getHtoelName, params.getKeyword())
                        .or().like(HotelInfo::getHotelDesc, params.getKeyword())
            );
        }

        IPage<HotelInfo> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public HotelInfo getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        HotelInfo entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<HotelInfo> recommend() {
        List<HotelInfo> list = this.list();
        if(ObjectUtil.isNotEmpty(list)){
            this.convert(list);
        }
        return RandomUtil.randomEleList(list,5);
    }

    private void convert(List<HotelInfo> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, HotelInfo.class)
                    .done()
                    .convert();
        }
    }

    private void convert(HotelInfo entity) {
        List<HotelInfo> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
