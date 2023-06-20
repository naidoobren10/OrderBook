package com.orderbook.service;

import com.orderbook.dao.Order;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public interface IOrderBook {

    public void addOrder(Order order);

    public Order deleteOrder(long orderID);

    public Order modifyOrder(long orderID, int quantity) throws Exception;

    public void retriveOrders(BigDecimal orderPrice, String side);

}
