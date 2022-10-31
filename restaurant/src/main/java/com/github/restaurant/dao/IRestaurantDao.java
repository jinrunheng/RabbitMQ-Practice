package com.github.restaurant.dao;

import com.github.restaurant.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2022/10/31 10:06 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface IRestaurantDao {
    Restaurant queryRestaurant(Integer restaurantId);
}
