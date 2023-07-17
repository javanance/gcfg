package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;


@Entity
@Table(schema="QCM", name ="KICS_FSS_SCENARIO")
public class KicsFssScenario implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = -1046687391931918267L;

	@Id
	@Column(name="AS_OF_DATE")
	private LocalDate asOfDate;	
	
	@Id
	@Column(name="FSS_SCEN_TYP")
	private String fssScenTyp;
	
	@Id
	@Column(name="SCEN_NUM")
	private Integer scenNum;	
	
	@Id
	@Column(name="BUCKET_START_DATE")	
	private LocalDate bucketStartDate;
	
	@Id
	@Column(name="BUCKET_END_DATE")	
	private LocalDate bucketEndDate;
	
	private Double value;	
	
	public KicsFssScenario() {}	

	public LocalDate getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(LocalDate asOfDate) {
		this.asOfDate = asOfDate;
	}

	public String getFssScenTyp() {
		return fssScenTyp;
	}

	public void setFssScenTyp(String fssScenTyp) {
		this.fssScenTyp = fssScenTyp;
	}

	public Integer getScenNum() {
		return scenNum;
	}

	public void setScenNum(Integer scenNum) {
		this.scenNum = scenNum;
	}

	public LocalDate getBucketStartDate() {
		return bucketStartDate;
	}

	public void setBucketStartDate(LocalDate bucketStartDate) {
		this.bucketStartDate = bucketStartDate;
	}

	public LocalDate getBucketEndDate() {
		return bucketEndDate;
	}

	public void setBucketEndDate(LocalDate bucketEndDate) {
		this.bucketEndDate = bucketEndDate;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
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
		return "KicsFssScenario [asOfDate=" + asOfDate + ", fssScenTyp=" + fssScenTyp + ", scenNum=" + scenNum
				+ ", bucketStartDate=" + bucketStartDate + ", bucketEndDate=" + bucketEndDate + ", value=" + value
				+ "]";
	}
	
}
