package jp.gr.java_conf.nkzw.tbt.sales.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableBigDecimal;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableInteger;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariable.TgBindVariableLong;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.sql.result.TsurugiStatementResult;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;

import jp.gr.java_conf.nkzw.tbt.sales.dao.entity.DailySales;
import jp.gr.java_conf.nkzw.tbt.sales.dao.entity.SalesDetail;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.PsCacheSession;
import jp.gr.java_conf.nkzw.tbt.tools.common.dao.TsurugiDao;

public class SalesDao extends TsurugiDao {

    public SalesDao(PsCacheSession session) {
        super(session);
    }

    // select sales_detail
    private static final TgBindVariableLong MIN = TgBindVariable.ofLong("min");
    private static final TgBindVariableLong MAX = TgBindVariable.ofLong("max");
    private final QueryCache<TgBindParameters, SalesDetail> selectSalesDetail = new QueryCache<>(session -> {
        String sql = """
                SELECT * FROM sales_detail
                WHERE item_id >= :min AND item_id <= :max
                """;
        var parameterMapping = TgParameterMapping.of(MIN, MAX);
        var resultMapping = SalesDetail.RESULT_MAPPING;
        return session.createQuery(sql, parameterMapping, resultMapping);
    });

    public List<SalesDetail> selectSalesDetail(TsurugiTransaction transaction, Long min, Long max)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = selectSalesDetail.get();
        var parameterMapping = TgBindParameters.of(MIN.bind(min), MAX.bind(max));
        return transaction.executeAndGetList(ps, parameterMapping);
    }

    // insert sales_detail
    private final StatementCache<SalesDetail> insertSalesDetail = new StatementCache<>(session -> {
        var sql = "INSERT INTO sales_detail VALUES(" + SalesDetail.toValuesName() + ")";
        var parameterMapping = SalesDetail.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public int insertSalesDetail(TsurugiTransaction transaction, SalesDetail entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertSalesDetail.get();
        return transaction.executeAndGetCount(ps, entity);
    }
    public TsurugiStatementResult insertSalesDetail2(TsurugiTransaction transaction, SalesDetail entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertSalesDetail.get();
        return transaction.executeStatement(ps, entity);
    }

    // insert daily_sales
    private final StatementCache<DailySales> insertDailySales = new StatementCache<>(session -> {
        var sql = "INSERT INTO daily_sales VALUES(" + DailySales.toValuesName() + ")";
        var parameterMapping = DailySales.PARAMETER_MAPPING;
        return session.createStatement(sql, parameterMapping);
    });

    public int insertDailySales(TsurugiTransaction transaction, DailySales entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertDailySales.get();
        return transaction.executeAndGetCount(ps, entity);
    }
    // insert daily_sales 2
    public TsurugiStatementResult insertDailySales2(TsurugiTransaction transaction, DailySales entity)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = insertDailySales.get();
        return transaction.executeStatement(ps, entity);
    }

    // update daily_sales
    private static final TgBindVariableLong ITEM_ID = TgBindVariable.ofLong("item_id");
    private static final TgBindVariableInteger YEAR = TgBindVariable.ofInt("year");
    private static final TgBindVariableInteger MONTH = TgBindVariable.ofInt("month");
    private static final TgBindVariableInteger DAY = TgBindVariable.ofInt("day");
    private static final TgBindVariableInteger QTY = TgBindVariable.ofInt("qty");
    private static final TgBindVariableBigDecimal AMOUNT = TgBindVariable.ofDecimal("amount");
    private final StatementCache<TgBindParameters> updateDailySales = new StatementCache<>(session -> {
        String sql = """
                UPDATE daily_sales set sales_qty = sales_qty + :qty, sales_amount = sales_amount + :amount
                WHERE item_id = :item_id AND sales_year = :year AND sales_month = :month AND sales_day = :day
                """;
        var parameterMapping = TgParameterMapping.of(ITEM_ID, YEAR, MONTH, DAY, QTY, AMOUNT);
        return session.createStatement(sql, parameterMapping);
    });

    public int updateDailySales(TsurugiTransaction transaction, Long itemId, int year, int month, int day, int qty, BigDecimal amount)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = updateDailySales.get();
        var parameterMapping = TgBindParameters.of(ITEM_ID.bind(itemId), YEAR.bind(year), MONTH.bind(month), DAY.bind(day), QTY.bind(qty), AMOUNT.bind(amount));
        return transaction.executeAndGetCount(ps, parameterMapping);
    }

    public TsurugiStatementResult updateDailySales2(TsurugiTransaction transaction, Long itemId, int year, int month, int day, int qty, BigDecimal amount)
            throws IOException, InterruptedException, TsurugiTransactionException {
        var ps = updateDailySales.get();
        var parameterMapping = TgBindParameters.of(ITEM_ID.bind(itemId), YEAR.bind(year), MONTH.bind(month), DAY.bind(day), QTY.bind(qty), AMOUNT.bind(amount));
        return transaction.executeStatement(ps, parameterMapping);
    }

}
