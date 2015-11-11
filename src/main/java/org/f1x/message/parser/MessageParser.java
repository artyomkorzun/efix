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

package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

/**
 * Not thread safe
 */
public interface MessageParser {

    boolean next();

    int getTagNum();

    byte getByteValue();

    int getIntValue();

    long getLongValue();

    double getDoubleValue();

    CharSequence getCharSequenceValue();

    void getByteSequence(ByteSequence seq);

    String getStringValue();

    void getStringBuilder(StringBuilder appendable);

    boolean getBooleanValue();

    long getUTCTimestampValue();

    int getUTCTimeOnly();

    long getUTCDateOnly();

    long getLocalMktDate();

    int getLocalMktDate2();

    boolean isValueEquals(byte[] constant);

    int fieldOffset();

    int fieldLength();

    MessageParser wrap(Buffer buffer, int offset, int length);

    MessageParser reset();

}


