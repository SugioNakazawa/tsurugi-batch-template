package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity;

//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;

public class Seats implements Cloneable {
	private long id;
	private int rowNo;
	private int seatNo;
	private int seatZone;
	private long assignedApplicationId;

	public Seats() {
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

	// genetrated by TgData.
	public static final TgResultMapping<Seats> RESULT_MAPPING = TgResultMapping.of(Seats::new) //
			.addLong("id", Seats::setId)
			.addInt("row_no", Seats::setRowNo)
			.addInt("seat_no", Seats::setSeatNo)
			.addInt("seat_zone", Seats::setSeatZone)
			.addLong("assigned_application_id", Seats::setAssignedApplicationId);
	// genetrated by TgData.
	public static final TgParameterMapping<Seats> PARAMETER_MAPPING = TgParameterMapping.of(Seats.class)
			.addLong("id", Seats::getId)
			.addInt("row_no", Seats::getRowNo)
			.addInt("seat_no", Seats::getSeatNo)
			.addInt("seat_zone", Seats::getSeatZone)
			.addLong("assigned_application_id", Seats::getAssignedApplicationId);

	// genetrated by TgData.
	public static String toValuesName() {
		return ":id, :row_no, :seat_no, :seat_zone, :assigned_application_id";
	}

	// genetrated by TgData.
	@Override
	public Seats clone() {
		try {
			return (Seats) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("Cloning not supported", e);
		}
	}
}
