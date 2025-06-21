DROP TABLE IF EXISTS seats;

CREATE TABLE seats (
/** id: */
id BIGINT NOT NULL,
/** 列番号: */
row_no INT NOT NULL,
/** 席番号: */
seat_no INT NOT NULL,
/** 席ゾーン: */
seat_zone INT NULL DEFAULT 0,
/** 割り当て申込ID: */
assigned_application_id BIGINT NULL DEFAULT 0
,
PRIMARY KEY (id)
);

CREATE INDEX seat_idx1 ON seats (row_no,seat_no);
