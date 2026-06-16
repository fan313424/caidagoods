package com.caida.controller;

import com.caida.common.Result;
import com.caida.entity.CartItem;
import com.caida.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*") // 允许跨域访问
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<CartItem> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = request.get("quantity") != null ? 
                Integer.valueOf(request.get("quantity").toString()) : 1;
            
            CartItem cartItem = cartService.addToCart(userId, productId, quantity);
            return Result.success("添加成功", cartItem);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户购物车列表
     */
    @GetMapping
    public Result<List<CartItem>> getCartItems(@RequestParam Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            return Result.success(cartItems);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<CartItem> updateQuantity(@RequestBody Map<String, Object> request) {
        try {
            Long cartId = Long.valueOf(request.get("cartId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            CartItem cartItem = cartService.updateQuantity(cartId, quantity);
            return Result.success("更新成功", cartItem);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/remove/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户购物车商品数量
     */
    @GetMapping("/count")
    public Result<Map<String, Long>> getCartCount(@RequestParam Long userId) {
        try {
            Long count = cartService.getCartCount(userId);
            Map<String, Long> data = new HashMap<>();
            data.put("count", count);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
