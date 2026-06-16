package com.caida.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.entity.CartItem;
import com.caida.entity.Product;
import com.caida.mapper.CartItemMapper;
import com.caida.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() == 0) {
            throw new RuntimeException("该商品卖完了哟~");
        }

        // 检查是否已在购物车中
        QueryWrapper<CartItem> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("product_id", productId);
        CartItem existingItem = cartItemMapper.selectOne(wrapper);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
            cartItemMapper.updateById(existingItem);
            return existingItem;
        } else {
            CartItem cartItem = new CartItem(userId, productId, quantity);
            cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cartItemMapper.insert(cartItem);
            return cartItem;
        }
    }

    /**
     * 获取用户购物车列表
     */
    public List<CartItem> getCartItems(Long userId) {
        QueryWrapper<CartItem> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);

        // 填充商品信息
        for (CartItem item : cartItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                item.setProductName(product.getName());
                item.setProductPrice(product.getPrice());
                item.setProductImage(product.getImageUrl());
            }
        }

        return cartItems;
    }

    /**
     * 更新购物车商品数量
     */
    @Transactional
    public CartItem updateQuantity(Long cartId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("数量必须大于0");
        }

        CartItem cartItem = cartItemMapper.selectById(cartId);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }

        Product product = productMapper.selectById(cartItem.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemMapper.updateById(cartItem);
        return cartItem;
    }

    /**
     * 从购物车移除商品
     */
    @Transactional
    public void removeFromCart(Long cartId) {
        CartItem cartItem = cartItemMapper.selectById(cartId);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }
        cartItemMapper.deleteById(cartId);
    }

    /**
     * 获取用户购物车商品数量
     */
    public Long getCartCount(Long userId) {
        QueryWrapper<CartItem> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return cartItemMapper.selectCount(wrapper);
    }
}
