package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.User;

import java.util.List;
import java.util.Set;

public interface UserMapper {
    List<User> selectAll(User user);

    void insert(User user);

    void update(User user);

    @Delete("delete from `user` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `user` where username = #{username}")
    User selectByUsername(String username);

    @Select("select * from `user` where id = #{id}")
    User selectById(String id);

    List<User> selectBatchIds(@Param("ids") Set<Integer> ids);
}
