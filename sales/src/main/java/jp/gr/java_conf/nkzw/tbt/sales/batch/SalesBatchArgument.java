package jp.gr.java_conf.nkzw.tbt.sales.batch;

import com.beust.jcommander.Parameter;

public class SalesBatchArgument {
    // help
    @Parameter(names = { "--help", "-h" }, arity = 0, description = "print this message", help = true)
    private Boolean help;

    public boolean isHelp() {
        return (this.help != null) && this.help;
    }
    // endpoint
    @Parameter(names = { "--endpoint" }, //
            arity = 1, //
            description = "endpoint for tsurugidb", //
            required = false)
    private String endpoint = System.getProperty("TG_ENDPOINT", "tcp://localhost:12345");

    public String getEndpoint() {
        return this.endpoint;
    }
    // timeout
    @Parameter(names = { "--timeout" }, //
            arity = 1, //
            description = "session time out", //
            required = false)
    private long timeout = 300L;

    public long getTimeout() {
        return this.timeout;
    }
    // threadsize
    @Parameter(names = { "--threadsize" }, //
            arity = 1, //
            description = "thread size", //
            required = false)
    private int threadSize = 8;

    public int getThreadSize() {
        return this.threadSize;
    }
    // tasknum
    @Parameter(names = { "--tasknum" }, //
            arity = 1, //
            description = "number of task", //
            required = false)
    private int taskNum = 4;
    public int getTaskNum() {
        return this.taskNum;
    }
    // per task
    @Parameter(names = { "--pertask" }, //
            arity = 1, //
            description = "insert count per task", //
            required = false)
    private int perTask = 3;
    public int getPerTask() {
        return this.perTask;
    }
    // mode
    @Parameter(names = { "--mode", "-m" }, //
            arity = 1, //
            description = "mode: (insert|update)", //
            required = false)
    private String mode = "insert";

    public String getMode() {
        return this.mode;
    }
}