package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Goods;

import java.util.List;

public interface GoodsMapper {
    List<Goods> selectAll(Goods goods);

    void insert(Goods goods);

    void update(Goods goods);

    List<Goods> selectHot(Goods goods);

    List<Goods> selectByCategoryId(Integer categoryId);


    @Delete("delete from `goods` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `goods` where id =#{id}")
    Goods selectById(Integer id);

    List<Goods> selectFrontList(
            @Param("categoryId") Integer categoryId,
            @Param("keyword") String keyword,
            @Param("sortType") String sortType
    );


    List<Goods> selectByCategory(@Param("categoryId") Integer categoryId,
                                 @Param("excludeId") Integer excludeId
                                 );

    void addWantNum(@Param("id") Integer id);

    void reduceWantNum(@Param("id") Integer id);

    int updateNum(@Param("id") Integer id);

    List<Goods> selectByUserId(@Param("userId") Integer userId);



}
