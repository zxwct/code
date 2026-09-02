package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Banner;
import org.example.springboot.service.BannerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/banner") //统一定义接口前缀
public class BannerController {

    @Resource
    BannerService bannerService;

    @PostMapping("/add")
    public Result add(@RequestBody Banner banner) { //@RequestBody 接收前端传来的json参数
        bannerService.add(banner);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Banner banner) {
        bannerService.update(banner);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        bannerService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Banner banner) {
        List<Banner> bannerList = bannerService.selectAll(banner);
        return Result.success(bannerList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Banner banner) {
        PageInfo<Banner> pageInfo = bannerService.selectPage(pageNum, pageSize,banner);
        return Result.success(pageInfo);

    }


}
