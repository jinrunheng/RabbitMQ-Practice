package com.github.order.controller;

import com.github.order.service.OrderService;
import com.github.order.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderCreateVO orderCreateVO) {

        log.info("createOrder : {}", orderCreateVO);
        orderService.createOrder(orderCreateVO);
    }
}
