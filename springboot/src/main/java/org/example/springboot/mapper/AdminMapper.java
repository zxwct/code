package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Admin;

import java.util.List;

public interface AdminMapper {
    List<Admin> selectAll(Admin admin);

    void insert(Admin admin);

    void update(Admin admin);

    @Delete("delete from `admin` where id = #{id} ")
    void deleteById(Integer id);

     @Select("select * from `admin` where username = #{username}")
     Admin selectByUsername(String username);

    @Select("select * from `admin` where id = #{id}")
    Admin selectById(String id);
}
