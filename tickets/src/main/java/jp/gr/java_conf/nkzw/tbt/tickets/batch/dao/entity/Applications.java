package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity;
//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
public class Applications implements Cloneable {
private long id;
private int seatZone;
private int applyNum;
private int assignedFlag;
public Applications() {
}
public void setId(long id) {
	this.id = id;
}
public long getId() {
	return this.id;
}
public void setSeatZone(int seatZone) {
	this.seatZone = seatZone;
}
public int getSeatZone() {
	return this.seatZone;
}
public void setApplyNum(int applyNum) {
	this.applyNum = applyNum;
}
public int getApplyNum() {
	return this.applyNum;
}
public void setAssignedFlag(int assignedFlag) {
	this.assignedFlag = assignedFlag;
}
public int getAssignedFlag() {
	return this.assignedFlag;
}
//  genetrated by TgData.
public static final TgResultMapping<Applications> RESULT_MAPPING = TgResultMapping.of(Applications::new) //
.addLong("id", Applications::setId)
.addInt("seat_zone", Applications::setSeatZone)
.addInt("apply_num", Applications::setApplyNum)
.addInt("assigned_flag", Applications::setAssignedFlag)
;
//  genetrated by TgData.
public static final TgParameterMapping<Applications> PARAMETER_MAPPING = TgParameterMapping.of(Applications.class)
.addLong("id",Applications::getId)
.addInt("seat_zone",Applications::getSeatZone)
.addInt("apply_num",Applications::getApplyNum)
.addInt("assigned_flag",Applications::getAssignedFlag)
;
//  genetrated by TgData.
public static String toValuesName() {
return ":id, :seat_zone, :apply_num, :assigned_flag";
}
//  genetrated by TgData.
    @Override
    public Applications clone() {
        try {
            return (Applications) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
