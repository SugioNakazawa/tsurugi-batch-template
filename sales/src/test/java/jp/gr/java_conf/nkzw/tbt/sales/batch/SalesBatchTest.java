package jp.gr.java_conf.nkzw.tbt.sales.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jp.gr.java_conf.nkzw.tbt.tools.ExcelLoader;

public class SalesBatchTest {

    private static final String END_POINT = "tcp://localhost:12345";

    private static ExcelLoader excelLoader = new ExcelLoader(END_POINT);

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        // テーブル生成
        var daily_sales = Paths.get("src/test/resources/sql/create_daily_sales.sql");
        excelLoader.execDdls(Files.readString(daily_sales));
        var sales_detail = Paths.get("src/test/resources/sql/create_sales_detail.sql");
        excelLoader.execDdls(Files.readString(sales_detail));
    }

    @Test
    void testMainHelp() {
        // 標準出力を確認
        var originalOut = System.out;
        var output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        String[] args = { "--help" };
        try {
            SalesBatch.main(args);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }

        System.setOut(originalOut);
        var result = output.toString();
        assertTrue(result.contains("Usage: jp.gr.java_conf.nkzw.tbt.sales.batch.SalesBatch [options]"));
    }

    @Test
    void testMainInsert() {
        try {
            String[] args_insert = { "--mode", "insert" };
            SalesBatch.main(args_insert);
            assertEquals(12, excelLoader.recordCount("daily_sales"));
            assertEquals(12, excelLoader.recordCount("sales_detail"));

            String[] args_update = { "--mode", "update" };
            SalesBatch.main(args_update);
            assertEquals(
                2,
                excelLoader.executeSql("SELECT sales_qty FROM daily_sales WHERE item_id = 1").get(0).getInt("sales_qty"));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }
}
