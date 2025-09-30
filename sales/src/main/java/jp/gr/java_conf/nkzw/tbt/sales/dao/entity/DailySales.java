package jp.gr.java_conf.nkzw.tbt.sales.dao.entity;
//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
public class DailySales implements Cloneable {
private long itemId;
private int salesYear;
private int salesMonth;
private int salesDay;
private int salesQty;
private BigDecimal salesAmount;
private LocalDateTime createdAt;
public DailySales() {
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
public static final TgResultMapping<DailySales> RESULT_MAPPING = TgResultMapping.of(DailySales::new) //
.addLong("item_id", DailySales::setItemId)
.addInt("sales_year", DailySales::setSalesYear)
.addInt("sales_month", DailySales::setSalesMonth)
.addInt("sales_day", DailySales::setSalesDay)
.addInt("sales_qty", DailySales::setSalesQty)
.addDecimal("sales_amount", DailySales::setSalesAmount)
.addDateTime("created_at", DailySales::setCreatedAt)
;
//  genetrated by TgData.
public static final TgParameterMapping<DailySales> PARAMETER_MAPPING = TgParameterMapping.of(DailySales.class)
.addLong("item_id",DailySales::getItemId)
.addInt("sales_year",DailySales::getSalesYear)
.addInt("sales_month",DailySales::getSalesMonth)
.addInt("sales_day",DailySales::getSalesDay)
.addInt("sales_qty",DailySales::getSalesQty)
.addDecimal("sales_amount",DailySales::getSalesAmount)
.add("created_at",LocalDateTime.class, DailySales::getCreatedAt)
;
//  genetrated by TgData.
public static String toValuesName() {
return ":item_id, :sales_year, :sales_month, :sales_day, :sales_qty, :sales_amount, :created_at";
}
//  genetrated by TgData.
    @Override
    public DailySales clone() {
        try {
            return (DailySales) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
