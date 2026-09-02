package org.example.springboot.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Category;
import org.example.springboot.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Resource
    CategoryMapper categoryMapper;

    public void add(Category category) {
        categoryMapper.insert(category);
    }


    public void update(Category category) {
        categoryMapper.update(category);
    }

    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
    }


    public List<Category> selectAll(Category category){
        return categoryMapper.selectAll(category);
    }

    public PageInfo<Category> selectPage(Integer pageNum, Integer pageSize,Category category) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Category> list = categoryMapper.selectAll(category);
        return PageInfo.of(list);

    }

    public List<Category> selectHot(Category category){
        return categoryMapper.selectHot(category);
    }


    
}
