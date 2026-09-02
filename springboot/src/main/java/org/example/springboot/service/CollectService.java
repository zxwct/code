package org.example.springboot.service;


import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.Collect;
import org.example.springboot.entity.Goods;
import org.example.springboot.mapper.CollectMapper;
import org.example.springboot.mapper.GoodsMapper;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollectService {

    @Resource
    CollectMapper collectMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Transactional
    public void add(Collect collect) {
        // 1. 防止重复收藏
        Collect db = collectMapper.selectOne(collect);
        if (db != null) {
            return;
        }

        // 2. 查询商品当前价格
        Goods goods = goodsMapper.selectById(collect.getItemId());
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }

        // 3. 设置收藏时间 & 收藏价
        collect.setTime(DateUtil.today());
        collect.setCollectPrice(goods.getPrice()); // ⭐ 关键

        // 4. 插入收藏记录
        collectMapper.insert(collect);

        // 5. 商品想要人数 +1
        goodsMapper.addWantNum(collect.getItemId());
    }


    public void update(Collect collect) {
      
        collectMapper.update(collect);
    }

    public void deleteById(Integer id) {
        Collect collect = collectMapper.selectById(id);
        if (collect == null) return;
     
        collectMapper.deleteById(id);
        goodsMapper.reduceWantNum(collect.getItemId());
    }


    public List<Collect> selectAll(Collect collect){
        return collectMapper.selectAll(collect);
    }

    public PageInfo<Collect> selectPage(Integer pageNum, Integer pageSize,Collect collect) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> list = collectMapper.selectAll(collect);
        return PageInfo.of(list);

    }

    public PageInfo<Goods> selectCollectPage(Integer pageNum, Integer pageSize) {

        // 获取当前登录用户
        Account currentUser = TokenUtils.getCurrentUser();

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        List<Goods> list = collectMapper.selectCollectGoods(currentUser.getId());

        return PageInfo.of(list);
    }

    public List<Collect> selectByUserId(Integer userId) {
        return collectMapper.selectByUserId(userId);
    }


    
}
