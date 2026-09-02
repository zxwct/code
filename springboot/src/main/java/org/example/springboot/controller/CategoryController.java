package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Category;
import org.example.springboot.entity.Goods;
import org.example.springboot.service.CategoryService;
import org.example.springboot.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/category") //统一定义接口前缀
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @Resource
    GoodsService goodsService;

    @PostMapping("/add")
    public Result add(@RequestBody Category category) { //@RequestBody 接收前端传来的json参数
        categoryService.add(category);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        categoryService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Category category) {
        List<Category> categoryList = categoryService.selectAll(category);
        return Result.success(categoryList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Category category) {
        PageInfo<Category> pageInfo = categoryService.selectPage(pageNum, pageSize,category);
        return Result.success(pageInfo);

    }

    @GetMapping("/selectHot")
    public Result selectHot(Category category) {

        List<Category> categoryList = categoryService.selectHot(category);

        for (Category c : categoryList) {
            List<Goods> goods = goodsService.selectByCategoryId(c.getId());
            c.setGoodsList(goods);

        }

        return Result.success(categoryList);
    }


}
