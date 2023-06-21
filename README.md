# OrderBook

The datastructures used for this project:

A Tree map was used to store a sorted price level tree. I used a Treemap, because I needed a key value pair data structure, that can also keep the prices sorted in their natural order. This Treemap is a red black tree that sorts the price levels using the red black tree sorting algorithm. 
By choosing a Tree data structure to store the orders sorted by their price level, gives me the ability to use binary search to retrieve, add, update and remove items, which gives a worst case performance of O(log n).

A LinkedhashMap was used to store the orders maintaining insertion order which allows me to implement update, add functionality to make the Linkedhash map behave a FIFO manner. It also allowed me the ability to store orders in a key value pair, making retrieval of the orders easier. The LinkedHashMap has a time complexity of O(1) for insertions and lookups.

I used a HashMap as a order lookup, as I needed a key value pair data structure to store the order lookup based on a key as the orderID. Hashmap is has much better performance then that of a LinkedHashMap or TreeMap, which is why I used this implementation. Insertion Order or Sorting did not matter in storing the order lookup, as the data structure is just used purely as a lookup, to find the orders details.



