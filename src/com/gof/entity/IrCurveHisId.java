package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IrCurveHisId implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = 6046213123503006677L;	
	
	@Column(name="BASE_DATE", nullable=false) 
	private String baseDate;	
		
	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;	


	public IrCurveHisId() {}
	
}
