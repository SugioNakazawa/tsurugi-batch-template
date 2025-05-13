package jp.gr.java_conf.nkzw.tbt.tools.common.dao;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.sql.TsurugiSqlPreparedQuery;
import com.tsurugidb.iceaxe.sql.TsurugiSqlPreparedStatement;

public abstract class TsurugiDao {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final PsCacheSession session;

    public TsurugiDao(PsCacheSession session) {
        this.session = session;
    }

    @FunctionalInterface
    protected interface QuerySupplier<P, R> {
        TsurugiSqlPreparedQuery<P, R> createQuery(PsCacheSession session) throws IOException, InterruptedException;
    }

    @FunctionalInterface
    protected interface GetParameterMappingSupplier<P> {
        TsurugiSqlPreparedStatement<P> createStatement(PsCacheSession session) throws IOException, InterruptedException;
    }

    protected class QueryCache<P, R> {
        private final QuerySupplier<P, R> supplier;
        private TsurugiSqlPreparedQuery<P, R> ps; // psのクローズはTsurugiSession.close()に任せる

        public QueryCache(QuerySupplier<P, R> supplier) {
            this.supplier = supplier;
        }

        public TsurugiSqlPreparedQuery<P, R> get() throws IOException, InterruptedException {
            if (this.ps == null) {
                this.ps = supplier.createQuery(session);
            }
            return ps;
        }

    }

    protected class StatementCache<P> {
        private final GetParameterMappingSupplier<P> supplier;
        private TsurugiSqlPreparedStatement<P> ps; // psのクローズはTsurugiSession.close()に任せる

        public StatementCache(GetParameterMappingSupplier<P> supplier) {
            this.supplier = supplier;
        }

        public TsurugiSqlPreparedStatement<P> get() throws IOException, InterruptedException {
            if (this.ps == null) {
                this.ps = supplier.createStatement(session);
            }
            return ps;
        }

    }

    protected <P, R> void logExplain(TsurugiSqlPreparedQuery<P, R> ps, P parameter) {
        try {
            var explain = ps.explain(parameter);
            var planGraph = explain.getLowPlanGraph();
            logger.debug("explain sql={}, parameter={}\n, {}", ps.getSql(), parameter, planGraph);
        } catch (Exception e) {
            logger.debug("explain error. ps={}, parameter={}", ps, parameter, e);
        }
    }

}
