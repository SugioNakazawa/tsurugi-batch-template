package jp.gr.java_conf.nkzw.tbt.tickets.batch;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;

public class ReserveTicketsBatchArgument {
    @Parameter(names = { "--help", "-h" }, arity = 0, description = "print this message", help = true)
    private Boolean help;

    public boolean isHelp() {
        return (this.help != null) && this.help;
    }

    @Parameter(names = { "--endpoint" }, //
            arity = 1, //
            description = "endpoint for tsurugidb", //
            required = false)
    private String endpoint = System.getProperty("TG_ENDPOINT", "tcp://localhost:12345");

    public String getEndpoint() {
        return this.endpoint;
    }

    @Parameter(names = { "--timeout" }, //
            arity = 1, //
            description = "session time out", //
            required = false)
    private long timeout = 300L;

    public long getTimeout() {
        return this.timeout;
    }

    @Parameter(names = { "--threadSize" }, //
            arity = 1, //
            description = "thiread size", //
            required = false)
    private int threadSize = 8;

    public int getThreadSize() {
        return this.threadSize;
    }
    @Parameter(names = { "--function","-f" }, //
            arity = 1, //
            description = "function: prepare, assign, show", //
            required = false)
    private String function = "prepare";
    public String getFunction() {
        return this.function;
    }
    /* 1タスクの申込数 */
    @Parameter(names = { "--applicationPerTask" }, //
            arity = 1, //
            description = "application num per task", //
            required = false)
    private int applicationPerTask = 1;
    public int getApplicationPerTask() {
        return this.applicationPerTask;
    }
    /* 列数、席数 */
    @Parameter(names = { "--rowSheet" }, //
            arity = 2, //
            description = "row sheet num: [row] [sheet] default 10 10", //
            required = false)
    private List<Integer> rowShhet = Arrays.asList(10, 10);
    public  List<Integer> getRowSheet() {
        return this.rowShhet;
    }
}
