package jp.gr.java_conf.nkzw.tbt.tools.common.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.tsurugidb.iceaxe.session.TsurugiSession;
import com.tsurugidb.iceaxe.sql.TsurugiSqlPreparedQuery;
import com.tsurugidb.iceaxe.sql.TsurugiSqlPreparedStatement;
import com.tsurugidb.iceaxe.sql.TsurugiSqlQuery;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.manager.TsurugiTransactionManager;
import com.tsurugidb.iceaxe.util.InterruptedRuntimeException;

public class PsCacheSession implements AutoCloseable {

    private final TsurugiManager owner;
    private final TsurugiSession session;
    private final Map<String, TsurugiSqlPreparedQuery<?, ?>> queryMap = new ConcurrentHashMap<>();
    private final Map<String, TsurugiSqlPreparedStatement<?>> statementMap = new ConcurrentHashMap<>();

    public PsCacheSession(TsurugiManager tsurugiManager, TsurugiSession session) {
        this.owner = tsurugiManager;
        this.session = session;
    }

    public TsurugiTransactionManager createTransactionManager(TgTmSetting setting) {
        return session.createTransactionManager(setting);
    }

    public <P, R> TsurugiSqlPreparedQuery<P, R> createQuery(String sql, TgParameterMapping<P> parameterMapping, TgResultMapping<R> resultMapping) throws IOException, InterruptedException {
        try {
            @SuppressWarnings("unchecked")
            var query = (TsurugiSqlPreparedQuery<P, R>) queryMap.computeIfAbsent(sql, key -> {
                try {
                    return session.createQuery(key, parameterMapping, resultMapping);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (InterruptedException e) {
                    throw new InterruptedRuntimeException(e);
                }
            });
            return query;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        } catch (InterruptedRuntimeException e) {
            throw e.getCause();
        }
    }

    public <P> TsurugiSqlPreparedStatement<P> createStatement(String sql, TgParameterMapping<P> parameterMapping) throws IOException, InterruptedException {
        try {
            @SuppressWarnings("unchecked")
            var statement = (TsurugiSqlPreparedStatement<P>) statementMap.computeIfAbsent(sql, key -> {
                try {
                    return session.createStatement(key, parameterMapping);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } catch (InterruptedException e) {
                    throw new InterruptedRuntimeException(e);
                }
            });
            return statement;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        } catch (InterruptedRuntimeException e) {
            throw e.getCause();
        }
    }

    @Override
    public void close() throws IOException, InterruptedException {
        queryMap.clear();
        statementMap.clear();
        session.close();
        owner.releaseSession(this);
    }

	/**
	 * SQLクエリーを実行して、指定された結果マッピングに基づいて結果を返す。<br>
	 * 指定されたSQLクエリーを実行し、結果を {@link TgResultMapping} で定義されたオブジェクトにマッピングする。
	 * 
	 * @param <R> クエリーの結果がマッピングされるオブジェクトの型
	 * @param sql 実行するSQL文字列
	 * @param resultMapping 結果のマッピング情報
	 * @return SQLクエリーの実行結果
	 * @throws IOException 入出力エラーが発生したとき
	 * @throws NullPointerException パラメーターが {@code null} なとき
	 * @see TsurugiSqlQuery
	 * @see TgResultMapping
	 */
	public <R> TsurugiSqlQuery<R> createQuery(String sql, TgResultMapping<R> resultMapping) throws IOException, NullPointerException {
		Objects.requireNonNull(sql, "SQL statement must not be null");
		Objects.requireNonNull(resultMapping, "Result mapping must not be null");
		return session.createQuery(sql, resultMapping);
	}

}
