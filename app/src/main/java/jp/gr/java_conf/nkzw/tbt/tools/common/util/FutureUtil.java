package jp.gr.java_conf.nkzw.tbt.tools.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureUtil {

    public static void execute(List<? extends Callable<?>> taskList, int threadSize)
            throws IOException, InterruptedException {
        var service = Executors.newFixedThreadPool(threadSize);

        try (var c0 = (Closeable) service::shutdownNow) {
            var futureList = new ArrayList<Future<?>>();
            for (var task : taskList) {
                var future = service.submit(task);
                futureList.add(future);
            }

            closeFuture(futureList);
        }
    }

    public static void closeFuture(List<? extends Future<?>> futureList) throws IOException {
        IOException save = null;

        for (var future : futureList) {
            try {
                future.get();
            } catch (ExecutionException e) {
                var c = e.getCause();
                if (save == null) {
                    if (c instanceof IOException ie) {
                        save = ie;
                    } else {
                        save = new IOException(c);
                    }
                } else {
                    save.addSuppressed(c);
                }
            } catch (Exception e) {
                if (save == null) {
                    save = new IOException(e);
                } else {
                    save.addSuppressed(e);
                }
            }
        }

        if (save != null) {
            throw save;
        }
    }
}
