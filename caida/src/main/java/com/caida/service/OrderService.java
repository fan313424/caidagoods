package com.caida.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.entity.CartItem;
import com.caida.entity.Order;
import com.caida.entity.OrderItem;
import com.caida.entity.Product;
import com.caida.mapper.CartItemMapper;
import com.caida.mapper.OrderItemMapper;
import com.caida.mapper.OrderMapper;
import com.caida.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 结算：从购物车生成订单
     */
    @Transactional
    public Order checkout(Long userId) {
        // 1. 获取购物车所有商品
        QueryWrapper<CartItem> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        List<CartItem> cartItems = cartItemMapper.selectList(cartWrapper);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法结算");
        }

        // 2. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }

        // 3. 生成订单号
        String orderNo = "CD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + userId;

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("paid");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 5. 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null) continue;

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImageUrl());
            orderItem.setProductDesc(product.getDescription());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);
            orderItems.add(orderItem);
        }

        // 6. 清空购物车
        for (CartItem item : cartItems) {
            cartItemMapper.deleteById(item.getId());
        }

        order.setItems(orderItems);
        return order;
    }

    /**
     * 获取用户订单列表
     */
    public List<Order> getOrderList(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        List<Order> orders = orderMapper.selectList(wrapper);

        for (Order order : orders) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            order.setItems(items);
        }

        return orders;
    }

    /**
     * 获取用户订单数量
     */
    public Long getOrderCount(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return orderMapper.selectCount(wrapper);
    }

    /**
     * 确认收货
     */
    @Transactional
    public void confirmReceive(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不允许此操作");
        }
        order.setStatus("delivered");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        order.setStatus("cancelled");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }
}
