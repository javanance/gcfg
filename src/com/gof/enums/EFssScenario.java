package com.gof.enums;


public enum EFssScenario {	
	
	   BASE           ( 1, "10", "IR")
     , MEAN_REVERSION ( 2, "10", "IR")
	 , UP             ( 3, "10", "IR")
	 , DOWN           ( 4, "10", "IR")
	 , FLAT           ( 5, "10", "IR")
	 , STEEP          ( 6, "10", "IR")
	 , DOWN_95P       ( 7, "10", "IR")
	 , DOWN_90P       ( 8, "10", "IR")
	 , UP_UFR15       ( 9, "10", "IR")
	 , DOWN_UFR15     (10, "10", "IR")
	 , STANDARD       ( 0, "10", "ST")
	;	
	
	private Integer sceNo;
	
	private String fssScenTyp;
	
	private String sceType;	
			
	private EFssScenario (Integer sceNo, String fssScenTyp, String sceType) {		
		this.sceNo = sceNo;		
		this.fssScenTyp = fssScenTyp;
		this.sceType = sceType;		
	}	
	
	public Integer getSceNo() {
		return this.sceNo;
	}
	
	public String getFssScenTyp() {
		return this.fssScenTyp;
	}	
	
	public String getSceType() {
		return sceType;
	}

	public static EFssScenario getEIrScenario(Integer sceNo) {		 
		
		for(EFssScenario scen : EFssScenario.values()) {
			if(scen.getSceNo().equals(sceNo)) return scen;			
		}
		return STANDARD;		
	}
	    	
}
	