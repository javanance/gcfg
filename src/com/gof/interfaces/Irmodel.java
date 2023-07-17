package com.gof.interfaces;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrmodelResult;


public interface Irmodel extends Constant {
	
	public Map<String, Map<String, IrmodelResult>> getIrmodelResult();	
	
	public LocalDate getBaseDate();
	
	public void setBaseDate(LocalDate baseDate);

	public char getCmpdType();
	
	public void setCmpdType(char cmpdType);	

	public char getTimeUnit();
	
	public void setTimeUnit(char timeUnit);

	public int getDayCountBasis();
	
	public void setDayCountBasis(int dayCountBasis);	
	
}
	
