package jp.gr.java_conf.nkzw.tbt.app.batch;

import org.junit.jupiter.api.Test;

public class TemplateBatchTest {
    @Test
    void testMain() {

        TemplateBatch.main(new String[] {
                "--endpoint", "tcp://localhost:12345",
                "--timeout", "300",
                "--threadsize", "8" });
    }

    @Test
    void testMainHelp() {

        TemplateBatch.main(new String[] { "--help" });
    }
}
