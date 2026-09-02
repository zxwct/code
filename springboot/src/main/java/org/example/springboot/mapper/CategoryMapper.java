package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Category;

import java.util.List;

public interface CategoryMapper {
    List<Category> selectAll(Category category);

    void insert(Category category);

    void update(Category category);

    List<Category> selectHot(Category category);

    @Delete("delete from `category` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from category where id = #{id}")
    Category selectById(Integer id);

}
