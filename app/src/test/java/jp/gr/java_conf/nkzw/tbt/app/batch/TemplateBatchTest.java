package jp.gr.java_conf.nkzw.tbt.app.batch;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateBatchTest {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateBatchTest.class);

    @Test
    void testMain() {

        try {
            TemplateBatch.main(new String[] {
                    "--endpoint", "tcp://localhost:12345",
                    "--timeout", "300",
                    "--threadsize", "8" });
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testMainHelp() {

        try {
            TemplateBatch.main(new String[] { "--help" });
        } catch (Throwable e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            fail();
        }
    }
}
