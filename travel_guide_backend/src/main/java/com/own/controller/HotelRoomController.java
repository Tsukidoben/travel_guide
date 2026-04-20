package com.own.controller;

import com.own.common.annotation.IgnoreAuth;
import com.own.model.HotelRoom;
import com.own.service.HotelRoomService;
import cn.y8e.common.utils.ResultUtil;
import com.own.model.vo.DelVo;
import cn.y8e.common.vo.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间管理表
 * 控制层
 */
@RestController
@RequestMapping("/hotelRoom")
public class HotelRoomController {

    @Autowired
    private HotelRoomService hotelRoomService;

    /**
     * 新增/修改
     */
    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(@RequestBody HotelRoom hotelRoom) {
        hotelRoomService.saveOrUpdatePlus(hotelRoom);
        return ResultUtil.success("操作成功");
    }

    /**
     * 根据id删除
     */
    @PostMapping("delById/{id}")
    public String delById(@PathVariable("id") String id) {
        hotelRoomService.delById(id);
        return ResultUtil.success("删除成功");
    }

    /**
     * 批量删除
     */
    @PostMapping("delBatch")
    public String delBatch(@RequestBody DelVo delVo) {
        hotelRoomService.delBatch(delVo);
        return ResultUtil.success("删除成功");
    }

    /**
     * 分页查询
     */
    @PostMapping("listPage")
    public String listPage(@RequestBody(required = false) QueryFilter<HotelRoom> queryFilter) {
        return ResultUtil.<HotelRoom>returnPages(hotelRoomService.listPage(queryFilter));
    }

    /**
     * 根据id查询
     */
    @PostMapping("getById/{id}")
    public String getById(@PathVariable("id") String id) {
        return ResultUtil.<HotelRoom>successWithData(hotelRoomService.getByIdPlus(id));
    }

    /**
     * 根据酒店id查询房间
     */
    @PostMapping("getByHotelId/{hotelId}")
    @IgnoreAuth
    public String getByHotelId(@PathVariable("hotelId") String hotelId) {
        return ResultUtil.<List<HotelRoom>>successWithData(hotelRoomService.getByHotelId(hotelId));
    }
}
