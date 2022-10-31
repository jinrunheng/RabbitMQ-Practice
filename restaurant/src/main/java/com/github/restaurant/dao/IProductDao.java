package com.github.restaurant.dao;

import com.github.restaurant.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2022/10/31 9:53 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface IProductDao {
    Product queryProduct(Integer productId);
}
