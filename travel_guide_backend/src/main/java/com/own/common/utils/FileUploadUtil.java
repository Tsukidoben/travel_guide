package com.own.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.y8e.common.constant.ResultConstant;
import cn.y8e.common.exception.BaseException;
import com.own.common.constant.FileConstant;
import com.own.common.vo.UserContext;
import com.own.model.FileUploadRecords;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* 文件上传工具类
*/
@CrossOrigin
@Slf4j
@Component
public class FileUploadUtil {
    @Autowired
    private ContextUtil contextUtil;
    @Value("${server.port}")
    private String port;

    public String getBasePath(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + this.port + request.getContextPath();
    }

    public String upload(MultipartFile file,String prefix) {
        if (ObjectUtil.isEmpty(file)) {
            throw new BaseException(ResultConstant.NULL_CODE, "请传入文件");
        }
        String httpStr = "";
        // 获取文件名
        String fileName = file.getOriginalFilename();

        String newName = getFileName(fileName,prefix);
//            File pathFile = new File(path);
        // 新文件
        File destFile = new File(FileConstant.FILE_REALFOLDER + File.separator + newName);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        try {
            // 压缩文件保存
            file.transferTo(destFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        httpStr = FileConstant.FILE_REFLEXFOLDER + newName;

        return httpStr;
    }

    /**
     * 生成新文件名
     * @param filename
     * @return
     */
    private String getFileName(String filename,String prefix) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 设置存储对象名称
        String dir = sdf.format(new Date());
        String ext = filename.lastIndexOf(".") > 0 ? filename.substring(filename.lastIndexOf(".")+1) : "";
        String rando = UUID.randomUUID().toString().replace("-", "");
        if (ObjectUtil.isNotEmpty(prefix)) {
            return dir + "/" + prefix + "_" + rando + "." + ext;
        } else {
            return dir + "/" + rando + "." + ext;
        }
    }

    private List<String> getImgLastList() {
        List<String> imgLastList = new ArrayList<>();
        imgLastList.add("jpg");
        imgLastList.add("jpeg");
        imgLastList.add("png");
        imgLastList.add("gif");
        imgLastList.add("webp");
        imgLastList.add("txt");
        imgLastList.add("doc");
        imgLastList.add("docx");
        imgLastList.add("xls");
        imgLastList.add("xlsx");
        imgLastList.add("pptx");
        imgLastList.add("ppt");
        imgLastList.add("md");
        imgLastList.add("pdf");
        return imgLastList;
    }

    private String getImageLast(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (ObjectUtil.isEmpty(filename)) {
            throw new BaseException(ResultConstant.NULL_CODE, "文件格式错误");
        }
        String[] split = filename.split("\\.");
        return split[split.length - 1];
    }

    public List<FileUploadRecords> uploadMore(List<MultipartFile> files, String prefix) {
        List<FileUploadRecords> resp = new ArrayList<>();
        UserContext currentUser = contextUtil.getCurrentUser();
        files.forEach(file->{
            String uploadUrl = this.upload(file, prefix);

            FileUploadRecords temp = new FileUploadRecords();
            temp.setFileName(file.getOriginalFilename())
                    .setFileSize(file.getSize() + "字节/" + String.format("%.2f", (Double.parseDouble(String.valueOf(file.getSize())) / (1024 * 1024))) + "MB")
                    .setFileType(Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1))
                    .setFileUploadSys("loacl")
                    .setFileUploadTime(new Date())
                    .setType("local")
                    .setFileUploadUser(currentUser.getUserId())
                    .setFileUploadUsername(currentUser.getUserName())
                    .setFileUrl(uploadUrl);
            resp.add(temp);
        });

        return resp;
    }
}
