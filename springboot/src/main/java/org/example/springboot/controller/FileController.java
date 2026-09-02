package org.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.common.Result;
import org.example.springboot.exception.CustomerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 处理文件上传下载相关的接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private String filePath() {
        return FileUtil.normalize(uploadDir) + "/";
    }

    /**
     * 文件上传
     */

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception{
        //找到文件位置
        String filePath = filePath();
        if (!FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        byte[] bytes = file.getBytes();
        String fileName =System.currentTimeMillis()+"_"+file.getOriginalFilename();
        // 写入文件
        FileUtil.writeBytes(bytes, filePath+fileName);
        String url = "/api/files/download/" + fileName;
        return Result.success(url);
    }

    /**
     * 文件下载
     * 下载路径："/api/files/download/780.jpg"
     */

    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception {
        //找到文件位置
        String filePath = filePath();
        String realPath = filePath+fileName; //  E:\毕设\代码\code\files\780.jpg
        boolean exist = FileUtil.exist(realPath);
        if (!exist) {
            throw new CustomerException("文件不存在");

        }
        // 读取文件的位置
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8"));
        response.setContentType("application/octet-stream");
        // 输出流对象把文件写出到客户端
        os.write(bytes);
        os.flush();
        os.close();

    }

    /**
     * wang-editor编辑器文件上传接口
     */

    @PostMapping("/wang/upload")
    public Map<String,Object> wangEditorUpload(MultipartFile file){
        String flag= System.currentTimeMillis() + "";
        String fileName=file.getOriginalFilename();
        try {
            //找到文件位置
            String filePath = filePath();
            //文件存储形式：时间戳_文件名
            FileUtil.writeBytes(file.getBytes(),filePath + flag +"_"+fileName);
            System.out.println(fileName +"--上传成功");
            Thread.sleep(1L);
        }catch (Exception e){
            System.err.println(fileName+"--文件上传失败");
        }
        String http = "/api/files/download/";
        Map<String,Object> resMap =new HashMap<>();
        //wangEditor上传图片成功后，需要返回的参数
        resMap.put("errno",0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url",http +flag+"_"+fileName)));
        return resMap;
    }

}
