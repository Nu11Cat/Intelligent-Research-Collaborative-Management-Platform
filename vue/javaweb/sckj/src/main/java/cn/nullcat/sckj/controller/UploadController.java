package cn.nullcat.sckj.controller;

import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
public class UploadController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    /**
     * 上传图片
     * @param image
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile image) throws IOException {
        //调用阿里云oss工具类进行文件上传
        String url = aliOSSUtils.upload(image);

        return Result.success(url);
    }
}
