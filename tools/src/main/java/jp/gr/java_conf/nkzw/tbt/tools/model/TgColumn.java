package jp.gr.java_conf.nkzw.tbt.tools.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.gr.java_conf.nkzw.tbt.tools.common.util.TgStringUtil;

public class TgColumn {

    private static final Logger LOG = LoggerFactory.getLogger(TgColumn.class);

    /**
     * Convert SQL type to Java type.
     *
     * @param columnType SQL type
     * @return Java type
     */
    private static Object[] convJavaType(String columnType) {
        Object[] rets = new Object[3];
        if (columnType == null) {
            return null;
        }
        var ColumnPrecision = 0;
        var columnScale = 0;
        if (columnType.contains("(")) {
            var digits = columnType.split("\\(")[1].split("\\)")[0];
            var digitArray = digits.split(",");
            if (digitArray.length > 0) {
                ColumnPrecision = Integer.parseInt(digitArray[0].trim());
            }
            if (digitArray.length > 1) {
                columnScale = Integer.parseInt(digitArray[1].trim());
            }
        }

        var type_pre = columnType.split("\\(")[0].toUpperCase();
        var javaType = switch (type_pre) {
            case "INT","INTEGER" -> "int";
            case "BIGINT" -> "long";
            case "REAL" -> "float";
            case "DOUBLE","DOUBLE PRECISION" -> "double";
            case "DECIMAL", "NUMBER" -> "BigDecimal";
            case "CHAR", "CHARACTER", "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" -> "String";
            case "DATE" -> "LocalDate";
            case "TIME" -> "LocalTime";
            case "TIMESTAMP" -> "LocalDateTime";
            case "TIMESTAMP WITH TIME ZONE" -> "OffsetDateTime";
            case "BINARY", "VARBINARY", "BINARY VARYING" -> "byte[]";
            case "BOOLEAN" -> "boolean";
            default -> {
                var message = "unknown type: " + columnType;
                LOG.error(message);
                throw new IllegalArgumentException(message);
            }
        };

        // adjust precision and scale
        if (ColumnPrecision == 0) {
            ColumnPrecision = adjustPrecision(columnType);
        }
        rets[0] = javaType;
        rets[1] = ColumnPrecision;
        rets[2] = columnScale;
        return rets;
    }

    private static int adjustPrecision(String columnType) {
        var type_pre = columnType.split("\\(")[0];
        return switch (type_pre) {
            case "INT","INTEGER" -> 9;
            case "BIGINT" -> 19;
            case "REAL" -> 7;
            case "DOUBLE","DOUBLE PRECISION" -> 15;
            case "DECIMAL" -> 38;
            case "CHAR", "CHARACTER", "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" -> 255;
            case "BINARY", "VARBINARY", "BINARY VARYING" -> 255;
            case "DATE", "TIME", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE" -> 0;
            case "BOOLEAN" -> 1;
            default -> {
                var message = "unknown type: " + columnType;
                LOG.error(message);
                throw new IllegalArgumentException(message);
            }
        };
    }

    private String columnName;
    private String columnType;
    private int ColumnPrecision;
    private int columnScale;
    private String javaType;
    private boolean isPrimaryKey = false;
    private boolean isNullable = true;
    private String comment;
    private String defaultValue;
    private String columnExplain;
    
    public TgColumn(String columnExplain,String columnName, String columnType, String nullCell, String defaultCell, String commentCell) {
        this.columnName = columnName;
        this.columnType = columnType;
        var ret = convJavaType(columnType);
        this.javaType = ret[0].toString();
        this.ColumnPrecision = (int) ret[1];
        this.columnScale = (int) ret[2];
        this.isNullable = (nullCell != null) && !nullCell.isEmpty() ? false : true;
        this.isPrimaryKey = (nullCell != null) && nullCell.contains("PK") ? true : false;
        this.defaultValue = defaultCell;
        this.comment = commentCell;
        this.columnExplain = columnExplain;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
        var rets = convJavaType(columnType);
        this.javaType = rets[0].toString();
        this.ColumnPrecision = (int) rets[1];
        this.columnScale = (int) rets[2];
    }

    public int getColumnPrecision() {
        return ColumnPrecision;
    }

    public int getColumnScale() {
        return columnScale;
    }

    public void setColumnScale(int columnDigits) {
        this.columnScale = columnDigits;
    }

    public String getJavaType() {
        return this.javaType;
    }

    public String getResultMapAddMethodType() {
        return switch (columnType.split("\\(")[0]) {
            case "INT","INTEGER" -> "addInt";
            case "BIGINT" -> "addLong";
            case "REAL" -> "addFloat";
            case "DOUBLE","DOUBLE PRECISION" -> "addDouble";
            case "DECIMAL" -> "addDecimal";
            case "CHAR", "CHARACTER", "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" -> "addString";
            case "DATE" -> "addDate";
            case "TIME" -> "addTime";
            case "TIMESTAMP" -> "addDateTime";
            case "TIMESTAMP WITH TIME ZONE" -> "addOffsetDateTime";
            case "BINARY", "VARBINARY", "BINARY VARYING" -> "addBytes";
            case "BOOLEAN" -> "addBoolean";
            default -> {
                var message = "unknown type: " + columnType;
                LOG.error(message);
                throw new IllegalArgumentException(message);
            }
        };
    }

    public String getMemberDef() {
        return "private "
                + getJavaType()
                + " "
                + TgStringUtil.toCamelCase(columnName)
                + ";\n";
    }

    // toString
    @Override
    public String toString() {
        return "TgColumn name=" + this.columnName
                + ", \ttype=" + this.columnType
                + ", \tprecision=" + this.ColumnPrecision
                + ", \tscale=" + this.columnScale
                + ", \tjavaType=" + this.javaType
                + ", \tisPrimaryKey=" + this.isPrimaryKey
                + "\n";
    }

    public String getSetterDef() {
        StringBuilder sb = new StringBuilder();
        sb.append("public void set")
                .append(TgStringUtil.toCamelCaseTopUpper(columnName))
                .append("(")
                .append(this.javaType)
                .append(" ")
                .append(TgStringUtil.toCamelCase(columnName))
                .append(") {\n\t")
                .append("this.")
                .append(TgStringUtil.toCamelCase(columnName))
                .append(" = ")
                .append(TgStringUtil.toCamelCase(columnName))
                .append(";\n")
                .append("}\n");
        return sb.toString();
    }

    public Object getGetterDef() {
        StringBuilder sb = new StringBuilder();
        sb.append("public ")
                .append(this.javaType)
                .append(" get")
                .append(TgStringUtil.toCamelCaseTopUpper(columnName))
                .append("() {\n")
                .append("\treturn this.")
                .append(TgStringUtil.toCamelCase(columnName))
                .append(";\n")
                .append("}\n");
        return sb.toString();
    }

    public boolean isNumeric() {
        return switch (columnType.split("\\(")[0].trim()) {
            case "INT", "BIGINT", "REAL", "DOUBLE", "DECIMAL" -> true;
            default -> false;
        };
    }

    public boolean isString() {
        return switch (columnType.split("\\(")[0].trim()) {
            case "CHAR", "CHARACTER", "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" -> true;
            default -> false;
        };
    }

    public boolean isDate() {
        return switch (columnType.split("\\(")[0].trim()) {
            case "DATE", "TIME", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE" -> true;
            default -> false;
        };
    }

    public boolean isBinary() {
        return switch (columnType.split("\\(")[0]) {
            case "BINARY", "VARBINARY", "BINARY VARYING" -> true;
            default -> false;
        };
    }

    public int getStoreValue(int seed) {
        if (isBinary() || isDate()) {
            return seed;
        }
        // TODO エクセルに生成する値を指定したい
        // 桁数が溢れる場合は余りを返す
        var max = Math.pow(10, (this.ColumnPrecision - this.columnScale));
        if (seed >= max) {
            return (int) (seed % max);
        }
        return seed;
    }

    public void setPrimaryKey(boolean b) {
        this.isPrimaryKey = b;
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public String getParameterMapAddMethod(String tableName) {
        var sb = new StringBuilder();
        switch (columnType.split("\\(")[0]) {
            case "INT","INTEGER" ->
                sb.append("addInt(\"").append(columnName).append("\",");
            case "BIGINT" -> sb.append("addLong(\"").append(columnName).append("\",");
            case "REAL" -> sb.append("addFloat(\"").append(columnName).append("\",");
            case "DOUBLE","DOUBLE PRECISION" -> sb.append("addDouble(\"").append(columnName).append("\",");
            case "DECIMAL" -> sb.append("addDecimal(\"").append(columnName).append("\",");
            case "CHAR", "CHARACTER", "VARCHAR", "CHAR VARYING", "CHARACTER VARYING" ->
                sb.append("addString(\"").append(columnName).append("\",");
            case "DATE", "TIME", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE" ->
                sb.append("add(\"").append(columnName).append("\",")
                        .append(getJavaType()).append(".class, ");
            case "BINARY", "VARBINARY", "BINARY VARYING" ->
                sb.append("addBytes(\"").append(columnName).append("\",");
            case "BOOLEAN" -> sb.append("addBoolean(\"").append(columnName).append("\",");
            default -> {
                var message = "unknown type: " + columnType;
                LOG.error(message);
                throw new IllegalArgumentException(message);
            }
        }
        sb.append(TgStringUtil.toCamelCaseTopUpper(tableName))
                .append("::").append("get").append(TgStringUtil.toCamelCaseTopUpper(columnName))
                .append(")");

        return sb.toString();
    }

    public String getComment() {
        return comment;
    }

    public String getDdlDef() {
        StringBuilder sb = new StringBuilder();
        // コメント
        if (comment != null) {
            sb.append("/** ").append(columnExplain).append(":").append(comment).append(" */\n");
        }
        // カラム タイプ
        sb.append(columnName).append(" ").append(columnType);
        // NULL制約
        if (this.isNullable) {
            sb.append(" NULL");
        } else {
            sb.append(" NOT NULL");
        }
        // デフォルト
        if ((this.defaultValue != null) && !this.defaultValue.isEmpty()) {
            sb.append(" DEFAULT ").append(this.defaultValue);
        }
        return sb.toString();
    }
}
