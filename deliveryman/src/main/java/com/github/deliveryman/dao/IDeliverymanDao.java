package com.github.deliveryman.dao;

import com.github.deliveryman.entity.Deliveryman;
import com.github.deliveryman.enummeration.DeliverymanStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 7:03 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface IDeliverymanDao {

    @Select("select id,name,status,date from rabbit.deliveryman where status = #{status}")
    List<Deliveryman> queryDeliverymanByStatus(DeliverymanStatusEnum statusEnum);
}
