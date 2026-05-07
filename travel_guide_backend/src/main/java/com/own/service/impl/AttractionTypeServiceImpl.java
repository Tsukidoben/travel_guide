package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.mappers.AttractionTypeMapper;
import com.own.model.AttractionInfo;
import com.own.model.AttractionType;
import com.own.service.AttractionInfoService;
import com.own.service.AttractionTypeService;
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
 * 分类管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttractionTypeServiceImpl extends ServiceImpl<AttractionTypeMapper, AttractionType> implements AttractionTypeService {

    @Override
    public void saveOrUpdatePlus(AttractionType attractionType) {
        if (ObjectUtil.isEmpty(attractionType)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(attractionType.getTypeName())) {
            throw new BaseException("分类名称不能为空");
        }
        if(ObjectUtil.isEmpty(attractionType.getId())){
            this.save(attractionType);
        }else{
            this.updateById(attractionType);
        }
    }

    @Override
    public void delById(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要删除的数据");
        }

        List<AttractionInfo> list = SpringUtil.getBean(AttractionInfoService.class)
                .lambdaQuery()
                .eq(AttractionInfo::getTypeId, id).list();
        if(ObjectUtil.isNotEmpty(list)){
            throw new BaseException("该分类下存在景点数据，不可删除");
        }
        this.removeById(id);
    }

    @Override
    public void delBatch(DelVo delVo) {
        if(ObjectUtil.isEmpty(delVo.getIds())){
            throw new BaseException("请选择要删除的数据");
        }
        List<AttractionInfo> list = SpringUtil.getBean(AttractionInfoService.class)
                .lambdaQuery()
                .in(AttractionInfo::getTypeId, delVo.getIds()).list();
        if(ObjectUtil.isNotEmpty(list)){
            throw new BaseException("分类下存在景点数据，不可删除");
        }
        this.removeBatchByIds(delVo.getIds());
    }

    @Override
    public IPage<AttractionType> listPage(QueryFilter<AttractionType> queryFilter) {
        // 获取分页条件
        IPage<AttractionType> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        AttractionType params = CommonUtil.getParams(queryFilter, AttractionType.class);
        LambdaQueryWrapper<AttractionType> wrapper = new LambdaQueryWrapper<>();

        // 排序
        wrapper.orderByDesc(AttractionType::getCreateTime);

        // 关键字模糊搜索
        if(ObjectUtil.isNotEmpty(params.getKeyword())){
            wrapper.and(w ->
                        w.like(AttractionType::getTypeName, params.getKeyword())
                        .or().like(AttractionType::getTypeDesc, params.getKeyword())
            );
        }

        IPage<AttractionType> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public AttractionType getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        AttractionType entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<AttractionType> listFront() {
        // 直接返回查询结果，不做转换处理
        return this.list();
    }

    private void convert(List<AttractionType> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, AttractionType.class)
                    .done()
                    .convert();
        }
    }

    private void convert(AttractionType entity) {
        List<AttractionType> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
