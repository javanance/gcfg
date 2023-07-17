package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdIdentifier;


@Embeddable
public class IrSceId implements Serializable, EntityIdIdentifier {
	
	private static final long serialVersionUID = 205371050298889931L;

	@Column(name="BASE_DATE", nullable=false)
	private String baseDate;
	
	@Column(name="IR_MODEL_ID", nullable=false)
	private String irModelId;

	@Column(name="MAT_CD", nullable=false)
	private String matCd;

	@Column(name="SCE_NO", nullable=false)
	private String sceNo;

	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	
	
	public IrSceId() {}

	public String getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}

	public String getIrModelId() {
		return irModelId;
	}

	public void setIrModelId(String irModelId) {
		this.irModelId = irModelId;
	}

	public String getMatCd() {
		return matCd;
	}

	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}

	public String getSceNo() {
		return sceNo;
	}

	public void setSceNo(String sceNo) {
		this.sceNo = sceNo;
	}

	public String getIrCurveId() {
		return irCurveId;
	}

	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
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
		return "IrSceId [baseDate=" + baseDate + ", irModelId=" + irModelId + ", matCd=" + matCd + ", scenNum=" + sceNo
				+ ", irCurveId=" + irCurveId + "]";
	}
			
}
