package jp.gr.java_conf.nkzw.tbt.sales.batch;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import jp.gr.java_conf.nkzw.tbt.sales.task.InsertSalesDetailTask;
import jp.gr.java_conf.nkzw.tbt.sales.task.UpdateDailySalesTask;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;
import jp.gr.java_conf.nkzw.tbt.tools.common.util.FutureUtil;

public class SalesBatch {

    public static final int PLAN = 2; // 1:executeGetAndCount, 2:executeStatement
    private static final Logger LOG = LoggerFactory.getLogger(SalesBatch.class);

    private TsurugiManager tsurugiManager;
    private SalesBatchArgument argument;

    public SalesBatch(SalesBatchArgument argument) {
        this.argument = argument;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        LOG.info("SalesBatch started");
        long start = System.currentTimeMillis();

        // パラメータ
        var argument = new SalesBatchArgument();
        var commander = JCommander.newBuilder()
                .programName(SalesBatch.class.getName())
                .addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }
        var salesBatch = new SalesBatch(argument);

        switch (argument.getMode()) {
            case "insert":
                salesBatch.execute_insert();
                break;
            case "update":
                salesBatch.execute_update();
                break;
            default:
                LOG.error("unknown mode:{}", argument.getMode());
                break;
        }

        LOG.info("SalesBatch finished. time:{}ms", (System.currentTimeMillis() - start));
    }

    private void execute_update() throws IOException, InterruptedException {
        try (var tsurugiManager = new TsurugiManager(
                this.argument.getEndpoint(),
                this.argument.getTimeout())) {
            this.tsurugiManager = tsurugiManager;
            // prepare task
            var taskList = new ArrayList<UpdateDailySalesTask>(
                    this.argument.getTaskNum() - 1);
            int perTask = this.argument.getPerTask();
            for (int i = 0; i < this.argument.getTaskNum(); i++) {
                taskList.add(new UpdateDailySalesTask(
                        this.tsurugiManager,
                        (i * perTask + 1),
                        ((i + 1) * perTask)));
            }
            FutureUtil.execute(taskList, this.argument.getThreadSize());
        } finally {
        }
    }

    private void execute_insert() throws IOException, InterruptedException {

        try (var tsurugiManager = new TsurugiManager(
                this.argument.getEndpoint(),
                this.argument.getTimeout())) {
            this.tsurugiManager = tsurugiManager;
            // prepare task
            var taskList = new ArrayList<InsertSalesDetailTask>(
                    this.argument.getTaskNum());
            int perTask = this.argument.getPerTask();
            for (int i = 0; i < this.argument.getTaskNum(); i++) {
                taskList.add(new InsertSalesDetailTask(
                        this.tsurugiManager,
                        i * perTask + 1, perTask));
            }
            FutureUtil.execute(taskList, this.argument.getThreadSize());
        } finally {
        }
    }
}