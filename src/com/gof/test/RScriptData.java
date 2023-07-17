package com.gof.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

//import org.renjin.primitives.vector.RowNamesVector;
//import org.renjin.sexp.DoubleArrayVector;
//import org.renjin.sexp.IntArrayVector;
//import org.renjin.sexp.ListVector;
//import org.renjin.sexp.SEXP;
//import org.renjin.sexp.StringArrayVector;
//import org.renjin.sexp.StringVector;
//import org.renjin.sexp.Symbols;

/**
 * hibernate Data -> Renjin Data 
 * @author Shin Woongseop
 *
 */
public class RScriptData {
//	final static String sTypeString = "String";
//	final static String sTypeDouble = "Double";
//	final static String sTypeLong = "Long";
//	final static String sTypeInteger = "Integer";
//	
//	
//	static public SEXP toDataFrame(List lData) {
//		if(lData == null){
//			return null;
//		}else if(lData.size() == 0){
//			return null;
//		}
//		//
//		try {
//			Class cls = lData.get(0).getClass();
//			Field[] clsField = cls.getDeclaredFields();
//			
//			// Field 단위로 추출
//			ListVector.NamedBuilder dfData = new ListVector.NamedBuilder();
//			dfData.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
//			dfData.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(lData.size()));
//		    for(int i = 0 ; i < clsField.length ; i ++) {
//		    	/**
//				 * logical -> LogicalVector
//				 * integer -> intVector
//				 * double -> DoubleVector
//				 * character -> StringVector
//				 * complex -> ComplexVector
//				 * raw -> RawVector
//				 * list -> ListVector
//				 * function -> Function
//				 * environment -> Environment 
//				 * NULL -> Null
//				 */
//		    	String sFieldName = clsField[i].getName();
//		    	String sFieldType = parseType(clsField[i].getType().getTypeName());
//		    	
//		    	StringArrayVector.Builder arrayVectorString = new StringArrayVector.Builder();
//			    DoubleArrayVector.Builder arrayVectorDouble = new DoubleArrayVector.Builder();
//			    IntArrayVector.Builder arrayVectorLong = new IntArrayVector.Builder(); // Long, Int 통합 처리
//	//		    ComplexArrayVector.Builder  arrayVectorComplex = new ComplexArrayVector.Builder(); // DB 유형 미존재
//	//		    LogicalArrayVector.Builder  arrayVectorLogical = new LogicalArrayVector.Builder(); // DB 유형 미존재? Boolean 지원 여부? String 으로 처리 
//
//			    if(sFieldType.equals(sTypeString)) {
//		    		for(Object obj : lData) {
//		    			Class<? extends Object> cls2 = obj.getClass();
//		    			Method m = cls2.getMethod("get"+camelToPascal(sFieldName));
//		    			arrayVectorString.add((String)m.invoke(obj));
//		    		}
//		    	}else if(sFieldName.equals(sTypeDouble)) {
//		    		for(Object obj : lData) {
//		    			Class<? extends Object> cls2 = obj.getClass();
//		    			Method m = cls2.getMethod("get"+camelToPascal(sFieldName));
//		    			arrayVectorDouble.add((Double)m.invoke(obj));
//		    		}
//		    	}else if(sFieldName.equals(sTypeInteger) || sFieldName.equals(sTypeLong)) {
//		    		for(Object obj : lData) {
//		    			Class<? extends Object> cls2 = obj.getClass();
//		    			Method m = cls2.getMethod("get"+camelToPascal(sFieldName));
//		    			arrayVectorLong.add((long)m.invoke(obj));
//		    		}
//		    	}
//			    if(sFieldType.equals(sTypeString)) {
//			    	dfData.add(pascaltoColName(sFieldName),arrayVectorString.build());
//		    	}else if(sFieldName.equals(sTypeDouble)) {
//		    		dfData.add(pascaltoColName(sFieldName),arrayVectorDouble.build());
//		    	}else if(sFieldName.equals(sTypeInteger) || sFieldName.equals(sTypeLong)) {
//		    		dfData.add(pascaltoColName(sFieldName),arrayVectorLong.build());
//		    	}
//		    }
//		    return dfData.build();
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	    
//	static private String parseType(String sType) {
//		return sType.substring(sType.lastIndexOf(".")+1, sType.length());
//	}
//	
//	/**
//	 * Pascal Type change
//	 * @param sString
//	 * @return
//	 */
//	static private String camelToPascal(String sString) {
//		String sReturn = sString.trim();
//		
//		if(sString != null) {
//			if(sString.length() > 0) {
//				if((int)sString.charAt(0) >= 97 && (int)sString.charAt(0) <= 122) { // 소문자
//					return sString.substring(0, 1).toUpperCase()+sString.substring(1);
//				}
//			}
//		}
//		return sReturn;		
//	}
//	
//	/**
//	 * ColName Type change
//	 * @param sString
//	 * @return
//	 */
//	static private String pascaltoColName(String sString) {
//		String sReturn = sString.trim();
//		
//		char[] c = sString.toCharArray();
//		
//		if(sString != null) {
//			if(sString.length() > 0) {
//				for(int i = 0 ; i < c.length ; i++) {
//					if((int)c[i] >= 65 && (int)c[i] <= 90 && i > 1) {
//						sReturn += "_"+ c[i];
//					}else {
//						sReturn += c[i];
//					}
//				}
//			}
//		}
//		return sReturn.toUpperCase();		
//	}
}
