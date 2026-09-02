package org.example.springboot.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Chat;
import org.example.springboot.mapper.ChatMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Resource
    ChatMapper chatMapper;

    public void add(Chat chat) {
        
        chatMapper.insert(chat);
    }


    public void update(Chat chat) {
      
        chatMapper.update(chat);
    }

    public void deleteById(Integer id) {
     
        chatMapper.deleteById(id);
    }


    public List<Chat> selectAll(Chat chat){
        return chatMapper.selectAll(chat);
    }

    public PageInfo<Chat> selectPage(Integer pageNum, Integer pageSize,Chat chat) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Chat> list = chatMapper.selectAll(chat);
        return PageInfo.of(list);

    }

    public List<Chat> selectByUserId(
            Integer userId
    ) {
        return chatMapper.selectByUserId(userId);
    }

    public List<Chat> selectMessage(Integer fromUserId, Integer toUserId) {
        return chatMapper.selectMessage(fromUserId, toUserId);
    }

    public  List<Chat> selectClear(Integer fromUserId, Integer toUserId) {
        return chatMapper.selectClear(fromUserId, toUserId);
    }

    public void updateBatch(List<Chat> chatList) {
        if (chatList != null && !chatList.isEmpty()) {
            chatMapper.updateBatch(chatList);
        }
    }


    
}
