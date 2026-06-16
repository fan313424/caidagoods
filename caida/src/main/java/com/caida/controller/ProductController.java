package com.caida.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caida.common.Result;
import com.caida.entity.Product;
import com.caida.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取所有商品列表
     */
    @GetMapping
    public Result<List<Product>> list() {
        List<Product> products = productMapper.selectList(null);
        return Result.success(products);
    }

    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("商品不存在");
    }

    /**
     * 新增商品
     */
    @PostMapping
    public Result<Product> add(@RequestBody Map<String, Object> request) {
        try {
            Product product = new Product();
            product.setName((String) request.get("name"));
            product.setDescription((String) request.get("description"));
            product.setPrice(new BigDecimal(request.get("price").toString()));
            product.setImageUrl((String) request.get("imageUrl"));
            product.setCategory((String) request.get("category"));
            product.setStock(request.get("stock") != null ?
                    Integer.valueOf(request.get("stock").toString()) : 0);
            product.setStatus(1);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());

            productMapper.insert(product);
            return Result.success("添加成功", product);
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Product product = productMapper.selectById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }

            if (request.get("name") != null) product.setName((String) request.get("name"));
            if (request.get("description") != null) product.setDescription((String) request.get("description"));
            if (request.get("price") != null) product.setPrice(new BigDecimal(request.get("price").toString()));
            if (request.get("imageUrl") != null) product.setImageUrl((String) request.get("imageUrl"));
            if (request.get("category") != null) product.setCategory((String) request.get("category"));
            if (request.get("stock") != null) product.setStock(Integer.valueOf(request.get("stock").toString()));
            if (request.get("status") != null) product.setStatus(Integer.valueOf(request.get("status").toString()));
            product.setUpdateTime(LocalDateTime.now());

            productMapper.updateById(product);
            return Result.success("更新成功", product);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            Product product = productMapper.selectById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }
            productMapper.deleteById(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Product> toggleStatus(@PathVariable Long id) {
        try {
            Product product = productMapper.selectById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }

            product.setStatus(product.getStatus() == 1 ? 0 : 1);
            product.setUpdateTime(LocalDateTime.now());
            productMapper.updateById(product);

            String msg = product.getStatus() == 1 ? "已上架" : "已下架";
            return Result.success(msg, product);
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}
