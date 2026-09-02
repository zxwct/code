package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.example.springboot.entity.Notice;

import java.util.List;

public interface NoticeMapper {
    List<Notice> selectAll(Notice notice);

    void insert(Notice notice);

    void update(Notice notice);

    @Delete("delete from `notice` where id = #{id} ")
    void deleteById(Integer id);

}
