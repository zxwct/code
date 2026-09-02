package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Chat;

import java.util.List;

public interface ChatMapper {
    List<Chat> selectAll(Chat chat);

    void insert(Chat chat);

    void update(Chat chat);

    @Delete("delete from `chat` where id = #{id} ")
    void deleteById(Integer id);

    @Select("select * from `chat` where id =#{id}")
    Chat selectById(Integer id);

    List<Chat> selectByUserId(
            @Param("userId") Integer userId

    );

    List<Chat> selectMessage(@Param("fromUserId") Integer fromUserId,
                             @Param("toUserId") Integer toUserId);

    List<Chat> selectClear(@Param("fromUserId") Integer fromUserId,
                             @Param("toUserId") Integer toUserId);

    void updateBatch(@Param("list") List<Chat> chatList);

}
