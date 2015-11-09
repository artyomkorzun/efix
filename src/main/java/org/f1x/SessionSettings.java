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
package org.f1x;


public class SessionSettings {

    protected FIXVersion fixVersion;
    protected SessionID sessionID;
    protected boolean initiator = true;
    protected int heartbeatInterval = 30;
    protected int heartbeatTimeout = 1000 * (heartbeatInterval + 1);
    protected boolean resetSeqNumsOnEachLogon;
    protected boolean logonWithNextExpectedSeqNum;

    public FIXVersion getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(FIXVersion fixVersion) {
        this.fixVersion = fixVersion;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTimeout = 1000 * (heartbeatInterval + 1);
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public boolean resetSeqNumsOnEachLogon() {
        return resetSeqNumsOnEachLogon;
    }

    public void resetSeqNumsOnEachLogon(boolean resetSeqNumsOnEachLogon) {
        this.resetSeqNumsOnEachLogon = resetSeqNumsOnEachLogon;
    }

    public boolean isLogonWithNextExpectedSeqNum() {
        return logonWithNextExpectedSeqNum;
    }

    public void setLogonWithNextExpectedSeqNum(boolean logonWithNextExpectedSeqNum) {
        this.logonWithNextExpectedSeqNum = logonWithNextExpectedSeqNum;
    }

}
