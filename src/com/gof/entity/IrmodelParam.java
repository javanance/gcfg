package com.gof.entity;

import java.io.Serializable;

import com.gof.interfaces.EntityIdIdentifier;


public class IrmodelParam implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = -7294135953944397163L;

	//@Id
	private Integer sequence;	

	private Double param1;	
    
	private Double param2;
	
	private Double param3;
	
	private Double param4;
	
	private Double param5;
    	
	public IrmodelParam() {}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Double getParam1() {
		return param1;
	}

	public void setParam1(Double param1) {
		this.param1 = param1;
	}

	public Double getParam2() {
		return param2;
	}

	public void setParam2(Double param2) {
		this.param2 = param2;
	}

	public Double getParam3() {
		return param3;
	}

	public void setParam3(Double param3) {
		this.param3 = param3;
	}

	public Double getParam4() {
		return param4;
	}

	public void setParam4(Double param4) {
		this.param4 = param4;
	}

	public Double getParam5() {
		return param5;
	}

	public void setParam5(Double param5) {
		this.param5 = param5;
	}	

}


