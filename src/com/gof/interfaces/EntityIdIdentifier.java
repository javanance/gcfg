package com.gof.interfaces;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;


public interface EntityIdIdentifier {
	
	public static final String getterPrefix  = "get";
	public static final String idClassSuffix = "Id";

	public default Map<String,Object> idMapColumn() throws Exception {		
		
		//Map<String,Object> idMapColumn = new LinkedHashMap<String,Object>();		
		Map<String,Object> idMapColumn = new TreeMap<String,Object>();

		Class<?> idClass;
		Annotation[] annotations;		
		String column;		
		Method meth;
		boolean hasFieldIdAnnotations = false;
		boolean hasFieldColumnAnnotations = false;

		String idClassIndicator = isIdClass() ? idClassSuffix : "";		

		idClass = Class.forName(this.getClass().getName() + idClassIndicator);		
		Field[] fields = idClass.getDeclaredFields();
			
		for(Field fd : fields) {						
			
			if(isIdClass()) { 			
			    hasFieldIdAnnotations = true;			
			    hasFieldColumnAnnotations = fd.isAnnotationPresent(Column.class);
			}
			else {
			    hasFieldIdAnnotations = fd.isAnnotationPresent(Id.class);			
			    hasFieldColumnAnnotations = fd.isAnnotationPresent(Column.class);
			}			
			
			if (hasFieldIdAnnotations && hasFieldColumnAnnotations) {
			
				annotations = fd.getDeclaredAnnotations();
				
				for(Annotation anno : annotations) {					
					
	    			if (anno.annotationType().equals(Column.class)) {					
						Column dbColumn = (Column) anno;
						
						column = fieldToGetter(fd.getName());
						meth = this.getClass().getDeclaredMethod(column);						
	    				
						idMapColumn.put(dbColumn.name(), meth.invoke(this));	    										
					}					
				}
			}
		}		
		return idMapColumn;	
	}	
	

	public default Map<String,Object> idMap() throws Exception {		

		Map<String,Object> idMap = new TreeMap<String,Object>();
		
		Annotation[] annotations;
		Field[] fields = this.getClass().getDeclaredFields();
		String getter;
		Method meth;
		
    	for(Field fd : fields) {	    		
    		annotations = fd.getDeclaredAnnotations();
    		
    		for(Annotation anno : annotations) {	  		
    			if (anno.annotationType().equals(Id.class)) {    	
    				
    				getter = fieldToGetter(fd.getName());
    				meth = this.getClass().getDeclaredMethod(getter);
    				idMap.put(fd.getName(), meth.invoke(this) );    				
    			}
    		}	        		
    	}		
    	return idMap;
	}	

	
	public default String idString(String delimiter) throws Exception {
		
		List<String> idList = new ArrayList<String>();		
		
		Annotation[] annotations;
		Field[] fields = this.getClass().getDeclaredFields();
		String getter;
		Method meth;
		
    	for(Field fd : fields) {	    		
    		annotations = fd.getDeclaredAnnotations();
    		
    		for(Annotation anno : annotations) {	  		
    			if (anno.annotationType().equals(Id.class)) {    	
    				
    				getter = fieldToGetter(fd.getName());
    				meth = this.getClass().getDeclaredMethod(getter);
    				idList.add((String) meth.invoke(this));    				
    			}
    		}	        		
    	}    	    	
		return idList.stream().collect(Collectors.joining(delimiter));    	
	}	

	
    public default String idString() throws Exception {		
	    return idString(",");				
	}
    
    
    public default boolean isIdClass() {
		
		Annotation[] annotations = this.getClass().getDeclaredAnnotations();

    	for(Annotation anno : annotations) {    			  		
			if (anno.annotationType().equals(IdClass.class)) {				
				return true;
    		}			
    	}        	    	
    	return false;		
	}    
    
    
	public default String fieldToGetter(String field) {
		
		String str = null;
		field = field.trim();

		if(field.length() > 0) {			
			str = field.substring(0,1).toUpperCase() + field.substring(1);		
		}		
		return getterPrefix + str;		
	}	
	
	
	public default String getterToField(String getter) {
		
		String str = null;
		getter = getter.trim();		
		int size = getterPrefix.length();		

		if(getter.length() > getterPrefix.length()) {			
			str = getter.substring(size,size + 1).toLowerCase() + getter.substring(size + 1);		
		}		
		return str;		
	}
	
	
	public static String fieldToGetterUtil(String field) {
		
		String str = null;
		field = field.trim();

		if(field.length() > 0) {			
			str = field.substring(0,1).toUpperCase() + field.substring(1);		
		}		
		return getterPrefix + str;		
	}	
	
	
	public static String getterToFieldUtil(String getter) {
		
		String str = null;		
		getter = getter.trim();
		int size = getterPrefix.length();

		if(getter.length() > getterPrefix.length()) {			
			str = getter.substring(size,size + 1).toLowerCase() + getter.substring(size + 1);
		}		
		return str;		
	}	
	
}
	
	
