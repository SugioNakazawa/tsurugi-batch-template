package jp.gr.java_conf.nkzw.tbt.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TgDataTest {
    @Test
    void testSample() throws IOException {
        // Test the main method of TgData
        String[] args = {
                "--excel",
                "src/test/resources/jp/gr/java_conf/nkzw/tbt/tools/TgData/table_design.xlsx",
                "--sheet", "サンプル", // シート:部門 のみを対象
                "--javapackage", "jp.gr.java_conf.nkzw.tbt.tools.TgData", // Javaソースのパッケージ名を指定
                "--out", "out",
                "--ddl", // DDLを出力
                "--silent" // 生成されたtーえぶる情報を出力しない
        };

        try {
            TgData.main(args);
            // check Java entity source
            {
                var actualFile = "out/jp/gr/java_conf/nkzw/tbt/tools/TgData/SampleTable.java";
                var expectFile = "src/test/resources/jp/gr/java_conf/nkzw/tbt/tools/TgData/SampleTable.java";
                assertTrue(
                        Arrays.equals(
                                Files.readAllBytes(Paths.get(actualFile)),
                                Files.readAllBytes(Paths.get(expectFile))));
            }
            // check DDL
            {
                var actualFile = "out/sql/create_sample_table.sql";
                var expectFile = "src/test/resources/jp/gr/java_conf/nkzw/tbt/tools/TgData/sql/create_sample_table.sql";
                assertTrue(
                        Arrays.equals(
                                Files.readAllBytes(Paths.get(actualFile)),
                                Files.readAllBytes(Paths.get(expectFile))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void createTickets() {
        // Ticketsの定義を生成
        String[] args = {
                "--excel",
                "src/test/resources/jp/gr/java_conf/nkzw/tbt/tools/TgData/tickets_design.xlsx",
                // "--sheet", "サンプル", // シート:部門 のみを対象
                "--javapackage", "jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity", // Javaソースのパッケージ名を指定
                "--out", "out",
                "--ddl", // DDLを出力
                // "--silent", // 生成されたtーえぶる情報を出力しない
                "--createtable", // DDL テーブル作成
                "--generatedata", // DML テストデータ生成
                "--datacount", "100", // テストデータ件数
        };

        try {
            TgData.main(args);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
