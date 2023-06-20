package com.orderbook.orderbooktests;

import com.orderbook.dao.Order;
import com.orderbook.service.LimitOrderBookImpl;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderBookServiceTest {


    private LimitOrderBookImpl orderService = new LimitOrderBookImpl();
    private static List<Order> orders = new ArrayList<>();
    private static List<Integer> newQuantities = new ArrayList<>();




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

            Map<BigDecimal, LinkedHashMap<Long, Order>> actualPriceLevelOrders = null;

            if(expectedOrder.getSide().equalsIgnoreCase(LimitOrderBookImpl.BUY)){
                actualPriceLevelOrders = orderService.getAllBuyOrdersByPriceLevel();
            }else{
                actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();
            }
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
            Map<BigDecimal, LinkedHashMap<Long, Order>> actualPriceLevelOrders = null;
                if(expectedOrder.getSide().equalsIgnoreCase(LimitOrderBookImpl.BUY)){
                    actualPriceLevelOrders = orderService.getAllBuyOrdersByPriceLevel();
                }else{
                    actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();
                }
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


    @Test
    public void modifyOrdersTest_orderIsModifiedOrderLookup(){


        for(int i =0; i< getListSizeforUpdateTest(); i++){

            Order expectedOrder = orders.get(i);
            Order modifiedOrder = null;

            try {
                modifiedOrder = orderService.modifyOrder(expectedOrder.getId(),newQuantities.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Assertions.assertNotEquals(expectedOrder.getQuantity(), modifiedOrder.getQuantity());
            Assertions.assertEquals(modifiedOrder.getQuantity(), newQuantities.get(i));

            Assertions.assertEquals(orderService.getAllOrders().get(expectedOrder.getId()).getQuantity(), newQuantities.get(i));


        }

    }

    @Test
    public void modifyOrdersTest_orderIsModifiedPriceLevelLookup_orderLosesPriority(){
        for(int i =0; i< getListSizeforUpdateTest(); i++){

            Order expectedOrder = orders.get(i);

            try {
               orderService.modifyOrder(expectedOrder.getId(),newQuantities.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinkedHashMap<Long, Order> actualModifiedOrders;

            if(expectedOrder.getSide().equalsIgnoreCase(LimitOrderBookImpl.BUY)){
                actualModifiedOrders = orderService.getAllBuyOrdersByPriceLevel().get(expectedOrder.getPrice());
            }else{
                actualModifiedOrders = orderService.getAllSellOrdersByPriceLevel().get(expectedOrder.getPrice());
            }

            Iterator iter = actualModifiedOrders.keySet().iterator();

            Order actualTailOrder = new Order();

            while(iter.hasNext()){
                actualTailOrder = actualModifiedOrders.get(iter.next());

            }

            Assertions.assertEquals(expectedOrder.getId(), actualTailOrder.getId());
            Assertions.assertEquals(expectedOrder.getPrice(), actualTailOrder.getPrice());
            Assertions.assertEquals(newQuantities.get(i), actualTailOrder.getQuantity());
            Assertions.assertEquals(expectedOrder.getSide(), actualTailOrder.getSide());
            Assertions.assertEquals(expectedOrder.getCreateDate(), actualTailOrder.getCreateDate());
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

            orderService.deleteOrder(expectedOrder.getId());
            Assertions.assertFalse(orderService.getAllOrders().containsKey(expectedOrder.getId()));
        }
    }

    @Test
    public void deleteOrdersTest_isOrderDeletedFromOrderPriceLookup(){
        for(Order expectedOrder : orders){
            orderService.deleteOrder(expectedOrder.getId());
            Map<BigDecimal, LinkedHashMap<Long, Order>> actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();

            if(expectedOrder.getSide().equalsIgnoreCase(LimitOrderBookImpl.BUY)){
                actualPriceLevelOrders = orderService.getAllBuyOrdersByPriceLevel();
            }else{
                actualPriceLevelOrders = orderService.getAllSellOrdersByPriceLevel();
            }

            LinkedHashMap<Long, Order> actualOrders = actualPriceLevelOrders.get(expectedOrder.getPrice());

            //Check that the order has been deleted from the price order level map
            Assertions.assertFalse(actualOrders.containsKey(expectedOrder.getId()));

        }
    }

    @Test
    public void deleteOrdersTest_orderDoesNotExistExceptionTest(){
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            orderService.deleteOrder(10l);
        }, "The order id does not exist" );
    }




    @BeforeAll
    public static void mockNewOrders(){
        String newOrdersfile ="src/test/resources/mock_new_orders.txt";
        String updateOrderQuantityFile = "src/test/resources/update_orders_quantities.txt";

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(newOrdersfile));
            String currentLine = null;

            while((currentLine = reader.readLine()) != null){
                String [] parts = currentLine.split(",");
                orders.add(new Order.OrderBuilder()
                        .setPrice(new BigDecimal(parts[0]))
                        .setQuantity(Integer.parseInt(parts[1]))
                        .setSide(parts[2])
                        .build());
            }

            reader = new BufferedReader(new FileReader(updateOrderQuantityFile));

            while((currentLine = reader.readLine()) != null){
                newQuantities.add(Integer.parseInt(currentLine));
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
        //orders.forEach((order) -> System.out.println(order.toString()));
        newQuantities.forEach((q) -> System.out.println(q));
    }*/

    @BeforeEach
    public  void addMockedOrders(){
        if(!orderService.getAllOrders().isEmpty()){
            for(Order order : orders){
                orderService.deleteOrder(order.getId());
            }
        }
        for(Order order : orders){
            orderService.addOrder(order);

        }
    }

    public static int getListSizeforUpdateTest(){
        int length;
        if(orders.size() < newQuantities.size()){
            length = orders.size();
        }else{
            length = newQuantities.size();
        }
        return length;
    }




    //Test case to test the update operation for buy orders

    //Test case to test the update operation for sell orders

    //Test case to test the retrieve order for specific price and side





}
