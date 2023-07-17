package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;


@Entity
@Table(schema="QCM", name ="KICS_SHCK_STK")
public class KicsShckStk implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = -3798362460194158069L;

	@Id
	@Column(name="STK_KND_CD")
	private String stkKndCd;
	
	@Id
	@Column(name="KICS_CRD_GRD")
	private String kicsCrdGrd;
	
	@Id
	@Column(name="APLY_STRT_DATE")	
	private String aplyStrtDate;
	
	private String aplyEndDate;		
	
	private Double shckVal;	

	public KicsShckStk() {}

	public String getStkKndCd() {
		return stkKndCd;
	}

	public void setStkKndCd(String stkKndCd) {
		this.stkKndCd = stkKndCd;
	}

	public String getKicsCrdGrd() {
		return kicsCrdGrd;
	}

	public void setKicsCrdGrd(String kicsCrdGrd) {
		this.kicsCrdGrd = kicsCrdGrd;
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

	public Double getShckVal() {
		return shckVal;
	}

	public void setShckVal(Double shckVal) {
		this.shckVal = shckVal;
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
		return "KicsShckStk [stkKndCd=" + stkKndCd + ", kicsCrdGrd=" + kicsCrdGrd + ", aplyStrtDate=" + aplyStrtDate
				+ ", aplyEndDate=" + aplyEndDate + ", shckVal=" + shckVal + "]";
	}	
	
}
