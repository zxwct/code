package org.example.springboot.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Category;
import org.example.springboot.entity.Goods;
import org.example.springboot.entity.User;
import org.example.springboot.service.CategoryService;
import org.example.springboot.service.GoodsService;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    public CategoryService categoryService;
    @Resource
    public GoodsService goodsService;
    @Resource
    public UserService userService;

    @GetMapping("/pie")
    public Result pie() {
        List<Map<String,Object>> list = new ArrayList<>();
        // 查询出所有的分类信息
        List<Category> categories = categoryService.selectAll(new Category());
        // 查询出所有商品信息
        List<Goods> goods = goodsService.selectAll(new Goods());
        for (Category category : categories) {
            long count = goods.stream().filter(x -> category.getId().equals(x.getCategoryId())).count();
            Map<String,Object> map= new HashMap<>();
            map.put("name",category.getName());
            map.put("value",count);
            list.add(map);
        }
        return Result.success(list);
    }

    @GetMapping("/bar")
    public Result bar() {
        Map<String,Object> resultMap = new HashMap<>();
        List<String> xList =new ArrayList<>();
        List<Long> yList =new ArrayList<>();

        Map<String,Long> map =new HashMap<>();

        // 查询所有的用户
        List<User> users = userService.selectAll(new User());

        // 查询出所有商品信息
        List<Goods> goodss = goodsService.selectAll(new Goods());

        for (User user : users) {
            long count = goodss.stream().filter(x -> user.getId().equals(x.getUserId())).count();
            map.put(user.getUsername(),count);
        }

        // 对map进行排序，按照value来倒序
        LinkedHashMap<String,Long> collectMap = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)-> e1,LinkedHashMap::new));

        for (String key : collectMap.keySet()) {
            xList.add(key);
            yList.add(collectMap.get(key));
        }

        // top几的问题，截断一下
        if(xList.size()>5 && yList.size()>5){
            xList=xList.subList(0,5);
            yList=yList.subList(0,5);
        }

        resultMap.put("xAxis",xList);
        resultMap.put("yAxis",yList);

        return Result.success(resultMap);
    }

    @GetMapping("/line")
    public Result line() {
        Map<String,Object> resultMap = new HashMap<>();
        List<Long> yList =new ArrayList<>();

        // 获取最近多少天的年月日List
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today,-6);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();

        // 查询出所有商品信息
        List<Goods> goods = goodsService.selectAll(new Goods());
        for (String day : xList) {
            long count = goods.stream().filter(x -> ObjectUtil.isNotEmpty(x.getDate()) && x.getDate().contains(day)).count();
            yList.add(count);
        }

        resultMap.put("xAxis",xList);
        resultMap.put("yAxis",yList);

        return Result.success(resultMap);
    }
}
