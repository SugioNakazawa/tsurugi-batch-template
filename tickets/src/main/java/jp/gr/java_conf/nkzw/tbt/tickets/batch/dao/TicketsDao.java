package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableInteger;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableLong;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;

import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Applications;
import jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity.Seats;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiDao;

public class TicketsDao extends TsurugiDao {
    private static final Logger LOG = LoggerFactory.getLogger(TicketsDao.class);

    public TicketsDao(PsCacheSession session) {
        super(session);
    }

    // select + from sample_table where int_col1 >= :min;
    private static final TgBindVariableInteger ROW = TgBindVariable.ofInt("row");
    private static final TgBindVariableInteger SEAT = TgBindVariable.ofInt("seat");
    private static final TgBindVariableLong ID = TgBindVariable.ofLong("id");
    private static final TgBindVariableLong ASSIGNED_APPLICATION_ID = TgBindVariable.ofLong("assigned_application_id");
    private static final TgBindVariableInteger ASSIGNED_FLAG = TgBindVariable.ofInt("assigned_flag");

    private final QueryCache<TgBindParameters, Seats> selectSeat = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM seats
                WHERE row_no = :row
                AND seat_no = :seat
                """;
        var parameterMapping = TgParameterMapping.of(ROW, SEAT);
        var resultMapping = Seats.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Seats> selectSeat(TsurugiTransaction transaction, int row, int seat)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectSeat.get();
        var parameterMapping = TgBindParameters.of(ROW.bind(row), SEAT.bind(seat));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // select * from setas;
    private final QueryCache<TgBindParameters, Seats> selectAllSeats = new QueryCache<>(session -> {
        String sql = "SELECT * FROM seats";
        var parameterMapping = TgParameterMapping.of();
        var resultMapping = Seats.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Seats> selectAllSeats(TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectAllSeats.get();
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

    // select * from Applications where assigned_flag = 0;
    private final QueryCache<TgBindParameters, Applications> selectPendingApplications = new QueryCache<>(session -> {
        String sql = "SELECT * FROM applications WHERE assigned_flag = 0";
        var parameterMapping = TgParameterMapping.of();
        var resultMapping = Applications.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Applications> selectPendingApplications(TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectPendingApplications.get();
        var parameterMapping = TgBindParameters.of();
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // insert into seats values(...);
    private final StatementCache<Seats> insertSeats = new StatementCache<>(session -> {
        var sql = "INSERT INTO seats VALUES(" + Seats.toValuesName() + ")";
        var parameterMapping = Seats.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public void insertSeats(TsurugiTransaction transaction, Seats entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertSeats.get();
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

    // delete from seats;
    public int deleteAllSeats(TsurugiTransaction transaction)
            throws IOException, InterruptedException, TsurugiTransactionException {

        var sql = "delete from seats";
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

    // select * from seats where row_no = :row;
    private final QueryCache<TgBindParameters, Seats> selectVacantSeats = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM seats
                WHERE row_no = :row
                    AND assigned_application_id = 0
                ORDER BY seat_no
                """;
        var parameterMapping = TgParameterMapping.of(ROW);
        var resultMapping = Seats.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<Seats> selectVacantSeats(TsurugiTransaction transaction, int row)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectVacantSeats.get();
        var parameterMapping = TgBindParameters.of(ROW.bind(row));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // update seats set assignedFlag = :assignedFlag where id = :id;
    private final StatementCache<TgBindParameters> updateSeatsAssignedFlag = new StatementCache<>(session -> {
        String sql = """
                UPDATE seats
                SET assigned_application_id = :assigned_application_id
                WHERE id = :id
                """;
        var parameterMapping = TgParameterMapping.of(ID, ASSIGNED_APPLICATION_ID);
        return session.createStatement(sql, parameterMapping);
    });

    public int updateSeatsAssignedFlag(TsurugiTransaction transaction, Seats seats)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = updateSeatsAssignedFlag.get();
        var parameterMapping = TgBindParameters.of(ID.bind(seats.getId()),
                ASSIGNED_APPLICATION_ID.bind(seats.getAssignedApplicationId()));
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
        LOG.debug("updateApplicationsAssignedFlag: id={}, assigned_flag={}", application.getId(),
                application.getAssignedFlag());
        return transaction.executeAndGetCount(ps, parameterMapping);
    }
}
