package com.jic.marketonlinev2.Model;

/**
 * Created by Jic on 10/12/2016.
 */

public class SendOrder {
    private String buyerAddress;
    private String buyerMes;
    private String buyderOrder;
    private String buyerID;
    private String orderStatus;
    private String storeID;

    public SendOrder() {
    }

    public SendOrder(String buyerAddress, String buyerMes, String buyderOrder, String buyerID, String orderStatus, String storeID) {
        this.buyerAddress = buyerAddress;
        this.buyerMes = buyerMes;
        this.buyderOrder = buyderOrder;
        this.buyerID = buyerID;
        this.orderStatus = orderStatus;
        this.storeID = storeID;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerMes() {
        return buyerMes;
    }

    public void setBuyerMes(String buyerMes) {
        this.buyerMes = buyerMes;
    }

    public String getBuyderOrder() {
        return buyderOrder;
    }

    public void setBuyderOrder(String buyderOrder) {
        this.buyderOrder = buyderOrder;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }
}
