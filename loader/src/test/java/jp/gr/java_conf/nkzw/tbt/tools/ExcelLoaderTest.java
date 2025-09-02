package jp.gr.java_conf.nkzw.tbt.tools;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.EncryptedDocumentException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExcelLoaderTest {
  private static final String ENDPOINT = "tcp://localhost:12345";
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeAll
  static void setUp() throws Exception {
    var execDdl = new ExcelLoader(ENDPOINT);
    try {
      execDdl.execDdls(Files.readString(Paths.get("src/test/resources/sql/create_sample_table1.sql")));
      execDdl.execDdls(Files.readString(Paths.get("src/test/resources/sql/create_sample_table2.sql")));
      execDdl.execDdls(Files.readString(Paths.get("src/test/resources/sql/create_sample_table3.sql")));
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  void testCompare() throws EncryptedDocumentException, IOException {
    // prepare
    var sql = "SELECT * FROM sample_table1";
    var expFilePath = "jp/gr/java_conf/nkzw/tbt/tools/ExcelLoaderTest_compare_expected.csv";

    var excelLoader = new ExcelLoader(ENDPOINT);
    excelLoader.loadData("./template1.xlsx", false);
    // done, assert
    assertTrue(
        excelLoader.compare(sql, expFilePath, true));
  }

  @Test
  void testLoadData() {
    var args = new String[] {
        "--srcfile", "./template1.xlsx",
        "--table", "sample_table1"
    };
    try {
      ExcelLoader.main(args);

      var excelLoader = new ExcelLoader(ENDPOINT);
      assertEquals(1, excelLoader.recordCount("sample_table1"));

    } catch (EncryptedDocumentException | IOException | InterruptedException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  void testLoadData2() {
    var args = new String[] {
        "--srcfile", "./template2.xlsx",
        "--table", "sample_table2"
    };
    try {
      ExcelLoader.main(args);

      var excelLoader = new ExcelLoader(ENDPOINT);
      assertEquals(1, excelLoader.recordCount("sample_table2"));

    } catch (EncryptedDocumentException | IOException | InterruptedException e) {
      e.printStackTrace();
      fail();
    }
  }

  /**
   * BLOBを含むテーブルのテスト。tcp接続ではエラー。
   */
  @Test
  void testLoadData3() {
    // TMPディレクトリを設定
    System.out.println("(before) java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
    System.setProperty(
        "java.io.tmpdir",
        Paths.get("").toAbsolutePath().getParent() + "/docker/send");

    var args = new String[] {
        "--srcfile", "./template3.xlsx",
        "--table", "sample_table3"
    };
    try {
      ExcelLoader.main(args);

      var excelLoader = new ExcelLoader(ENDPOINT);
      // 件数チェック
      assertEquals(1, excelLoader.recordCount("sample_table3"));
      // BLOB読み込みチェック
      var sql = "SELECT blob_col19 FROM sample_table3";
      var list = excelLoader.executeSql(sql);
      assertEquals(1, list.size());
      assertEquals(100033, list.get(0).getBytes("blob_col19").length);

      // BLOB をファイルとして保存。手動確認用
      var ref = list.get(0).getBlob("blob_col19").openInputStream();
      try (var out = new FileOutputStream("out/blob_col19.jpg")) {
        out.write(ref.readAllBytes());
      }
    } catch (EncryptedDocumentException | IOException | InterruptedException e) {
      e.printStackTrace();
      fail();
    // }finally {
    //   // TMPディレクトリの設定を戻す
    //   System.setProperty("java.io.tmpdir", "/tmp");
    } 
  }

  @Test
  void testMainHelp() {
    String exp = """
        Usage: jp.gr.java_conf.nkzw.tbt.tools.ExcelLoader [options]
          Options:
            --endpoint, -c
              connect tsurugi endpont
              Default: tcp://localhost:12345
            --help, -h
              print this message
            --mode, -m
              run mode [load | template | dump | compare]
              Default: load
            --srcfile, -s
              load file path
            --table
              table name
            --templatefile, -t
              template file path
              Default: template.xlsx""";
    var args = new String[] { "--help" };

    System.setOut(new PrintStream(outContent));
    try {
      ExcelLoader.main(args);
      assertEquals(exp, outContent.toString().trim());
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testMainNoParam() {
    var exp = """
        srcfile is required for load mode.
        """;

    System.setOut(new PrintStream(outContent));
    try {
      ExcelLoader.main();
      assertThat(outContent.toString(), containsString(exp));
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testMainDumpMissParam() {
    var exp = """
        table is required for dump mode.
        """;
    var args = new String[] { "--mode", "dump" };

    System.setOut(new PrintStream(outContent));
    try {
      ExcelLoader.main(args);
      assertThat(outContent.toString(), containsString(exp));
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testMainCompareMissParam1() {
    var exp = """
        table is required for compare mode.
        """;
    var args = new String[] { "--mode", "compare" };

    System.setOut(new PrintStream(outContent));
    try {
      ExcelLoader.main(args);
      assertThat(outContent.toString(), containsString(exp));
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testMainCompareMissParam2() {
    var exp = """
        srcfile is required for load mode.
        """;
    var args = new String[] { "--mode", "compare", "--table", "test_table" };

    System.setOut(new PrintStream(outContent));
    try {
      ExcelLoader.main(args);
      assertThat(outContent.toString(), containsString(exp));
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
      System.setOut(originalOut);
    }
  }

  @Test
  void testMainLoad() throws EncryptedDocumentException, IOException, InterruptedException {
    var args = new String[] {
        "--srcfile", "./template1.xlsx",
        "--table", "sample_table1"
    };

    ExcelLoader.main(args);

    var excelLoader = new ExcelLoader(ENDPOINT);

    assertEquals(1, excelLoader.recordCount("sample_table1"));
  }

  @Test
  void testMainTemplate() {
    // prepare
    try {
      Files.createDirectory(Paths.get("./out"));
    } catch (FileAlreadyExistsException fae) {
      System.out.println("Directory already exists: ./out");
      try {
        if (Files.deleteIfExists(Paths.get("./out/template.xlsx"))) {
          System.out.println("Deleted existing template file.");
        }
      } catch (IOException e) {
        e.printStackTrace();
        fail("Failed to delete existing template file.");
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    var args = new String[] { "--mode", "template",
        "--templatefile", "./out/template.xlsx"
    };

    // done
    try {
      ExcelLoader.main(args);

      assertTrue(Paths.get("./out/template.xlsx").toFile().exists(),
          "Template file should be created at ./out/template.xlsx");
    } catch (EncryptedDocumentException | IOException e) {
      e.printStackTrace();
      fail("Exception occurred");
    } finally {
    }
  }
}
