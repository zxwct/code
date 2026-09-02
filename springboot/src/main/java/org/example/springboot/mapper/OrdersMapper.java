package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Orders;

import java.util.List;

public interface OrdersMapper {
    List<Orders> selectAll(Orders orders);

    void insert(Orders orders);

    void update(Orders orders);

    @Delete("delete from `orders` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `orders` where id =#{id}")
    Orders selectById(Integer id);

    List<Orders> selectByUserId(
            @Param("userId") Integer userId,
            @Param("activeTab") String activeTab,
            @Param("keyword") String keyword,
            @Param("sideBar") String sideBar

    );

    List<Orders> selectComment(@Param("userId") Integer userId);

}
