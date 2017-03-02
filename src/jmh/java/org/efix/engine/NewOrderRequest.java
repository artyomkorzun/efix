package org.efix.engine;

import org.efix.message.FieldUtil;
import org.efix.util.ByteSequence;


final class NewOrderRequest {

    private ByteSequence account = null;
    private ByteSequence destination = null;
    private ByteSequence currency = null;
    private ByteSequence exchange = null;
    private ByteSequence orderId = null;
    private ByteSequence symbol = null;
    private ByteSequence instrumentType = null;


    private long expireTime = FieldUtil.LONG_NULL;
    private long displayQuantity = FieldUtil.LONG_NULL;
    private long limitPrice = FieldUtil.LONG_NULL;
    private long minQuantity = FieldUtil.LONG_NULL;
    private long quantity = FieldUtil.LONG_NULL;
    private long stopPrice = FieldUtil.LONG_NULL;
    private long timestamp = FieldUtil.LONG_NULL;

    private byte orderType = FieldUtil.BYTE_NULL;
    private byte side = FieldUtil.BYTE_NULL;
    private byte timeInForce = FieldUtil.BYTE_NULL;

    public ByteSequence getAccount() {
        return account;
    }

    public void setAccount(ByteSequence account) {
        this.account = account;
    }

    public ByteSequence getDestination() {
        return destination;
    }

    public void setDestination(ByteSequence destination) {
        this.destination = destination;
    }

    public ByteSequence getCurrency() {
        return currency;
    }

    public void setCurrency(ByteSequence currency) {
        this.currency = currency;
    }

    public ByteSequence getExchange() {
        return exchange;
    }

    public void setExchange(ByteSequence exchange) {
        this.exchange = exchange;
    }

    public ByteSequence getOrderId() {
        return orderId;
    }

    public void setOrderId(ByteSequence orderId) {
        this.orderId = orderId;
    }

    public ByteSequence getSymbol() {
        return symbol;
    }

    public void setSymbol(ByteSequence symbol) {
        this.symbol = symbol;
    }

    public ByteSequence getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(ByteSequence instrumentType) {
        this.instrumentType = instrumentType;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getDisplayQuantity() {
        return displayQuantity;
    }

    public void setDisplayQuantity(long displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public long getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(long limitPrice) {
        this.limitPrice = limitPrice;
    }

    public long getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(long minQuantity) {
        this.minQuantity = minQuantity;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(long stopPrice) {
        this.stopPrice = stopPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getOrderType() {
        return orderType;
    }

    public void setOrderType(byte orderType) {
        this.orderType = orderType;
    }

    public byte getSide() {
        return side;
    }

    public void setSide(byte side) {
        this.side = side;
    }

    public byte getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(byte timeInForce) {
        this.timeInForce = timeInForce;
    }

}
