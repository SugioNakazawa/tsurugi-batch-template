package jp.gr.java_conf.nkzw.tbt.app.task;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;

import jp.gr.java_conf.nkzw.tbt.app.batch.dao.SampleTableDao;
import jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity.SampleTable;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;

public class MyTask implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(MyTask.class);

    private TsurugiManager tsurugiManager;

    private SampleTable sampleTable;

    public MyTask(TsurugiManager tsurugiManager, SampleTable sampleTable) {
        this.tsurugiManager = tsurugiManager;
        this.sampleTable = sampleTable;
    }

    @Override
    public Void call() throws IOException, InterruptedException {
        var session = tsurugiManager.createSession();
        try {
            var txOption = TgTxOption.ofOCC().label(
                    MyTask.class.getSimpleName());
            tsurugiManager.execute(session, txOption, this::execute);
        } catch (Exception e) {
            LOG.error("error id={}", this.sampleTable.getIntCol1(), e);
            throw e;
        } finally {
            tsurugiManager.returnSession(session);
        }
        // 出力件数が多いため間引いて出力
        if ((sampleTable.getIntCol1() % 10 == 1) || (sampleTable.getIntCol1() < 10)) {
            LOG.info("end exec int_col1={}", sampleTable.getIntCol1());
        }
        return null;
    }

    private void execute(PsCacheSession session, TsurugiTransaction transactio)
            throws IOException, InterruptedException, TsurugiTransactionException {
        LOG.info("start int_col1={}", this.sampleTable.getIntCol1());

        var dao = new SampleTableDao(session);
        var target = this.sampleTable.clone();
        target.setIntCol1(target.getIntCol1() + 1000);
        dao.insertSampleTable(transactio, target);
    }

}