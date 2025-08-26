package jp.gr.java_conf.nkzw.tbt.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.metadata.TgTableMetadata;
import com.tsurugidb.iceaxe.session.TgSessionOption;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.sql.result.TsurugiResultEntity;
import com.tsurugidb.iceaxe.transaction.manager.TgTmSetting;
import com.tsurugidb.iceaxe.transaction.option.TgTxOption;
import com.tsurugidb.iceaxe.util.IceaxeConvertUtil;

import jp.gr.java_conf.nkzw.tbt.tools.util.TgSheet;
import jp.gr.java_conf.nkzw.tbt.tools.util.TgWorkbook;

public class ExcelLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelLoader.class);

    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter FORMAT_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss[.SSS]");
    public static final DateTimeFormatter FORMAT_OFFSET_DATETIME = DateTimeFormatter
            .ofPattern("yyyy/MM/dd HH:mm:ssZZZZZ");
    public static final LocalDateTime NOW_DATETIME = LocalDateTime.now();
    public static final OffsetDateTime NOW_OFFSET_DATETIME = OffsetDateTime.now();

    private final TsurugiConnector connector;

    public static void main(String... args) throws EncryptedDocumentException, IOException {

        var argument = new ExcelLoaderArgument();
        var commander = JCommander.newBuilder().programName(ExcelLoader.class.getName()).addObject(argument).build();
        commander.parse(args);
        if (argument.isHelp()) {
            commander.usage();
            return;
        }

        ExcelLoader obj = new ExcelLoader(argument.getEndpont());
        if ("load".equalsIgnoreCase(argument.getMode())) {
            if (argument.getSrc() == null || argument.getSrc().isEmpty()) {
                System.out.println("srcfile is required for load mode.");
                return;
            }
            obj.loadData(argument.getSrc());
        } else if ("template".equalsIgnoreCase(argument.getMode())) {
            obj.createTemplate(argument.getTemplatePath());
        } else if ("dump".equalsIgnoreCase(argument.getMode())) {
            if (argument.getTableName() == null || argument.getTableName().isEmpty()) {
                System.out.println("table is required for dump mode.");
                return;
            }
            obj.dumpTableData(argument.getTableName());
        } else if ("compare".equalsIgnoreCase(argument.getMode())) {
            // TODO ヘッダーなし固定
            if (argument.getTableName() == null || argument.getTableName().isEmpty()) {
                System.out.println("table is required for compare mode.");
                return;
            }
            if (argument.getSrc() == null || argument.getSrc().isEmpty()) {
                System.out.println("srcfile is required for load mode.");
                return;
            }
            obj.compare(argument.getTableName(), argument.getSrc(), false);
        } else {
            System.out.println("mode is [load] or [template] or [dump] or [compare].");
        }
    }

    /**
     * DBとファイルの比較。
     * 
     * @param gettingSql   DBからの取得SQL
     * @param expFilePath  期待値ファイルパス
     * @param expHasHeader 期待値ファイルにヘッダー行あり
     * @return
     */
    public boolean compare(String gettingSql, String expFilePath, boolean expHasHeader) {
        boolean match = true;
        // read file
        List<String> expRecList;
        String fileName;
        try {
            expRecList = ResourceLoader.readAllLines(expFilePath);
            Path file = Paths.get(expFilePath);
            fileName = file.getFileName().toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        LOG.debug("expRecList num={}", expRecList.size());
        // read db
        List<String> actRecList = this.getRecordList(gettingSql);
        LOG.debug("actRecList num={}", actRecList.size());
        // compare
        Iterator<String> expIt = expRecList.iterator();
        Iterator<String> actIt = actRecList.iterator();
        if (expHasHeader) {
            // expの1行目のヘッダー空読み
            expIt.next();
        }
        int line_count = 0;
        while (expIt.hasNext()) {
            // assertTrue(actIt.hasNext(), "actIt has no next. line_count=" + line_count);
            line_count++;
            var expRec = expIt.next();
            var actRec = actIt.next();
            if (!compareRec(expRec, actRec)) {
                // if (!expRec.equals(actRec)) {
                LOG.warn("unmatch line={} expData={}", line_count, expRec);
                LOG.warn("unmatch line={} actData={}", line_count, actRec);
                match = false;
                break;
            }
        }
        if (match) {
            LOG.info(fileName + " match all records");
        }
        return match;
    }

    private boolean compareRec(String exp, String act) {
        boolean ret = true;
        String[] expCols = exp.split(",");
        String[] actCols = act.split(",");
        for (int i = 0; i < expCols.length - 1; i++) {
            if (!compareColumn(expCols[i], actCols[i])) {
                ret = false;
            }
        }
        return ret;
    }

    private boolean compareColumn(String exp, String act) {
        if (trimTopEndDoubleQuate(exp).equals(trimTopEndDoubleQuate(act))) {
            // 単純比較
            return true;
        } else if (!compareAsDecimal(exp, act)) {
            LOG.warn("unmatch exp:{} act:{}",
                    trimTopEndDoubleQuate(exp),
                    trimTopEndDoubleQuate(act));
            return false;
        }
        LOG.warn("unmatch exp:" + exp + " act:" + act);
        return false;
    }

    private boolean compareAsDecimal(String exp, String act) {
        try {
            BigDecimal expDec = new BigDecimal(exp);
            BigDecimal actDec = new BigDecimal(act);
            if (expDec.equals(actDec)) {
                return true;
            }
            return false;
        } catch (java.lang.NumberFormatException e) {
            return false;
        }
    }

    private String trimTopEndDoubleQuate(String str) {
        if (str.length() < 2) {
            return str.trim();
        }
        if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
            return str.substring(1, str.length() - 1).trim();
        } else {
            return str.trim();
        }
    }

    public ExcelLoader(String endpoint) {
        this.connector = TsurugiConnector.of(endpoint);
    }

    /**
     * Tsurugiからエクセルテンプレート作成。
     */
    public void createTemplate(String templateFilePath) throws EncryptedDocumentException, IOException {
        LOG.info("start generate template={}", templateFilePath);

        createTemplateExcel(templateFilePath);

        LOG.info("end");
    }

    /**
     * エクセルからTsurugiへデータ登録。
     *
     * @param loadFilePath
     * @throws EncryptedDocumentException
     * @throws IOException
     */
    public void loadData(String loadFilePath) throws EncryptedDocumentException, IOException {
        loadData(loadFilePath, false);
    }

    /**
     * Excelファイルからデータをロードし、各シートごとに指定された処理（テーブル削除・データ挿入）を実行する。
     * <p>
     * 指定されたファイルパスからExcelファイル（暗号化対応）を読み取り、全シートに対して下記の処理を順次実施する。<br>
     * <ul>
     * <li>{@code withoutDeleting} が {@code false} のときはシート名に対応するテーブルのデータを削除する</li>
     * <li>シートの内容をバインドSQLにより一括挿入する</li>
     * </ul>
     *
     * @param loadFilePath    ロード対象となるExcelファイルのパス
     * @param withoutDeleting 削除処理を行わずにデータ挿入のみ行う場合は {@code true}
     * @throws EncryptedDocumentException Excelが暗号化されている場合に発生する例外
     * @throws IOException                ファイルの入出力エラーが発生した場合にスローする
     */
    public void loadData(String loadFilePath, boolean withoutDeleting) throws EncryptedDocumentException, IOException {
        LOG.debug("start loadData = {}", loadFilePath);

        try (var is = ResourceLoader.getInputStream(loadFilePath)) {
            var book = TgWorkbook.buider(is, getConnector());
            book.getTgSheetList().forEach(sheet -> {
                if (!withoutDeleting) {
                    delete(sheet.getSheetName());
                }
                insertAllBindSql(sheet);
            });
        }

        LOG.debug("end loadData");
    }

    public TsurugiConnector getConnector() {
        return connector;
    }

    public List<TsurugiResultEntity> executeSql(String sql) throws IOException, InterruptedException {
        try (var session = connector.createSession()) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
            var tm = session.createTransactionManager(setting);
            return tm.executeAndGetList(sql);
        }
    }

    public int recordCount(String tableName) throws IOException, InterruptedException {
        var sql = "select count(*) as count_num from " + tableName;
        List<TsurugiResultEntity> list = executeSql(sql);
        return list.get(0).getInt("count_num");
    }

    void executeDdl(String ddlSql) throws IOException, InterruptedException {
        var session = connector.createSession();
        var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
        var tm = session.createTransactionManager(setting);
        tm.executeDdl(ddlSql);
    }

    /**
     * Tsurugiへのバインドインサート。
     *
     * @param sheet
     * @throws Exception 
     * @throws Throwable 
     */
    public void insertAllBindSql(TgSheet sheet) {
        var sql_top = "INSERT INTO " //
                + sheet.getExSheet().getSheetName() //
                + "(" //
                + String.join(",", sheet.getColNameList()) //
                + ") VALUES";
        var sql = sql_top + sheet.getBindSql();
        var variables = sheet.getBindVariables();
        var parameterMapping = TgParameterMapping.of(variables);

        var sessionOption = TgSessionOption.of()
                .addLargeObjectPathMappingOnSend(Path.of("/Users/sugionakazawa/github/tsurugi_fdw_docker/client"), "/mnt/client")
                .addLargeObjectPathMappingOnReceive("/opt/tsurugi/var/data/log", Path.of("/Users/sugionakazawa/github/tsurugi_fdw_docker/log"));
        try (var session = connector.createSession(sessionOption); //
        // try (var session = connector.createSession(); //
                var ps = session.createStatement(sql, parameterMapping)) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
            var tm = session.createTransactionManager(setting);
            tm.execute(transaction -> {
                // レコード数分繰り返し
                for (int j = 1; j < sheet.getExSheet().getLastRowNum() + 1; j++) {
                    var parameter = sheet.getBindParameters(j);
                    Thread.sleep(1000);
                    // SQL実行
                    transaction.executeAndGetCount(ps, parameter);
                }
                return;
            });
            LOG.debug("inserted {} rec into {}", sheet.getExSheet().getLastRowNum(), sheet.getExSheet().getSheetName());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Tsurugi メタデータからテンプレート作成。
     *
     * @param outPath
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void createTemplateExcel(String outPath) throws FileNotFoundException, IOException {

        Workbook book = new XSSFWorkbook();
        int createdNum = 0;
        try (var session = connector.createSession()) {
            for (var tableName : session.getTableNameList()) {
                if (!tableName.startsWith("_")) {
                    createSheet(book, session.findTableMetadata(tableName).get());
                    createdNum++;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        // ブック保存
        book.write(new FileOutputStream(outPath));
        LOG.info("output file: {} sheet: {}", outPath, createdNum);
    }

    private void createSheet(Workbook book, TgTableMetadata meta) {
        // シート作成
        var sheet = book.createSheet(meta.getTableName());
        // ヘッダー
        {
            var row = sheet.createRow(0);
            int i_col = 0;
            for (var col : meta.getLowColumnList()) {
                var cell = row.createCell(i_col++);
                cell.setCellValue(col.getName());
            }
        }
        // データ
        {
            var row = sheet.createRow(1);
            int i_col = 0;
            for (var col : meta.getLowColumnList()) {
                var cell = row.createCell(i_col++);
                switch (col.getAtomType()) {
                    case CHARACTER:
                        cell.setCellValue("A");
                        break;
                    case INT4:
                    case INT8:
                    case DECIMAL:
                        cell.setCellValue("0");
                        break;
                    case FLOAT4:
                    case FLOAT8:
                        cell.setCellValue("0.0");
                        break;
                    case DATE:
                        cell.setCellValue(NOW_DATETIME.format(FORMAT_DATE));
                        break;
                    case TIME_OF_DAY:
                        cell.setCellValue(NOW_DATETIME.format(FORMAT_TIME));
                        break;
                    case TIME_POINT:
                        cell.setCellValue(NOW_DATETIME.format(FORMAT_DATETIME));
                        break;
                    case TIME_POINT_WITH_TIME_ZONE:
                        cell.setCellValue(NOW_OFFSET_DATETIME.format(FORMAT_OFFSET_DATETIME));
                        break;
                    case OCTET:
                        cell.setCellValue("A");
                        break;
                    default:
                        String message = "not support table: " + sheet.getSheetName() + "colName: " + col.getName()
                                + " type: " + col.getAtomType();
                        LOG.error(message);
                        throw new NotImplementedException(message);
                }
            }
        }
    }

    /**
     * テーブルデータのクリア。
     *
     * @param tableName
     */
    public void delete(String tableName) {
        var sql = "DELETE FROM " + tableName;
        try (var session = connector.createSession()) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
            var tm = session.createTransactionManager(setting);
            tm.executeAndGetCount(sql);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dumpTableData(String tableName) {
        for (var record : getRecordList(tableName)) {
            System.out.println(record);
        }
    }

    public List<String> getRecordList(String gettingSql) {
        List<String> ret = List.of();
        try (var session = connector.createSession()) {
            var setting = TgTmSetting.ofAlways(TgTxOption.ofOCC());
            var tm = session.createTransactionManager(setting);

            var sb = new StringBuilder();
            var resultMapping = TgResultMapping.of(record -> {
                var convertUtil = IceaxeConvertUtil.INSTANCE;

                sb.setLength(0);
                while (record.moveCurrentColumnNext()) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append("\"");
                    sb.append(convertUtil.toString(record.fetchCurrentColumnValue()));
                    sb.append("\"");
                }
                return sb.toString();
            });
            try (var ps = session.createQuery(gettingSql, resultMapping)) {
                ret = tm.executeAndGetList(ps);
            } catch (IOException | InterruptedException e) {
                LOG.error(gettingSql, e);
                // e.printStackTrace();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
