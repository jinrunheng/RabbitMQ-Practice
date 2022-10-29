package com.github.takeaway.dao;

import com.github.takeaway.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
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
    void insert(OrderDetail orderDetail);

    /**
     * 修改订单
     *
     * @param orderDetail
     */
    void update(OrderDetail orderDetail);

    /**
     * 根据订单 ID 查询订单
     *
     * @param orderId
     * @return
     */
    OrderDetail queryOrder(Integer orderId);
}
