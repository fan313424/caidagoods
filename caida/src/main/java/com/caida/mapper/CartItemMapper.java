package com.caida.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caida.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
