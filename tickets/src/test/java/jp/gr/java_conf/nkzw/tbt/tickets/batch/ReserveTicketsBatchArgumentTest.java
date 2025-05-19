package jp.gr.java_conf.nkzw.tbt.tickets.batch;

import org.junit.jupiter.api.Test;

public class ReserveTicketsBatchArgumentTest {
    @Test
    void testSetEndpoint() {
        ReserveTicketsBatchArgument argument = new ReserveTicketsBatchArgument();
        argument.setEndpoint("tcp://localhost:12345");
        assert "tcp://localhost:12345".equals(argument.getEndpoint());
    }

    @Test
    void testSetThreadSize() {
        ReserveTicketsBatchArgument argument = new ReserveTicketsBatchArgument();
        argument.setThreadSize(5);
        assert 5 == argument.getThreadSize();
    }

    @Test
    void testSetTimeout() {
        ReserveTicketsBatchArgument argument = new ReserveTicketsBatchArgument();
        argument.setTimeout(1000L);
        assert 1000L == argument.getTimeout();
    }
}
