package com.github.order.mapper;

import com.github.order.entity.OrderDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2023/2/5 5:50 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface OrderDetailMapper {

    void insert(OrderDetail orderDetail);

    void update(OrderDetail orderDetail);

    OrderDetail queryOrder(Integer orderId);
}
