package com.own.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.own.model.FileUploadRecords;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadRecordsService extends IService<FileUploadRecords> {
    /**
      * 文件上传
      */
    List<FileUploadRecords> upload(List<MultipartFile> files, String prefix);

    /**
     * 文件下载
     */
    void download(String fileId, HttpServletResponse response);

    /**
     * 文件预览
     */
    void preview(String fileId, HttpServletResponse response);
}
