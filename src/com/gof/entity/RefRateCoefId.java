package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter

public class RefRateCoefId implements Serializable, EntityIdIdentifier {
	
	private static final long serialVersionUID = 205371050298889931L;

	private String baseDate;
	private String baseIrateCd;
	
	public RefRateCoefId() {}
	
	
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
			
}
