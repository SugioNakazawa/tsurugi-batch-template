DROP TABLE IF EXISTS sample_table2;

CREATE TABLE sample_table2 (
/** カラム１:INTのサンプルカラム */
int_col1 INT NOT NULL,
/** カラム２:BIGINTのサンプルカラム */
bigint_col2 BIGINT NOT NULL,
/** カラム３:REALのサンプルカラム */
real_col3 REAL NULL DEFAULT 0,
/** カラム４:DOUBLEのサンプルカラム */
double_col4 DOUBLE NULL DEFAULT 0,
/** カラム５:DECIMAL(10,2)のサンプルカラム */
decimal_col5 DECIMAL(10,2) NULL,
/** カラム６:DECIMALのサンプルカラム */
decimal_col6 DECIMAL NULL,
/** カラム７:CHAR(10)のサンプルカラム */
char_col7 CHAR(10) NULL,
/** カラム８:CHARACTER(10)のサンプルカラム */
character_col8 CHARACTER(10) NULL,
/** カラム９:VARCHAR(12)のサンプルカラム */
varchar_col9 VARCHAR(12) NULL,
/** カラム１０:CHAR VARYING(12)のサンプルカラム */
char_varying_col10 CHAR VARYING(12) NULL,
/** カラム１１:CHARACTER VARYINGのサンプルカラム */
character_varying_col11 CHARACTER VARYING NULL,
/** カラム１２:BINARY(14)のサンプルカラム */
binary_col12 BINARY(14) NULL,
/** カラム１３:VARBINARYのサンプルカラム */
varbinary_col13 VARBINARY NULL,
/** カラム１４:BINARY VARYINGのサンプルカラム */
binary_varying_col14 BINARY VARYING NULL,
/** カラム１５:DATEのサンプルカラム */
date_col15 DATE NULL,
/** カラム１６:TIMEのサンプルカラム */
time_col16 TIME NULL,
/** カラム１７:TIMESTAMP のサンプルカラム */
timestamp_col17 TIMESTAMP NULL,
/** カラム１８:TIMESTAMP WITH TIME ZONEのサンプルカラム */
timestamp_with_time_zone_col18 TIMESTAMP WITH TIME ZONE NULL
,
PRIMARY KEY (int_col1, bigint_col2)
);

CREATE INDEX sample_table2_idx1 ON sample_table2 (char_col7);
