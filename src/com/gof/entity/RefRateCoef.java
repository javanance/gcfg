package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@IdClass(RefRateCoefId.class)
@Table(name ="IKRUSH_FLOATINGIRATE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefRateCoef implements Serializable, EntityIdIdentifier {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id
	private String baseDate;
	
    @Id
	private String baseIrateCd;
    
    private Double intercept;
    
    private Double slope;


}


