package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdIdentifier;


@Embeddable
public class IrCurveHis2Id implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = 700195763463719771L;

	@Column(name="BASE_DATE", nullable=false) 
	private String baseDate;	
		
	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;

	public IrCurveHis2Id() {}
	
	public String getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}
	
	public String getIrCurveId() {
		return irCurveId;
	}

	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	
	public String getMatCd() {
		return matCd;
	}

	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "IrCurveHisId [baseDate=" + baseDate + ", irCurveId=" + irCurveId + ", matCd=" + matCd + "]";
	}
	
}
