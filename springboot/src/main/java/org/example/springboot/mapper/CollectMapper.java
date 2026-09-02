package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Collect;
import org.example.springboot.entity.Goods;

import java.util.List;

public interface CollectMapper {
    List<Collect> selectAll(Collect collect);

    void insert(Collect collect);

    void update(Collect collect);

    @Delete("delete from `collect` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `collect` where id =#{id}")
    Collect selectById(Integer id);

    Collect selectOne(Collect collect);

    List<Goods> selectCollectGoods(@Param("userId") Integer userId);

    List<Collect> selectByUserId(@Param("userId") Integer userId);



}
