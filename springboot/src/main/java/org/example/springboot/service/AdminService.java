package org.example.springboot.service;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Account;
import org.example.springboot.entity.Admin;
import org.example.springboot.exception.CustomerException;
import org.example.springboot.mapper.AdminMapper;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Resource
    AdminMapper adminMapper;

    public void add(Admin admin) {
        //根据新的账号查询数据库，是否存在同样的账号的数据
        Admin dbadmin = adminMapper.selectByUsername(admin.getUsername());
        if(dbadmin != null){
            throw new CustomerException("账号重复");
        }
        //默认密码
        if(StrUtil.isBlank(admin.getPassword())){
            admin.setPassword("1");
        }
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
    }


    public void update(Admin admin) {
        adminMapper.update(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Admin> list) {
        for (Admin admin : list) {
            this.deleteById(admin.getId());
        }
    }
    public Admin selectById(String id) {
       return adminMapper.selectById(id);
    }

    public List<Admin> selectAll(Admin admin){
        return adminMapper.selectAll(admin);
    }

    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize,Admin admin) {

        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);

    }


    public Admin login(Account account) {
        //验证账号是否存在
        Admin admin1 = adminMapper.selectByUsername(account.getUsername());
        if(admin1 == null){
            throw new CustomerException("账号不存在");
        }
        //验证密码是否正确
        if(!admin1.getPassword().equals(account.getPassword())){
            throw new CustomerException("账号或密码错误");
        }
        //创建token并返回给前端
        String token= TokenUtils.createToken(admin1.getId()+"-"+"ADMIN",admin1.getPassword());
        admin1.setToken(token);
        return admin1;
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
        Admin admin = adminMapper.selectById(currentUser.getId().toString());
        admin.setPassword(account.getNewPassword());
        adminMapper.update(admin);

    }
}
