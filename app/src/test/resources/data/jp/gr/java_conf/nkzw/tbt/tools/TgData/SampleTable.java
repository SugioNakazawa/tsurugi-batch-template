package data.jp.gr.java_conf.nkzw.tbt.tools.TgData;
//  genetrated by TgData.
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.tsurugidb.iceaxe.sql.result.TgResultMapping;
import com.tsurugidb.iceaxe.sql.parameter.TgParameterMapping;
public class SampleTable implements Cloneable {
private int intCol1;
private long bigintCol2;
private float realCol3;
private double doubleCol4;
private BigDecimal decimalCol5;
private BigDecimal decimalCol6;
private String charCol7;
private String characterCol8;
private String varcharCol9;
private String charVaryingCol10;
private String characterVaryingCol11;
private byte[] binaryCol12;
private byte[] varbinaryCol13;
private byte[] binaryVaryingCol14;
private LocalDate dateCol15;
private LocalTime timeCol16;
private LocalDateTime timestampCol17;
private OffsetDateTime timestampWithTimeZoneCol18;
public SampleTable() {
}
public void setIntCol1(int intCol1) {
	this.intCol1 = intCol1;
}
public int getIntCol1() {
	return this.intCol1;
}
public void setBigintCol2(long bigintCol2) {
	this.bigintCol2 = bigintCol2;
}
public long getBigintCol2() {
	return this.bigintCol2;
}
public void setRealCol3(float realCol3) {
	this.realCol3 = realCol3;
}
public float getRealCol3() {
	return this.realCol3;
}
public void setDoubleCol4(double doubleCol4) {
	this.doubleCol4 = doubleCol4;
}
public double getDoubleCol4() {
	return this.doubleCol4;
}
public void setDecimalCol5(BigDecimal decimalCol5) {
	this.decimalCol5 = decimalCol5;
}
public BigDecimal getDecimalCol5() {
	return this.decimalCol5;
}
public void setDecimalCol6(BigDecimal decimalCol6) {
	this.decimalCol6 = decimalCol6;
}
public BigDecimal getDecimalCol6() {
	return this.decimalCol6;
}
public void setCharCol7(String charCol7) {
	this.charCol7 = charCol7;
}
public String getCharCol7() {
	return this.charCol7;
}
public void setCharacterCol8(String characterCol8) {
	this.characterCol8 = characterCol8;
}
public String getCharacterCol8() {
	return this.characterCol8;
}
public void setVarcharCol9(String varcharCol9) {
	this.varcharCol9 = varcharCol9;
}
public String getVarcharCol9() {
	return this.varcharCol9;
}
public void setCharVaryingCol10(String charVaryingCol10) {
	this.charVaryingCol10 = charVaryingCol10;
}
public String getCharVaryingCol10() {
	return this.charVaryingCol10;
}
public void setCharacterVaryingCol11(String characterVaryingCol11) {
	this.characterVaryingCol11 = characterVaryingCol11;
}
public String getCharacterVaryingCol11() {
	return this.characterVaryingCol11;
}
public void setBinaryCol12(byte[] binaryCol12) {
	this.binaryCol12 = binaryCol12;
}
public byte[] getBinaryCol12() {
	return this.binaryCol12;
}
public void setVarbinaryCol13(byte[] varbinaryCol13) {
	this.varbinaryCol13 = varbinaryCol13;
}
public byte[] getVarbinaryCol13() {
	return this.varbinaryCol13;
}
public void setBinaryVaryingCol14(byte[] binaryVaryingCol14) {
	this.binaryVaryingCol14 = binaryVaryingCol14;
}
public byte[] getBinaryVaryingCol14() {
	return this.binaryVaryingCol14;
}
public void setDateCol15(LocalDate dateCol15) {
	this.dateCol15 = dateCol15;
}
public LocalDate getDateCol15() {
	return this.dateCol15;
}
public void setTimeCol16(LocalTime timeCol16) {
	this.timeCol16 = timeCol16;
}
public LocalTime getTimeCol16() {
	return this.timeCol16;
}
public void setTimestampCol17(LocalDateTime timestampCol17) {
	this.timestampCol17 = timestampCol17;
}
public LocalDateTime getTimestampCol17() {
	return this.timestampCol17;
}
public void setTimestampWithTimeZoneCol18(OffsetDateTime timestampWithTimeZoneCol18) {
	this.timestampWithTimeZoneCol18 = timestampWithTimeZoneCol18;
}
public OffsetDateTime getTimestampWithTimeZoneCol18() {
	return this.timestampWithTimeZoneCol18;
}
//  genetrated by TgData.
public static final TgResultMapping<SampleTable> RESULT_MAPPING = TgResultMapping.of(SampleTable::new) //
.addInt("int_col1", SampleTable::setIntCol1)
.addLong("bigint_col2", SampleTable::setBigintCol2)
.addFloat("real_col3", SampleTable::setRealCol3)
.addDouble("double_col4", SampleTable::setDoubleCol4)
.addDecimal("decimal_col5", SampleTable::setDecimalCol5)
.addDecimal("decimal_col6", SampleTable::setDecimalCol6)
.addString("char_col7", SampleTable::setCharCol7)
.addString("character_col8", SampleTable::setCharacterCol8)
.addString("varchar_col9", SampleTable::setVarcharCol9)
.addString("char_varying_col10", SampleTable::setCharVaryingCol10)
.addString("character_varying_col11", SampleTable::setCharacterVaryingCol11)
.addBytes("binary_col12", SampleTable::setBinaryCol12)
.addBytes("varbinary_col13", SampleTable::setVarbinaryCol13)
.addBytes("binary_varying_col14", SampleTable::setBinaryVaryingCol14)
.addDate("date_col15", SampleTable::setDateCol15)
.addTime("time_col16", SampleTable::setTimeCol16)
.addDateTime("timestamp_col17", SampleTable::setTimestampCol17)
.addOffsetDateTime("timestamp_with_time_zone_col18", SampleTable::setTimestampWithTimeZoneCol18)
;
//  genetrated by TgData.
public static final TgParameterMapping<SampleTable> PARAMETER_MAPPING = TgParameterMapping.of(SampleTable.class)
.addInt("int_col1",SampleTable::getIntCol1)
.addLong("bigint_col2",SampleTable::getBigintCol2)
.addFloat("real_col3",SampleTable::getRealCol3)
.addDouble("double_col4",SampleTable::getDoubleCol4)
.addDecimal("decimal_col5",SampleTable::getDecimalCol5)
.addDecimal("decimal_col6",SampleTable::getDecimalCol6)
.addString("char_col7",SampleTable::getCharCol7)
.addString("character_col8",SampleTable::getCharacterCol8)
.addString("varchar_col9",SampleTable::getVarcharCol9)
.addString("char_varying_col10",SampleTable::getCharVaryingCol10)
.addString("character_varying_col11",SampleTable::getCharacterVaryingCol11)
.addBytes("binary_col12",SampleTable::getBinaryCol12)
.addBytes("varbinary_col13",SampleTable::getVarbinaryCol13)
.addBytes("binary_varying_col14",SampleTable::getBinaryVaryingCol14)
.add("date_col15",LocalDate.class, SampleTable::getDateCol15)
.add("time_col16",LocalTime.class, SampleTable::getTimeCol16)
.add("timestamp_col17",LocalDateTime.class, SampleTable::getTimestampCol17)
.add("timestamp_with_time_zone_col18",OffsetDateTime.class, SampleTable::getTimestampWithTimeZoneCol18)
;
//  genetrated by TgData.
public static String toValuesName() {
return ":int_col1, :bigint_col2, :real_col3, :double_col4, :decimal_col5, :decimal_col6, :char_col7, :character_col8, :varchar_col9, :char_varying_col10, :character_varying_col11, :binary_col12, :varbinary_col13, :binary_varying_col14, :date_col15, :time_col16, :timestamp_col17, :timestamp_with_time_zone_col18";
}
//  genetrated by TgData.
    @Override
    public SampleTable clone() {
        try {
            return (SampleTable) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
