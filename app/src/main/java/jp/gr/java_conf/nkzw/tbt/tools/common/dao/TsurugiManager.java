package jp.gr.java_conf.nkzw.tbt.tools.common.dao;

import java.io.Closeable;
import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.formula.functions.T;

import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.session.TgSessionOption;
import com.tsurugidb.iceaxe.session.TgSessionOption.TgTimeoutKey;
import com.tsurugidb.iceaxe.transaction.TsurugiTransaction;
import com.tsurugidb.iceaxe.transaction.exception.TsurugiTransactionException;
import com.tsurugidb.iceaxe.transaction.function.TsurugiTransactionAction;
import com.tsurugidb.iceaxe.transaction.function.TsurugiTransactionTask;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;
import com.tsurugidb.tsubakuro.channel.common.connection.UsernamePasswordCredential;

public class TsurugiManager implements Closeable {

    private final TsurugiConnector connector;
    private final TgSessionOption sessionOption;
    private final Map<PsCacheSession, Object> sessions = new ConcurrentHashMap<>();
    private final Deque<PsCacheSession> sessionCacheQueue = new ConcurrentLinkedDeque<>();

    /**
     * コンストラクター。
     * 
     * @param endPoint
     * @param user
     * @param password
     * @param sessionTimeOut
     * @param applicationName
     */
    public TsurugiManager(String endPoint, String user, String password, long sessionTimeOut, String applicationName) {

        this.connector = TsurugiConnector.of(endPoint, new UsernamePasswordCredential(user, password))
                .setApplicationName(applicationName);
        this.sessionOption = TgSessionOption.of()
                .setTimeout(
                        TgTimeoutKey.DEFAULT,
                        sessionTimeOut,
                        TimeUnit.MINUTES);
    }
    public TsurugiManager() {
        this.connector = TsurugiConnector.of("tcp://localhost:12345", new UsernamePasswordCredential("user", "password"))
                .setApplicationName("TsurugiManager");
        this.sessionOption = TgSessionOption.of()
                .setTimeout(
                        TgTimeoutKey.DEFAULT,
                        300,
                        TimeUnit.MINUTES);
    }

    public PsCacheSession createSession() throws IOException {
        var session = sessionCacheQueue.pollFirst();
        if (session == null) {
            var s = connector.createSession(sessionOption);
            session = new PsCacheSession(this, s);
            sessions.put(session, Boolean.TRUE);
        }
        return session;
    }

    public void returnSession(PsCacheSession session) {
        sessionCacheQueue.addFirst(session);
    }

    public void releaseSession(PsCacheSession session) {
        sessions.remove(session);
    }

    // execute

    @FunctionalInterface
    public interface PsCacheAction {
        public void execute(PsCacheSession session, TsurugiTransaction transaction)
                throws IOException, InterruptedException, TsurugiTransactionException;
    }

    public void executeOcc(String label, PsCacheSession session, PsCacheAction action)
            throws IOException, InterruptedException {
        var txOption = TgTxOption.ofOCC().label(label);
        execute(session, txOption, action);
    }

    public void execute(PsCacheSession session, TgTxOption txOption, PsCacheAction action)
            throws IOException, InterruptedException {
        var setting = TgTmSetting.ofAlways(txOption);
        var tm = session.createTransactionManager(setting);
        tm.execute((TsurugiTransactionAction) tx -> action.execute(session, tx));
    }

    @FunctionalInterface
    public interface PsCacheTask<R> {
        public R execute(PsCacheSession session, TsurugiTransaction transaction)
                throws IOException, InterruptedException, TsurugiTransactionException;
    }

    public <R> R executeOcc(String label, PsCacheSession session, PsCacheTask<R> action)
            throws IOException, InterruptedException {
        var txOption = TgTxOption.ofOCC().label(label);
        return execute(session, txOption, action);
    }

    public <R> R execute(PsCacheSession session, TgTxOption txOption, PsCacheTask<R> action)
            throws IOException, InterruptedException {
        var setting = TgTmSetting.ofAlways(txOption);
        var tm = session.createTransactionManager(setting);
        return tm.execute((TsurugiTransactionTask<R>) tx -> action.execute(session, tx));
    }

    // close

    @Override
    public void close() throws IOException {
        IOException save = null;
        var list = List.copyOf(sessions.keySet());
        for (var session : list) {
            try {
                session.close();
            } catch (IOException e) {
                if (save == null) {
                    save = e;
                } else {
                    save.addSuppressed(e);
                }
            } catch (Exception e) {
                if (save == null) {
                    save = new IOException(e);
                } else {
                    save.addSuppressed(e);
                }
            }
        }
        if (save != null) {
            throw save;
        }
    }
}
