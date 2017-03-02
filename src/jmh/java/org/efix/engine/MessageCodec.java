package org.efix.engine;

import org.efix.message.FieldUtil;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.Tag;
import org.efix.message.parser.MessageParser;
import org.efix.util.ByteSequenceWrapper;


public class MessageCodec {

    private static final int SCALE = 6;

    private final ByteSequenceWrapper account = new ByteSequenceWrapper();
    private final ByteSequenceWrapper broker = new ByteSequenceWrapper();
    private final ByteSequenceWrapper currency = new ByteSequenceWrapper();
    private final ByteSequenceWrapper exchange = new ByteSequenceWrapper();
    private final ByteSequenceWrapper orderId = new ByteSequenceWrapper();
    private final ByteSequenceWrapper symbol = new ByteSequenceWrapper();
    private final ByteSequenceWrapper instrumentType = new ByteSequenceWrapper();

    public void decode(Message message, MessageParser parser) {
        ByteSequenceWrapper account = null;
        ByteSequenceWrapper broker = null;
        ByteSequenceWrapper currency = null;
        ByteSequenceWrapper exchange = null;
        ByteSequenceWrapper orderId = null;
        ByteSequenceWrapper symbol = null;
        ByteSequenceWrapper securityType = null;

        long expireTime = FieldUtil.LONG_NULL;
        long displayQuantity = FieldUtil.LONG_NULL;
        long limitPrice = FieldUtil.LONG_NULL;
        long minQuantity = FieldUtil.LONG_NULL;
        long quantity = FieldUtil.LONG_NULL;
        long stopPrice = FieldUtil.LONG_NULL;
        long transactTime = FieldUtil.LONG_NULL;

        byte orderType = FieldUtil.BYTE_NULL;
        byte side = FieldUtil.BYTE_NULL;
        byte timeInForce = FieldUtil.BYTE_NULL;

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.Account:
                    account = this.account;
                    parser.parseByteSequence(account);
                    break;

                case Tag.ClOrdID:
                    orderId = this.orderId;
                    parser.parseByteSequence(orderId);
                    break;

                case Tag.Currency:
                    currency = this.currency;
                    parser.parseByteSequence(currency);
                    break;

                case Tag.OrderQty:
                    quantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.OrdType:
                    orderType = parser.parseByte();
                    break;

                case Tag.Price:
                    limitPrice = parser.parseDecimal(SCALE);
                    break;

                case Tag.Symbol:
                    symbol = this.symbol;
                    parser.parseByteSequence(symbol);
                    break;

                case Tag.Side:
                    side = parser.parseByte();
                    break;

                case Tag.TimeInForce:
                    timeInForce = parser.parseByte();
                    break;

                case Tag.ExpireTime:
                    expireTime = parser.parseTimestamp();
                    break;

                case Tag.ExecBroker:
                    broker = this.broker;
                    parser.parseByteSequence(broker);
                    break;

                case Tag.StopPx:
                    stopPrice = parser.parseDecimal(SCALE);
                    break;

                case Tag.ExDestination:
                    exchange = this.exchange;
                    parser.parseByteSequence(exchange);
                    break;

                case Tag.MaxFloor:
                    minQuantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.SecurityType:
                    securityType = this.instrumentType;
                    parser.parseByteSequence(securityType);
                    break;

                case Tag.MaxShow:
                    displayQuantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.TransactTime:
                    transactTime = parser.parseTimestamp();
                    break;

                default:
                    parser.parseValue();
            }
        }

        message.setAccount(account);
        message.setBroker(broker);
        message.setCurrency(currency);
        message.setExchange(exchange);
        message.setOrderId(orderId);
        message.setSymbol(symbol);

        message.setExpireTime(expireTime);
        message.setDisplayQuantity(displayQuantity);
        message.setLimitPrice(limitPrice);
        message.setMinQuantity(minQuantity);
        message.setQuantity(quantity);
        message.setStopPrice(stopPrice);

        message.setSecurityType(securityType);
        message.setOrderType(orderType);
        message.setSide(side);
        message.setTimeInForce(timeInForce);
        message.setTransactTime(transactTime);
    }

    public void encode(Message message, MessageBuilder builder) {
        if (message.hasAccount()) {
            builder.addCharSequence(Tag.Account, message.getAccount());
        }

        if (message.hasOrderId()) {
            builder.addCharSequence(Tag.ClOrdID, message.getOrderId());
        }

        if (message.hasCurrency()) {
            builder.addCharSequence(Tag.Currency, message.getCurrency());
        }

        if (message.hasQuantity()) {
            builder.addDecimal(Tag.OrderQty, message.getQuantity(), SCALE);
        }

        if (message.hasOrderType()) {
            builder.addByte(Tag.OrdType, message.getOrderType());
        }

        if (message.hasLimitPrice()) {
            builder.addDecimal(Tag.Price, message.getLimitPrice(), SCALE);
        }

        if (message.hasSide()) {
            builder.addByte(Tag.Side, message.getSide());
        }

        if (message.hasSymbol()) {
            builder.addCharSequence(Tag.Symbol, message.getSymbol());
        }

        if (message.hasTimeInForce()) {
            builder.addByte(Tag.TimeInForce, message.getTimeInForce());
        }

        if (message.hasTransactTime()) {
            builder.addTimestamp(Tag.TransactTime, message.getTransactTime());
        }

        if (message.hasBroker()) {
            builder.addCharSequence(Tag.ExecBroker, message.getBroker());
        }

        if (message.hasStopPrice()) {
            builder.addDecimal(Tag.StopPx, message.getStopPrice(), SCALE);
        }

        if (message.hasExchange()) {
            builder.addCharSequence(Tag.ExDestination, message.getExchange());
        }

        if (message.hasMinQuantity()) {
            builder.addDecimal(Tag.MinQty, message.getMinQuantity(), SCALE);
        }

        if (message.hasDisplayQuantity()) {
            builder.addDecimal(Tag.MaxFloor, message.getDisplayQuantity(), SCALE);
        }

        if (message.hasExpireTime()) {
            builder.addTimestamp(Tag.ExpireTime, message.getExpireTime());
        }

        if (message.hasSecurityType()) {
            builder.addCharSequence(Tag.SecurityType, message.getSecurityType());
        }
    }

    public void predefinedDecode(Message message, MessageParser parser) {
        for (int i = 0; i < 7; i++) {
            parser.parseTag();
            parser.parseValue();
        }

        ByteSequenceWrapper account = this.account;
        parser.parseTag();
        parser.parseByteSequence(account);

        ByteSequenceWrapper orderId = this.orderId;
        parser.parseTag();
        parser.parseByteSequence(orderId);

        parser.parseTag();
        long quantity = parser.parseDecimal(SCALE);

        parser.parseTag();
        byte orderType = parser.parseByte();

        parser.parseTag();
        long limitPrice = parser.parseDecimal(SCALE);

        parser.parseTag();
        byte side = parser.parseByte();

        ByteSequenceWrapper symbol = this.symbol;
        parser.parseTag();
        parser.parseByteSequence(symbol);

        parser.parseTag();
        byte timeInForce = parser.parseByte();

        parser.parseTag();
        long transactTime = parser.parseTimestamp();

        parser.parseTag();
        ByteSequenceWrapper broker = this.broker;
        parser.parseByteSequence(broker);

        parser.parseTag();
        ByteSequenceWrapper exchange = this.exchange;
        parser.parseByteSequence(exchange);

        parser.parseTag();
        ByteSequenceWrapper securityType = this.exchange;
        parser.parseByteSequence(securityType);

        parser.parseTag();
        parser.parseValue();

        message.setAccount(account);
        message.setBroker(broker);
        message.setCurrency(currency);
        message.setExchange(exchange);
        message.setOrderId(orderId);
        message.setSymbol(symbol);

        message.setLimitPrice(limitPrice);
        message.setQuantity(quantity);

        message.setSecurityType(securityType);
        message.setOrderType(orderType);
        message.setSide(side);
        message.setTimeInForce(timeInForce);
        message.setTransactTime(transactTime);

    }

}
