package jp.gr.java_conf.nkzw.tbt.tools.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.TsurugiConnector;
import com.tsurugidb.iceaxe.metadata.TgTableMetadata;
import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariables;
import com.tsurugidb.iceaxe.sql.type.IceaxeObjectFactory;
import com.tsurugidb.iceaxe.sql.type.TgBlob;

import jp.gr.java_conf.nkzw.tbt.tools.ExcelLoader;

public class TgSheet {
    private static final Logger LOG = LoggerFactory.getLogger(TgSheet.class);

    private Sheet exSheet;
    private List<String> colNameList;
    private TgTableMetadata metadata;

    public TgSheet(Sheet sheet, TsurugiConnector connector) throws InstantiationException {
        this.exSheet = sheet;
        this.colNameList = new ArrayList<String>();
        Row header = sheet.getRow(0);
        for (int i = 0; i < header.getLastCellNum(); i++) {
            this.colNameList.add(header.getCell(i).toString());
        }
        try (var session = connector.createSession()) {
            var metaOpt = session.findTableMetadata(sheet.getSheetName());
            if (metaOpt.isPresent()) {
                this.metadata = metaOpt.get();
            } else {
                String message = "テーブルが存在しないのでスキップします。 sheet: " + sheet.getSheetName();
                LOG.warn(message);
                throw new InstantiationException(message);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * バインド変数生成。
     *
     * @return
     */
    public TgBindVariables getBindVariables() {
        TgBindVariables ret = TgBindVariables.of();
        for (int i = 0; i < this.metadata.getLowColumnList().size(); i++) {
            var col = this.metadata.getLowColumnList().get(i);
            switch (col.getAtomType()) {
                case INT4:
                    ret.addInt(col.getName());
                    break;
                case INT8:
                    ret.addLong(col.getName());
                    break;
                case FLOAT4:
                    ret.addFloat(col.getName());
                    break;
                case FLOAT8:
                    ret.addDouble(col.getName());
                    break;
                case DECIMAL:
                    ret.addDecimal(col.getName());
                    break;
                case CHARACTER:
                    ret.addString(col.getName());
                    break;
                case DATE:
                    ret.addDate(col.getName());
                    break;
                case TIME_OF_DAY:
                    ret.addTime(col.getName());
                    break;
                case TIME_POINT:
                    ret.addDateTime(col.getName());
                    break;
                case OCTET:
                    ret.addBytes(col.getName());
                    break;
                case TIME_POINT_WITH_TIME_ZONE:
                    ret.addOffsetDateTime(col.getName());
                    break;
                case BLOB:
                    ret.addBlob(col.getName());
                    break;
                default:
                    String message = "not support table: " + exSheet.getSheetName() + "colName: " + col.getName()
                            + " type: " + col.getAtomType();
                    LOG.error(message);
                    throw new NotImplementedException(message);
            }
        }
        return ret;
    }

    /**
     * バインドパラメータ生成。
     *
     * @param i_data エクセルデータ行
     * @return
     */
    public TgBindParameters getBindParameters(int i_data) {
        TgBindParameters ret = TgBindParameters.of();
        for (int i = 0; i < this.metadata.getLowColumnList().size(); i++) {
            var col = this.metadata.getLowColumnList().get(i);
            // var cellValue = this.exSheet.getRow(i_data).getCell(i) !=
            // null?this.exSheet.getRow(i_data).getCell(i).toString().trim():null;
            var cellValue = this.exSheet.getRow(i_data).getCell(i) != null
                    ? this.exSheet.getRow(i_data).getCell(i).toString()
                    : null;
            switch (col.getAtomType()) {
                case INT4:
                    ret.addInt(col.getName(), cellValue == null ? null : new BigDecimal(cellValue).intValue());
                    break;
                case INT8:
                    ret.addLong(col.getName(), cellValue == null ? null : new BigDecimal(cellValue).longValue());
                    break;
                case FLOAT4:
                    ret.addFloat(col.getName(), cellValue == null ? null : Float.parseFloat(cellValue));
                    break;
                case FLOAT8:
                    ret.addDouble(col.getName(), cellValue == null ? null : Double.parseDouble(cellValue));
                    break;
                case DECIMAL:
                    ret.addDecimal(col.getName(), cellValue == null ? null : new BigDecimal(cellValue), 6);
                    break;
                case CHARACTER:
                    ret.addString(col.getName(), cellValue);
                    break;
                case DATE:
                    ret.addDate(col.getName(),
                            cellValue == null ? null : LocalDate.parse(cellValue, ExcelLoader.FORMAT_DATE));
                    break;
                case TIME_OF_DAY:
                    ret.addTime(col.getName(),
                            cellValue == null ? null : LocalTime.parse(cellValue, ExcelLoader.FORMAT_TIME));
                    break;
                case TIME_POINT:
                    ret.addDateTime(col.getName(),
                            cellValue == null ? null : LocalDateTime.parse(cellValue, ExcelLoader.FORMAT_DATETIME));
                    break;
                case TIME_POINT_WITH_TIME_ZONE:
                    ret.addOffsetDateTime(col.getName(),
                            cellValue == null ? null
                                    : OffsetDateTime.parse(cellValue, ExcelLoader.FORMAT_OFFSET_DATETIME));
                    break;
                case OCTET:
                    ret.addBytes(col.getName(), cellValue.getBytes());
                    break;
                case BLOB:
                    try {
                        switch (1) {
                            case 1:
                                // bytes配列設定
                                ret.addBlob(col.getName(), getBlobBytes(cellValue));
                                break;
                            case 2:
                                // path設定
                                ret.addBlob(col.getName(), getBlob(cellValue));
                            default:
                                break;
                        }
                    } catch (IOException | InterruptedException e) {
                        String message = "not support table: " + exSheet.getSheetName() + "colName: " + col.getName()
                                + " type: " + col.getAtomType();
                        LOG.error(message);
                        throw new NotImplementedException(message);
                    }
                    break;
                default:
                    String message = "not support table: " + exSheet.getSheetName() + "colName: " + col.getName()
                            + " type: " + col.getAtomType();
                    LOG.error(message);
                    throw new NotImplementedException(message);
            }
        }
        return ret;
    }

    private TgBlob getBlobBytes(String value) throws IOException, InterruptedException {
        var objectFactory = IceaxeObjectFactory.getDefaultInstance();
        return objectFactory.createBlob(value.getBytes(), false);

    }

    private TgBlob getBlob(String cellValue) throws IOException, InterruptedException {
        var objectFactory = IceaxeObjectFactory.getDefaultInstance();
        var is = getInputStream(cellValue.getBytes(StandardCharsets.UTF_8));
        return objectFactory.createBlob(is, false);
    }

    private InputStream getInputStream(byte[] data) throws IOException {
        return new ByteArrayInputStream(data);
    }

    public Sheet getExSheet() {
        return exSheet;
    }

    public List<String> getColNameList() {
        return colNameList;
    }

    /**
     * バインドSQL生成。
     *
     * @return
     */
    public String getBindSql() {
        StringBuilder sb = new StringBuilder("(");
        for (var val : this.colNameList) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            sb.append(":" + val);
        }
        sb.append(")");
        return sb.toString();
    }

    public String getSheetName() {
        return this.getExSheet().getSheetName();
    }
}
