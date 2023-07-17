package com.gof.util;

import com.gof.interfaces.Constant;

public class GeneralUtil implements Constant {	
	
//	public GeneralUtil() {}
//	
//	private Object first;
//	
//	private Object second;
//	
//	inner class multiList {
//		
//	}
//	
//	private List<Object> multiList (Object first, Object second) {
//		
//		if(first instanceof String && second instanceof String) {
//			
//		}
//		
//		
//		return null;
//	}

	public static double setScenarioValue(double originalValue, String typCd, double val) {
		
		char type = typCd.charAt(0);		
		switch(type) {		
			
			case MULTIPLY: return originalValue*(1.0 + val);			
			case ADD:      return originalValue + val;			
			case SUBTRACT: return originalValue - val;
			default:       return originalValue;
		}
	}
	
	
	public static String setScenarioValueString(String typCd, double val) {
		
		char type = typCd.charAt(0);		
		switch(type) {		
			
			case MULTIPLY: return "VALUE x " + String.format("%.2f",(1.0 + val));			
			case ADD:      return "VALUE + " + String.format("%.2f",val);			
			case SUBTRACT: return "VALUE - " + String.format("%.2f",val);
			default:       return "VALUE";			
		}
	}	
	
	
	public static String leftPad(Integer attr, Integer size) {
		
		String format;
		
		if(size == null || size.intValue() == 0) {
			return attr.toString();
		}
		else format = "%0" + size.intValue() + "d" ;
		
		if(attr == null) return null;		
		else return String.format(format, attr);		
	}
	
	
	public static String leftPad(String attr, Integer size) {		
		return leftPad(Integer.valueOf(attr), size);
	}	
	
	
	public static double objectToPrimitive(Double attr) {		
		
		if(attr == null) return NULL_DOUBLE;
		else return attr.doubleValue();		
	}
	

	public static int objectToPrimitive(Integer attr) {		
		
		if(attr == null) return NULL_INT;
		else return attr.intValue();		
	}

	
	public static boolean objectToPrimitive(Boolean attr) {		
		
		if(attr == null) return false;
		else return attr.booleanValue();		
	}	
	
	
	public static double objectToPrimitive(Double attr, Double defaultValue) {		
		
		if(attr == null || attr.isNaN() || attr.isInfinite() || Math.abs(attr) < ZERO_DOUBLE) return defaultValue.doubleValue();
		else return attr.doubleValue();
	}	
	

	public static int objectToPrimitive(String attr) {
		
		if(attr == null) return NULL_INT;
		else return Integer.parseInt(attr);
	}	
		

	public static int objectToPrimitive(String attr, Integer defaultValue) {
		
		if(attr == null) return defaultValue.intValue();
		else return Integer.parseInt(attr);				
	}	
	
	
	public static String objectToPrimitive(String attr, String defaultValue) {
		
		if(attr == null) return defaultValue; 
		else return attr;
	}
	
	
	public static int stringComparator(String attr, String compare) {
		
		if(attr == null) return INTEGER_NULL;
		else return attr.equals(compare) ? INTEGER_TRUE : INTEGER_FALSE;		
	}	

	
    public static String concatenate(char delimiter, String... string) {    
    	
    	StringBuffer concat = new StringBuffer();
    	
    	if(string.length == 0) return null;
    	
    	for(int i=0; i<string.length-1; i++) {    		
    		concat.append(string[i]);
    		concat.append(delimiter);
    	}
    	concat.append(string[string.length-1]);
    	
    	return concat.toString();    	
    }   
    
    
    public static String concatenate(String... string) {
    	return concatenate('_', string);
    }    
        
}
