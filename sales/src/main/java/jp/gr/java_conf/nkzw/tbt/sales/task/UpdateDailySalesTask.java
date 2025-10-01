package jp.gr.java_conf.nkzw.tbt.sales.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.sql.result.TsurugiStatementResult;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;

import jp.gr.java_conf.nkzw.tbt.sales.batch.SalesBatch;
import jp.gr.java_conf.nkzw.tbt.sales.dao.SalesDao;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;

public class UpdateDailySalesTask implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDailySalesTask.class);

    private static final int YEAR = 2025;
    private static final int MONTH = 9;
    private static final int DAY = 15;

    private TsurugiManager tsurugiManager;
    private long min;
    private long max;

    public UpdateDailySalesTask(TsurugiManager tsurugiManager, long min, long max) {
        this.tsurugiManager = tsurugiManager;
        this.min = min;
        this.max = max;
    }

    @Override
    public Void call() throws Exception {

        var session = tsurugiManager.createSession();
        try {
            var txOption = TgTxOption.ofOCC().label(
                    UpdateDailySalesTask.class.getSimpleName());
            tsurugiManager.execute(session, txOption, this::execute);
        } catch (Exception e) {
            LOG.error("error min={}, max={}", this.min, this.max, e);
            throw e;
        } finally {
            tsurugiManager.returnSession(session);
        }

        return null;
    }

    private void execute(PsCacheSession session, TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {

        // LOG.info("start min={}, max={}", this.min, this.max);

        var salesDao = new SalesDao(session);
        var list = salesDao.selectSalesDetail(transaction, this.min, this.max);
        var result1List = new ArrayList<TsurugiStatementResult>(10000);

        try {
            for (var detail : list) {
                switch (SalesBatch.PLAN) {
                    case 1:
                        salesDao.updateDailySales(transaction, detail.getItemId(), YEAR, MONTH, DAY,
                                detail.getSalesQty(),
                                detail.getSalesAmount());
                        break;
                    case 2:
                        salesDao.updateDailySales2(transaction, detail.getItemId(), YEAR, MONTH, DAY,
                                detail.getSalesQty(),
                                detail.getSalesAmount());
                        break;
                    default:
                        break;
                }
            }
        } finally {
            if (SalesBatch.PLAN == 2) {
                for (var r : result1List) {
                    try {
                        r.close();
                    } catch (Exception e) {
                        LOG.error("error closing result1", e);
                    }
                }
            }
        }
    }
}
