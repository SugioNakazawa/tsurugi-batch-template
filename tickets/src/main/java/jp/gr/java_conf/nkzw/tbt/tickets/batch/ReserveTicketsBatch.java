package jp.gr.java_conf.nkzw.tbt.tickets.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.SampleTableDao;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.TicketsDao;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Applications;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.SampleTable;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Seats;
import jp.gr.java_conf.nkzw.tbt.tickets.task.AllocTask;
import jp.gr.java_conf.nkzw.tbt.tickets.task.MyTask;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiManager;
import jp.gr.java_conf.nkzw.tbt.tools.common.util.FutureUtil;

public class ReserveTicketsBatch {

    private static final Logger LOG = LoggerFactory.getLogger(ReserveTicketsBatch.class);

    private TsurugiManager tsurugiManager;
    private ReserveTicketsBatchArgument argument;

    public static void main(String[] args) {
        String[] defaultArgs = {
                "-f", "assign",
                "--rowSheet", "40", "40",
                "--threadSize", "3",
                // "--applicationPerTask", "10",
        };
        args = args.length == 0 ? defaultArgs : args;
        // パラメータのパース
        var argument = new ReserveTicketsBatchArgument();
        var commander = JCommander.newBuilder()
                .programName(ReserveTicketsBatch.class.getName())
                .addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }
        new ReserveTicketsBatch(argument).run();
    }

    public ReserveTicketsBatch(ReserveTicketsBatchArgument argument) {
        this.argument = argument;
        this.tsurugiManager = new TsurugiManager(argument.getEndpoint(), argument.getTimeout());
    }

    public void run() {

        LOG.info("run start row={} seat={} threadSize={} aoolicationPerTask={}",
                argument.getRowSheet().get(0),
                argument.getRowSheet().get(1),
                argument.getThreadSize(),
                argument.getApplicationPerTask());

        long start = System.currentTimeMillis();
        try {
            switch (argument.getFunction()) {
                case "prepare":
                    prepare(argument.getRowSheet().get(0), argument.getRowSheet().get(1));
                    break;
                case "assign":
                    prepare(argument.getRowSheet().get(0), argument.getRowSheet().get(1));
                    allocSeats();
                    showSeats();
                    break;
                case "show":
                    show();
                    break;
                default:
                    LOG.error("不正な引数です。{}", argument.getFunction());
                    return;
            }
            postProcess();
            tsurugiManager.close();
        } catch (Exception e) {
            LOG.error("バッチ実行時に例外が発生し異常終了しました。");
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis();
            long executeTime = TimeUnit.MILLISECONDS.toMillis(end - start);
            LOG.info("end {} ms", executeTime);
            LOG.info("run finished");
        }
    }

    public void allocSeats() throws IOException, InterruptedException {
        LOG.info("allocSeats start");
        long start = System.currentTimeMillis();
        // 実行タスクを作成
        var taskList = generateTask();
        LOG.info("task num={}", taskList.size());
        // タスクの実行
        FutureUtil.execute(taskList, argument.getThreadSize());

        long end = System.currentTimeMillis();
        long executeTime = TimeUnit.MILLISECONDS.toMillis(end - start);
        LOG.info("allocSeats {} ms", executeTime);

    }

    private List<AllocTask> generateTask() throws IOException, InterruptedException {
        // 全ての申込を取得してタスクに振り分ける
        List<Applications> applications = getAllApplications();
        if (applications.size() < 1) {
            LOG.info("対象が存在しません。");
            return null;
        }
        // タスクはコミット単位
        List<AllocTask> taskList = new ArrayList<>();
        // 各タスクが空席検索を開始する列のずれ
        int deltaRow = argument.getRowSheet().get(0) / argument.getThreadSize() / 2;
        deltaRow = deltaRow < 1 ? 1 : deltaRow;
        LOG.info("deltaRow={}", deltaRow);
        int startRow = 1;
        // 1タスクには指定数分の申込を入れる。
        int maxRow = argument.getRowSheet().get(0);
        var task = new AllocTask(
                this.tsurugiManager,
                startRow,
                argument.getRowSheet().get(0));
        for (int i = 0; i < applications.size(); i++) {
            task.addApplication(applications.get(i));
            // 申込数が指定数に達したら次のタスクを生成
            if (task.getApplications().size() >= argument.getApplicationPerTask()) {
                // タスクの実行
                taskList.add(task);
                // patter 1
                startRow = startRow + deltaRow;
                startRow = startRow > maxRow ? 1 : startRow;
                // patter 2
                // startRow = (startRow + 1) > maxRow ? 1 : (startRow + 1);
                task = new AllocTask(this.tsurugiManager, startRow, argument.getRowSheet().get(0));
            }
        }
        // 最後のタスクを追加
        if (task.getApplications().size() > 0) {
            taskList.add(task);
        }
        return taskList;
    }

    private List<AllocTask> generateTask2() throws IOException, InterruptedException {
        // 全ての申込を取得してタスクに振り分ける
        List<Applications> applications = getAllApplications();
        if (applications.size() < 1) {
            LOG.info("対象が存在しません。");
            return null;
        }
        // タスクはコミット単位
        List<AllocTask> taskList = new ArrayList<>();
        // 各タスクが空席検索を開始する列のずれ
        int deltaRow = argument.getRowSheet().get(0) / argument.getThreadSize();
        deltaRow = deltaRow < 1 ? 1 : deltaRow;
        LOG.info("deltaRow={}", deltaRow);
        int startRow = 1;
        // 1タスクには指定数分の申込を入れる。
        int maxRow = argument.getRowSheet().get(0);
        var task = new AllocTask(
                this.tsurugiManager,
                startRow++,
                argument.getRowSheet().get(0));
        for (int i = 0; i < applications.size(); i++) {
            task.addApplication(applications.get(i));
            // 申込数が指定数に達したら次のタスクを生成
            if (task.getApplications().size() >= argument.getApplicationPerTask()) {
                // タスクの実行
                taskList.add(task);
                // patter 1
                // startRow = startRow + deltaRow;
                // startRow = startRow > maxRow ? 1 : startRow;
                // patter 2
                // startRow = (startRow + 1) > maxRow ? 1 : (startRow + 1);
                task = new AllocTask(this.tsurugiManager, startRow++, argument.getRowSheet().get(0));
            }
        }
        // 最後のタスクを追加
        if (task.getApplications().size() > 0) {
            taskList.add(task);
        }
        return taskList;
    }

    private void show() throws IOException, InterruptedException {
        showSeats();
        showApplications();
    }

    private void showSeats() throws IOException, InterruptedException {
        LOG.info("show start");
        List<Seats> seats = getAllSeats();
        if (seats.size() < 1) {
            LOG.info("対象が存在しません。");
        } else {
            StringBuilder sb = new StringBuilder("Assigned Seats\n");
            for (int i = 0; i < argument.getRowSheet().get(0); i++) {
                for (int j = 0; j < argument.getRowSheet().get(1); j++) {
                    var entity = find(seats, i + 1, j + 1);
                    sb.append(String.format("%4d ", entity.getAssignedApplicationId()));
                }
                sb.append("\n");
            }
            LOG.info("show seats\n {}", sb.toString());
        }
    }

    private void showApplications() throws IOException, InterruptedException {

        List<Applications> applications = getAllApplications();
        if (applications.size() < 1) {
            LOG.info("対象が存在しません。");
        } else {
            StringBuilder sb = new StringBuilder("Application List\n");
            for (var entity : applications) {
                sb.append("id:").append(String.format("%4d", entity.getId()));
                sb.append(" num:").append(entity.getApplyNum());
                sb.append(" assigned:").append(entity.getAssignedFlag()).append("\n");
            }
            LOG.info("show application\n {}", sb.toString());
        }
        LOG.info("show end");
    }

    private Seats find(List<Seats> seats, int row, int seat) {
        for (var entity : seats) {
            if (entity.getRowNo() == row && entity.getSeatNo() == seat) {
                return entity;
            }
        }
        return null;
    }

    public List<Seats> getAllSeats() throws IOException, InterruptedException {
        LOG.info("getAllSeats start");
        try (var session = tsurugiManager.createSession()) {
            var dao = new TicketsDao(session);
            return tsurugiManager.executeOcc("show", session, (s, transaction) -> {
                return dao.selectAllSeats(transaction);
            });
        }
    }

    private List<Applications> getAllApplications() throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var dao = new TicketsDao(session);
            return tsurugiManager.executeOcc("show", session, (s, transaction) -> {
                return dao.selectAllApplications(transaction);
            });
        }
    }

    public List<Applications> getPendingApplications() throws IOException, InterruptedException {
        LOG.info("getPendingApplications start");
        try (var session = tsurugiManager.createSession()) {
            var dao = new TicketsDao(session);
            return tsurugiManager.executeOcc("getPendingApplications", session, (s, transaction) -> {
                return dao.selectPendingApplications(transaction);
            });
        }
    }

    private void prepare(int row, int seat) throws IOException, InterruptedException {
        prepareSeats(row, seat);
        prepareApplications(row * seat, 4);
    }

    public void prepareApplications(int capacity, int maxQty) throws IOException, InterruptedException {
        try (var session = tsurugiManager.createSession()) {
            var dao = new TicketsDao(session);
            tsurugiManager.executeOcc("prepareApplications", session,
                    (TsurugiManager.PsCacheAction) (s, transaction) -> {
                        int count = dao.deleteAllApplications(transaction);
                        LOG.info("delete Appllications num={}", count);
                        // 申込作成
                        int id = 1;
                        int assigned = 0;
                        while (assigned < capacity) {
                            int qty = (id % maxQty) + 1;
                            if (assigned + qty > capacity) {
                                qty = capacity - assigned;
                            }
                            var entity = new Applications();
                            entity.setId(id++);
                            entity.setApplyNum(qty);
                            entity.setAssignedFlag(0);
                            entity.setSeatZone(0);
                            dao.insertApplications(transaction, entity);
                            assigned += qty;
                        }
                    });
        }
    }

    public void prepareSeats(int row, int seat) throws IOException, InterruptedException {
        LOG.info("prepare start row={} seat={}", row, seat);
        try (var session = tsurugiManager.createSession()) {
            var dao = new TicketsDao(session);
            // 削除
            tsurugiManager.executeOcc(this.getClass().getSimpleName() + "#prepare", session, (s, transaction) -> {
                int count = dao.deleteAllSeats(transaction);
                LOG.info("delete seats num={}", count);
            });
            // 座席作成
            tsurugiManager.executeOcc(this.getClass().getSimpleName() + "prepare", session,
                    (TsurugiManager.PsCacheAction) (s, transaction) -> {
                        int id = 1;
                        for (int i = 0; i < row; i++) {
                            for (int j = 0; j < seat; j++) {
                                var entity = new Seats();
                                entity.setId(id++);
                                entity.setRowNo(i + 1);
                                entity.setSeatNo(j + 1);
                                entity.setSeatZone(0);
                                entity.setAssignedApplicationId(0);
                                dao.insertSeats(transaction, entity);
                            }
                        }
                    });
        } catch (Exception e) {
            LOG.error("prepare error", e);
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
        LOG.info("task num={}", taskList.size());
        // タスクの実行
        FutureUtil.execute(taskList, argument.getThreadSize());
    }

    private List<SampleTable> getTaskKeys(int targetStart) throws IOException, InterruptedException {
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

    private void postProcess() {
    }
}
