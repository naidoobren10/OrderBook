package com.orderbook.orderbooktests;

import com.orderbook.dao.Order;
import com.orderbook.service.IOrderBook;
import com.orderbook.service.LimitOrderBookImpl;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderBookServiceTest {


    private IOrderBook orderService = new LimitOrderBookImpl();
    private static List<Order> orders = new ArrayList<>();
    private static List<Integer> newQuantities = new ArrayList<>();
    private static Map<BigDecimal, String> ordersByPriceLevel = new TreeMap<>();
    private static Map<Long, Order>  mockedOrdersByPriceLevel = new LinkedHashMap<>();


    @Test
    public void generateUniqueIdentifierTest(){

        Assertions.assertInstanceOf(Long.class, orderService.generateUniqueOrderIdentifier());

        List<Long> uniqueIdentifiersList = new ArrayList<>();
        for(int i =0 ; i<1000; i++){
            uniqueIdentifiersList.add(orderService.generateUniqueOrderIdentifier());
        }
        Set<Long> uniqueIdentifiersSet = new HashSet<>(uniqueIdentifiersList);

        Assertions.assertTrue(uniqueIdentifiersSet.size() < uniqueIdentifiersList.size());



    }

    @Test
    public void addOrderTest_checkOrderExistsInLookupMap(){

        for(Order order : orders){
            Order expectedOrder = order;
            Map<?, Order> actualOrders = orderService.getAllOrders();

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
            Map<?, Order> orders = orderService.getAllOrders();
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

            Assertions.assertEquals( newQuantities.get(i), modifiedOrder.getQuantity());

            Assertions.assertEquals(newQuantities.get(i), orderService.getAllOrders().get(expectedOrder.getId()).getQuantity());


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

    //Test case to test the retrieve order for specific price and side

    @Test
    public void retrievePriceLevelOrdersTest_checkPriorityOfOrdersTest(){
        Iterator iter = ordersByPriceLevel.keySet().iterator();

        while(iter.hasNext()){
            BigDecimal priceLevel = new BigDecimal(iter.next().toString());
            Map<Long, Order> orderPriority = orderService
                    .retriveBuySellOrdersByPriceLevel(priceLevel, ordersByPriceLevel.get(priceLevel));
            Iterator<Long> actualOrderIDPriorityIter = orderPriority.keySet().iterator();

            Map<Long, Order> expectedOrderIDPriorityMap = mockedOrdersByPriceLevel.entrySet()
                    .stream()
                    .filter(x-> x.getValue().getPrice().equals(priceLevel))
                    .collect(LinkedHashMap::new,(map, item) -> map.put(item.getKey(), item.getValue()), Map::putAll);

            Iterator<Long> expectedOrderIDPriorityIter = expectedOrderIDPriorityMap.keySet().iterator();


            while(actualOrderIDPriorityIter.hasNext()){
                 long actualOrderIDPriority = actualOrderIDPriorityIter.next();
                 long expectedOrderIDPriority = expectedOrderIDPriorityIter.next();

                Assertions.assertEquals(expectedOrderIDPriority, actualOrderIDPriority );
                System.out.print(orderPriority.get(actualOrderIDPriority).toString() + ",");
            }
            System.out.println("\n");

        }

    }


    @BeforeAll
    public static void mockNewOrders(){
        String newOrdersfile ="src/test/resources/mock_new_orders.txt";
        String updateOrderQuantityFile = "src/test/resources/update_orders_quantities.txt";
        String retrievePriceLevelBuySellOrdersFile = "src/test/resources/retrieve_buy_sell_orders_by_price_level.txt";

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

            reader = new BufferedReader(new FileReader(retrievePriceLevelBuySellOrdersFile));

            while((currentLine = reader.readLine()) != null){
                String [] parts = currentLine.split(",");
                ordersByPriceLevel.put(new BigDecimal(parts[0]), parts[1]);
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

    @Test
    @BeforeEach
    public  void addMockedOrders(){
        if(!orderService.getAllOrders().isEmpty()){
            for(Order order : orders){
                orderService.deleteOrder(order.getId());
            }
        }
        for(Order order : orders){
            boolean orderAdded = orderService.addOrder(order);
            Assertions.assertTrue(orderAdded);
        }
    }

    @BeforeEach
    public void mapOrdersByPriority(){

        for(Order order : orders){
            if(ordersByPriceLevel.containsKey(order.getPrice())
                    && order.getSide().equalsIgnoreCase(ordersByPriceLevel.get(order.getPrice()))){
                mockedOrdersByPriceLevel.put(order.getId(), order);
            }
        }
    }

    @AfterEach
    public void clearMapWithOrdersByPriority(){
        mockedOrdersByPriceLevel.clear();
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








}
