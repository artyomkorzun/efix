package org.efix.engine;

import org.efix.util.Command;

public class CloseCommand implements Command<SessionProcessor> {

    public static final CloseCommand INSTANCE = new CloseCommand();

    @Override
    public void execute(SessionProcessor processor) {
        processor.processCloseCommand(this);
    }

}
