package com.orderbook.service;

import java.math.BigDecimal;

public interface IOrderService {

    public void addOrder(Order order);

    public boolean deleteOrder(long OrderID);

    public void modifyOrder(long orderID, int quantity);

    public void retriveOrders(BigDecimal orderPrice, String side);
}
