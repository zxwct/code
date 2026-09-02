package org.example.springboot.service;


import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.Goods;
import org.example.springboot.mapper.GoodsMapper;
import org.example.springboot.utils.RedisUtils;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    private RedisUtils redisUtils;

    private static final String HOT_GOODS_KEY = "mall:goods:hot";

    public void add(Goods goods) {

        Account currentUser = TokenUtils.getCurrentUser();
        if(goods.getId() == null){
            goods.setUserId(currentUser.getId());
            goods.setNum(0);
            goods.setWantNum(0);
            goods.setStatus("已上架");
            goods.setDate(DateUtil.today());
        }
        
        goodsMapper.insert(goods);

        evictHotGoodsCache();
    }


    public void update(Goods goods) {
      
        goodsMapper.update(goods);

        evictHotGoodsCache();
    }

    public void deleteById(Integer id) {
     
        goodsMapper.deleteById(id);

        evictHotGoodsCache();
    }

    public void evictHotGoodsCache() {
        redisUtils.del(HOT_GOODS_KEY);
    }


    public List<Goods> selectAll(Goods goods){
        return goodsMapper.selectAll(goods);
    }

    public PageInfo<Goods> selectPage(Integer pageNum, Integer pageSize,Goods goods) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);

    }

    public PageInfo<Goods> selectFrontPage(
            Integer pageNum,
            Integer pageSize,
            Integer categoryId,
            String sortType,
            String keyword
    ) {
        PageHelper.startPage(pageNum, pageSize);

        List<Goods> list = goodsMapper.selectFrontList(
                categoryId,
                keyword,
                sortType
        );

        return PageInfo.of(list);
    }



    public List<Goods> selectHot(Goods goods){

        List<Goods> cache = redisUtils.get(
                HOT_GOODS_KEY,
                new TypeReference<List<Goods>>() {}
        );

        if(cache != null){
            return cache;
        }

        List<Goods> list = goodsMapper.selectHot(goods);

        redisUtils.set(HOT_GOODS_KEY, list, 600);

        return list;
    }


    public List<Goods> selectByCategoryId(Integer categoryId) {
        return goodsMapper.selectByCategoryId(categoryId);
    }

    public Goods selectById(Integer goodsId) {
        return goodsMapper.selectById(goodsId);
    }

    public List<Goods> getByCategory(Integer categoryId, Integer excludeId) {
        return goodsMapper.selectByCategory(categoryId, excludeId);
    }

    public void incrementView(Integer id) {
        goodsMapper.updateNum(id);
    }

    public List<Goods> selectByUserId(Integer userId) {
        return goodsMapper.selectByUserId(userId);
    }

    
}
