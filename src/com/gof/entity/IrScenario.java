package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Entity
//@Table(schema="IQCMOWN", name ="IR_SCENARIO")
@Table(name ="IR_SCENARIO")
@FilterDef(name="FILTER", parameters= { @ParamDef(name="baseDate", type="string") })
@Filters( { @Filter(name ="FILTER", condition="BASE_DATE = :baseDate") } )
@Getter
@Setter
@EqualsAndHashCode
public class IrScenario implements Serializable, EntityIdIdentifier {	

	private static final long serialVersionUID = -3798696837380432299L;

	@Id
	private String baseDate;
	
    @Id
	private String matCd;

    @Id
	private String scenNum;
	
	private Double intRate;	
	
	public IrScenario() {}

}


