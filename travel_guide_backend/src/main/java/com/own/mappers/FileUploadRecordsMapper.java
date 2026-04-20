package com.own.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.own.model.FileUploadRecords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 文件上传记录表 Mapper
 */
@Mapper
@Repository
public interface FileUploadRecordsMapper extends BaseMapper<FileUploadRecords> {

}
