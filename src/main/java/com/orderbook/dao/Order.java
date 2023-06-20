package com.orderbook.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {


    private long id;
    private BigDecimal price;
    private int quantity;
    private String side;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;


    public Order(){

    }

    public Order(OrderBuilder orderBuilder){
        this.id = orderBuilder.id;
        this.price = orderBuilder.price;
        this.quantity = orderBuilder.quantity;
        this.side = orderBuilder.side;
        this.createDate = orderBuilder.createDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", side='" + side + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }

    public static class OrderBuilder {
        private long id;
        private BigDecimal price;
        private int quantity;
        private String side;
        private LocalDateTime createDate;

        public OrderBuilder setId(long id) {
            this.id = id;
            return this;
        }

        public OrderBuilder setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderBuilder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderBuilder setSide(String side) {
            this.side = side;
            return this;
        }

        public OrderBuilder setCreateDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public Order build(){
            return new Order(this);
        }


    }
}
