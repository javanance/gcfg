package com.gof.enums;


public enum ERunArgument {
	
	  time ( "TIME")
	, properties ( "PROPERTIES")
	, expoid ( "EXPO_ID")
	;	
	
	private String alias;

	private ERunArgument(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}	
	
}
