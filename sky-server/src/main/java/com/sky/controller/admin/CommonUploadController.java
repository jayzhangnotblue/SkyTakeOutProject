package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 普通上传文件接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonUploadController {
    @Autowired
    AliOssUtil aliOssUtil;

    /**
     * 用阿里云对象存储完成文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            String originalFilename = file.getOriginalFilename();
            String extentionName = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + extentionName;
            String upload = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(upload);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.toString());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
