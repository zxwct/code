package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Address;

import java.util.List;

public interface AddressMapper {
    List<Address> selectAll(Address address);

    void insert(Address address);

    void update(Address address);

    @Delete("delete from `address` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `address` where id =#{id}")
    Address selectById(Integer id);

    List<Address> selectByUserId(@Param("userId") Integer userId);


}
