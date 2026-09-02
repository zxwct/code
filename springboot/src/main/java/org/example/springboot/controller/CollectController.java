package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Collect;
import org.example.springboot.entity.Goods;
import org.example.springboot.service.CollectService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/collect") //统一定义接口前缀
public class CollectController {

    @Resource
    CollectService collectService;

    @PostMapping("/add")
    public Result add(@RequestBody Collect collect) { //@RequestBody 接收前端传来的json参数

        collectService.add(collect);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Collect collect) {
        collectService.update(collect);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        collectService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Collect collect) {
        List<Collect> collectList = collectService.selectAll(collect);
        return Result.success(collectList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Collect collect) {
        PageInfo<Collect> pageInfo = collectService.selectPage(pageNum, pageSize,collect);
        return Result.success(pageInfo);

    }

    @GetMapping("/love/selectPage")
    public Result selectCollectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {

        PageInfo<Goods> pageInfo = collectService.selectCollectPage(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/selectByUserId")
    public Result selectByUserId() {
        Integer userId = TokenUtils.getCurrentUser().getId();
        List<Collect> list = collectService.selectByUserId(userId);
        return Result.success(list);
    }


}
