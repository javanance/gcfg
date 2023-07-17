package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.gof.interfaces.EntityIdIdentifier;


public class IrmodelResult implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = -6151628283658004576L;

//	//@Id
//	private String baseDate;	

	private String resultType;
	
	//@Id
	private Double matTerm;	
	
	private LocalDate matDate;
    
	private Double spotCont;
	
	private Double spotDisc;
	
	private Double fwdCont;
	
	private Double fwdDisc;
    	
	public IrmodelResult() {}

//	public String getBaseDate() {
//		return baseDate;
//	}
//
//	public void setBaseDate(String baseDate) {
//		this.baseDate = baseDate;
//	}

	public String getResultType() {
		return resultType;
	}
	
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public Double getMatTerm() {
		return matTerm;
	}

	public void setMatTerm(Double matTerm) {
		this.matTerm = matTerm;
	}	

	public LocalDate getMatDate() {
		return matDate;
	}

	public void setMatDate(LocalDate matDate) {
		this.matDate = matDate;
	}

	public Double getSpotCont() {
		return spotCont;
	}

	public void setSpotCont(Double spotCont) {
		this.spotCont = spotCont;
	}

	public Double getSpotDisc() {
		return spotDisc;
	}

	public void setSpotDisc(Double spotDisc) {
		this.spotDisc = spotDisc;
	}

	public Double getFwdCont() {
		return fwdCont;
	}

	public void setFwdCont(Double fwdCont) {
		this.fwdCont = fwdCont;
	}

	public Double getFwdDisc() {
		return fwdDisc;
	}

	public void setFwdDisc(Double fwdDisc) {
		this.fwdDisc = fwdDisc;
	}

}


