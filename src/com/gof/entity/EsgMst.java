package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import javax.persistence.Column;


@Entity
@Table(schema="QCM", name ="EAS_ESG_MST")
public class EsgMst implements Serializable, EntityIdIdentifier {
	
	private static final long serialVersionUID = -5543511960388092730L;

	private String irModelId;
	
	private String irModelNm;	
	
	public EsgMst() {}

	@Id	
	@Column(name ="IR_MODEL_ID")
	public String getIrModelId() {
		return irModelId;
	}

	public void setIrModelId(String irModelId) {
		this.irModelId = irModelId;
	}

	public String getIrModelNm() {
		return irModelNm;
	}

	public void setIrModelNm(String irModelNm) {
		this.irModelNm = irModelNm;
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
		return "EsgMst [irModelId=" + irModelId + ", irModelNm=" + irModelNm + "]";
	}	

}
