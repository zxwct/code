package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Address;
import org.example.springboot.service.AddressService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/address") //统一定义接口前缀
public class AddressController {

    @Resource
    AddressService addressService;

    @PostMapping("/add")
    public Result add(@RequestBody Address address) { //@RequestBody 接收前端传来的json参数
        addressService.add(address);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Address address) {
        addressService.update(address);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        addressService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Address address) {
        List<Address> addressList = addressService.selectAll(address);
        return Result.success(addressList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Address address) {
        PageInfo<Address> pageInfo = addressService.selectPage(pageNum, pageSize,address);
        return Result.success(pageInfo);

    }

    @GetMapping("/selectByUserId")
    public Result selectByUserId() {
        Integer userId = TokenUtils.getCurrentUser().getId();
        List<Address> list = addressService.selectByUserId(userId);
        return Result.success(list);
    }


}
