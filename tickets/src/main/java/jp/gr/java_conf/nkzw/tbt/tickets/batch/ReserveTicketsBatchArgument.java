package jp.gr.java_conf.nkzw.tbt.tickets.batch;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;

public class ReserveTicketsBatchArgument {
    @Parameter(names = { "--help", "-h" }, arity = 0, description = "print this message", help = true)
    private Boolean help;

    @Parameter(names = { "--endpoint", "-c"}, //
            arity = 1, //
            description = "endpoint for tsurugidb", //
            required = false)
    private String endpoint = System.getProperty("TG_ENDPOINT", "tcp://localhost:12345");

    @Parameter(names = { "--timeout" }, //
            arity = 1, //
            description = "session time out", //
            required = false)
    private long timeout = 300L;

    @Parameter(names = { "--threadSize" }, //
            arity = 1, //
            description = "thiread size", //
            required = false)
    private int threadSize = 1;

    @Parameter(names = { "--function", "-f" }, //
            arity = 1, //
            description = "function: prepare, assign, show", //
            required = false)
    private String function = "show";

    /* 1タスクの申込数 */
    @Parameter(names = { "--applicationPerTask" }, //
            arity = 1, //
            description = "application num per task", //
            required = false)
    private int applicationPerTask = 1;

    /* 列数、席数 */
    @Parameter(names = { "--rowSeat" }, //
            arity = 2, //
            description = "row seat num: [row] [seat] default 10 10", //
            required = false)
    private List<Integer> rowSeat = Arrays.asList(10, 10);

    public boolean isHelp() {
        return (this.help != null) && this.help;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public int getThreadSize() {
        return this.threadSize;
    }

    public String getFunction() {
        return this.function;
    }

    public int getApplicationPerTask() {
        return this.applicationPerTask;
    }

    public List<Integer> getRowSeat() {
        return this.rowSeat;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setRowSeat(List<Integer> rowSeat) {
        this.rowSeat = rowSeat;
    }
}
