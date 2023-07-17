package com.gof.enums;


public enum EKtbFuturesInfo {
	//WHETHER DEPRECATED(?)
	   KR4165    ("165",  3, 6)	   
	 , KR4166    ("166",  5, 6)
	 , KR4167    ("167", 10, 6)
	 , DEFAULT   ("165",  3, 6) 
	 ;	
	
	private String matCd;
	
	private Integer matYear;
	
	private Integer pmtTerm;
			
	private EKtbFuturesInfo(String matCd, Integer matYear, Integer pmtTerm) {
		this.matCd = matCd;
		this.matYear = matYear;
		this.pmtTerm = pmtTerm;
	}	
	
	public String getMatCd() {
		return matCd;
	}

	public Integer getMatYear() {
		return matYear;
	}	

	public Integer getPmtTerm() {
		return pmtTerm;
	}

	public static EKtbFuturesInfo getEKtbFuturesInfo(String matCd) {		 
		
		for(EKtbFuturesInfo mat : EKtbFuturesInfo.values()) {
			if(mat.getMatCd().equals(matCd)) return mat;			
		}
		return DEFAULT;		
	}	
	
	
	public static EKtbFuturesInfo getEKtbFuturesInfo(Integer matYear) {

		for(EKtbFuturesInfo mat : EKtbFuturesInfo.values()) {
			if(mat.getMatYear().equals(matYear)) return mat;
		}
		return DEFAULT;		
	}		
	    	
}
	