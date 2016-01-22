package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionIDBean;
import org.f1x.log.FileMessageLog;
import org.f1x.log.layout.TimeLayout;
import org.f1x.util.Command;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Scanner;

public class CloseCommand implements Command<SessionProcessor> {

    public static final CloseCommand INSTANCE = new CloseCommand();

    @Override
    public void execute(SessionProcessor processor) {
        processor.processCloseCommand(this);
    }

    public static void main(String[] args) {
        SessionContext context = new SessionContext(
                false,
                new InetSocketAddress(25000),
                new SessionIDBean("TTDEV14O", "DELTIX"),
                FIXVersion.FIX42
        ).log(new FileMessageLog(1 << 22, Paths.get("D:/f1x-log.messages"), new TimeLayout()));

        SingleSessionEngine engine = new SingleSessionEngine(context, new SessionProcessor(context) {

            @Override
            protected void onError(Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }

        });
        engine.start();

        System.out.println("Close?");
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        engine.close();
    }

}
