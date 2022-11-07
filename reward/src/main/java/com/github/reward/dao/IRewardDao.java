package com.github.reward.dao;

import com.github.reward.entity.Reward;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IRewardDao {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into rabbit.reward (order_id,amount,status,date) " +
            "values(#{orderId},#{amount},#{status},#{date})")
    void insert(Reward reward);
}