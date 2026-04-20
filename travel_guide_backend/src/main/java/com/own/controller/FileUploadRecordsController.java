package com.own.controller;

import cn.y8e.common.utils.ResultUtil;
import com.own.common.annotation.IgnoreAuth;
import com.own.service.FileUploadRecordsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/file/")
public class FileUploadRecordsController {
    @Autowired
    private FileUploadRecordsService baseService;

    /**
     * 文件上传
     */
    @PostMapping("upload")
    @IgnoreAuth
    public String upload(@RequestParam("file") List<MultipartFile> file, @RequestParam(value = "prefix", required = false) String prefix) {
        return ResultUtil.successWithData("上传成功", baseService.upload(file, prefix));
    }

    /**
     * 下载文件
     */
    @GetMapping("download/{fileId}")
    @IgnoreAuth
    public void download(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        this.baseService.download(fileId,response);
    }

    /**
     * 预览文件
     */
    @GetMapping("preview/{fileId}")
    @IgnoreAuth
    public void preview(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        this.baseService.preview(fileId,response);
    }
}
