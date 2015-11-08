package org.f1x.log;

import org.f1x.SessionComponent;

public interface MessageLog extends SessionComponent {

    void log(boolean isInbound);

}
