package com.caida.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.common.Result;
import com.caida.entity.Order;
import com.caida.entity.OrderItem;
import com.caida.mapper.OrderItemMapper;
import com.caida.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminOrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 获取全部订单列表
     */
    @GetMapping("/orders")
    public Result<List<Order>> listAll() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Order> orders = orderMapper.selectList(wrapper);

        for (Order order : orders) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
            order.setItems(items);
        }

        return Result.success(orders);
    }

    /**
     * 标记发货（将订单状态改为 delivered）
     */
    @PutMapping("/orders/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!"paid".equals(order.getStatus())) {
            return Result.error("当前状态不允许发货");
        }
        order.setStatus("delivered");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("已标记发货", null);
    }

    /**
     * 管理员取消订单
     */
    @PutMapping("/orders/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if ("cancelled".equals(order.getStatus())) {
            return Result.error("订单已取消");
        }
        order.setStatus("cancelled");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("订单已取消", null);
    }
}
