package jp.gr.java_conf.nkzw.tbt.tools;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.session.TsurugiSession;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;

import jp.gr.java_conf.nkzw.tbt.tools.common.util.TgStringUtil;
import jp.gr.java_conf.nkzw.tbt.tools.model.TgColumn;
import jp.gr.java_conf.nkzw.tbt.tools.model.TgIndex;
import jp.gr.java_conf.nkzw.tbt.tools.model.TgTable;

/**
 * TgData class for handling TBT data.
 * This class is a placeholder for future implementations.
 * 
 * 機能１：DDL生成
 * 機能２：Java entityソース生成
 * 機能３：テストデータ生成
 */
public class TgData {

    static private final Logger LOG = LoggerFactory.getLogger(TgData.class);
    static private final String SPA = FileSystems.getDefault().getSeparator();

    static private TgDataArgument argument = new TgDataArgument();

    private List<TgTable> tables = new ArrayList<TgTable>();

    public static void main(String[] args) throws IOException, EncryptedDocumentException {
        LOG.info("TgData main method started");

        if (args.length == 0) {
            // for test
            String[] t_args = { "--excel",
                    "tools/src/test/resources/data/jp/gr/java_conf/nkzw/tbt/tools/TgData/table_design.xlsx",
                    "--sheet", "サンプル", // 指定したシートのみを対象
                    "--javaentity", // Javaエンティティソースを出力
                    "--javapackage", "jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity", // Javaソースのパッケージ名を指定
                    "--out", "./out",
                    "--ddl", // DDLを出力
                    // "--silent", // 生成されたテーブル情報を出力しない
                    // "--createtable", // DDL テーブル作成
                    // "--generatedata", // DML テストデータ生成
                    // "--datacount", "100", // テストデータ件数
            };
            args = t_args;
        }

        var commander = JCommander.newBuilder().programName(TgData.class.getName()).addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }
        try {
            new TgData().run();
        } catch (EncryptedDocumentException | IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LOG.info("TgData main method finished");
    }

    public void run() throws IOException, EncryptedDocumentException, InterruptedException {
        //// Excelファイルの読み込み
        loadExcel(argument.getExcelFileName());

        // JavaEntityの生成
        if (argument.isJavaEntity()) {
            writeJavaEntities();
        }
        // DDLの生成
        if (argument.isDdl()) {
            writeDdlFiles();
        }
        // テーブル作成:DDL実行
        if (argument.isCreateTable()) {
            createTables();
        }
        // データ生成
        if (argument.isGenerateData()) {
            generateDatas();
        }

        // テーブル情報出力
        if (!argument.isSilent()) {
            for (var table : this.tables) {
                LOG.info("table:\n{}", table.toString());
            }
        }
    }

    private void generateDatas() throws IOException, InterruptedException {
        var connecter = TsurugiConnector.of(URI.create(argument.getEndPoint()));
        try (var session = connecter.createSession()) {
            for (var table : this.tables) {
                generateData(session, table);
            }
        }
    }

    private void generateData(TsurugiSession session, TgTable table) throws IOException, InterruptedException {

        var insertSql = table.generateInsertSql();
        var variables = table.generateBindVariables();
        var parameterMapping = TgParameterMapping.of(variables);
        try (var ps = session.createStatement(insertSql, parameterMapping)) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
            var tm = session.createTransactionManager(setting);
            tm.execute(transaction -> {
                var count = 1;
                while (count <= argument.getDataCount()) {
                    var parameter = table.getTgBnidData(count++);
                    transaction.executeAndGetCount(ps, parameter);
                }
                LOG.info("insert count: {}", count - 1);
            });
        }
    }

    private void createTables() throws IOException, InterruptedException {
        var connecter = TsurugiConnector.of(URI.create(argument.getEndPoint()));
        try (var session = connecter.createSession()) {
            for (var table : this.tables) {
                var tm = session.createTransactionManager();
                // テーブル作成
                if (existsTable(session, table.getTableName())) {
                    var dropTableSql = "DROP TABLE " + table.getTableName();
                    tm.executeDdl(dropTableSql);
                    LOG.info("drop table {}", table.getTableName());
                }
                // テーブル作成
                var ddl = table.getDdlDef();
                tm.executeDdl(ddl);
                LOG.info("create table {}", table.getTableName());
                // インデックス作成
                for (var indexDef : table.getIndexDefs()) {
                    tm.executeDdl(indexDef);
                    LOG.info("create index {}", indexDef);
                }
            }
        }
    }

    private boolean existsTable(TsurugiSession session, String tableName) throws IOException, InterruptedException {
        var metadataOpt = session.findTableMetadata(tableName);
        if (metadataOpt.isPresent()) {
            LOG.info("table exists");
            return true;
        } else {
            System.out.println("table not exists");
            return false;
        }
    }

    private void writeDdlFiles() throws IOException {
        for (var table : this.tables) {
            var outputDdl = new StringBuilder(table.getDdlDef());
            outputDdl.append("\n");
            // インデックス作成
            for (var indexDef : table.getIndexDefs()) {
                outputDdl.append(indexDef).append("\n");
            }
            // ディレクトリ作成
            Path targetPath = Paths.get(argument.getOutPath()).resolve("sql");
            Files.createDirectories(targetPath);
            var outputPath = targetPath.resolve("create_" + table.getTableName() + ".sql");
            Files.writeString(outputPath, outputDdl.toString());
            LOG.info("write ddl: {}", outputPath);
        }
    }

    /**
     * JavaEntityソースファイル出力
     * 
     * @throws IOException
     */
    private void writeJavaEntities() throws IOException {
        // ディレクトリ作成
        Path targetPath = Paths.get(argument.getOutPath()
                + SPA
                + argument.getJavaPackageName().replace(".", SPA));
        for (var table : this.tables) {
            Files.createDirectories(targetPath);
            var outputJava = table.generateJavaEntity(argument.getJavaPackageName());
            var outputPath = targetPath.resolve(TgStringUtil.toCamelCaseTopUpper(table.getTableName()) + ".java");
            Files.writeString(outputPath, outputJava);
            LOG.info("write java entity: {}", outputPath.toAbsolutePath());
        }
    }

    private void loadExcel(String filename) {

        var file = new File(filename);
        Workbook book;
        try {
            book = WorkbookFactory.create(file);
            for (int i = 0; i < book.getNumberOfSheets(); i++) {
                var sheet = book.getSheetAt(i);
                if (!argument.hasExcelSheetName()
                        || argument.getExcelSheetName().equalsIgnoreCase(sheet.getSheetName())) {
                    LOG.debug("sheetName: {}", sheet.getSheetName());
                    // テーブル生成
                    var table = generateTable(sheet);
                    if (table != null) {
                        this.tables.add(table);
                        LOG.info("read table= {}", table.getTableName());
                    } else {
                        LOG.warn("table is null");
                    }
                }
            }
        } catch (EncryptedDocumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private TgTable generateTable(Sheet sheet) {
        TgTable table = new TgTable();
        // 物理テーブル名を取得
        var tableNameRow = searchRow(sheet, 0, "物理テーブル名", 1);
        if (tableNameRow == null) {
            return null;
        }
        table.setTableName(tableNameRow.getCell(2).toString());
        // LOG.debug("tableName: {}", tableNameRow.getCell(2).toString());

        // カラム定義を取得
        var columnDefRow = searchRow(sheet, 0, "カラム情報", 0);
        if (columnDefRow != null) {
            for (int cur_row = columnDefRow.getRowNum() + 2; cur_row < sheet.getPhysicalNumberOfRows(); cur_row++) {
                var row = sheet.getRow(cur_row);
                if (row == null) {
                    break;
                }
                var culumnCell = row.getCell(2).getStringCellValue().trim();
                if ((culumnCell == null) || culumnCell.toString().isEmpty()) {
                    break;
                }
                var typeCell = row.getCell(3).getStringCellValue().trim().toUpperCase();
                if (typeCell == null) {
                    break;
                }
                table.addCulmn(new TgColumn(culumnCell, typeCell));
                // LOG.debug("columnName: {}, type: {}", culumnCell.toString(),
                // typeCell.toString());
            }
        }
        // インデックス
        var columnDindeefRow = searchRow(sheet, 0, "インデックス情報", 0);
        if (columnDindeefRow != null) {
            for (int cur_row = columnDindeefRow.getRowNum() + 2; cur_row < sheet.getPhysicalNumberOfRows(); cur_row++) {
                var row = sheet.getRow(cur_row);
                if (row == null) {
                    break;
                }
                var indexNameCell = row.getCell(1).getStringCellValue().trim();
                if ((indexNameCell == null) || indexNameCell.toString().isEmpty()) {
                    break;
                }
                var indexColNames = row.getCell(2).getStringCellValue().trim();
                if ((indexColNames == null) || indexColNames.toString().isEmpty()) {
                    break;
                }
                if (indexNameCell.equalsIgnoreCase("primary key")) {
                    // 主キーの場合は、カラム名を取得
                    var colNames = indexColNames.split(",");
                    for (var colName : colNames) {
                        // カラム存在をチェック
                        var column = table.getColumn(colName.trim());
                        if (column != null) {
                            column.setPrimaryKey(true);
                        } else {
                            new RuntimeErrorException(null, "primary key column not found: " + colName.trim());
                        }
                    }
                } else {
                    var colNames = indexColNames.split(",");
                    for (var colName : colNames) {
                        // カラム存在をチェック
                        var column = table.getColumn(colName.trim());
                        if (column == null) {
                            new RuntimeErrorException(null, "primary key column not found: " + colName.trim());
                        }
                    }
                    var index = new TgIndex(indexNameCell, indexColNames);
                    table.addIndex(index);
                }
            }
        }

        return table;
    }

    private Row searchRow(Sheet sheet, int firstRowNum, String searchKey, int searchCell) {
        for (int cur_row = firstRowNum; // 1行目からスタート
                cur_row < sheet.getPhysicalNumberOfRows(); cur_row++) {
            var row = sheet.getRow(cur_row);
            if (row != null) {
                var cell = row.getCell(searchCell);
                if (cell != null) {
                    if (searchKey.equals(cell.toString())) {
                        return row;
                    }
                }
            }
        }
        return null;
    }
}
