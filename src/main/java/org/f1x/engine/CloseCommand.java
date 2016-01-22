package org.f1x.engine;

import org.f1x.util.Command;

public class CloseCommand implements Command<SessionProcessor> {

    public static final CloseCommand INSTANCE = new CloseCommand();

    @Override
    public void execute(SessionProcessor processor) {
        processor.processCloseCommand(this);
    }

}
