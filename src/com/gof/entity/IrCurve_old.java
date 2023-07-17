package com.gof.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SortNatural;

import com.gof.interfaces.EntityIdIdentifier;


@Entity
@Table(schema="IQCMOWN", name ="IR_CURVE")
public class IrCurve_old implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = 5329760904768734490L;
	
	@Id	
	@Column(name="IR_CURVE_ID", nullable=false)	 
	private String irCurveId;
	
	private String irCurveNm;	

	private String curCd;

	private String applBizDv;
	
	private String applMethDv;
	
	private String crdGrdCd;
	
	private String intpMethCd;
	
	private String useYn;
	
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy = "irCurve")
	@NotFound(action=NotFoundAction.IGNORE)
	private List<KicsAssetFide> kicsAssDe;	
	
	public List<KicsAssetFide> getKicsAssDe() {
		return kicsAssDe;
	}
	public void setKicsAssDe(List<KicsAssetFide> kicsAssDe) {
		this.kicsAssDe = kicsAssDe;
	}

//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "irCurve")
//	@NotFound(action=NotFoundAction.IGNORE)	
//	@Filter(name ="BASE_DATE", condition="BASE_DATE = :baseDate")		
//	private List<IrCurveHis> irCurveHis;	
//	public List<IrCurveHis> getIrCurveHis() { return irCurveHis; }
//	public void setIrCurveHis(List<IrCurveHis> irCurveHis) { this.irCurveHis = irCurveHis; }

	
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "irCurve")
	@NotFound(action=NotFoundAction.IGNORE)	
	@Filter(name ="BASE_DATE", condition="BASE_DATE = :baseDate")	
	@MapKey(name="matCd")
	@SortNatural
	private Map<String, IrCurveHis> irCurveHis;// = new TreeMap<String, IrCurveHis>();	
	public Map<String, IrCurveHis> getIrCurveHis() { return irCurveHis; }
	public void setIrCurveHis(Map<String, IrCurveHis> irCurveHis) { this.irCurveHis = irCurveHis; }

//	@MapKey(name="irCurveId")	
//	private Map<String, List<IrCurveHis>> irCurveHis = new HashMap<String, List<IrCurveHis>>();	
//	public Map<String, List<IrCurveHis>> getIrCurveHis() { return irCurveHis; }
//	public void setIrCurveHis(Map<String, List<IrCurveHis>> irCurveHis) { this.irCurveHis = irCurveHis; }
	
	public IrCurve_old() {}

	public String getIrCurveId() {
		return irCurveId;
	}

	public void setIrCurveId(String irCurveId) {
		this.irCurveId = irCurveId;
	}
	
	public String getIrCurveNm() {
		return irCurveNm;
	}

	public void setIrCurveNm(String irCurveNm) {
		this.irCurveNm = irCurveNm;
	}

	public String getCurCd() {
		return curCd;
	}

	public void setCurCd(String curCd) {
		this.curCd = curCd;
	}

	public String getApplBizDv() {
		return applBizDv;
	}

	public void setApplBizDv(String applBizDv) {
		this.applBizDv = applBizDv;
	}

	public String getApplMethDv() {
		return applMethDv;
	}

	public void setApplMethDv(String applMethDv) {
		this.applMethDv = applMethDv;
	}

	public String getCrdGrdCd() {
		return crdGrdCd;
	}

	public void setCrdGrdCd(String crdGrdCd) {
		this.crdGrdCd = crdGrdCd;
	}

	public String getIntpMethCd() {
		return intpMethCd;
	}

	public void setIntpMethCd(String intpMethCd) {
		this.intpMethCd = intpMethCd;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
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
		return "IrCurve [irCurveId=" + irCurveId + ", irCurveNm=" + irCurveNm + ", curCd=" + curCd + ", applBizDv="
				+ applBizDv + ", applMethDv=" + applMethDv + ", crdGrdCd=" + crdGrdCd + ", intpMethCd=" + intpMethCd
				+ ", useYn=" + useYn + "]";
	}	
	
}
