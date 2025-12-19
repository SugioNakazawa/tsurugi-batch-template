package jp.gr.java_conf.nkzw.tbt.tools.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsurugidb.iceaxe.sql.TgDataType;
import com.tsurugidb.iceaxe.sql.parameter.TgBindParameters;
import com.tsurugidb.iceaxe.sql.parameter.TgBindVariables;

import jp.gr.java_conf.nkzw.tbt.tools.common.util.TgStringUtil;

public class TgTable {

    static private final Logger LOG = LoggerFactory.getLogger(TgTable.class);

    static private final OffsetDateTime OFFSET_DATE_TIME_NOW = OffsetDateTime.now();

    // case "LocalTime" -> parameter.addTime(columnName, LocalTime.now());
    // case "LocalDateTime" -> parameter.addDateTime(columnName,
    // LocalDateTime.now());
    // case "OffsetDateTime" -> parameter.addOffsetDateTime(columnName,
    // OffsetDateTime.now());

    private String tableName;
    private List<TgColumn> columns;

    private List<TgIndex> indexes;

    public TgTable() {
        this.columns = new java.util.ArrayList<>();
        this.indexes = new java.util.ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addCulmn(TgColumn tgColumn) {
        this.columns.add(tgColumn);
    }

    // toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TgTable tableName =").append(tableName).append("\n");
        for (TgColumn column : columns) {
            sb.append(column.toString());
        }
        for (TgIndex index : indexes) {
            sb.append(index.toString());
        }
        return sb.toString();
    }

    public List<TgColumn> getColumns() {
        return this.columns;
    }

    public String getClassDefStr() {
        return "public class "
                + TgStringUtil.toCamelCaseTopUpper(tableName)
                + " implements Cloneable {\n";
    }

    public String getClassEndStr() {
        return "}\n";
    }

    public String getDropTableDef() {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");
        return sb.toString();
    }

    public String getDdlDef() {
        StringBuilder sb = new StringBuilder();

        // create table
        sb.append("CREATE TABLE ").append(tableName).append(" (\n");
        for(var column : columns) {
            sb.append(column.getDdlDef());
            // カンマ区切り
            if (columns.indexOf(column) < columns.size() - 1) {
                sb.append(",\n");
            }else {
                sb.append("\n");
            }
        }
        // primary key
        sb.append(",\n").append(genetatePrimaryKey()).append("\n);\n");

        return sb.toString();
    }

    public List<String> getIndexDefs() {
        List<String> indexList = new ArrayList<>();
        for (var index : indexes) {
            indexList.add(index.getIndexDef(this.tableName));
        }
        return indexList;
    }

    private String genetatePrimaryKey() {
        List<String> primaryKeyColumns = new ArrayList<>();
        for (var column : columns) {
            if (column.isPrimaryKey()) {
                primaryKeyColumns.add(column.getColumnName());
            }
        }
        if (primaryKeyColumns.isEmpty()) {
            return "";
        }
        return "PRIMARY KEY (" + String.join(", ", primaryKeyColumns) + ")";
    }

    public String getConstructorDefStr() {
        return "public "
                + TgStringUtil.toCamelCaseTopUpper(tableName)
                + "() {\n"
                + "}\n";
    }

    public String getResultMappingDefStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("public static final TgResultMapping<")
                .append(TgStringUtil.toCamelCaseTopUpper(tableName))
                .append("> RESULT_MAPPING = TgResultMapping.of(")
                .append(TgStringUtil.toCamelCaseTopUpper(tableName))
                .append("::new) //\n");
        for (var column : columns) {
            sb.append(".")
                    .append(column.getResultMapAddMethodType())
                    .append("(\"")
                    .append(column.getColumnName())
                    .append("\", ")
                    .append(TgStringUtil.toCamelCaseTopUpper(tableName))
                    .append("::set")
                    .append(TgStringUtil.toCamelCaseTopUpper(column.getColumnName()))
                    .append(")\n");
        }
        sb.append(";\n");

        return sb.toString();
    }

    private String getParameterMappingDefStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("public static final TgParameterMapping<")
                .append(TgStringUtil.toCamelCaseTopUpper(tableName))
                .append("> PARAMETER_MAPPING = TgParameterMapping.of(")
                .append(TgStringUtil.toCamelCaseTopUpper(tableName))
                .append(".class)\n");
        for (var column : columns) {
            sb.append(".")
                    .append(column.getParameterMapAddMethod(tableName))
                    .append("\n");
        }
        sb.append(";\n");

        return sb.toString();
    }

    private String getToValuesNameDefStr() {
        StringBuilder sb = new StringBuilder("public static String toValuesName() {\n");
        sb.append("return \"");
        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            sb.append(":").append(column.getColumnName());
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("\";\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * JavaEntityソースコード生成。
     * 
     * @param table
     */
    public String generateJavaEntity(String packagePrefix) {

        StringBuilder sb = new StringBuilder();

        // package
        var packageDefStr = "package "
                + packagePrefix
                + ";\n";
        sb.append(packageDefStr);
        // comment
        sb.append("//  genetrated by TgData.\n");
        // import
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.time.LocalDate;\n");
        sb.append("import java.time.LocalTime;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.time.OffsetDateTime;\n");
        sb.append("import com.tsurugidb.iceaxe.sql.result.TgResultMapping;\n");
        sb.append("import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;\n");
        // class def
        var classDefStr = getClassDefStr();
        sb.append(classDefStr);
        // member
        for (var column : getColumns()) {
            var fieldDefStr = column.getMemberDef();
            sb.append(fieldDefStr);
        }
        // constructor
        var constructorDefStr = getConstructorDefStr();
        sb.append(constructorDefStr);
        // setter/getter
        for (var column : getColumns()) {
            var setterDefStr = column.getSetterDef();
            sb.append(setterDefStr);
            var getterDefStr = column.getGetterDef();
            sb.append(getterDefStr);
        }
        // result mapping
        sb.append("//  genetrated by TgData.\n");
        var resultMappingDefStr = getResultMappingDefStr();
        sb.append(resultMappingDefStr);
        // parameter mapping
        sb.append("//  genetrated by TgData.\n");
        var parameterMappingDefStr = getParameterMappingDefStr();
        sb.append(parameterMappingDefStr);
        // toValuesName
        sb.append("//  genetrated by TgData.\n");
        var toValuesNameDefStr = getToValuesNameDefStr();
        sb.append(toValuesNameDefStr);
        // clone
        sb.append("//  genetrated by TgData.\n");
        var cloneMethodDefStr = getCloneMethodDefStr();
        sb.append(cloneMethodDefStr);

        // class end clause
        var classEndStr = getClassEndStr();
        sb.append(classEndStr);

        return sb.toString();
    }

    private String getCloneMethodDefStr() {
        return """
                    @Override
                    public SampleTable clone() {
                        try {
                            return (SampleTable) super.clone();
                        } catch (CloneNotSupportedException e) {
                            throw new AssertionError("Cloning not supported", e);
                        }
                    }
                """.replaceAll("SampleTable", TgStringUtil.toCamelCaseTopUpper(tableName));
    }

    public String generateInsertSql() {
        var sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" VALUES(");
        for (int i = 0; i < columns.size(); i++) {
            var column = columns.get(i);
            sb.append(":").append(column.getColumnName());
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public TgBindVariables generateBindVariables() {
        var variables = TgBindVariables.of();
        for (var column : columns) {
            var columnName = column.getColumnName();
            var columnType = column.getJavaType();
            switch (columnType) {
                case "int" -> variables.add(columnName, TgDataType.INT);
                case "long" -> variables.add(columnName, TgDataType.LONG);
                case "String" -> variables.add(columnName, TgDataType.STRING);
                case "float" -> variables.add(columnName, TgDataType.FLOAT);
                case "double" -> variables.add(columnName, TgDataType.DOUBLE);
                case "boolean" -> variables.add(columnName, TgDataType.BOOLEAN);
                case "byte" -> variables.add(columnName, TgDataType.BYTES);
                case "BigDecimal" -> variables.add(columnName, TgDataType.DECIMAL);
                case "LocalDate" -> variables.add(columnName, TgDataType.DATE);
                case "LocalTime" -> variables.add(columnName, TgDataType.TIME);
                case "LocalDateTime" -> variables.add(columnName, TgDataType.DATE_TIME);
                case "OffsetDateTime" -> variables.add(columnName, TgDataType.OFFSET_DATE_TIME);
                case "byte[]" -> variables.add(columnName, TgDataType.BYTES);
                default -> LOG.error("unknown column type: {}", columnType);
            }
        }
        return variables;
    }

    public TgBindParameters getTgBnidData(int seed) {
        if (seed > 999) {
            var a = 1;
        }
        var parameter = TgBindParameters.of();
        for (var column : columns) {
            var columnName = column.getColumnName();
            var columnType = column.getJavaType();
            switch (columnType) {
                case "int" -> parameter.addInt(columnName, seed);
                case "long" -> parameter.addLong(columnName, (long) column.getStoreValue(seed));
                case "float" -> parameter.addFloat(columnName, (float) column.getStoreValue(seed));
                case "double" -> parameter.addDouble(columnName, (double) column.getStoreValue(seed));
                case "BigDecimal" -> parameter.addDecimal(columnName, new BigDecimal(column.getStoreValue(seed)));
                case "String" -> parameter.addString(columnName, Integer.toString(column.getStoreValue(seed)).trim());
                case "boolean" -> parameter.addBoolean(columnName, (seed % 2) == 0);
                case "byte[]" -> parameter.addBytes(columnName, new byte[] { (byte) seed });
                case "LocalDate" -> parameter.addDate(columnName, OFFSET_DATE_TIME_NOW.toLocalDate());
                case "LocalTime" -> parameter.addTime(columnName, OFFSET_DATE_TIME_NOW.toLocalTime());
                case "LocalDateTime" -> parameter.addDateTime(columnName, OFFSET_DATE_TIME_NOW.toLocalDateTime());
                case "OffsetDateTime" -> parameter.addOffsetDateTime(columnName, OFFSET_DATE_TIME_NOW);
                default -> LOG.error("unknown column type: {}", columnType);
            }
        }
        return parameter;
    }

    public TgColumn getColumn(String colName) {
        for (TgColumn column : this.columns) {
            if (column.getColumnName().equals(colName)) {
                return column;
            }
        }
        return null;
    }

    public void addIndex(TgIndex index) {
        this.indexes.add(index);
    }
}
