package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdIdentifier;


@Entity
@IdClass(IrSceId.class)
@Table(schema="QCM", name ="EAS_IR_SCE")
@FilterDef(name="FILTER", parameters= { @ParamDef(name="baseDate", type="string") })
@Filters( { @Filter(name ="FILTER", condition="BASE_DATE = :baseDate") } )
public class IrSce implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id
	private String baseDate;
	
    @Id
	private String irModelId;

    @Id
	private String matCd;

    @Id
	private String sceNo;

    @Id
	private String irCurveId;	
	
	private Double rfIr;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
	@NotFound(action=NotFoundAction.IGNORE)
	private EsgMst esgMst;	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable=false)
	@NotFound(action=NotFoundAction.IGNORE)
	private IrCurve irCurve;	
	
	public IrSce() {}

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

	public Double getRfIr() {
		return rfIr;
	}

	public void setRfIr(Double rfIr) {
		this.rfIr = rfIr;
	}

	public EsgMst getEsgMst() {
		return esgMst;
	}

	public void setEsgmst(EsgMst esgMst) {
		this.esgMst = esgMst;
	}

	public IrCurve getIrCurve() {
		return irCurve;
	}

	public void setIrCurve(IrCurve irCurve) {
		this.irCurve = irCurve;
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
		return "IrSce [baseDate=" + baseDate + ", irModelId=" + irModelId + ", matCd=" + matCd + ", scenNum=" + sceNo
				+ ", irCurveId=" + irCurveId + ", rfIr=" + rfIr + ", esgMst=" + esgMst + ", irCurve=" + irCurve + "]";
	}

}


