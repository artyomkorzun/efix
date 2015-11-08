/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.FixTags;
import org.f1x.store.MessageStore;
import org.f1x.util.AsciiUtils;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.format.IntFormatter;
import org.f1x.util.format.TimestampFormatter;

import java.io.IOException;

/**
 * Assembles message using header information and MessageBuilder. Not thread safe.
 * The following header fields are used:
 * <ul>
 * <li>BodyLength</li>
 * <li>MsgType</li>
 * <li>MsgSeqNum</li>
 * <li>SenderCompID,SenderSubID</li>
 * <li>TargetCompID,TargetSubID</li>
 * <li>SendingTime</li>
 * </ul>
 */
final class RawMessageAssembler {

    private final static byte SOH = 1;

    private final TimestampFormatter timestampFormatter = TimestampFormatter.createUTCTimestampFormatter();  //TODO Reuse instance kept by MessageBuilder?
    private final Buffer beginString;
    private final MutableBuffer buffer;

    RawMessageAssembler(FIXVersion version, int maxMessageSize) {
        buffer = new UnsafeBuffer(new byte[maxMessageSize]);
        beginString = new UnsafeBuffer(AsciiUtils.getBytes("" + FixTags.BeginString + '=' + version.getBeginString() + (char) SOH));
        buffer.putBytes(0, beginString, 0, beginString.capacity());
    }

    void send(SessionID sessionID, int msgSeqNum, MessageBuilder messageBuilder, MessageStore messageStore, long sendingTime, OutputChannel out) throws IOException {
        if (out == null)
            throw new IllegalStateException("Not connected");

        int offset = beginString.capacity();

        final CharSequence msgType = messageBuilder.getMessageType();
        final CharSequence senderSubId = sessionID.getSenderSubId();
        final CharSequence targetSubId = sessionID.getTargetSubId();

        // BodyLength is the number of characters in the message following the BodyLength field up to, and including, the delimiter immediately preceding the CheckSum tag ("10=")
        int bodyLength = (4 + msgType.length()) +
                (4 + IntFormatter.stringSize(msgSeqNum)) +
                (4 + TimestampFormatter.DATE_TIME_LENGTH) +
                (4 + sessionID.getSenderCompId().length()) +   // T O D O: Pre-compute and keep in session ID?
                (4 + sessionID.getTargetCompId().length()) +
                messageBuilder.getLength();

        if (senderSubId != null)
            bodyLength += 4 + senderSubId.length();

        if (targetSubId != null)
            bodyLength += 4 + targetSubId.length();

        // Standard Header tags
        offset = setIntField(FixTags.BodyLength, bodyLength, buffer, offset);
        offset = setTextField(FixTags.MsgType, msgType, buffer, offset);
        offset = setIntField(FixTags.MsgSeqNum, msgSeqNum, buffer, offset);
        offset = setTextField(FixTags.SenderCompID, sessionID.getSenderCompId(), buffer, offset);
        if (senderSubId != null)
            offset = setTextField(FixTags.SenderSubID, senderSubId, buffer, offset);
        offset = setUtcTimestampField(FixTags.SendingTime, sendingTime, buffer, offset);
        offset = setTextField(FixTags.TargetCompID, sessionID.getTargetCompId(), buffer, offset);
        if (targetSubId != null)
            offset = setTextField(FixTags.TargetSubID, targetSubId, buffer, offset);

        // Message-specific and custom tags
        offset = messageBuilder.output(buffer, offset);

        // Standard footer
        int checkSum = Tools.calcCheckSum(buffer, offset);  //T O D O: Let MessageBuilder accumulate payload checksum as we build each message
        offset = set3DigitIntField(FixTags.CheckSum, checkSum, buffer, offset);

        try {
            out.write(buffer, 0, offset);
        } finally {
            if (messageStore != null)
                messageStore.write(msgSeqNum, buffer.byteArray(), 0, offset);
        }
    }

    private static int setTextField(int tagNo, CharSequence value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        buffer.putByte(offset++, (byte) '=');
        for (int i = 0; i < value.length(); i++)
            buffer.putByte(offset++, (byte) value.charAt(i));

        buffer.putByte(offset++, SOH);
        return offset;
    }

    private static int setIntField(int tagNo, int value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        buffer.putByte(offset++, (byte) '=');
        offset = IntFormatter.format(value, buffer, offset);
        buffer.putByte(offset++, SOH);
        return offset;
    }

    private static int set3DigitIntField(int tagNo, int value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        buffer.putByte(offset++, (byte) '=');
        offset = IntFormatter.format3digits(value, buffer, offset);
        buffer.putByte(offset++, SOH);
        return offset;
    }

    private int setUtcTimestampField(int tagNo, long value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        buffer.putByte(offset++, (byte) '=');
        offset = timestampFormatter.formatDateTime(value, buffer, offset);
        buffer.putByte(offset++, SOH);
        return offset;
    }

}
