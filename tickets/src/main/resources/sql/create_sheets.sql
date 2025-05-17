CREATE TABLE sheets (
id BIGINT,
row_no INT,
seat_no INT,
seat_zone INT,
assigned_application_id BIGINT,
PRIMARY KEY (id)
);

CREATE INDEX idx_sheets_1 ON sheets (row_no, seat_no);
CREATE INDEX idx_sheets_2 ON sheets (assigned_application_id);