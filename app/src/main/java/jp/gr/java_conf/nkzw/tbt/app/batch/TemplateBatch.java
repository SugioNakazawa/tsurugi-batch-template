package jp.gr.java_conf.nkzw.tbt.app.batch;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
    static private final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.now();

    private TsurugiManager tsurugiManager;
    private TemplateBatchArgument argument;

    public static void main(String[] args) throws Throwable {
        LOG.info("TemplateBatch started");
        // パラメータのパース
        var argument = new TemplateBatchArgument();
        var commander = JCommander.newBuilder()
                .programName(TemplateBatch.class.getName())
                .addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }
        new TemplateBatch(argument).main();
    }

    public TemplateBatch(TemplateBatchArgument argument) {
        this.argument = argument;
    }

    private void main() throws Throwable {

        LOG.info("TemplateBatch main start");
        long start = System.currentTimeMillis();
        try (var tsurugiManager = new TsurugiManager(argument.getEndpoint(), argument.getTimeout())) {
            this.tsurugiManager = tsurugiManager;

            prepareProcess();
            LOG.info("TemplateBatch main prepareProcess exucuted");

            executeProcess();
            LOG.info("TemplateBatch main executeProcess exucuted");

            postProcess();
            LOG.info("TemplateBatch main postProcess exucuted");
        } catch (Exception e) {
            LOG.error("バッチ実行時に例外が発生し異常終了しました。");
            e.printStackTrace();
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            long executeTime = TimeUnit.MILLISECONDS.toMillis(end - start);
            LOG.info("end {} {}[ms]", TemplateBatch.class.getSimpleName(), executeTime);
            LOG.info("TemplateBatch main finished");
        }
    }

    /**
     * 処理の準備を行う
     * SampleTableのデータを作成する。
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private void prepareProcess() throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var dao = new SampleTableDao(session);
            tsurugiManager.executeOcc("prepareProcess", session, (s, transaction) -> {
                dao.deleteSampleTable(transaction, 0);
            });
            tsurugiManager.executeOcc("prepareProcess", session, (s, transaction) -> {
                int inserted = 0;
                for (int i = 0; i < 10; i++) {
                    var sampleTable = createSampleTable(i + 1);
                    inserted += dao.insertSampleTable(transaction, sampleTable);
                }
                LOG.info("delete SampleTable num={}", inserted);
            });
        }
    }

    private SampleTable createSampleTable(int i) {
        var sampleTable = new SampleTable();
        sampleTable.setIntCol1(i);
        sampleTable.setBigintCol2(i);
        sampleTable.setRealCol3(i);
        sampleTable.setDoubleCol4(i);
        sampleTable.setDecimalCol5(new BigDecimal(i));
        sampleTable.setDecimalCol6(new BigDecimal(i));
        sampleTable.setCharCol7(String.valueOf(i));
        sampleTable.setCharacterCol8(String.valueOf(i));
        sampleTable.setVarcharCol9(String.valueOf(i));
        sampleTable.setCharVaryingCol10(String.valueOf(i));
        sampleTable.setCharacterVaryingCol11(String.valueOf(i));
        sampleTable.setBinaryCol12(new byte[] { (byte) i });
        sampleTable.setVarbinaryCol13(new byte[] { (byte) i });
        sampleTable.setBinaryVaryingCol14(new byte[] { (byte) i });
        sampleTable.setDateCol15(OFFSET_DATE_TIME_NOW.toLocalDate());
        sampleTable.setTimeCol16(OFFSET_DATE_TIME_NOW.toLocalTime());
        sampleTable.setTimestampCol17(OFFSET_DATE_TIME_NOW.toLocalDateTime());
        sampleTable.setTimestampWithTimeZoneCol18(OFFSET_DATE_TIME_NOW);
        return sampleTable;
    }

    private void executeProcess() throws IOException, InterruptedException {
        var taskList = new ArrayList<MyTask>(10);

        // 繰り返し処理のキー情報を取得
        var keyList = getSsampleTables(0);
        for (var key : keyList) {
            taskList.add(new MyTask(this.tsurugiManager, key));
        }
        LOG.info("task num={}", taskList.size());
        // タスクの実行
        FutureUtil.execute(taskList, argument.getThreadSize());
    }

    private List<SampleTable> getSsampleTables(int targetStart) throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var list = getSampleTableList(session, targetStart);
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

    private void postProcess() throws IOException, InterruptedException {
        // try (var session = tsurugiManager.createSession()) {
        // var dao = new SampleTableDao(session);
        // tsurugiManager.executeOcc("prepareProcess", session, (s, transaction) -> {
        // int count = dao.deleteSampleTable(transaction, 100);
        // LOG.info("delete SampleTable num={}", count);
        // });
        // }
    }
}
