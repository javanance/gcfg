package com.gof.enums;

import com.gof.interfaces.Constant;


public enum EDayCountBasis {
	
	   ACT_365   ("1", Constant.DCB_ACT_365)
	 , A30_360   ("2", Constant.DCB_A30_360)	
	 , E30_360   ("3", Constant.DCB_E30_360)
	 , ACT_ACT   ("4", Constant.DCB_ACT_ACT)	 
	 , ACT_360   ("5", Constant.DCB_ACT_360)	 
	 , DEFAULT   ("1", Constant.DCB_ACT_365)
	 ;
	
	//SAP CODE: 360E/360 / 360E/365 / 360/360 / Act/360 / Act/364 / Act/365 / Act/366 / Act/ActP (ICMA) / Act/ActY (ISDA) / Act/ActE (AFB) / ActW/252 / 365/360 / 365/365 / 360E/ActY

	private String legacyCode;		
	
	private Integer dcbCode;
			
	private EDayCountBasis(String legacyCode, Integer dcbCode) {
		this.legacyCode = legacyCode;
		this.dcbCode = dcbCode;
	}	
	
	public String getLegacyCode() {
		return legacyCode;
	}
	
	public Integer getDcbCode() {
		return dcbCode;
	}	
	
	public static EDayCountBasis getEDayCountBasis(String legacyCode) {		 
		
		for(EDayCountBasis dcb : EDayCountBasis.values()) {
			if(dcb.getLegacyCode().equals(legacyCode)) return dcb;			
		}
		return DEFAULT;		
	}		
	
	public static EDayCountBasis getEDayCountBasis(Integer dcbCode) {
		for(EDayCountBasis dcb : EDayCountBasis.values()) {
			if(dcb.getDcbCode().equals(dcbCode)) return dcb;
		}
		return DEFAULT;		
	}
	    	
}
	