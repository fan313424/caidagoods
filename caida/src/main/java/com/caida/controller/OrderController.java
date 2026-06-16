package com.caida.controller;

import com.caida.common.Result;
import com.caida.entity.Order;
import com.caida.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 结算（从购物车创建订单，即支付成功）
     */
    @PostMapping("/checkout")
    public Result<Order> checkout(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Order order = orderService.checkout(userId);
            return Result.success("支付成功！", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Result<List<Order>> getOrderList(@RequestParam Long userId) {
        try {
            List<Order> orders = orderService.getOrderList(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户订单数量
     */
    @GetMapping("/count")
    public Result<Map<String, Long>> getOrderCount(@RequestParam Long userId) {
        try {
            Long count = orderService.getOrderCount(userId);
            Map<String, Long> data = new HashMap<>();
            data.put("count", count);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PutMapping("/confirm")
    public Result<Void> confirmReceive(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            orderService.confirmReceive(orderId, userId);
            return Result.success("已确认收货", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    public Result<Void> cancelOrder(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            orderService.cancelOrder(orderId, userId);
            return Result.success("订单已取消", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
