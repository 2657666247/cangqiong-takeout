package com.sky.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CommonService {

    /**
     * 上传文件
     *
     * @return
     */
    String upload(MultipartFile file) throws IOException;
}
