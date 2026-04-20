package com.own.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.ContextUtil;
import com.own.mappers.HotelRoomMapper;
import com.own.model.HotelInfo;
import com.own.model.HotelRoom;
import com.own.model.TicketInfo;
import com.own.service.HotelInfoService;
import com.own.service.HotelRoomService;
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
 * 房间管理表
 * 业务层实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HotelRoomServiceImpl extends ServiceImpl<HotelRoomMapper, HotelRoom> implements HotelRoomService {

    @Override
    public void saveOrUpdatePlus(HotelRoom hotelRoom) {
        if (ObjectUtil.isEmpty(hotelRoom)) {
            throw new BaseException("参数不全");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getHotelId())) {
            throw new BaseException("所属酒店不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getRoomName())) {
            throw new BaseException("房间名称不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getRoomDesc())) {
            throw new BaseException("房间介绍不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getPrice())) {
            throw new BaseException("房间价格不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getRoomCount())) {
            throw new BaseException("房间数量不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getPeopleCount())) {
            throw new BaseException("可住人数不能为空");
        }
        if (ObjectUtil.isEmpty(hotelRoom.getRoomFloor())) {
            throw new BaseException("房间楼层不能为空");
        }
        if(ObjectUtil.isEmpty(hotelRoom.getId())){
            this.save(hotelRoom);
        }else{
            this.updateById(hotelRoom);
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
    public IPage<HotelRoom> listPage(QueryFilter<HotelRoom> queryFilter) {
        // 获取分页条件
        IPage<HotelRoom> page = CommonUtil.getPage(queryFilter);
        // 获取查询参数
        HotelRoom params = CommonUtil.getParams(queryFilter, HotelRoom.class);
        LambdaQueryWrapper<HotelRoom> wrapper = new LambdaQueryWrapper<>();

        if("3".equals(ContextUtil.getCurrentUserRole())){
            // 查询我的酒店有哪些
            List<HotelInfo> list = SpringUtil.getBean(HotelInfoService.class)
                    .lambdaQuery()
                    .eq(HotelInfo::getCreator, ContextUtil.getCurrentUserId()).list();
            if(CollUtil.isEmpty(list)){
                return page;
            }
            wrapper.in(HotelRoom::getHotelId, list.stream().map(HotelInfo::getId).toList());
        }

        wrapper
            .eq(ObjectUtil.isNotEmpty(params.getHotelId()), HotelRoom::getHotelId, params.getHotelId());

        // 关键字模糊搜索
        if(ObjectUtil.isNotEmpty(params.getKeyword())){
            wrapper.and(w ->
                        w.like(HotelRoom::getRoomName, params.getKeyword())
                        .or().like(HotelRoom::getRoomDesc, params.getKeyword())
            );
        }

        IPage<HotelRoom> resp = this.page(page, wrapper);
        if(ObjectUtil.isNotEmpty(resp.getRecords())){
            // 部分需要转译的文字
            this.convert(resp.getRecords());
        }
        return resp;
    }

    @Override
    public HotelRoom getByIdPlus(String id) {
        if(ObjectUtil.isEmpty(id)){
            throw new BaseException("请选择要查看的数据");
        }
        HotelRoom entity = this.getById(id);
        if(ObjectUtil.isNotEmpty(entity)){
            // 转换数据
            this.convert(entity);
        }

        return entity;
    }

    @Override
    public List<HotelRoom> getByHotelId(String hotelId) {
        return this.lambdaQuery()
                .eq(HotelRoom::getHotelId, hotelId)
                .orderByAsc(HotelRoom::getPrice)
                .list();
    }

    private void convert(List<HotelRoom> list) {
        if (CollUtil.isNotEmpty(list)) {
            // 字段转换
            ConvertUtil.of(list, HotelRoom.class)
                    .done()
                    .convert();
        }
    }

    private void convert(HotelRoom entity) {
        List<HotelRoom> list = new ArrayList<>();
        list.add(entity);
        this.convert(list);
    }
}
