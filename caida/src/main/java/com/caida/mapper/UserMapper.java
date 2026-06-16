package com.caida.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caida.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
