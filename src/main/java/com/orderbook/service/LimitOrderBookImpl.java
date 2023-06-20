package com.orderbook.service;

import com.orderbook.dao.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class LimitOrderBookImpl implements IOrderBook {

    public static final String BUY = "buy";
    public static final String SELL = "sell";

    private final Map<BigDecimal, LinkedHashMap<Long, Order>> buyOrders = new TreeMap<>();
    private final Map<BigDecimal, LinkedHashMap<Long, Order>> sellOrders = new TreeMap<>();
    private final HashMap<Long, Order> orderMap = new HashMap<>();



    public long generateUniqueOrderIdentifier(){

       long orderID = System.currentTimeMillis();

       while(orderMap.containsKey(orderID)){
              orderID = System.currentTimeMillis();
       }

       return orderID;

    }

    public void addOrder(Order order) {

        if(order == null){
            throw new NullPointerException("Invalid Order, Order cannot be null");
        }

        order.setId(generateUniqueOrderIdentifier());
        order.setCreateDate(LocalDateTime.now());
        orderMap.put(order.getId(), order);


        if(order.getSide().equalsIgnoreCase(LimitOrderBookImpl.BUY)){
            addOrdersByPriceLevel(order,buyOrders);
        }else{
            addOrdersByPriceLevel(order,sellOrders);
        }


    }

    @Override
    public Order deleteOrder(long orderID) throws NoSuchElementException {

        if(!orderMap.containsKey(orderID)){
            throw new NoSuchElementException("The order id does not exist");
        }

        Order order = orderMap.get(orderID);

        if(order.getSide().equalsIgnoreCase(BUY)){
           buyOrders.get(order.getPrice()).remove(orderID);
        }else{
           sellOrders.get(order.getPrice()).remove(orderID);
        }

        orderMap.remove(orderID);

        return order;

    }

    private void addOrdersByPriceLevel(Order order, Map<BigDecimal, LinkedHashMap<Long, Order>> orders){

        if(orders.containsKey(order.getPrice())){
            orders.get(order.getPrice()).put(order.getId(), order);
        }else{
             LinkedHashMap<Long, Order> newOrder = new LinkedHashMap<>();
             newOrder.put(order.getId(), order);
             orders.put(order.getPrice(),newOrder);
        }
    }



    public Order modifyOrder(long orderID, int quantity) throws Exception {

        if(!orderMap.containsKey(orderID)){
           throw new NoSuchElementException("The order id does not exist");
        }else if(quantity < 0){
           throw new Exception("Invalid quantity provided");
        }

        Order modifiedOrder = orderMap.get(orderID);
        modifiedOrder.setQuantity(quantity);
        modifiedOrder.setUpdateDate(LocalDateTime.now());

        if(modifiedOrder.getSide().equalsIgnoreCase(BUY)){
            buyOrders.get(modifiedOrder.getPrice()).remove(orderID);
            buyOrders.get(modifiedOrder.getPrice()).put(orderID, modifiedOrder);
        }else{
            sellOrders.get(modifiedOrder.getPrice()).remove(orderID);
            sellOrders.get(modifiedOrder.getPrice()).put(orderID, modifiedOrder);
        }

        orderMap.remove(orderID);
        orderMap.put(orderID, modifiedOrder);

        return modifiedOrder;
    }

    public Map<Long,Order> retriveBuySellOrdersByPriceLevel(BigDecimal orderPrice, String side) {
       LinkedHashMap<Long, Order> priceLevelOrders = null;

       if(side.equalsIgnoreCase(BUY)){
           priceLevelOrders =  buyOrders.get(orderPrice);
       }else{
           priceLevelOrders = sellOrders.get(orderPrice);
       }

       return priceLevelOrders;
    }

    public Map<BigDecimal, LinkedHashMap<Long, Order>> getAllBuyOrdersByPriceLevel() {
        return buyOrders;
    }

    public Map<BigDecimal, LinkedHashMap<Long, Order>> getAllSellOrdersByPriceLevel() {
        return sellOrders;
    }

    public HashMap<Long, Order> getAllOrders(){
        return orderMap;
    }

    public int getOrdersSize(){
        return orderMap.size();
    }

    public int getSellOrdersByPriceLevelSize(){
        return sellOrders.size();
    }

    public int getBuyOrdersByPriceLevelSize() {
        return buyOrders.size();
    }

 }