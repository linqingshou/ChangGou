package com.changgou.file.controller;


import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.file.pojo.FastDFSFile;
import com.changgou.file.util.FastDFSUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {

    /***
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file){
        try {
            if (file == null){
                throw new RuntimeException("文件不存在");
            }
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)) {
                throw new RuntimeException("文件不存在");
            }

            FastDFSFile fastDFSFile = new FastDFSFile(
                    originalFilename,
                    file.getBytes(),
                    StringUtils.getFilenameExtension(originalFilename)
            );
            FastDFSUtil.upload(fastDFSFile);
            return new Result(true, StatusCode.OK,"文件上传成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Result(false, StatusCode.ERROR,"文件上传失败");
    }
}
