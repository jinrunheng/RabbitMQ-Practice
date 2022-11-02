package com.github.restaurant.dao;

import com.github.restaurant.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2022/10/31 10:06 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface IRestaurantDao {

    @Select("SELECT id,name,address,status,settlement_id settlementId,date FROM restaurant WHERE id = #{id}")
    Restaurant queryRestaurant(Integer restaurantId);
}
