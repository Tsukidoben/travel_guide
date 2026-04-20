package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.mappers.CarouselImageMapper;
import com.own.model.CarouselImage;
import com.own.service.CarouselImageService;
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
 * 轮播图管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CarouselImageServiceImpl extends ServiceImpl<CarouselImageMapper, CarouselImage> implements CarouselImageService {

    @Override
    public void saveOrUpdatePlus(CarouselImage carouselImage) {
        if (ObjectUtil.isEmpty(carouselImage)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(carouselImage.getImgUrl())) {
            throw new BaseException("轮播图图片不能为空");
        }
        if (ObjectUtil.isEmpty(carouselImage.getImgTitle())) {
            throw new BaseException("轮播图标题不能为空");
        }
        if (ObjectUtil.isEmpty(carouselImage.getStatus())) {
            throw new BaseException("状态不能为空");
        }
        if (ObjectUtil.isEmpty(carouselImage.getSortNum())) {
            throw new BaseException("排序不能为空");
        }
        if(ObjectUtil.isEmpty(carouselImage.getId())){
            this.save(carouselImage);
        }else{
            this.updateById(carouselImage);
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
    public IPage<CarouselImage> listPage(QueryFilter<CarouselImage> queryFilter) {
        // 获取分页条件
        IPage<CarouselImage> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        CarouselImage params = CommonUtil.getParams(queryFilter, CarouselImage.class);
        LambdaQueryWrapper<CarouselImage> wrapper = new LambdaQueryWrapper<>();

        wrapper
                .like(ObjectUtil.isNotEmpty(params.getImgTitle()), CarouselImage::getImgTitle, params.getImgTitle())
        ;

        // 排序
        wrapper.orderByAsc(CarouselImage::getSortNum)
                .orderByDesc(CarouselImage::getCreateTime);

        IPage<CarouselImage> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public CarouselImage getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        CarouselImage entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    private void convert(List<CarouselImage> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, CarouselImage.class)
                    .done()
                    .convert();
        }
    }

    private void convert(CarouselImage entity) {
        List<CarouselImage> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }

    @Override
    public List<CarouselImage> listFront() {
        return this.lambdaQuery()
                .eq(CarouselImage::getStatus,"1")
                .orderByAsc(CarouselImage::getSortNum)
                .list();
    }
}
