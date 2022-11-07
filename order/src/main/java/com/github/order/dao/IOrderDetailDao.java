package com.github.order.dao;

import com.github.order.entity.OrderDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 8:51 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface IOrderDetailDao {

    /**
     * 新增订单
     *
     * @param orderDetail
     */
    @Insert("INSERT INTO order_detail (status, address, account_id, product_id, deliveryman_id, settlement_id, " +
            "reward_id, price, date) VALUES(#{status}, #{address},#{accountId},#{productId},#{deliverymanId}," +
            "#{settlementId}, #{rewardId},#{price}, #{date})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderDetail orderDetail);

    /**
     * 修改订单
     *
     * @param orderDetail
     */
    @Update("update order_detail set status =#{status}, address =#{address}, account_id =#{accountId}, " +
            "product_id =#{productId}, deliveryman_id =#{deliverymanId}, settlement_id =#{settlementId}, " +
            "reward_id =#{rewardId}, price =#{price}, date =#{date} where id=#{id}")
    void update(OrderDetail orderDetail);

    /**
     * 根据订单 ID 查询订单
     *
     * @param orderId
     * @return
     */
    @Select("SELECT id,status,address,account_id accountId, product_id productId,deliveryman_id deliverymanId," +
            "settlement_id settlementId,reward_id rewardId,price, date FROM order_detail WHERE id = #{id}")
    OrderDetail queryOrder(Integer orderId);
}
