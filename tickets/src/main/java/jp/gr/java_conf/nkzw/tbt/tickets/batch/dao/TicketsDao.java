package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableInteger;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableLong;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariables;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;

import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Applications;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Sheets;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiDao;

public class TicketsDao extends TsurugiDao {
    private static final Logger LOG = LoggerFactory.getLogger(TicketsDao.class);

    public TicketsDao(PsCacheSession session) {
        super(session);
    }

    // select + from sample_table where int_col1 >= :min;
    private static final TgBindVariableInteger ROW = TgBindVariable.ofInt("row");
    private static final TgBindVariableInteger SHEET = TgBindVariable.ofInt("sheet");
    private static final TgBindVariableLong MIN = TgBindVariable.ofLong("min");
    private static final TgBindVariableLong ID = TgBindVariable.ofLong("id");
    private static final TgBindVariableLong ASSIGNED_APPLICATION_ID = TgBindVariable.ofLong("assigned_application_id");
    private static final TgBindVariableInteger ASSIGNED_FLAG = TgBindVariable.ofInt("assigned_flag");

    private final QueryCache<TgBindParameters, Sheets> selectSheet = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM sheets
                WHERE row_no = :row
                AND seat_no = :sheet
                """;
        var parameterMapping = TgParameterMapping.of(ROW, SHEET);
        var resultMapping = Sheets.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Sheets> selectSheet(TsurugiTransaction transaction, int row, int seat)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectSheet.get();
        var parameterMapping = TgBindParameters.of(ROW.bind(row), SHEET.bind(seat));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // select * from sheets;
    private final QueryCache<TgBindParameters, Sheets> selectAllSheets = new QueryCache<>(session -> {
        String sql = "SELECT * FROM sheets";
        var parameterMapping = TgParameterMapping.of();
        var resultMapping = Sheets.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });
    public List<Sheets> selectAllSheets(TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectAllSheets.get();
        var parameterMapping = TgBindParameters.of();
        return transaction.executeAndGetList(ps, parameterMapping);
    }
    // select * from Applications;
    private final QueryCache<TgBindParameters, Applications> selectAllApplications = new QueryCache<>(session -> {
        String sql = "SELECT * FROM applications";
        var parameterMapping = TgParameterMapping.of();
        var resultMapping = Applications.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });
    public List<Applications> selectAllApplications(TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectAllApplications.get();
        var parameterMapping = TgBindParameters.of();
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // insert into Sheets values(...);
    private final StatementCache<Sheets> insertSheets = new StatementCache<>(session -> {
        var sql = "INSERT INTO sheets VALUES(" + Sheets.toValuesName() + ")";
        var parameterMapping = Sheets.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public void insertSheets(TsurugiTransaction transaction, Sheets entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertSheets.get();
        transaction.executeAndGetCount(ps, entity);
    }
    // insert into Applications values(...);
    private final StatementCache<Applications> insertApplications = new StatementCache<>(session -> {
        var sql = "INSERT INTO applications VALUES(" + Applications.toValuesName() + ")";
        var parameterMapping = Applications.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public void insertApplications(TsurugiTransaction transaction, Applications entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertApplications.get();
        transaction.executeAndGetCount(ps, entity);
    }

    // delete from sheets;
    public int deleteAllSheets(TsurugiTransaction transaction)
        throws IOException, InterruptedException, TsurugiTransactionException {

        var sql = "delete from sheets";
        var session = transaction.getSession();
        try (var ps = session.createStatement(sql)) {
            return transaction.executeAndGetCount(ps);
        } catch (InterruptedException e) {
            throw e;
        }
    }
    // delete from applications;
    public int deleteAllApplications(TsurugiTransaction transaction)
        throws IOException, InterruptedException, TsurugiTransactionException {

        var sql = "delete from applications";
        var session = transaction.getSession();
        try (var ps = session.createStatement(sql)) {
            return transaction.executeAndGetCount(ps);
        } catch (InterruptedException e) {
            throw e;
        }
    }

    // TODO: 使う時に変更
    // delete from sample_table where int_col1 > value;
    private static final TgBindVariableInteger INT_COL1 = TgBindVariable.ofInt("int_col1");

    public int deleteSampleTable(TsurugiTransaction transaction, int int_col1)
            throws IOException, InterruptedException, TsurugiTransactionException {

        var sql = "delete from sample_table where int_col1 > :int_col1";
        var variables = TgBindVariables.of(INT_COL1);
        var parameterMapping = TgParameterMapping.of(variables);
        var parameter = TgBindParameters.of(INT_COL1.bind(int_col1));
        var session = transaction.getSession();
        try (var ps = session.createStatement(sql, parameterMapping)) {
            return transaction.executeAndGetCount(ps, parameter);
        } catch (InterruptedException e) {
            throw e;
        }
    }

    // select * from sheets where row_no = :row;
    private final QueryCache<TgBindParameters, Sheets> selectVacantSeats = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM sheets
                WHERE row_no = :row
                    AND assigned_application_id = 0
                """;
        var parameterMapping = TgParameterMapping.of(ROW);
        var resultMapping = Sheets.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Sheets> selectVacantSeats(TsurugiTransaction transaction, int row)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectVacantSeats.get();
        var parameterMapping = TgBindParameters.of(ROW.bind(row));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // update sheets set assignedFlag = :assignedFlag where id = :id;
    private final StatementCache<TgBindParameters> updateSheetsAssignedFlag = new StatementCache<>(session -> {
        String sql = """
                UPDATE sheets
                SET assigned_application_id = :assigned_application_id
                WHERE id = :id
                """;
        var parameterMapping = TgParameterMapping.of(ID, ASSIGNED_APPLICATION_ID);
        return session.createStatement(sql, parameterMapping);
    });

    public int updateSheetsAssignedFlag(TsurugiTransaction transaction, Sheets sheets)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = updateSheetsAssignedFlag.get();
        var parameterMapping = TgBindParameters.of(ID.bind(sheets.getId()),
                ASSIGNED_APPLICATION_ID.bind(sheets.getAssignedApplicationId()));
        return transaction.executeAndGetCount(ps, parameterMapping);
    }
    // update applications set assigned_flag = :assigned_flag where id = :id;
    private final StatementCache<TgBindParameters> updateApplicationsAssignedFlag = new StatementCache<>(session -> {
        String sql = """
                UPDATE applications
                SET assigned_flag = :assigned_flag
                WHERE id = :id
                """;
        var parameterMapping = TgParameterMapping.of(ID, ASSIGNED_FLAG);
        return session.createStatement(sql, parameterMapping);
    });

    public int updateApplicationsAssignedFlag(TsurugiTransaction transaction, Applications application)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = updateApplicationsAssignedFlag.get();
        var parameterMapping = TgBindParameters.of(
                ID.bind(application.getId()),
                ASSIGNED_FLAG.bind(application.getAssignedFlag()));
        LOG.debug("updateApplicationsAssignedFlag: id={}, assigned_flag={}", application.getId(), application.getAssignedFlag());
        return transaction.executeAndGetCount(ps, parameterMapping);
    }
}

