package com.gof.entity;

import static com.gof.interfaces.Instrument.DEF_CURRENCY;

import java.io.Serializable;
import java.util.Collections;
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
//@Table(schema="IQCMOWN", name ="Q_IC_ASSET_ACCO")
@Table(name ="Q_IC_ASSET_ACCO")
@FilterDef(name="BASE_DATE_ASSET", parameters= { @ParamDef(name="baseDate", type="string") })
@Filter(name ="BASE_DATE_ASSET", condition="BASE_DATE = :baseDate")
@Getter
@Setter
@EqualsAndHashCode
@ToString (exclude = "irCurve")
public class KicsAssetAcco implements Serializable, EntityIdIdentifier, KicsAsset {	
	
	private static final long serialVersionUID = -1663038166633478673L;

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
    
	public KicsAssetAcco() {}

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
