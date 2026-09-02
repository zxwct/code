package org.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot.common.Result;
import org.example.springboot.entity.User;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/user") //统一定义接口前缀
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody User user) { //@RequestBody 接收前端传来的json参数
        userService.add(user);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        userService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<User> list) {
        userService.deleteBatch(list);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(User user) {
        List<User> userList = userService.selectAll(user);
        return Result.success(userList);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.selectById(id.toString());
        return Result.success(user);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             User user) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize,user);
        return Result.success(pageInfo);

    }
    //数据导出
    @GetMapping("/export")
    public void exportData(User user, HttpServletResponse response) throws Exception {
        String ids = user.getIds();
        if(StrUtil.isNotBlank(ids)){
            String[] idsArr = ids.split(",");
            user.setIdsArr(idsArr);
        }

        //拿到导出数据
        List<User> list = userService.selectAll(user);
        //构建Writer对象
        ExcelWriter writer = ExcelUtil.getWriter();
        //设置中文表头
        writer.addHeaderAlias("username","账号");
        writer.addHeaderAlias("name","名称");
        writer.addHeaderAlias("phone","电话");
        writer.addHeaderAlias("email","邮箱");
        writer.setOnlyAlias(true);
        //写出数据到writer
        writer.write(list);
        //设置输出的文件的名称以及输出流的头信息
        String fileName = URLEncoder.encode("管理员信息",UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName + ".xlsx");
        //写出到输出流
        ServletOutputStream os =response.getOutputStream();
        writer.flush(os);
        writer.close();
        os.close();
    }

    //数据导入
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {

        //拿到输入流 构建 reader对象
        InputStream inputStream =file.getInputStream();
        ExcelReader reader=ExcelUtil.getReader(inputStream);
        //通过 reader 读取 excel 里面的数据
        reader.addHeaderAlias("账号","username");
        reader.addHeaderAlias("名称","name");
        reader.addHeaderAlias("电话","phone");
        reader.addHeaderAlias("邮箱","email");
        List<User> list = reader.readAll(User.class);
        //将数据写入到数据库
        for (User user : list) {
            userService.add(user);
        }
        return Result.success();

    }

}
