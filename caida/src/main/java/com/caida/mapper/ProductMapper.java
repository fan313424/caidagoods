package com.caida.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caida.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
