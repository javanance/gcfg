package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.interfaces.EntityIdIdentifier;


@Entity
@Table(schema="QCM", name ="KICS_SCE_CHNG_MST")
public class KicsSceChngMst implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = 4636817366615837497L;

	@Id
	@Column(name="SCE_NO")
	private Integer sceNo;	
	
	@Id
	@Column(name="APLY_STRT_DATE")	
	private String aplyStrtDate;
	
	private String aplyEndDate;
	
	private String sceNm;
	
	@Transient
	private String stkChngTypCd;
	
	@Transient
	private Double stkChngVal;
	
	private String fxChngTypCd;
	
	private Double fxChngVal;
	
	private String ralChngTypCd;
	
	private Double ralChngVal;
	
	private String volChngTypCd;
	
	private Double volChngVal;	

	public KicsSceChngMst() {}	

	public Integer getSceNo() {
		return sceNo;
	}

	public void setSceNo(Integer sceNo) {
		this.sceNo = sceNo;
	}

	public String getAplyStrtDate() {
		return aplyStrtDate;
	}

	public void setAplyStrtDate(String aplyStrtDate) {
		this.aplyStrtDate = aplyStrtDate;
	}

	public String getAplyEndDate() {
		return aplyEndDate;
	}

	public void setAplyEndDate(String aplyEndDate) {
		this.aplyEndDate = aplyEndDate;
	}

	public String getSceNm() {
		return sceNm;
	}

	public void setSceNm(String sceNm) {
		this.sceNm = sceNm;
	}

	public String getStkChngTypCd() {
		return stkChngTypCd;
	}

	public void setStkChngTypCd(String stkChngTypCd) {
		this.stkChngTypCd = stkChngTypCd;
	}

	public Double getStkChngVal() {
		return stkChngVal;
	}

	public void setStkChngVal(Double stkChngVal) {
		this.stkChngVal = stkChngVal;
	}

	public String getFxChngTypCd() {
		return fxChngTypCd;
	}

	public void setFxChngTypCd(String fxChngTypCd) {
		this.fxChngTypCd = fxChngTypCd;
	}

	public Double getFxChngVal() {
		return fxChngVal;
	}

	public void setFxChngVal(Double fxChngVal) {
		this.fxChngVal = fxChngVal;
	}

	public String getRalChngTypCd() {
		return ralChngTypCd;
	}

	public void setRalChngTypCd(String ralChngTypCd) {
		this.ralChngTypCd = ralChngTypCd;
	}

	public Double getRalChngVal() {
		return ralChngVal;
	}

	public void setRalChngVal(Double ralChngVal) {
		this.ralChngVal = ralChngVal;
	}

	public String getVolChngTypCd() {
		return volChngTypCd;
	}

	public void setVolChngTypCd(String volChngTypCd) {
		this.volChngTypCd = volChngTypCd;
	}

	public Double getVolChngVal() {
		return volChngVal;
	}

	public void setVolChngVal(Double volChngVal) {
		this.volChngVal = volChngVal;
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
		return "KicsSceChngMst [scenNum=" + sceNo + ", aplyStrtDate=" + aplyStrtDate + ", aplyEndDate=" + aplyEndDate
				+ ", sceNm=" + sceNm + ", stkChngTypCd=" + stkChngTypCd + ", stkChngVal=" + stkChngVal
				+ ", fxChngTypCd=" + fxChngTypCd + ", fxChngVal=" + fxChngVal + ", ralChngTypCd=" + ralChngTypCd
				+ ", ralChngVal=" + ralChngVal + ", volChngTypCd=" + volChngTypCd + ", volChngVal=" + volChngVal + "]";
	}
	
}
