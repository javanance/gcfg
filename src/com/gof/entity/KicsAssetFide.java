package com.gof.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdIdentifier;
import com.gof.interfaces.KicsAsset;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@IdClass(KicsAssDeId.class)
//@FilterDef(name="BASE_DATE", parameters= { @ParamDef(name="baseDate", type="string"), @ParamDef(name="dgsNo", type="string") })
//@Filters( { @Filter(name ="BASE_DATE", condition="BASE_DATE = :baseDate") } )
//@FilterDef(name="BASE_DATE", parameters= {@ParamDef(name="dgsNo", type="string") })
//@Filter(name ="BASE_DATE", condition="DGS_NO = :dgsNo")
//@Table(schema="IQCMOWN", name ="Q_IC_ASSET_FIDE")
@Table(name ="Q_IC_ASSET_FIDE")
@FilterDef(name="BASE_DATE_ASSET", parameters= { @ParamDef(name="baseDate", type="string") })
@Filter(name ="BASE_DATE_ASSET", condition="BASE_DATE = :baseDate")
@Getter
@Setter
@EqualsAndHashCode
@ToString (exclude = "irCurve")
public class KicsAssetFide implements Serializable, EntityIdIdentifier, KicsAsset {	
	
	private static final long serialVersionUID = -7495051060668876802L;	
	
//    //OneToMany인 경우에는 주체가 되는 entity를 mappedBy 해주어야 한다. 역시 One에 해당하는 본 엔티티의 ID는 하나이므로 BSE_DT 이냐 BASE_DATE이냐는 영향을 미치지 않는다. 
//	//OneToOne과는 다르게 이경우에는 참조하는 엔티티에도 본 엔티티에 관한 내용을 구현해주어야 한다.  그리고 이 경우는 JoinColumn 대신에 mappedBy가 사용된 형태이다.
//	//@JoinColumn(name="BSE_DT", insertable=false, updatable=false) 이런 표현이 들어가서는 안된다.
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="kicsAssDe")	
//	@NotFound(action=NotFoundAction.IGNORE)	
//	private List<IrCurveHis> irCurveHis;	
//	public List<IrCurveHis> getIrCurveHis() { return irCurveHis; }
//	public void setIrCurveHis(List<IrCurveHis> irCurveHis) { this.irCurveHis = irCurveHis; }
	
//    //OneToOne인 경우는 Target Entity에는 어떠한 처리를 하지 않고 여기에서만 @를 정의한다. 이경우는 조인키가 이름이 다르지만(BSE_DT vs BASE_DATE) OneToOne이라서 인지 결과에 영향을 미치지는 않는다. 이 상태는 두 entity의 ID가 한개이어야만 한다.
//    //아래 클래스 변수를 선언할때 Collection형태를 사용하면 에러가 난다(LIST등)
//	@OneToOne
//	@JoinColumn(name ="BSE_DT")
//	private IrCurveHis irCurveHis;
//	public IrCurveHis getIrCurveHis() {	return irCurveHis; }
//	public void setIrCurveHis(IrCurveHis irCurveHis) { this.irCurveHis = irCurveHis; }
	
//	@OneToOne
//	@JoinTable(name ="KICS_INST_CURVE", 
//    joinColumns = @JoinColumn(name ="DGS_NO"), 
//    inverseJoinColumns = @JoinColumn(name ="IR_CURVE_ID"))
//	@NotFound(action=NotFoundAction.IGNORE)
//	private IrCurve irCurve;	
//	public IrCurve getIrCurve() { return irCurve; }
//	public void setIrCurve(IrCurve irCurve) { this.irCurve = irCurve; }

//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "kicsAssDe")	
//	@NotFound(action=NotFoundAction.IGNORE)	
//	private List<IrCurveHis> irCurveHis;	
//	public List<IrCurveHis> getIrCurveHis() { return irCurveHis; }
//	public void setIrCurveHis(List<IrCurveHis> irCurveHis) { this.irCurveHis = irCurveHis; }
	
	
	@ManyToMany(fetch=FetchType.LAZY)	
	@JoinTable(name ="Q_IC_INST_CURVE", 
	           joinColumns = @JoinColumn(name ="EXPO_ID"), 
	           inverseJoinColumns = @JoinColumn(name ="IRATE_CURVE_ID"))
	@NotFound(action=NotFoundAction.IGNORE)	
	private List<IrCurve> irCurve;	
	public List<IrCurve> getIrCurve() { return irCurve; }
	public void setIrCurve(List<IrCurve> irCurve) { this.irCurve = irCurve; }	

	private String baseDate;
	
	@Id	
	@Column(name="EXPO_ID", nullable=false)
	private String expoId;
	
	@Transient
	private Boolean isRealNumber;	

	private String fundCd;
	private String prodTpcd;
	private String isinCd;	
	private String accoCd;	
	private String instTpcd;
	private String instDtlsTpcd;
	private String potnTpcd;	
		
	private String issuDate;	
	private String matrDate;
	private String frstIntDate;
	private String prinExcgYn;
	private String ktbMatYear;	
	
	private String recCrnyCd;	
	private Double recCrnyFxrt;	
	private Double recNotlAmt;
    private Double recIrate;
    private String recIntPayCyc;        
    private String recIrateTpcd;
    private String recIrateCurveId;
    private String recIrateDtlsTpcd;
    private Double recAddSprd;    
    private String recIrateRpcCyc;
    private String recDcbCd;
	
	private String payCrnyCd;	
	private Double payCrnyFxrt;	
	private Double payNotlAmt;	
    private Double payIrate;
    private String payIntPayCyc;        
    private String payIrateTpcd;
    private String payIrateCurveId;
    private String payIrateDtlsTpcd;
    private Double payAddSprd;    
    private String payIrateRpcCyc;
    private String payDcbCd;
    
    private Double bsAmt;
    private Double vltAmt;
    private Double fairBsAmt;
    private Double cva;
    private Double accrAmt;
    private Double uernAmt;    

    private Double contQnty;
    private Double contMult;
    private Double contPrc;
    private Double spotPrc;
    private Double irateCap;
    private Double irateFlo;   
    private Double optVolt;
    private Double undlExecPrc;
    private Double undlSpotPrc;    
    
    private Double extUprc;
    private Double extUprcUnit;
    private Double extDura;    
    private Double recExtUprc;    
    private Double payExtUprc;    
    private Double recExtDura;    
    private Double payExtDura;
    
//    @Transient
//    private String custId;
//    @Transient
//    private String custNm;
    
    private String corpNo;    
    private String crgrKis;
    private String crgrNice;
    private String crgrKr;
    
    private String stocTpcd;
    private String prfStocYn;
    private String bondRankClcd;
    private String stocListClcd;
    private String cntyCd;
    private String assetLiqClcd;
    private String deptCd;

	public KicsAssetFide() {}
	
	@Override
	public String getCrnyCd() {		
		return null;
	}
	
}
