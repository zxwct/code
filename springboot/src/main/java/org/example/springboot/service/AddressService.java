package org.example.springboot.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Address;
import org.example.springboot.mapper.AddressMapper;
import org.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Resource
    AddressMapper addressMapper;

    public void add(Address address) {
        if (address.getId() == null) {
            address.setUserId(TokenUtils.getCurrentUser().getId());
        }
        
        addressMapper.insert(address);
    }


    public void update(Address address) {
      
        addressMapper.update(address);
    }

    public void deleteById(Integer id) {
     
        addressMapper.deleteById(id);
    }


    public List<Address> selectAll(Address address){
        return addressMapper.selectAll(address);
    }

    public PageInfo<Address> selectPage(Integer pageNum, Integer pageSize,Address address) {
        //开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Address> list = addressMapper.selectAll(address);
        return PageInfo.of(list);

    }

    public List<Address> selectByUserId(Integer userId) {
        return addressMapper.selectByUserId(userId);
    }

    
}
