package jp.gr.java_conf.nkzw.tbt.tickets.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;

import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.TicketsDao;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Applications;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Sheets;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;

public class AllocTask implements Callable<Void> {
    private static final Logger LOG = LoggerFactory.getLogger(AllocTask.class);
    private TsurugiManager tsurugiManager;
    private List<Applications> applications;
    /* 空席検索を開始する列 */
    private int startRow;
    private int maxRow;

    public AllocTask(TsurugiManager tsurugiManager, int startRow, int maxRow) {
        // TODO: tsurugiManaderは引数にあるべき？
        this.tsurugiManager = tsurugiManager;
        this.startRow = startRow;
        this.maxRow = maxRow;
        this.applications = new ArrayList<>();
    }

    @Override
    public Void call() throws Exception {
        var session = tsurugiManager.createSession();
        try {
            var txOption = TgTxOption.ofOCC().label(
                    AllocTask.class.getSimpleName());
            tsurugiManager.execute(session, txOption, this::execute);
        } catch (Exception e) {
            LOG.error("error id={}", applications.get(0).getId(), e);
            throw e;
        } finally {
            tsurugiManager.returnSession(session);
        }

        return null;
    }

    public void addApplication(Applications application) {
        this.applications.add(application);
    }

    public List<Applications> getApplications() {
        return applications;
    }

    private void execute(PsCacheSession session, TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {

        var dao = new TicketsDao(session);
        // タスクが保持する申込を順に処理
        for (var application : applications) {
            LOG.debug("start appId:{} num:{}", application.getId(), application.getApplyNum());
            boolean isAssigned = false;
            var foundSheets = new ArrayList<Sheets>();
            for (int curRow = startRow; curRow < maxRow + 1; curRow++) {
                // 対象列の空席を検索
                var vacabtSeats = dao.selectVacantSeats(transaction, curRow);
                LOG.debug("vacantSeats row: {} seats [{}]", curRow,
                        String.join(",", vacabtSeats.stream().map(s -> String.valueOf(s.getSeatNo())).toList()));
                // 申込席数分の空席を確認
                if (vacabtSeats.size() >= application.getApplyNum()) {
                    // 連続席を検索
                    foundSheets = (ArrayList<Sheets>) searchVacantsRow(application, vacabtSeats);
                    // 連続席が見つかった場合
                    if (foundSheets.size() == application.getApplyNum()) {
                        isAssigned = true;
                        // 席を確保
                        application.setAssignedFlag(1);
                        dao.updateApplicationsAssignedFlag(transaction, application);
                        // 空席を更新
                        for (var sheet : foundSheets) {
                            sheet.setAssignedApplicationId(application.getId());
                            dao.updateSheetsAssignedFlag(transaction, sheet);
                        }
                        LOG.debug("reserved num:{} row:{} sheets:[{}]",
                                application.getApplyNum(),
                                curRow,
                                String.join(",", foundSheets.stream().map(s -> String.valueOf(s.getSeatNo())).toList()));
                        break; // 申込処理終了
                    }
                }
            }
            if(!isAssigned) {
                // 申込処理終了
                LOG.warn("not assigned row:{} appId:{} num:{}", startRow, application.getId(), application.getApplyNum());
            }
        }
        // TODO Auto-generated method stub
        LOG.debug("execute startRow: {} app.size: {} appIds: [{}]",
                startRow,
                applications.size(),
                String.join(",", applications.stream().map(a -> String.valueOf(a.getId())).toList()));
    }

    private List<Sheets> searchVacantsRow(Applications application, List<Sheets> vacabtSeats) {
        boolean isContinuous = true;
        var foundSheets = new ArrayList<Sheets>();
        for (int i = 0; i < vacabtSeats.size(); i++) {
            isContinuous = true;
            foundSheets.clear();
            foundSheets.add(vacabtSeats.get(i));
            for (int j = 1; j < application.getApplyNum(); j++) {
                if (vacabtSeats.get(i + j).getSeatNo() != vacabtSeats.get(i).getSeatNo() + j) {
                    // 連続していない
                    isContinuous = false;
                    break;
                } else {
                    foundSheets.add(vacabtSeats.get(i + j));
                }
            }
            if (isContinuous) {
                break; // 連続している席が見つかった
            }
        }
        return foundSheets;
    }
}
