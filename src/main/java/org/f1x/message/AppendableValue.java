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
package org.f1x.message;

import org.f1x.util.buffer.Buffer;

public interface AppendableValue extends Appendable {

    AppendableValue append(CharSequence csq);

    AppendableValue append(CharSequence csq, int start, int end);

    AppendableValue append(char c);

    AppendableValue append(byte c);

    AppendableValue append(int value);

    AppendableValue append(int value, int minLength);

    AppendableValue append(long value);

    AppendableValue append(double value);

    AppendableValue append(Buffer buffer);

    AppendableValue append(Buffer buffer, int offset, int length);

    AppendableValue appendTimestamp(long timestamp);

    /**
     * Appends FIX tag separator (ASCII SOH character).
     */
    void end();
}
