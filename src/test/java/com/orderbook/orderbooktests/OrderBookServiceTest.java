package com.orderbook.orderbooktests;

import com.orderbook.dao.Order;
import com.orderbook.service.LimitOrderBookImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderBookServiceTest {


    LimitOrderBookImpl orderService = new LimitOrderBookImpl();
    private static List<Order> orders = new ArrayList<>();



    public static Order getMockedOrderObject(){
        Order order_1 = new Order.OrderBuilder()
                .setId(12345l)
                .setPrice(new BigDecimal(1000))
                .setQuantity(50)
                .setSide("sell")
                .setCreateDate(LocalDateTime.now())
                .build();


        return order_1;
    }

    @Test
    public void generateUniqueIdentifierTest(){
    }
    //Test case to test the Add operation for buy orders
    @Test
    public void addOrderTest_checkOrderExistsInLookupMap(){

        for(Order order : orders){
            Order expectedOrder = order;
            HashMap<Long, Order> actualOrders = orderService.getAllOrders();

            Assertions.assertTrue(actualOrders.containsKey(expectedOrder.getId()));

        }



    }

    @Test
    public void addOrderTest_checkNewPriceLevelCreated(){

        for(Order order : orders){
            Order expectedOrder = order;
            Map<BigDecimal, LinkedHashMap<Long, Order>> actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();

            //Check the Price Level exists
            Assertions.assertTrue(actualPriceLevelOrders.containsKey(expectedOrder.getPrice()));
        }



    }

    @Test
    public void addOrdersTest_OrderAddedToLookupOrderList(){

        for(Order order : orders){
            Order expectedOrder = order;
            HashMap<Long, Order> orders = orderService.getAllOrders();
            Order actualOrder = orders.get(expectedOrder.getId());

            //check if the same object exists in the orderMap
            Assertions.assertEquals(expectedOrder.getId(), actualOrder.getId());
            Assertions.assertEquals(expectedOrder.getPrice(), actualOrder.getPrice());
            Assertions.assertEquals(expectedOrder.getQuantity(), actualOrder.getQuantity());
            Assertions.assertEquals(expectedOrder.getSide(), actualOrder.getSide());
            Assertions.assertEquals(expectedOrder.getCreateDate(), actualOrder.getCreateDate());



        }

    }
    @Test
    public void addOrdersTest_OrderAddedToPriceLevelOrderList(){
        for(Order order : orders){
            Order expectedOrder = order;
            Map<BigDecimal, LinkedHashMap<Long, Order>> actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();
            LinkedHashMap<Long, Order> actualOrders = actualPriceLevelOrders.get(expectedOrder.getPrice());
            Order actualOrder =  actualOrders.get(expectedOrder.getId());

            //check if the object has been added correctly as per its price level
            Assertions.assertEquals(expectedOrder.getId(), actualOrder.getId());
            Assertions.assertEquals(expectedOrder.getPrice(), actualOrder.getPrice());
            Assertions.assertEquals(expectedOrder.getQuantity(), actualOrder.getQuantity());
            Assertions.assertEquals(expectedOrder.getSide(), actualOrder.getSide());
            Assertions.assertEquals(expectedOrder.getCreateDate(), actualOrder.getCreateDate());
        }
    }

    //Test case to test the delete operation for buy orders
    @Test
    public void deleteOrdersTest_confirmDeletedOrderDetails(){

        for(Order order: orders){
            Order expectedOrder = order;

            Order actualOrder = orderService.deleteOrder(expectedOrder.getId());

            Assertions.assertEquals(expectedOrder.getId(), actualOrder.getId());
            Assertions.assertEquals(expectedOrder.getPrice(), actualOrder.getPrice());
            Assertions.assertEquals(expectedOrder.getQuantity(), actualOrder.getQuantity());
            Assertions.assertEquals(expectedOrder.getSide(), actualOrder.getSide());
            Assertions.assertEquals(expectedOrder.getCreateDate(), actualOrder.getCreateDate());
        }

    }

    @Test
    public void deleteOrdersTest_isOrderDeletedFromOrderLookup(){
        for(Order expectedOrder : orders){
            Assertions.assertFalse(orderService.getAllOrders().containsKey(expectedOrder.getId()));
        }
    }

    @Test
    public void deleteOrdersTest_isOrderDeletedFromOrderPriceLookup(){
        for(Order expectedOrder : orders){
            Map<BigDecimal, LinkedHashMap<Long, Order>> actualOrders = orderService.getAllSellOrdersByPriceLevel();
            LinkedHashMap<Long, Order> actualPriceLevelOrders = actualOrders.get(expectedOrder.getPrice());

            //Check that the order has been deleted from the price order level map
            Assertions.assertFalse(actualPriceLevelOrders.containsKey(expectedOrder.getId()));

        }
    }

    @Test
    public void deleteOrdersTest_orderDoesNotExistExceptionTest(){
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            orderService.deleteOrder(10l);
        }, "The order id does not exist" );
    }



    @Test
    public void modifyOrdersTest(){


        Order expectedOrder = getMockedOrderObject();
        orderService.addOrder(expectedOrder);

        Order modifiedOrder = null;

        try {
            modifiedOrder = orderService.modifyOrder(expectedOrder.getId(),20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Assertions.assertNotEquals(expectedOrder.getQuantity(), modifiedOrder.getQuantity());
        Assertions.assertEquals(modifiedOrder.getQuantity(), 20);

        Assertions.assertEquals(orderService.getAllOrders().get(expectedOrder.getId()).getQuantity(), 20 );

        LinkedHashMap<Long, Order> expectedModifiedOrders = orderService.getAllSellOrdersByPriceLevel().get(expectedOrder.getPrice());

        Iterator iter = expectedModifiedOrders.keySet().iterator();

        Order actualTailOrder = new Order();

        while(iter.hasNext()){
            actualTailOrder = expectedModifiedOrders.get(iter.next());

        }

        Assertions.assertEquals(expectedOrder.getId(), actualTailOrder.getId());
        Assertions.assertEquals(expectedOrder.getPrice(), actualTailOrder.getPrice());
        Assertions.assertEquals(20, actualTailOrder.getQuantity());
        Assertions.assertEquals(expectedOrder.getSide(), actualTailOrder.getSide());
        Assertions.assertEquals(expectedOrder.getCreateDate(), actualTailOrder.getCreateDate());

    }

    @BeforeAll
    public static void mockNewOrders(){
        String file ="src/test/resources/mock_new_orders.txt";

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String currentLine = null;

            while((currentLine = reader.readLine()) != null){
                String [] parts = currentLine.split(",");
                orders.add(new Order.OrderBuilder()
                        .setPrice(new BigDecimal(parts[0]))
                        .setQuantity(Integer.parseInt(parts[1]))
                        .setSide(parts[2])
                        .build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*@BeforeAll
    public static void printMockedOrders(){
        orders.forEach((order) -> System.out.println(order.toString()));
    }*/

    @BeforeAll
    public  void addMockedOrders(){
        for(Order order : orders){
            orderService.addOrder(order);
        }
    }




    //Test case to test the update operation for buy orders

    //Test case to test the update operation for sell orders

    //Test case to test the retrieve order for specific price and side





}
