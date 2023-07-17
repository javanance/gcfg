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
///@IdClass(IrCurveHis2Id.class)
@Table(schema="IQCMOWN", name ="IR_CURVE_HIS_TEMP")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrCurveHis2 implements Serializable, EntityIdIdentifier {	
	
	private static final long serialVersionUID = 452146475833993395L;

	@Id
	@Column(name="BASE_DATE", nullable=false)
	private String baseDate;	

	@Id
	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	
	
	@Id
	@Column(name="MAT_CD", nullable=false)
	private String matCd;
	
	private Double intRate;
	
	/**
	 * Simple entity of IrCurveHis -> Input for IR Model
	 */
	public IrCurveHis2() {}
	
	

}
