package org.example.springboot.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Banner;
import org.example.springboot.mapper.BannerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Resource
    BannerMapper bannerMapper;

    public void add(Banner banner) {
        
        bannerMapper.insert(banner);
    }


    public void update(Banner banner) {
      
        bannerMapper.update(banner);
    }

    public void deleteById(Integer id) {
     
        bannerMapper.deleteById(id);
    }


    public List<Banner> selectAll(Banner banner){
        return bannerMapper.selectAll(banner);
    }

    public PageInfo<Banner> selectPage(Integer pageNum, Integer pageSize,Banner banner) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Banner> list = bannerMapper.selectAll(banner);
        return PageInfo.of(list);

    }


    
}
