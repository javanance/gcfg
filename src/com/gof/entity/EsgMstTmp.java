package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import javax.persistence.Column;


@Entity
@Table(schema="QCM", name ="EAS_ESG_MST_TEMP")
public class EsgMstTmp implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = 5370330968175334208L;

	@Id	
	@Column(name ="IR_MODEL_ID")	
	private String irModelId;
	
	private String irModelNm;	
	
	@Lob
	@Column(name="REMARK", columnDefinition="CLOB NOT NULL")
	private String remark;	
	
	public EsgMstTmp() {}

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
		return "EsgMstTmp [irModelId=" + irModelId + ", irModelNm=" + irModelNm + ", remark=" + remark + "]";
	}	

}
