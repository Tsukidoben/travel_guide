package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 文件上传记录表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("file_upload_records")
public class FileUploadRecords  extends Model<FileUploadRecords> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 文件名
     */
    @TableField(value = "file_name")
    private String fileName;
    /**
     * 文件类型
     */
    @TableField(value = "file_type")
    private String fileType;
    /**
     * 文件上传时间
     */
    @TableField(value = "file_upload_time")
    @JsonIgnore
    private Date fileUploadTime;
    /**
     * 文件保存路径
     */
    @TableField(value = "file_url")
    @JsonIgnore
    private String fileUrl;
    /**
     * 文件大小
     */
    @TableField(value = "file_size")
    @JsonIgnore
    private String fileSize;
    /**
     * 文件上传桶
     */
    @TableField(value = "file_upload_sys")
    @JsonIgnore
    private String fileUploadSys;
    /**
     * 文件上传人
     */
    @TableField(value = "file_upload_user")
    @JsonIgnore
    private String fileUploadUser;
    /**
     * 文件上传人姓名
     */
    @TableField(value = "file_upload_username")
    @JsonIgnore
    private String fileUploadUsername;
    /**
     * minio/local   类型，oss或者本地
     */
    @TableField(value = "type")
    @JsonIgnore
    private String type;

    /**
     * 关键字查询
     */
    @TableField(exist = false)
    private String keyword;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
