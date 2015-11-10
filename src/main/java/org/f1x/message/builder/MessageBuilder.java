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

package org.f1x.message.builder;

import org.f1x.message.AppendableValue;
import org.f1x.message.types.ByteEnum;
import org.f1x.message.types.IntEnum;
import org.f1x.message.types.StringEnum;
import org.f1x.util.ByteArrayReference;
import org.f1x.util.buffer.MutableBuffer;

public interface MessageBuilder extends AppendableValue {

    void add(int tag, CharSequence value);

    void add(int tag, CharSequence value, int start, int end);

    void add(int tag, long value);

    void add(int tag, int value);

    void add(int tag, double value);

    void add(int tag, double value, int precision);

    void add(int tag, double value, int precision, boolean roundUp);

    void add(int tag, byte value);

    void add(int tag, boolean value);

    void add(int tag, ByteEnum value);

    void add(int tag, IntEnum value);

    void add(int tag, StringEnum value);

    void addUTCTimestamp(int tag, long timestamp);

    void addUTCTimeOnly(int tag, long timestamp);

    void addUTCDateOnly(int tag, long timestamp);

    void addLocalMktDate(int tag, long timestamp);

    void addLocalMktDate2(int tag, int yyyymmdd);

    void addRaw(int tag, byte[] buffer, int offset, int length);

    void addRaw(int tag, ByteArrayReference bytes);

    int length();

    MessageBuilder clear();

    MessageBuilder wrap(MutableBuffer buffer, int offset, int length);

    MessageBuilder wrap(MutableBuffer buffer);

}
