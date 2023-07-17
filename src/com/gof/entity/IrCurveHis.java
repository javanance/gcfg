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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(IrCurveHisId.class)
//@Table(schema="IQCMOWN", name ="Q_IC_IR_CURVE_HIS")
@Table(name ="Q_IC_IR_CURVE_HIS")
//@Table(schema="IQCMOWN", name ="IR_CURVE_HIS")
//@FilterDef(name="BASE_DATE", parameters= { @ParamDef(name="baseDate", type="string"), @ParamDef(name="irCurveId", type="string") })
//@Filters( { @Filter(name ="BASE_DATE", condition="BASE_DATE = :baseDate"),  @Filter(name ="IR_CURVE_FILTER", condition="IR_CURVE_ID like :irCurveId") } )
//@FilterDef(name="BASE_DATE", parameters= { @ParamDef(name="BASE_DATE", type="date"),  @ParamDef(name="irCurveId", type="string") })
//@FilterDef(name="BASE_DATE", parameters= { @ParamDef(name="BASE_DATE", type="date") })
@FilterDef(name="BASE_DATE", parameters= { @ParamDef(name="baseDate", type="string") })
@Filter(name ="BASE_DATE", condition="BASE_DATE = :baseDate")
@Getter
@Setter
@EqualsAndHashCode
public class IrCurveHis implements Serializable, EntityIdIdentifier {
	
	private static final long serialVersionUID = 2849090093441307856L;	
	
	@Id 
	private String baseDate;	
		
	@Id	
	private String irCurveId;	
	
	@Id
	private String matCd;	
	
	private Double intRate;	
	
//	@OneToOne(fetch=FetchType.LAZY)
//	//@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable=false)
//	@NotFound(action=NotFoundAction.IGNORE)
//	private IrCurve irCurve;
//	public IrCurve getIrCurve() { return irCurve; }
//	public void setIrCurve(IrCurve irCurve) { this.irCurve = irCurve; }
	
//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "irCurveHis")	
//	@NotFound(action=NotFoundAction.IGNORE)	
//	private List<KicsInstCurve> instCurveList;
//	public List<KicsInstCurve> getInstCurve() {	return instCurveList; }
//	public void setIrCurveHis(List<KicsInstCurve> instCurveList) { this.instCurveList = instCurveList; }
	
	//@OneToMany(fetch=FetchType.LAZY, mappedBy = "irCurveHis")
//	@JoinTable(name ="KICS_INST_CURVE", 
//	           joinColumns = @JoinColumn(name ="IR_CURVE_ID"), 
//	           inverseJoinColumns = @JoinColumn(name ="DGS_NO"))	
//	@NotFound(action=NotFoundAction.IGNORE)
//	private List<KicsInstCurve> instCurveList;
//	public List<KicsInstCurve> getInstCurve() {	return instCurveList; }
//	public void setIrCurveHis(List<KicsInstCurve> instCurveList) { this.instCurveList = instCurveList; }	
	
//	//JoinColumn에 해당하는 컬럼명은 ID선언부에서 반드시 정의되어야 한다. (IdClass에서라도...)
//	@ManyToOne(fetch=FetchType.LAZY)	 
//	@JoinColumn(name ="BASE_DATE", insertable=false, updatable=false)	
//	@NotFound(action=NotFoundAction.IGNORE)	
//	private KicsAssDe kicsAssDe;
	
	//JoinColumn에 해당하는 컬럼명은 ID선언부에서 반드시 정의되어야 한다. (IdClass에서라도...)
	@ManyToOne(fetch=FetchType.LAZY)	  
	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable=false)		
	@NotFound(action=NotFoundAction.IGNORE)	
	private IrCurve irCurve;
	public IrCurve getIrCurve() { return irCurve; }
	public void setIrCurve(IrCurve irCurve) { this.irCurve = irCurve; }
	
//    @OneToOne 인 경우는 본 annotation을 포함하여 어떠한 선언도 할 필요가 없다.	
	
	public IrCurveHis() {}	   
	
	@Override
	public String toString() {
		return "IrCurveHis [baseDate=" + baseDate + ", irCurveId=" + irCurveId + ", matCd=" + matCd + ", intRate="
				+ intRate + "]\n";
	}	

}
