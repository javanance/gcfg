package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@Table(schema="IQCMOWN", name ="Q_IC_ASSET_RSLT")
@Table(name ="Q_IC_ASSET_RSLT")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class KicsAssetResult implements Serializable, EntityIdIdentifier, Cloneable {	

	private static final long serialVersionUID = 7543251439435700019L;

	@Id	
	private String baseDate;	
	
	@Id
	private String expoId;
	
	@Id
	private String legType;
	
	@Id	
	@Column(name="CRNY_CD", nullable=false)
	private String currency;
	
	@Id
	@Column(name="RSLT_TYPE", nullable=false)
	private String resultType;	
	
	@Column(name="RSLT_NAME", nullable=false)
	private String resultName;	
	
	@Id
	@Column(name="RSLT_DATE", nullable=false)
	private String resultDate;
	
	@Id
	private String scenType;	

	private String scenName;
	
	@Id
	private Integer scenNum;	

	@Column(name = "`VALUE`")
	private Double value;	
		
	private String fundCd;
	
	private String prodTpcd;
	
	private String accoCd;
	
	private String deptCd;	
	
	private String lastModifiedBy;	
	
	private LocalDate lastUpdateDate;	

	public KicsAssetResult() {}

	@Override
	public KicsAssetResult clone() throws CloneNotSupportedException {
		return (KicsAssetResult) super.clone();		
	}
	
}
