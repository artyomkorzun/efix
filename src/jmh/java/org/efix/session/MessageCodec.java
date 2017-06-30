package org.efix.session;

import org.efix.message.FieldUtil;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.Tag;
import org.efix.message.parser.MessageParser;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;


public class MessageCodec {

    private static final int SCALE = 6;

    private final org.efix.message.Message map = new org.efix.message.Message();
    private final MutableInt offset = new MutableInt();

    private final ByteSequenceWrapper account = new ByteSequenceWrapper();
    private final ByteSequenceWrapper broker = new ByteSequenceWrapper();
    private final ByteSequenceWrapper currency = new ByteSequenceWrapper();
    private final ByteSequenceWrapper exchange = new ByteSequenceWrapper();
    private final ByteSequenceWrapper orderId = new ByteSequenceWrapper();
    private final ByteSequenceWrapper symbol = new ByteSequenceWrapper();
    private final ByteSequenceWrapper securityType = new ByteSequenceWrapper();

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

          /*      case Tag.Currency:
                    currency = this.currency;
                    parser.parseByteSequence(currency);
                    break;*/

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
/*
                case Tag.StopPx:
                    stopPrice = parser.parseDecimal(SCALE);
                    break;*/

                case Tag.ExDestination:
                    exchange = this.exchange;
                    parser.parseByteSequence(exchange);
                    break;

             /*   case Tag.MinQty:
                    minQuantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.MaxFloor:
                    displayQuantity = parser.parseDecimal(SCALE);
                    break;*/

                case Tag.SecurityType:
                    securityType = this.securityType;
                    parser.parseByteSequence(securityType);
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

    public void decodeWithTable(Message message, MessageParser parser) {
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
/*
                case Tag.Currency:
                    currency = this.currency;
                    parser.parseByteSequence(currency);
                    break;*/

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

              /*  case Tag.ExpireTime:
                    expireTime = parser.parseTimestamp();
                    break;
*/
                case Tag.ExecBroker:
                    broker = this.broker;
                    parser.parseByteSequence(broker);
                    break;

              /*  case Tag.StopPx:
                    stopPrice = parser.parseDecimal(SCALE);
                    break;*/

                case Tag.ExDestination:
                    exchange = this.exchange;
                    parser.parseByteSequence(exchange);
                    break;

            /*    case Tag.MinQty:
                    minQuantity = parser.parseDecimal(SCALE);
                    break;

                case Tag.MaxFloor:
                    displayQuantity = parser.parseDecimal(SCALE);
                    break;*/

                case Tag.SecurityType:
                    securityType = this.securityType;
                    parser.parseByteSequence(securityType);
                    break;

                case Tag.TransactTime:
                    transactTime = parser.parseTimestamp();
                    break;

                case 0:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 12:
                case 13:
                case 14:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 39:
                case 41:
                case 42:
                case 43:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 56:
                case 57:
                case 58:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                case 121:
                case 122:
                case 123:
                case 124:
                case 125:
                case 127:
                case 128:
                case 129:
                case 130:
                case 131:
                case 132:
                case 133:
                case 134:
                case 135:
                case 136:
                case 137:
                case 138:
                case 139:
                case 140:
                case 141:
                case 142:
                case 143:
                case 144:
                case 145:
                case 146:
                case 147:
                case 148:
                case 149:
                case 150:
                case 151:
                case 152:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
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

    public void decodeWithPredefinedLayout(Message message, MessageParser parser) {
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

        ByteSequenceWrapper exchange = this.exchange;
        parser.parseTag();
        parser.parseByteSequence(exchange);

        ByteSequenceWrapper securityType = this.securityType;
        parser.parseTag();
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

    // TODO: null check
    public void decodeWithIndexMap(Message message, MessageParser parser) {
        org.efix.message.Message map = this.map;

        Buffer buffer = parser.buffer();
        int offset = parser.offset();
        int length = parser.remaining();
        map.parse(buffer, offset, length);

        decodeWithIndexMap(message, map);
    }

    public void decodeWithIndexMap(Message message, org.efix.message.Message map) {
        CharSequence account = map.getString(Tag.Account, this.account, null);
        CharSequence orderId = map.getString(Tag.ClOrdID, this.orderId, null);
        CharSequence symbol = map.getString(Tag.Symbol, this.symbol, null);
        CharSequence exchange = map.getString(Tag.ExDestination, this.exchange, null);
        CharSequence broker = map.getString(Tag.ExecBroker, this.broker, null);
        CharSequence securityType = map.getString(Tag.SecurityType, this.securityType, null);

        byte orderType = map.getByte(Tag.OrdType, (byte) 0);
        byte side = map.getByte(Tag.Side, (byte) 0);

        long quantity = map.getDecimal(Tag.OrderQty, SCALE, 0);
        long limitPrice = map.getDecimal(Tag.Price, SCALE, 0);

        byte timeInForce = map.getByte(Tag.TimeInForce, (byte) 0);
        long transactTime = map.getTimestamp(Tag.TransactTime, 0);

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
