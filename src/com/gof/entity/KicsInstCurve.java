package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@Table(schema="IQCMOWN", name ="Q_IC_INST_CURVE")
@Table(name ="Q_IC_INST_CURVE")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class KicsInstCurve implements Serializable, EntityIdIdentifier {	
	
	private static final long serialVersionUID = -2197460732905486914L;
	
	@Id
	@Column(name="EXPO_ID")
	private String expoId;
	
	@Id
	@Column(name="IRATE_CURVE_ID")
	private String irateCurveId;

//	private String lastModifiedBy;	
//	
//	private LocalDate lastUpdateDate;	
	
	public KicsInstCurve() {}
	
}
