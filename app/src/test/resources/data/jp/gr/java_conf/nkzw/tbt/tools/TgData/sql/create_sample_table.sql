CREATE TABLE sample_table (
int_col1 INT,
bigint_col2 BIGINT,
real_col3 REAL,
double_col4 DOUBLE,
decimal_col5 DECIMAL(10,2),
decimal_col6 DECIMAL,
char_col7 CHAR(10),
character_col8 CHARACTER(10),
varchar_col9 VARCHAR(12),
char_varying_col10 CHAR VARYING(12),
character_varying_col11 CHARACTER VARYING,
binary_col12 BINARY(14),
varbinary_col13 VARBINARY,
binary_varying_col14 BINARY VARYING,
date_col15 DATE,
time_col16 TIME,
timestamp_col17 TIMESTAMP,
timestamp_with_time_zone_col18 TIMESTAMP WITH TIME ZONE,
PRIMARY KEY (int_col1, bigint_col2)
);

CREATE INDEX sample_table_idx1 ON sample_table (char_col7);
