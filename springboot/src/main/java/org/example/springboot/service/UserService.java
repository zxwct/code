package org.example.springboot.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.User;
import org.example.springboot.exception.CustomerException;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    public void add(User user) {
        //根据新的账号查询数据库，是否存在同样的账号的数据
        User dbuser = userMapper.selectByUsername(user.getUsername());
        if(dbuser != null){
            throw new CustomerException("账号重复");
        }
        //默认密码
        if(StrUtil.isBlank(user.getPassword())){
            user.setPassword("1");
        }
        if(StrUtil.isBlank(user.getName())){
            user.setName(user.getUsername());
        }
        user.setRole("USER");
        userMapper.insert(user);
    }


    public void update(User user) {
        userMapper.update(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {
            this.deleteById(user.getId());
        }
    }

    public User selectById(String id) {
        return userMapper.selectById(id.toString());
    }

    public List<User> selectAll(User user){
        return userMapper.selectAll(user);
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize,User user) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);

    }


    public User login(Account account) {
        //验证账号是否存在
        User user1 = userMapper.selectByUsername(account.getUsername());
        if(user1 == null){
            throw new CustomerException("账号不存在");
        }
        //验证密码是否正确
        if(!user1.getPassword().equals(account.getPassword())){
            throw new CustomerException("账号或密码错误");
        }
        //创建token并返回给前端
        String token= TokenUtils.createToken(user1.getId()+"-"+"USER",user1.getPassword());
        user1.setToken(token);
        return user1;
    }

    public void register(User user) {
       this.add(user);
    }


    public void updatePassword(Account account) {
        //先判断新密码和确认密码是否一致
        if(!account.getNewPassword().equals(account.getNew2Password())){
            throw new CustomerException("500","您两次输入的密码不一致");
        }
        //校验一下原密码是否正确
        Account currentUser = TokenUtils.getCurrentUser();
        if (!account.getPassword().equals(currentUser.getPassword())){
            throw new CustomerException("500","原密码输入错误");
        }
        // 开始更新密码了
        User user = userMapper.selectById(currentUser.getId().toString());
        user.setPassword(account.getNewPassword());
        userMapper.update(user);
    }

    public List<User> selectBatchIds(Set<Integer> ids) {
        // 1. 判空，避免 IN ()
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        // 2. 调用 Mapper
        return userMapper.selectBatchIds(ids);
    }


}
