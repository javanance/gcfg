package com.gof.entity;

import static com.gof.interfaces.Instrument.DEF_CURRENCY;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
//@Table(schema="IQCMOWN", name ="Q_IC_ASSET_SECR")
@Table(name ="Q_IC_ASSET_SECR")
@FilterDef(name="BASE_DATE_ASSET", parameters= { @ParamDef(name="baseDate", type="string") })
@Filter(name ="BASE_DATE_ASSET", condition="BASE_DATE = :baseDate")
@Getter
@Setter
@EqualsAndHashCode
@ToString (exclude = "irCurve")
public class KicsAssetSecr implements Serializable, EntityIdIdentifier, KicsAsset {	
	
	private static final long serialVersionUID = -2029627169285876967L;

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
		
	private String issuDate;	
	private String matrDate;	
	private String crnyCd;	
	private Double crnyFxrt;	
	private Double notlAmt;	
	private Double notlAmtOrg;	
    
    private Double bsAmt;
    private Double vltAmt;
    private Double fairBsAmt;
    private Double accrAmt;
    private Double uernAmt;
    
    private Double irate;
    private String intPayCyc;
    private String intProrPayYn;
    
    private String irateTpcd;    
    private String irateCurveId;
    private String irateDtlsTpcd;
    private Double addSprd;    
    private String irateRpcCyc;
    
    private String dcbCd;
    private String cfGenTpcd;
    private String frstIntDate;    
    
    private String graceEndDate;
    private String graceTerm;
    private String amortTerm;
    private Double amortAmt;

    private Double irateCap;
    private Double irateFlo;
    private Double matrPrmu;
    
    private Double contPrc;    
    private Double undlExecPrc;
    private Double undlSpotPrc;    
    private String optEmbTpcd;    
    private String optStrDate;    
    private String optEndDate;    
    
    private Double extUprc;
    private Double extUprcUnit;
    private Double extDura;    
    private Double extUprc1;    
    private Double extUprc2;    
    private Double extDura1;    
    private Double extDura2;
    
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
    
	public KicsAssetSecr() {}


    public String getCrnyCd() {
        return (crnyCd != null) ? crnyCd : DEF_CURRENCY ; 
    };
    
    @Override
    public Set<String> getCrnySet() {
    	
    	Set<String> crnySet = new HashSet<>();
        crnySet.add(getCrnyCd()); 
        return crnySet;	
    }
}
