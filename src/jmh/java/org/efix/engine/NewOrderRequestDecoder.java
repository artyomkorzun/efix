package org.efix.engine;

import org.efix.message.FieldUtil;
import org.efix.message.field.Tag;
import org.efix.message.parser.MessageParser;
import org.efix.util.ByteSequenceWrapper;

public class NewOrderRequestDecoder {

    private static final int SCALE = 6;

    private final NewOrderRequest request = new NewOrderRequest();

    private final ByteSequenceWrapper account = new ByteSequenceWrapper();
    private final ByteSequenceWrapper destination = new ByteSequenceWrapper();
    private final ByteSequenceWrapper currency = new ByteSequenceWrapper();
    private final ByteSequenceWrapper exchange = new ByteSequenceWrapper();
    private final ByteSequenceWrapper orderId = new ByteSequenceWrapper();
    private final ByteSequenceWrapper symbol = new ByteSequenceWrapper();
    private final ByteSequenceWrapper instrumentType = new ByteSequenceWrapper();

    public void decode(MessageParser parser) {
        ByteSequenceWrapper account = null;
        ByteSequenceWrapper destination = null;
        ByteSequenceWrapper currency = null;
        ByteSequenceWrapper exchange = null;
        ByteSequenceWrapper orderId = null;
        ByteSequenceWrapper symbol = null;
        ByteSequenceWrapper instrumentType = null;

        long expireTime = FieldUtil.LONG_NULL;
        long displayQuantity = FieldUtil.LONG_NULL;
        long limitPrice = FieldUtil.LONG_NULL;
        long minQuantity = FieldUtil.LONG_NULL;
        long quantity = FieldUtil.LONG_NULL;
        long stopPrice = FieldUtil.LONG_NULL;
        long timestamp = FieldUtil.LONG_NULL;

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
                    destination = this.destination;
                    parser.parseByteSequence(destination);
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
                    instrumentType = this.instrumentType;
                    parser.parseByteSequence(instrumentType);
                    break;

                case Tag.MaxShow:
                    displayQuantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.TransactTime:
                    timestamp = parser.parseTimestamp();
                    break;

                default:
                    parser.parseValue();
            }
        }

        request.setAccount(account);
        request.setDestination(destination);
        request.setCurrency(currency);
        request.setExchange(exchange);
        request.setOrderId(orderId);
        request.setSymbol(symbol);

        request.setExpireTime(expireTime);
        request.setDisplayQuantity(displayQuantity);
        request.setLimitPrice(limitPrice);
        request.setMinQuantity(minQuantity);
        request.setQuantity(quantity);
        request.setStopPrice(stopPrice);

        request.setInstrumentType(instrumentType);
        request.setOrderType(orderType);
        request.setSide(side);
        request.setTimeInForce(timeInForce);
        request.setTimestamp(timestamp);
    }

}
