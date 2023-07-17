package com.gof.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.gof.entity.*;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.util.HibernateUtil;


public class ReflectionTest {

	private final static Logger logger = LoggerFactory.getLogger("ConTest");
	
	//public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	public static void main(String[] args) throws Exception {
		
        IrCurve irCurve = new IrCurve(); 
		
        //System.out.println(irCurve.getClass().getName());
		//Class clazz = Class.forName("java.lang.String");
		//Class clazz = Class.forName("com.gof.entity.IrCurve");
		Class clazz = Class.forName(irCurve.getClass().getCanonicalName());		
		
		//Annotation[] annotations = clazz.getAnnotations();		
		
		//AnnotatedType[] annotations = clazz.getAnnotatedInterfaces();
		
		//System.out.println(clazz.getDeclaredField("irCurveId").toString());
		
		
		//Method[] methods = clazz.getDeclaredMethods();
		
		System.out.println("___________________________________________________");
				
		//for(Method meth : methods) System.out.println(meth.getName());
		
		
		
//		String test = "aaa";		
//		System.out.println(IdString.fieldToGetterUtil(test));
//		
//		String test2 = "gttAaa";
//		System.out.println(IdString.getterToFieldUtil(test2));	
		
		
		
		
		System.out.println("___________________________________________________");
		
		
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    session.beginTransaction();
	    
	    String filterName = "FILTER";	    
	    	    
	    //Filter filter  = session.enableFilter(filterName).setParameter("baseYymm", "201712");
    
    
    
        String entity = "IrCurveHis";
        
        String where = "";
        where = "where 1=1 and rownum <= 10";	        
        //where = "where a.matCd = 'M0003' and rownum < 10";
        //where = "where a.matCd = 'M0003'";
        
        
	    String sql = "select a from " + entity + " a " + where;
	    
	    List<IrCurveHis> rst = session.createQuery(sql).getResultList();
	    for(IrCurveHis aa : rst){    	
	    	//logger.info("aaa : {},{},{},{},{},{}", aa.idString("|"), aa.idMap().toString(), aa);
	    	
	    	System.out.println(aa.idMapColumn().toString());
	    		    	
//	    	Method meth1 = aa.getClass().getMethod("getIrCurveId", null);
//	    	System.out.println("[INVOKE]" + meth1.invoke(aa, null));    	
	    	
//	    	Method[] methods = aa.getClass().getMethods();	    	
//	    	
//	    	for(Method meth : methods) {	    		
//	    		annotations = meth.getAnnotations();
//	    		
//	    		for(Annotation anno : annotations) {	    			
//	    			if (anno.annotationType().equals(Id.class)) {	    				
//	    				System.out.println(meth.getName() + ": " + meth.invoke(aa));	    				
//	    			}
//	    		}	    		
//	    	}	    	
//   	
//	    	
//	    	Field[] field = aa.getClass().getFields();	    	
//	    	//for(Field fd : field) System.out.println("[Field]" + fd.get(aa));
//	    	for(Field fd : field) {	    		
//	    		annotations = fd.getAnnotations();	    		
//	    		for(Annotation anno : annotations) {	    			
//	    			//if (fd.isAnnotationPresent(Id.class)) System.out.println(fd.get(aa));	    		
//	    		}
//	    	}
	    	
//	    	Field[] field2 = aa.getClass().getDeclaredFields();	    	
//	    	for(Field fd : field2) System.out.println("[Field2]" + fd.getName());
//	    	
//	    	AnnotatedType[] anno1 = aa.getClass().getAnnotatedInterfaces();
//	    	for(AnnotatedType anno : anno1) System.out.println("[AnnotatedType1]" + anno.getType());
//	    	
//	    	Annotation[] anno2 = aa.getClass().getAnnotations();
//	    	for(Annotation anno : anno2) System.out.println("[Annotattion2]" + anno.annotationType());
//
//	    	Field[] field3 = aa.getClass().getFields();	    	
	    	//for(Field fd : field3) System.out.println("[Field3]" + fd.getAnnotationsByType(aa));
	    	
	    	

	    	
//	    	Method[] methList = aa.getClass().getMethods();
//	    	System.out.println(methList)
	    			
	    	//ReflectionUtil r = new ReflectionUtil(a0a.getClass().getPackage().getName());
//	    	ReflectionUtil r = new ReflectionUtil();
//	    	logger.info(r.getClass().getAnnota
	    	
	    	//logger.info(ircurve.getClass().getDeclaredAnnotations().toString());
	    	//logger.info("aaa : {},{},{},{},{},{}", aa);		    	
	    	
	    }	    
	    
	    session.getTransaction().commit();
	    session.close();		    
	    
	    HibernateUtil.shutdown();	    
	    
	  }

 

}

