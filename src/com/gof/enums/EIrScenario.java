package com.gof.enums;

import lombok.Getter;

@Getter
public enum EIrScenario {	
	
	   BASE           (  1, "FSS", "IR")
     , MEAN_REVERSION (  2, "FSS", "IR")
	 , UP             (  3, "FSS", "IR")
	 , DOWN           (  4, "FSS", "IR")
	 , FLAT           (  5, "FSS", "IR")
	 , STEEP          (  6, "FSS", "IR")
	 , SCEN7          (  7, "FSS", "IR")
	 , SCEN8          (  8, "FSS", "IR")
	 , SCEN9          (  9, "FSS", "IR")
	 , SCEN10         ( 10, "FSS", "IR")
	 , SCEN11         ( 11, "FSS", "IR")
	 , SCEN12         ( 12, "FSS", "IR")
	 , SCEN13         ( 13, "FSS", "IR")
	 , SCEN14         ( 14, "FSS", "IR")
	 , USER_DEFINE    ( 99, "FSS", "IR")
	 , CALIBRATION    (  1, "FSS", "CL")	 
	;
	
	private Integer scenNum;	
	
	private String irCurveId;
	
	private String scenType;	
			
	private EIrScenario (Integer scenNum, String irCurveId, String scenType) {		
		this.scenNum = scenNum;		
		this.irCurveId = irCurveId;
		this.scenType = scenType;		
	}		

	/**
	 * TODO: 메서드 맨 끝에서 return에서 null로  처리하면... 정의된 scenNum이 아닌 value가 들어오는 경우, NULL이 호출되고 ENUM의 특성상 NULL POINTER ERROR가 발생한다.
	 */
	public static EIrScenario getEIrScenario(Integer scenNum) {		 
		
		for(EIrScenario scen : EIrScenario.values()) {
			if(scen.getScenNum().equals(scenNum)) return scen;			
		}
		//return BASE;		
		//return null;
		return USER_DEFINE;
	}
	
}
	