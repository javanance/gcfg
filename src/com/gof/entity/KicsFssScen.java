package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
//@Table(schema="IQCMOWN", name ="Q_IC_FSS_SCEN")
@Table(name ="Q_IC_FSS_SCEN")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class KicsFssScen implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = -1046687391931918267L;

	@Id	
	private String baseYm;	
	
	@Id	
	private String fssCrveCd;
	
	@Id	
	private Integer scenNo;	

	@Id		
	private String matCd;
	
	private Double rate;	
	
	public KicsFssScen() {}
	
}
