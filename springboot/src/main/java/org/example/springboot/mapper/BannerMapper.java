package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Banner;

import java.util.List;

public interface BannerMapper {
    List<Banner> selectAll(Banner banner);

    void insert(Banner banner);

    void update(Banner banner);

    @Delete("delete from `banner` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `banner` where id =#{id}")
    Banner selectById(Integer id);

}
