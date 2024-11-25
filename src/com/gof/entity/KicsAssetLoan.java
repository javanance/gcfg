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
//@Table(schema="IQCMOWN", name ="Q_IC_ASSET_LOAN")
@Table(name ="Q_IC_ASSET_LOAN")
@FilterDef(name="BASE_DATE_ASSET", parameters= { @ParamDef(name="baseDate", type="string") })
@Filter(name ="BASE_DATE_ASSET", condition="BASE_DATE = :baseDate")
@Getter
@Setter
@EqualsAndHashCode
@ToString (exclude = "irCurve")
public class KicsAssetLoan implements Serializable, EntityIdIdentifier, KicsAsset {	
	
	private static final long serialVersionUID = -3301026130706047413L;

	@ManyToMany(fetch=FetchType.LAZY)	
	@JoinTable(name ="Q_IC_INST_CURVE", 
	           joinColumns = @JoinColumn(name ="EXPO_ID"), 
	           inverseJoinColumns = @JoinColumn(name ="IRATE_CURVE_ID"))
	@NotFound(action=NotFoundAction.IGNORE)	
	private List<IrCurve> irCurve;	
	public List<IrCurve> getIrCurve() { return irCurve; }
	public void setIrCurve(List<IrCurve> irCurve) { this.irCurve = irCurve; }	


//	@ManyToMany(fetch=FetchType.LAZY)	
//	@JoinTable(name ="Q_IC_INST_LOAN_SEG", 
//	           joinColumns = @JoinColumn(name ="EXPO_ID"), 
//	           inverseJoinColumns = @JoinColumn(name ="LOAN_SEG"))
//	@NotFound(action=NotFoundAction.IGNORE)
//	private List<LoanSeg> loanSeg;	
//	public List<LoanSeg> getLoanSeg() { return loanSeg; }
//	public void setLoanSeg(List<LoanSeg> loanSeg) { this.loanSeg = loanSeg; }
	
	private String baseDate;
	
	@Id
	@Column(name="EXPO_ID", nullable=false)
	private String expoId;

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
    
    @Transient
    private Boolean isRealNumber;    
    
//    private String custId;
//    private String custNm;
//    private String corpNo;    
//    private String crgrKis;
//    private String crgrNice;
//    private String crgrKr;
    
//    private String stocTpcd;
//    private String prfStocYn;
//    private String bondRankClcd;
//    private String stocListClcd;
//    private String cntyCd;
//    private String assetLiqClcd;
    private String deptCd;
    
	public KicsAssetLoan() {}

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
