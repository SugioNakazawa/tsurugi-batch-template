package jp.gr.java_conf.nkzw.tbt.tools.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TgColumnTest {
    @Test
    void testGetGetterDef() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        String expected = "public int getIntCol1() {\n" +
                "\treturn this.intCol1;\n" +
                "}\n";
        assertEquals(expected, tgColumn.getGetterDef());
    }

    @Test
    void testNumericColumn() {
        TgColumn tgColumn = new TgColumn("intCol1", "INT");
        assertEquals("int", tgColumn.getJavaType());
        assertEquals(9, tgColumn.getColumnPrecision());
        assertEquals(0, tgColumn.getColumnScale());

        tgColumn.setColumnType("BIGINT");
        assertEquals("long", tgColumn.getJavaType());
        assertEquals(19, tgColumn.getColumnPrecision());
        assertEquals(0, tgColumn.getColumnScale());

        tgColumn.setColumnType("REAL");
        assertEquals("float", tgColumn.getJavaType());

        tgColumn.setColumnType("DOUBLE");
        assertEquals("double", tgColumn.getJavaType());

        tgColumn.setColumnType("DECIMAL(10, 2)");
        assertEquals("BigDecimal", tgColumn.getJavaType());
        tgColumn.setColumnType("DECIMAL");
        assertEquals("BigDecimal", tgColumn.getJavaType());
        try {
            tgColumn.setColumnType("number");
        } catch (IllegalArgumentException e) {
            assertEquals("unknown type: number", e.getMessage());
        }
    }

    @Test
    void testStringColumn() {
        TgColumn tgColumn = new TgColumn("col", "CHAR");
        assertEquals("String", tgColumn.getJavaType());
        tgColumn.setColumnType("CHAR(10)");
        assertEquals("String", tgColumn.getJavaType());
        tgColumn.setColumnType("VARCHAR(10)");
        assertEquals("String", tgColumn.getJavaType());
        tgColumn.setColumnType("CHAR VARYING");
        assertEquals("String", tgColumn.getJavaType());
        tgColumn.setColumnType("CHARACTER VARYING");
        assertEquals("String", tgColumn.getJavaType());
    }

    @Test
    void testDateTimeColumn() {
        TgColumn tgColumn = new TgColumn("col1", "DATE");
        assertEquals("LocalDate", tgColumn.getJavaType());
        tgColumn.setColumnType("TIME");
        assertEquals("LocalTime", tgColumn.getJavaType());
        tgColumn.setColumnType("TIMESTAMP");
        assertEquals("LocalDateTime", tgColumn.getJavaType());
        tgColumn.setColumnType("TIMESTAMP WITH TIME ZONE");
        assertEquals("OffsetDateTime", tgColumn.getJavaType());
    }

    @Test
    void testBinaryColumn() {
        TgColumn tgColumn = new TgColumn("col1", "BINARY");
        assertEquals("byte[]", tgColumn.getJavaType());
        tgColumn.setColumnType("VARBINARY");
        assertEquals("byte[]", tgColumn.getJavaType());
        tgColumn.setColumnType("BINARY VARYING");
        assertEquals("byte[]", tgColumn.getJavaType());
    }

    @Test
    void testGetMemberDef() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        String expected = "private int intCol1;\n";
        assertEquals(expected, tgColumn.getMemberDef());
    }

    @Test
    void testGetResultMapAddMethodType() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        assertEquals("addInt", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("BIGINT");
        assertEquals("addLong", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("REAL");
        assertEquals("addFloat", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("DOUBLE");
        assertEquals("addDouble", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("DECIMAL(10, 2)");
        assertEquals("addDecimal", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("DECIMAL");
        assertEquals("addDecimal", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("CHAR");
        assertEquals("addString", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("CHAR(10)");
        assertEquals("addString", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("VARCHAR(10)");
        assertEquals("addString", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("CHAR VARYING");
        assertEquals("addString", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("CHARACTER VARYING");
        assertEquals("addString", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("DATE");
        assertEquals("addDate", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("TIME");
        assertEquals("addTime", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("TIMESTAMP");
        assertEquals("addDateTime", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("TIMESTAMP WITH TIME ZONE");
        assertEquals("addOffsetDateTime", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("BINARY");
        assertEquals("addBytes", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("VARBINARY");
        assertEquals("addBytes", tgColumn.getResultMapAddMethodType());
        tgColumn.setColumnType("BINARY VARYING");
        assertEquals("addBytes", tgColumn.getResultMapAddMethodType());
    }

    @Test
    void testGetSetterDef() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        String expected = "public void setIntCol1(int intCol1) {\n" +
                "\tthis.intCol1 = intCol1;\n" +
                "}\n";
        assertEquals(expected, tgColumn.getSetterDef());
    }

    @Test
    void testToString() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        String expected = "TgColumn name=int_col1, \ttype=INT, \tprecision=9, \tscale=0, \tjavaType=int, \tisPrimaryKey=false\n";
        assertEquals(expected, tgColumn.toString());
    }

    @Test
    void testGetStoreValue() {
        TgColumn tgColumn = new TgColumn("int_col1", "INT");
        assertEquals(234567890, tgColumn.getStoreValue(1234567890));

        tgColumn.setColumnType("BIGINT");
        assertEquals(1234567890L, tgColumn.getStoreValue(1234567890));
        tgColumn.setColumnType("REAL");
        assertEquals(4567890f, tgColumn.getStoreValue(1234567890));
        tgColumn.setColumnType("DOUBLE");
        assertEquals(1234567890D, tgColumn.getStoreValue(1234567890));
        tgColumn.setColumnType("DECIMAL");
        assertEquals(1234567890, tgColumn.getStoreValue(1234567890));
        tgColumn.setColumnType("DECIMAL(5, 2)");
        assertEquals(234, tgColumn.getStoreValue(1234));
        tgColumn.setColumnType("CHAR(1)");
        assertEquals(0, tgColumn.getStoreValue(1234567890));
    }
}
