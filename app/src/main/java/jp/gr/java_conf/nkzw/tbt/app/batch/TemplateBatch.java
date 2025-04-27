package jp.gr.java_conf.nkzw.tbt.app.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import jp.gr.java_conf.nkzw.tbt.app.batch.dao.SampleTableDao;
import jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity.SampleTable;
import jp.gr.java_conf.nkzw.tbt.app.task.MyTask;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;
import jp.gr.java_conf.nkzw.tbt.tools.common.util.FutureUtil;

public class TemplateBatch {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateBatch.class);

    private TsurugiManager tsurugiManager;
    private final TemplateBatchConfig batchConfig;

    public static void main(String[] args) {
        LOG.info("TemplateBatch started");
        // パラメータのパース
        var argument = new TemplateBatchArgument();
        var commander = JCommander.newBuilder().programName(TemplateBatch.class.getName()).addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }
        var batchConfig = new TemplateBatchConfig(argument);
        new TemplateBatch(batchConfig).main();

    }

    public TemplateBatch(TemplateBatchConfig batchConfig) {
        this.batchConfig = batchConfig;
    }

    private void main() {

        LOG.info("TemplateBatch main start");
        long start = System.currentTimeMillis();

        try (var tsurugiManager = new TsurugiManager()) {
            this.tsurugiManager = tsurugiManager;

            prepareProcess();
            LOG.info("TemplateBatch main prepareProcess exucuted");

            executeProcess();
            LOG.info("TemplateBatch main executeProcess exucuted");

            postProcess();
            LOG.info("TemplateBatch main postProcess exucuted");
        } catch (Exception e) {
            LOG.error("原価再計算バッチ実行時に例外が発生し異常終了しました。");
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis();
            long executeTime = TimeUnit.MILLISECONDS.toMillis(end - start);
            LOG.info("end {} {}[ms]", TemplateBatch.class.getSimpleName(), executeTime);
            LOG.info("TemplateBatch main finished");
        }
    }

    private void prepareProcess() throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var dao = new SampleTableDao(session);
            tsurugiManager.executeOcc("prepareProcess", session, (s, transaction) -> {
                int count = dao.deleteSampleTable(transaction, 100);
                LOG.info("delete SampleTable num={}", count);
            });
        }
    }

    private void executeProcess() throws IOException, InterruptedException {
        var taskList = new ArrayList<MyTask>(10);

        // 繰り返し処理のキー情報を取得
        var keyList = getTaskKeys(0);
        for (var key : keyList) {
            taskList.add(new MyTask(this.tsurugiManager, key));
        }
        LOG.info("task num={}",taskList.size());
        // タスクの実行
        FutureUtil.execute(taskList, 20);
    }

    private List<SampleTable> getTaskKeys(int targetOver) throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var list = getSampleTableList(session, targetOver);
            if (list.size() < 1) {
                LOG.info("対象が存在しません。");
            }
            return list;
        }
    }

    private List<SampleTable> getSampleTableList(PsCacheSession session, long min)
            throws IOException, InterruptedException {
        List<SampleTable> list = new ArrayList<>();

        var dao = new SampleTableDao(session);

        list = this.tsurugiManager.executeOcc(
                getClass().getSimpleName() + "getSampleTableList",
                session, (s, transaction) -> {
                    return dao.selectSampleTable(transaction, min);
                });
        return list;
    }

    private void postProcess() {
    }
}
