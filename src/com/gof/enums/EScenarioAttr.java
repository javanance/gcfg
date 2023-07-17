package com.gof.enums;


public enum EScenarioAttr {
	
	  SD ("STANDARD")
	, IR ("IR_SCENARIO")
	, ST ("STOCK_SHOCK")
	, CL ("CALIBRATION")			
	, FX ("FX_SHOCK")
	;	
	
	private String alias;

	private EScenarioAttr(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}	
	
}
