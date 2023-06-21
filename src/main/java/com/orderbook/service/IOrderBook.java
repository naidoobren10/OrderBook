package com.orderbook.service;

import com.orderbook.dao.Order;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;


public interface IOrderBook {

    public boolean addOrder(Order order);

    public Order deleteOrder(long orderID);

    public Order modifyOrder(long orderID, int quantity) throws Exception;

    public Map<Long, Order> retriveBuySellOrdersByPriceLevel(BigDecimal orderPrice, String side);

    public <T>T generateUniqueOrderIdentifier();

    public Map<BigDecimal, LinkedHashMap<Long, Order>> getAllBuyOrdersByPriceLevel();

    public Map<BigDecimal, LinkedHashMap<Long, Order>> getAllSellOrdersByPriceLevel();

    public Map<?, Order> getAllOrders();
}
