package com.sky.service.impl;

import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
    @Autowired
    AliOssUtil aliOssUtil;

    @Override
    public String upload(MultipartFile file) {
        String filePath = null;
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        //构造新的文件名
        String objectName = UUID.randomUUID() + extension;
        try {
            filePath = aliOssUtil.upload(file.getBytes(), objectName);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }
        return filePath;

    }
}
