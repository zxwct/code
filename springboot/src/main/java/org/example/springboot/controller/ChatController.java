package org.example.springboot.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.Chat;
import org.example.springboot.entity.User;
import org.example.springboot.service.ChatService;
import org.example.springboot.service.UserService;
import org.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController //这是一个 RESTful风格的控制器。所有方法的返回值都会被自动转换为 JSON 数据并写入到响应体中，而不是跳转到页面
@RequestMapping("/chat") //统一定义接口前缀
public class ChatController {

    @Resource
    ChatService chatService;

    @Resource
    UserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody Chat chat) { //@RequestBody 接收前端传来的json参数
        chatService.add(chat);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Chat chat) {
        chatService.update(chat);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {//@PathVariable 接收前端传来的路径参数
        chatService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Chat chat) {
        List<Chat> chatList = chatService.selectAll(chat);
        return Result.success(chatList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum, //@RequestParam接收前端传来的表单参数
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Chat chat) {
        PageInfo<Chat> pageInfo = chatService.selectPage(pageNum, pageSize,chat);
        return Result.success(pageInfo);

    }

    @GetMapping("/clear")
    public Result clear(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {

        //查询对方发给当前用户发的未读消息
        List<Chat> chatList = chatService.selectClear(fromUserId,toUserId);
        //更新对方发给当前用户的消息为已读
        chatList.forEach(chat -> chat.setRead(true));
        //批量更新数据库
        chatService.updateBatch(chatList);
        return Result.success(chatList);
    }

    @GetMapping("/message")
    public Result message(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {
        //获取两个用户间的所有对话
        List<Chat> chatList = chatService.selectMessage(fromUserId,toUserId);
        return Result.success(chatList);
    }

    @GetMapping("/user")
    public Result user() {

        Account account = TokenUtils.getCurrentUser();

        //获取与当前用户有关的所有对话
        List<Chat> chatList = chatService.selectByUserId(account.getId());

        //记录不同用户有多少消息是发给自己并且还未读的
        Map<Integer,Long> unreadMap = chatList.stream().filter(item -> ObjectUtil.equal(item.getToUserId(), account.getId()) && !item.isRead()).collect(Collectors.groupingBy(Chat::getFromUserId,Collectors.counting()));

        //记录所有和自己聊过天的用户，可以使用LinkedHashSet保证有序
        Set<Integer> userIds = new LinkedHashSet<>();

        for (Chat chat : chatList) {
            userIds.add(chat.getFromUserId());
            userIds.add(chat.getToUserId());
        }

        //排除自己
        userIds.remove(account.getId());

        //如果没有对话过，直接返回
        if (CollectionUtil.isEmpty(userIds)) {
            return Result.success(new ArrayList<>());
        }

        //和自己沟通过的所有用户
        List<User> userList = userService.selectBatchIds(userIds);

        JSONArray array = new JSONArray();
        for (User user : userList) {
            JSONObject object = new JSONObject();
            object.set("id", user.getId());
            object.set("name", user.getName());
            object.set("avatar", user.getAvatar());
            object.set("count",unreadMap.getOrDefault(user.getId(), 0L));
            object.set("online",false);
            array.add(object);
        }

        return Result.success(array);
    }

    @GetMapping("/user/{id}")
    public Result user(@PathVariable Integer id) {
        User user =userService.selectById(id.toString());
        JSONObject object = new JSONObject();
        object.set("id", user.getId());
        object.set("name", user.getName());
        object.set("avatar", user.getAvatar());
        object.set("count",0);
        object.set("online",false);

        return Result.success(object);

    }

}
