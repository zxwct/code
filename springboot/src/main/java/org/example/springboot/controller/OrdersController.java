package org.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Goods;
import org.example.springboot.entity.Orders;
import org.example.springboot.service.GoodsService;
import org.example.springboot.service.OrdersService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/orders") //统一定义接口前缀
public class OrdersController {

    @Resource
    OrdersService ordersService;

    @Resource
    GoodsService goodsService;

    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) { //@RequestBody 接收前端传来的json参数
        ordersService.add(orders);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Orders orders) {
        ordersService.update(orders);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        ordersService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Orders orders) {
        List<Orders> ordersList = ordersService.selectAll(orders);
        return Result.success(ordersList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Orders orders) {
        PageInfo<Orders> pageInfo = ordersService.selectPage(pageNum, pageSize,orders);
        return Result.success(pageInfo);

    }

    @GetMapping("/selectByUserId")
    public Result selectByUserId(
            @RequestParam(required = false) String activeTab,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sideBar
    ) {
        Integer userId = TokenUtils.getCurrentUser().getId();
        List<Orders> list = ordersService.selectByUserId(
                userId,
                activeTab,
                keyword,
                sideBar
        );
        return Result.success(list);
    }

    @PostMapping("/pay/{id}")
    public Result pay(@PathVariable Integer id) {
        Orders pay = ordersService.pay(id);
        pay.setStatus("待发货");
        ordersService.update(pay);

        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        Orders pay = ordersService.pay(id);
        pay.setStatus("已取消");
        ordersService.update(pay);

        Goods goods = goodsService.selectById(pay.getGoodsId());
        goods.setStatus("已上架");
        goodsService.update(goods);


        return Result.success();
    }

    @PostMapping("/ship/{id}")
    public Result ship(@PathVariable Integer id) {
        Orders pay = ordersService.pay(id);
        pay.setStatus("待收货");
        ordersService.update(pay);

        return Result.success();
    }

    @PostMapping("/confirm/{id}")
    public Result confirm(@PathVariable Integer id) {
        Orders pay = ordersService.pay(id);
        pay.setStatus("待评价");
        ordersService.update(pay);

        return Result.success();
    }

    @PutMapping("/comment")
    public Result comment(@RequestBody Orders orders) {

        orders.setStatus("已完成");
        orders.setReviewTime(DateUtil.now());
        ordersService.update(orders);
        return Result.success();
    }

    @GetMapping("/selectComment")
    public Result selectComment() {
        Integer userId = TokenUtils.getCurrentUser().getId();
        List<Orders> list = ordersService.selectComment(userId);
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        List<Orders> list = ordersService.selectComment(id);
        return Result.success(list);
    }




}
