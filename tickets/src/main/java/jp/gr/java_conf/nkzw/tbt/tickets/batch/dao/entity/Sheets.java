package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity;
//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
public class Sheets implements Cloneable {
private long id;
private int rowNo;
private int seatNo;
private int seatZone;
private long assignedApplicationId;
public Sheets() {
}
public void setId(long id) {
	this.id = id;
}
public long getId() {
	return this.id;
}
public void setRowNo(int rowNo) {
	this.rowNo = rowNo;
}
public int getRowNo() {
	return this.rowNo;
}
public void setSeatNo(int seatNo) {
	this.seatNo = seatNo;
}
public int getSeatNo() {
	return this.seatNo;
}
public void setSeatZone(int seatZone) {
	this.seatZone = seatZone;
}
public int getSeatZone() {
	return this.seatZone;
}
public void setAssignedApplicationId(long assignedApplicationId) {
	this.assignedApplicationId = assignedApplicationId;
}
public long getAssignedApplicationId() {
	return this.assignedApplicationId;
}
//  genetrated by TgData.
public static final TgResultMapping<Sheets> RESULT_MAPPING = TgResultMapping.of(Sheets::new) //
.addLong("id", Sheets::setId)
.addInt("row_no", Sheets::setRowNo)
.addInt("seat_no", Sheets::setSeatNo)
.addInt("seat_zone", Sheets::setSeatZone)
.addLong("assigned_application_id", Sheets::setAssignedApplicationId)
;
//  genetrated by TgData.
public static final TgParameterMapping<Sheets> PARAMETER_MAPPING = TgParameterMapping.of(Sheets.class)
.addLong("id",Sheets::getId)
.addInt("row_no",Sheets::getRowNo)
.addInt("seat_no",Sheets::getSeatNo)
.addInt("seat_zone",Sheets::getSeatZone)
.addLong("assigned_application_id",Sheets::getAssignedApplicationId)
;
//  genetrated by TgData.
public static String toValuesName() {
return ":id, :row_no, :seat_no, :seat_zone, :assigned_application_id";
}
//  genetrated by TgData.
    @Override
    public Sheets clone() {
        try {
            return (Sheets) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
