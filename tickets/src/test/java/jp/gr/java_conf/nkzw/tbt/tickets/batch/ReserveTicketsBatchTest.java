package jp.gr.java_conf.nkzw.tbt.tickets.batch;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;
import com.tsurugidb.iceaxe.util.IceaxeConvertUtil;

import jp.gr.java_conf.nkzw.tbt.tools.ExcelLoader;

public class ReserveTicketsBatchTest {

    private static final Logger LOG = LoggerFactory.getLogger(ReserveTicketsBatchTest.class);
    private static final String ENDPOINT = "tcp://localhost:12345";

    private static TsurugiConnector conn;

    private ReserveTicketsBatch target;
    private ReserveTicketsBatchArgument argument;

    @BeforeAll
    static void setUp() throws Exception {
        conn = TsurugiConnector.of("tcp://localhost:12345");
        var execDdl = new ExcelLoader(ENDPOINT);
        try {
            execDdl.execDdls(Files.readString(Paths.get("src/main/resources/sql/create_applications.sql")));
            execDdl.execDdls(Files.readString(Paths.get("src/main/resources/sql/create_seats.sql")));
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testRunPrepare() throws IOException, InterruptedException {
        argument = new ReserveTicketsBatchArgument();
        argument.setFunction("prepare");
        argument.setRowSeat(Arrays.asList(4, 4));

        target = new ReserveTicketsBatch(argument);
        target.run();

        var pending = target.getPendingApplications();
        Assertions.assertEquals(7, pending.size());

        // Check if the batch was executed successfully
        var records = getRecords("SELECT COUNT(*) FROM applications");
        Assertions.assertEquals(7, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM applications WHERE assigned_flag = 0");
        Assertions.assertEquals(7, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats");
        Assertions.assertEquals(16, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats WHERE assigned_application_id = 0");
        Assertions.assertEquals(16, Integer.valueOf(records.get(0)));

    }

    @Test
    void testRunAssign() throws IOException, InterruptedException {
        argument = new ReserveTicketsBatchArgument();
        argument.setFunction("assign");
        argument.setRowSeat(Arrays.asList(5, 5));

        target = new ReserveTicketsBatch(argument);

        target.run();

        // Check if the batch was executed successfully
        var records = getRecords("SELECT COUNT(*) FROM applications");
        Assertions.assertEquals(10, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM applications WHERE assigned_flag = 0");
        Assertions.assertEquals(1, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats");
        Assertions.assertEquals(25, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats WHERE assigned_application_id = 0");
        Assertions.assertEquals(3, Integer.valueOf(records.get(0)));
    }

    @Test
    void testRunShow() throws IOException, InterruptedException {
        argument = new ReserveTicketsBatchArgument();
        argument.setFunction("show");
        argument.setRowSeat(Arrays.asList(5, 5));

        target = new ReserveTicketsBatch(argument);

        try {
            target.run();
        } catch (Exception e) {
            LOG.error("Error running batch: {}", e.getMessage(), e);
            fail();
        }
    }

    @Test
    void testMainHelp() {
        // エラーのないことを確認
        String[] args = { "-h" };
        try {
            ReserveTicketsBatch.main(args);
        } catch (Exception e) {
            LOG.error("Error running batch: {}", e.getMessage(), e);
            fail();
        }
    }

    @Test
    void testMain() throws IOException, InterruptedException {

        String[] args = {
                "-f", "assign",
                "--rowSeat", "10", "10",
                "--threadSize", "1",
                "--applicationPerTask", "10",
        };
        ReserveTicketsBatch.main(args);

        // Check if the batch was executed successfully
        var records = getRecords("SELECT COUNT(*) FROM applications");
        Assertions.assertEquals(40, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM applications WHERE assigned_flag = 0");
        Assertions.assertEquals(0, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats");
        Assertions.assertEquals(100, Integer.valueOf(records.get(0)));

        records = getRecords("SELECT COUNT(*) FROM seats WHERE assigned_application_id = 0");
        Assertions.assertEquals(0, Integer.valueOf(records.get(0)));
    }

    /**
     * SQLを実行して、結果を取得
     * 
     * @param sql
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private List<String> getRecords(String sql) throws IOException, InterruptedException {
        List<String> ret = List.of();
        try (var session = conn.createSession()) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofRTX());
            var tm = session.createTransactionManager(setting);
            var sb = new StringBuilder();
            var resultMapping = TgResultMapping.of(record -> {
                var convertUtil = IceaxeConvertUtil.INSTANCE;
                sb.setLength(0);
                while (record.moveCurrentColumnNext()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(convertUtil.toString(record.fetchCurrentColumnValue()));
                }
                return sb.toString();
            });
            try (var ps = session.createQuery(sql, resultMapping)) {
                ret = tm.executeAndGetList(ps);
            } catch (IOException | InterruptedException e) {
                LOG.error("Error executing SQL: {}", sql, e);
            }
        }
        return ret;
    }
}