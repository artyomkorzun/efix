package org.efix.session;

import org.efix.message.FieldUtil;

import static org.efix.message.FieldUtil.BYTE_NULL;
import static org.efix.message.FieldUtil.LONG_NULL;


final class Message {

    private CharSequence account = null;
    private CharSequence broker = null;
    private CharSequence currency = null;
    private CharSequence exchange = null;
    private CharSequence orderId = null;
    private CharSequence symbol = null;
    private CharSequence securityType = null;

    private long expireTime = FieldUtil.LONG_NULL;
    private long displayQuantity = FieldUtil.LONG_NULL;
    private long limitPrice = FieldUtil.LONG_NULL;
    private long minQuantity = FieldUtil.LONG_NULL;
    private long quantity = FieldUtil.LONG_NULL;
    private long stopPrice = FieldUtil.LONG_NULL;
    private long transactTime = FieldUtil.LONG_NULL;

    private byte orderType = BYTE_NULL;
    private byte side = BYTE_NULL;
    private byte timeInForce = BYTE_NULL;

    public void setAccount(CharSequence account) {
        this.account = account;
    }

    public CharSequence getAccount() {
        return account;
    }

    public boolean hasAccount() {
        return account != null;
    }

    public CharSequence getBroker() {
        return broker;
    }

    public void setBroker(CharSequence execBroker) {
        this.broker = execBroker;
    }

    public boolean hasBroker() {
        return broker != null;
    }

    public void setOrderId(CharSequence orderId) {
        this.orderId = orderId;
    }

    public CharSequence getOrderId() {
        return orderId;
    }

    public boolean hasOrderId() {
        return orderId != null;
    }

    public void setCurrency(CharSequence currency) {
        this.currency = currency;
    }

    public CharSequence getCurrency() {
        return currency;
    }

    public boolean hasCurrency() {
        return currency != null;
    }

    public void setSymbol(CharSequence symbol) {
        this.symbol = symbol;
    }

    public CharSequence getSymbol() {
        return symbol;
    }

    public boolean hasSymbol() {
        return symbol != null;
    }

    public void setSecurityType(CharSequence securityType) {
        this.securityType = securityType;
    }

    public CharSequence getSecurityType() {
        return securityType;
    }

    public boolean hasSecurityType() {
        return securityType != null;
    }

    public void setExchange(CharSequence exchange) {
        this.exchange = exchange;
    }

    public CharSequence getExchange() {
        return exchange;
    }

    public boolean hasExchange() {
        return exchange != null;
    }

    public void setSide(byte side) {
        this.side = side;
    }

    public byte getSide() {
        return side;
    }

    public boolean hasSide() {
        return side != BYTE_NULL;
    }

    public void setTransactTime(long transactTime) {
        this.transactTime = transactTime;
    }

    public long getTransactTime() {
        return transactTime;
    }

    public boolean hasTransactTime() {
        return transactTime != LONG_NULL;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    public boolean hasQuantity() {
        return quantity != LONG_NULL;
    }

    public void setMinQuantity(long minQuantity) {
        this.minQuantity = minQuantity;
    }

    public long getMinQuantity() {
        return minQuantity;
    }

    public boolean hasMinQuantity() {
        return minQuantity != LONG_NULL;
    }

    public void setDisplayQuantity(long displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public long getDisplayQuantity() {
        return displayQuantity;
    }

    public boolean hasDisplayQuantity() {
        return displayQuantity != LONG_NULL;
    }

    public void setOrderType(byte orderType) {
        this.orderType = orderType;
    }

    public byte getOrderType() {
        return orderType;
    }

    public boolean hasOrderType() {
        return orderType != BYTE_NULL;
    }

    public void setLimitPrice(long limitPrice) {
        this.limitPrice = limitPrice;
    }

    public long getLimitPrice() {
        return limitPrice;
    }

    public boolean hasLimitPrice() {
        return limitPrice != LONG_NULL;
    }

    public void setStopPrice(long stopPrice) {
        this.stopPrice = stopPrice;
    }

    public long getStopPrice() {
        return stopPrice;
    }

    public boolean hasStopPrice() {
        return stopPrice != LONG_NULL;
    }

    public void setTimeInForce(byte timeInForce) {
        this.timeInForce = timeInForce;
    }

    public byte getTimeInForce() {
        return timeInForce;
    }

    public boolean hasTimeInForce() {
        return timeInForce != BYTE_NULL;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean hasExpireTime() {
        return expireTime != LONG_NULL;
    }

}
