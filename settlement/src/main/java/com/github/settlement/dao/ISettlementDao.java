package com.github.settlement.dao;

import com.github.settlement.entity.Settlement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

/**
 * @Author Dooby Kim
 * @Date 2022/11/3 9:58 下午
 * @Version 1.0
 */
@Mapper
@Repository
public interface ISettlementDao {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into rabbit.settlement(order_id,transaction_id,status,amount,date) " +
            "values(#{orderId},#{transactionId},#{status},#{amount},#{date})")
    void insert(Settlement settlement);
}
