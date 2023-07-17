package com.gof.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SortNatural;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@Table(schema="IQCMOWN", name ="Q_CM_IRC")
@Table(name ="Q_CM_IRC")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrCurve implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = 5329760904768734490L;	

	@Id	
	@Column(name="IRC_ID", nullable=false)	 
	private String irCurveId;	
	
	@Column(name="IRC_NM")
	private String irCurveNm;	
	
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
	
	public IrCurve() {}
	
}
