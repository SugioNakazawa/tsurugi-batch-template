DROP TABLE IF EXISTS applications;

CREATE TABLE applications (
/** id: */
id BIGINT NULL,
/** 席ゾーン: */
seat_zone INT NULL,
/** 申込席数: */
apply_num INT NULL,
/** 割り当て済みフラグ:0:false,1:true booleanが定義できない。 */
assigned_flag INT NULL
,
PRIMARY KEY (id)
);

