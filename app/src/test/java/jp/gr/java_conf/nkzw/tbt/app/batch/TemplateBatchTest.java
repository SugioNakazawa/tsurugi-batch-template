package jp.gr.java_conf.nkzw.tbt.app.batch;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.gr.java_conf.nkzw.tbt.tools.ExcelLoader;

public class TemplateBatchTest {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateBatchTest.class);
    private static final String ENDPOINT = "tcp://localhost:12345";

    @BeforeAll
    static void setUp() throws Exception {
        var execDdl = new ExcelLoader(ENDPOINT);
        try {
            execDdl.execDdls(Files.readString(Paths.get(
                    "src/test/resources/data/jp/gr/java_conf/nkzw/tbt/tools/TgData/sql/create_sample_table.sql")));
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

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
