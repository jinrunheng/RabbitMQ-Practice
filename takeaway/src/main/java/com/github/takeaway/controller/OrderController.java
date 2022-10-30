package com.github.takeaway.controller;

import com.github.takeaway.service.OrderService;
import com.github.takeaway.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author Dooby Kim
 * @Date 2022/10/30 6:03 下午
 * @Version 1.0
 */
@RestController
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;


    public void createOrder(OrderCreateVO orderCreateVO) throws IOException {

        log.info("createOrder : orderCreateVO : {}", orderCreateVO);
        orderService.createOrder(orderCreateVO);
    }
}
