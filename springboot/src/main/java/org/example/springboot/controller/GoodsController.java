package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Goods;
import org.example.springboot.service.GoodsService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/goods") //统一定义接口前缀
public class GoodsController {

    @Resource
    GoodsService goodsService;

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) { //@RequestBody 接收前端传来的json参数
        goodsService.add(goods);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Goods goods) {
        goodsService.update(goods);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        goodsService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Goods goods) {
        List<Goods> goodsList = goodsService.selectAll(goods);
        return Result.success(goodsList);
    }


    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Goods goods) {
        PageInfo<Goods> pageInfo = goodsService.selectPage(pageNum, pageSize,goods);
        return Result.success(pageInfo);

    }

    @GetMapping("/front/selectPage")
    public Result selectFrontPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) String keyword
    ) {

        PageInfo<Goods> pageInfo = goodsService.selectFrontPage(
                pageNum,
                pageSize,
                categoryId,
                sortType,
                keyword
        );

        return Result.success(pageInfo);
    }



    @GetMapping("/selectHot")
    public Result selectHot(Goods goods) {
        List<Goods> goodsList = goodsService.selectHot(goods);
        return Result.success(goodsList);
    }


    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Goods goods = goodsService.selectById(id);
        return Result.success(goods);
    }

    @GetMapping("/selectByCategory/{categoryId}")
    public Result selectByCategory(@PathVariable Integer categoryId,
                                   @RequestParam(required = false) Integer excludeId) {
        List<Goods> list = goodsService.getByCategory(categoryId, excludeId);
        return Result.success(list);
    }

    @PutMapping("/incrementView/{id}")
    public Result incrementView(@PathVariable Integer id) {
        goodsService.incrementView(id);
        return Result.success();
    }

    @GetMapping("/selectByUserId")
    public Result selectByUserId() {
        Integer userId = TokenUtils.getCurrentUser().getId();
        List<Goods> list = goodsService.selectByUserId(userId);
        return Result.success(list);
    }

    @GetMapping("/selectByUserId/{id}")
    public Result selectByUserId(@PathVariable Integer id) {
        List<Goods> list = goodsService.selectByUserId(id);
        return Result.success(list);
    }




}
