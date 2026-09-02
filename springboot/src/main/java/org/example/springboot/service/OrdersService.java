package org.example.springboot.service;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Address;
import org.example.springboot.entity.Goods;
import org.example.springboot.entity.Orders;
import org.example.springboot.mapper.AddressMapper;
import org.example.springboot.mapper.GoodsMapper;
import org.example.springboot.mapper.OrdersMapper;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrdersService {

    @Resource
    OrdersMapper ordersMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsService goodsService;

    @Resource
    AddressMapper addressMapper;

    public void add(Orders orders) {
        if(orders.getId()==null){
            orders.setNo(DateUtil.format(new Date(), "yyyyMMddHHmmss")+ RandomUtil.randomNumbers(6));
            Goods goods = goodsMapper.selectById(orders.getGoodsId());
            orders.setGoodsId(goods.getId());
            orders.setGoodsName(goods.getName());
            orders.setGoodsImg(goods.getImg());
            orders.setFromId(goods.getUserId());
            orders.setToId(TokenUtils.getCurrentUser().getId());
            orders.setPrice(goods.getPrice());
            orders.setTime(DateUtil.now());
            orders.setStatus("待付款");
            Address address = addressMapper.selectById(orders.getAddressId());
            orders.setAddress(address.getAddress());
            orders.setInfo(address.getInfo());
            orders.setName(address.getName());
            orders.setPhone(address.getPhone());


            goods.setStatus("已卖出");
            goodsMapper.update(goods);
            goodsService.evictHotGoodsCache();
        }
        ordersMapper.insert(orders);
    }


    public void update(Orders orders) {
      
        ordersMapper.update(orders);
    }

    public void deleteById(Integer id) {
     
        ordersMapper.deleteById(id);
    }


    public List<Orders> selectAll(Orders orders){
        return ordersMapper.selectAll(orders);
    }

    public PageInfo<Orders> selectPage(Integer pageNum, Integer pageSize,Orders orders) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);

    }

    public List<Orders> selectByUserId(
            Integer userId,
            String activeTab,
            String keyword,
            String sideBar

    ) {
        return ordersMapper.selectByUserId(userId,activeTab,keyword,sideBar);
    }


    public Orders pay(Integer id){
        return ordersMapper.selectById(id);
    }

    public List<Orders> selectComment(Integer userId) {
        return ordersMapper.selectComment(userId);
    }


    
}
