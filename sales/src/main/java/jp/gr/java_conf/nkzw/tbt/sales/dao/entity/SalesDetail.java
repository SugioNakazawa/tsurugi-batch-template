package jp.gr.java_conf.nkzw.tbt.sales.dao.entity;
//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
public class SalesDetail implements Cloneable {
private long itemId;
private int salesYear;
private int salesMonth;
private int salesDay;
private int salesHour;
private int salesMinute;
private int salesQty;
private BigDecimal salesAmount;
private LocalDateTime createdAt;
public SalesDetail() {
}
public void setItemId(long itemId) {
	this.itemId = itemId;
}
public long getItemId() {
	return this.itemId;
}
public void setSalesYear(int salesYear) {
	this.salesYear = salesYear;
}
public int getSalesYear() {
	return this.salesYear;
}
public void setSalesMonth(int salesMonth) {
	this.salesMonth = salesMonth;
}
public int getSalesMonth() {
	return this.salesMonth;
}
public void setSalesDay(int salesDay) {
	this.salesDay = salesDay;
}
public int getSalesDay() {
	return this.salesDay;
}
public void setSalesHour(int salesHour) {
	this.salesHour = salesHour;
}
public int getSalesHour() {
	return this.salesHour;
}
public void setSalesMinute(int salesMinute) {
	this.salesMinute = salesMinute;
}
public int getSalesMinute() {
	return this.salesMinute;
}
public void setSalesQty(int salesQty) {
	this.salesQty = salesQty;
}
public int getSalesQty() {
	return this.salesQty;
}
public void setSalesAmount(BigDecimal salesAmount) {
	this.salesAmount = salesAmount;
}
public BigDecimal getSalesAmount() {
	return this.salesAmount;
}
public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}
public LocalDateTime getCreatedAt() {
	return this.createdAt;
}
//  genetrated by TgData.
public static final TgResultMapping<SalesDetail> RESULT_MAPPING = TgResultMapping.of(SalesDetail::new) //
.addLong("item_id", SalesDetail::setItemId)
.addInt("sales_year", SalesDetail::setSalesYear)
.addInt("sales_month", SalesDetail::setSalesMonth)
.addInt("sales_day", SalesDetail::setSalesDay)
.addInt("sales_hour", SalesDetail::setSalesHour)
.addInt("sales_minute", SalesDetail::setSalesMinute)
.addInt("sales_qty", SalesDetail::setSalesQty)
.addDecimal("sales_amount", SalesDetail::setSalesAmount)
.addDateTime("created_at", SalesDetail::setCreatedAt)
;
//  genetrated by TgData.
public static final TgParameterMapping<SalesDetail> PARAMETER_MAPPING = TgParameterMapping.of(SalesDetail.class)
.addLong("item_id",SalesDetail::getItemId)
.addInt("sales_year",SalesDetail::getSalesYear)
.addInt("sales_month",SalesDetail::getSalesMonth)
.addInt("sales_day",SalesDetail::getSalesDay)
.addInt("sales_hour",SalesDetail::getSalesHour)
.addInt("sales_minute",SalesDetail::getSalesMinute)
.addInt("sales_qty",SalesDetail::getSalesQty)
.addDecimal("sales_amount",SalesDetail::getSalesAmount)
.add("created_at",LocalDateTime.class, SalesDetail::getCreatedAt)
;
//  genetrated by TgData.
public static String toValuesName() {
return ":item_id, :sales_year, :sales_month, :sales_day, :sales_hour, :sales_minute, :sales_qty, :sales_amount, :created_at";
}
//  genetrated by TgData.
    @Override
    public SalesDetail clone() {
        try {
            return (SalesDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
