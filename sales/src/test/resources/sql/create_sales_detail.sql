DROP TABLE IF EXISTS sales_detail;

CREATE TABLE sales_detail (
/** 商品ID: */
item_id BIGINT NOT NULL,
/** 販売年: */
sales_year INT NOT NULL,
/** 販売月: */
sales_month INT NOT NULL,
/** 販売日: */
sales_day INT NOT NULL,
/** 販売時: */
sales_hour INT NOT NULL,
/** 販売分: */
sales_minute INT NOT NULL,
/** 販売数: */
sales_qty INT NULL DEFAULT 0,
/** 販売金額: */
sales_amount DECIMAL NULL DEFAULT 0,
/** 作成日時: */
created_at TIMESTAMP NULL DEFAULT LOCALTIMESTAMP
,
PRIMARY KEY (item_id, sales_year, sales_month, sales_day, sales_hour, sales_minute)
);

