package org.efix;

import org.efix.util.Disposable;

public interface SessionComponent extends Disposable {

    void flush();

}
