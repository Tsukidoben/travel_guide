package com.own.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.y8e.common.exception.BaseException;
import cn.y8e.common.utils.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.own.common.utils.FileUploadUtil;
import com.own.common.constant.FileConstant;
import com.own.mappers.FileUploadRecordsMapper;
import com.own.model.FileUploadRecords;
import com.own.service.FileUploadRecordsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FileUploadRecordsServiceImpl extends ServiceImpl<FileUploadRecordsMapper, FileUploadRecords> implements FileUploadRecordsService {
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Override
    public List<FileUploadRecords> upload(List<MultipartFile> files, String prefix) {
        List<FileUploadRecords> fileUploadRecords = fileUploadUtil.uploadMore(files,prefix);

        this.saveBatch(fileUploadRecords);

        return fileUploadRecords;
    }

    @Override
    public void download(String fileId, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(fileId)) {
            throw new BaseException("文件id不能为空");
        }
        FileUploadRecords fileInfo = this.getById(fileId);

        if (ObjectUtil.isEmpty(fileInfo)) {
            throw new BaseException("文件不存在");
        }

        String fileUrl = "";

        if (fileInfo.getFileUrl().contains("http://") || fileInfo.getFileUrl().contains("https://")) {
            fileUrl = fileInfo.getFileUrl();
        } else {
            fileUrl = fileUploadUtil.getBasePath() + fileInfo.getFileUrl();
        }

        if(ObjectUtil.isNotEmpty(fileUrl)){
            FileUtil.download(fileUrl,fileInfo.getFileName(),response);
        }
    }

    @Override
    public void preview(String fileId, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(fileId)) {
            throw new BaseException("文件id不能为空");
        }
        FileUploadRecords fileInfo = this.getById(fileId);

        if (ObjectUtil.isEmpty(fileInfo)) {
            throw new BaseException("文件不存在");
        }

        String fileUrl = "";

        if (fileInfo.getFileUrl().contains("http://") || fileInfo.getFileUrl().contains("https://")) {
            fileUrl = fileInfo.getFileUrl();
        } else {
            fileUrl = fileInfo.getFileUrl().replaceFirst(FileConstant.FILE_REFLEXFOLDER,FileConstant.FILE_REALFOLDER);
        }

        if(ObjectUtil.isNotEmpty(fileUrl)){
            FileUtil.preview(fileUrl,fileInfo.getFileName(),response);
        }
    }
}
