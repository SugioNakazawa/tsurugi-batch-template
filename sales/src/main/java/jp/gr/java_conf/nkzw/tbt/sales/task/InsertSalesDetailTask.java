package jp.gr.java_conf.nkzw.tbt.sales.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;

import jp.gr.java_conf.nkzw.tbt.sales.dao.SalesDao;
import jp.gr.java_conf.nkzw.tbt.sales.dao.entity.DailySales;
import jp.gr.java_conf.nkzw.tbt.sales.dao.entity.SalesDetail;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;

public class InsertSalesDetailTask implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(InsertSalesDetailTask.class);

    private static final int YEAR = 2025;
    private static final int MONTH = 9;
    private static final int DAY = 15;
    private static final int HOUR = 12;
    private static final int MINUTE = 30;

    private TsurugiManager tsurugiManager;
    private int start_num;
    private int number;

    public InsertSalesDetailTask(TsurugiManager tsurugiManager, int start_num, int number) {
        this.tsurugiManager = tsurugiManager;
        this.start_num = start_num;
        this.number = number;
    }

    @Override
    public Void call() throws Exception {

        var session = tsurugiManager.createSession();
        try {
            var txOption = TgTxOption.ofOCC().label(
                    InsertSalesDetailTask.class.getSimpleName());
            tsurugiManager.execute(session, txOption, this::execute);
        } catch (Exception e) {
            LOG.error("error start={}", this.start_num, e);
            throw e;
        } finally {
            tsurugiManager.returnSession(session);
        }

        return null;
    }

    private void execute(PsCacheSession session, TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {
        LOG.info("start start={}, number={}", this.start_num, this.number);

        var salesDao = new SalesDao(session);

        for (int i = 0; i < this.number; i++) {
            var id = (long) this.start_num + i;

            var daily = createDailySales(id);
            salesDao.insertDailySales(transaction, daily);

            var detail = createSalesDetail(this.start_num + i);
            salesDao.insertSalesDetail(transaction, detail);
        }
    }

    private SalesDetail createSalesDetail(long id) {
        var detail = new SalesDetail();
        detail.setItemId(id);
        detail.setSalesYear(YEAR);
        detail.setSalesMonth(MONTH);
        detail.setSalesDay(DAY);
        detail.setSalesHour(HOUR);
        detail.setSalesMinute(MINUTE);
        detail.setSalesQty(1);
        detail.setSalesAmount(BigDecimal.ONE.multiply(BigDecimal.valueOf(1000)));
        detail.setCreatedAt(LocalDateTime.now());
        return detail;
    }

    private DailySales createDailySales(long id) {
        var daily = new DailySales();
        daily.setItemId(id);
        daily.setSalesYear(YEAR);
        daily.setSalesMonth(MONTH);
        daily.setSalesDay(DAY);
        daily.setSalesQty(1);
        daily.setSalesAmount(BigDecimal.ONE.multiply(BigDecimal.valueOf(1000)));
        daily.setCreatedAt(LocalDateTime.now());
        return daily;
    }
}
