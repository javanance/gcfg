package com.gof.enums;


public enum EBoolean {
	
	Y(true)
,	YES(true)
,   N(false)	
,   NO(false)
;
	
	private boolean trueFalse;
	
	private EBoolean(boolean trueFalse) {
		this.trueFalse =trueFalse;
	}	
	
	public boolean isTrueFalse() {
		return trueFalse;
	}
	
	
}
