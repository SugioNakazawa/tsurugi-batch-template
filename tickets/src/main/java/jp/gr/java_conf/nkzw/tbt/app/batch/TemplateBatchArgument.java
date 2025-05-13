package jp.gr.java_conf.nkzw.tbt.app.batch;

import com.beust.jcommander.Parameter;

public class TemplateBatchArgument {
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

    @Parameter(names = { "--threadsize" }, //
            arity = 1, //
            description = "thiread size", //
            required = false)
    private int threadSize = 8;

    public int getThreadSize() {
        return this.threadSize;
    }
}
