package jp.gr.java_conf.nkzw.tbt.app.batch.dao;

import java.io.IOException;
import java.util.List;

import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableInteger;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableLong;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariables;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;

import jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity.SampleTable;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiDao;

public class SampleTableDao extends TsurugiDao {

    public SampleTableDao(PsCacheSession session) {
        super(session);
    }

    // select + from sample_table where int_col1 >= :min;
    private static final TgBindVariableLong MIN = TgBindVariable.ofLong("min");

    private final QueryCache<TgBindParameters, SampleTable> selectSampleTable = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM sample_table
                WHERE int_col1 >= :min
                """;
        var parameterMapping = TgParameterMapping.of(MIN);
        var resultMapping = SampleTable.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<SampleTable> selectSampleTable(TsurugiTransaction transaction, long min)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectSampleTable.get();
        var parameterMapping = TgBindParameters.of(MIN.bind(min));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // insert into sample_table values(int_col1. ...);
    private final StatementCache<SampleTable> insertSampleTable = new StatementCache<>(session -> {
        var sql = "INSERT INTO sample_table VALUES(" + SampleTable.toValuesName() + ")";
        var parameterMapping = SampleTable.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public int insertSampleTable(TsurugiTransaction transaction, SampleTable entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertSampleTable.get();
        return transaction.executeAndGetCount(ps, entity);
    }


    // delete from sample_table where int_col1 > value;
    private static final TgBindVariableInteger INT_COL1 = TgBindVariable.ofInt("int_col1");

    public int deleteSampleTable(TsurugiTransaction transaction, int int_col1)
            throws IOException, InterruptedException, TsurugiTransactionException {

        var sql = "delete from sample_table where int_col1 > :int_col1";
        var variables = TgBindVariables.of(INT_COL1);
        var parameter = TgBindParameters.of(INT_COL1.bind(int_col1));
        var parameterMapping = TgParameterMapping.of(variables);
        var session = transaction.getSession();
        try (var ps = session.createStatement(sql, parameterMapping)) {
            return transaction.executeAndGetCount(ps, parameter);
        } catch (InterruptedException e) {
            throw e;
        }
    }
}
