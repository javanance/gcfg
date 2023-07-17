package com.gof.test;

import java.awt.Window.Type;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.print.attribute.standard.MediaSize.Engineering;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.ParameterMetadata;
//import org.renjin.eval.Context;
//import org.renjin.primitives.vector.RowNamesVector;
//import org.renjin.repackaged.asm.Attribute;
//import org.renjin.script.RenjinBindings;
//import org.renjin.script.RenjinScriptEngineFactory;
//import org.renjin.sexp.AttributeMap;
//import org.renjin.sexp.ComplexVector;
//import org.renjin.sexp.DoubleArrayVector;
//import org.renjin.sexp.ListVector;
//import org.renjin.sexp.PairList;
//import org.renjin.sexp.SEXP;
//import org.renjin.sexp.SEXPBuilder;
//import org.renjin.sexp.StringArrayVector;
//import org.renjin.sexp.StringVector;
//import org.renjin.sexp.Symbol;
//import org.renjin.sexp.Symbols;
//import org.renjin.sexp.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.EsgMst;
import com.gof.util.HibernateUtil;

import javassist.bytecode.SignatureAttribute.TypeVariable;

public class ConTest_sws {
//	private final static Logger logger = LoggerFactory.getLogger("ConTest");
//	 public static void main(String[] args) {
//			System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
//			System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
////		 Context context = new Context();
//		    Session session = HibernateUtil.getSessionFactory().openSession();
//		    session.beginTransaction();
//
//		    String sql = "select a from EsgMst a" ;
//		    List<EsgMst> rst = session.createQuery(sql).getResultList();
//
//		    for(EsgMst aa : rst){
//		    	logger.info("aaa : {},{},{}", aa.getIrModelId(), aa.getIrModelNm(), aa);
//		    }
//		    session.getTransaction().commit();
//		    session.close();
//		    
//			RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
//			ScriptEngine engine = factory.getScriptEngine();
//			
//			
//			try {
//				/* 출력할 데이터 목록 */
//				String[] sResultData = {"int.raw.full.a100","bse_dt_set","ufr","swaptionMaturities","bse_dt"};
//				
//				/* 입력데이터 정의 */
//				engine.put("bse_dt", "20171229");
//				engine.put("ufr", 0.045);
//				engine.put("UFR_T", 60);
//				engine.put("LLP", 20);
//				engine.put("TEST_DATA", RScriptData.toDataFrame(rst));
//                /**
//				 * 결과를 Stream 가로채기 위한 구문 Test
//				 */
////				engine.getContext().setWriter(outpuw);
////				String output = outpuw.toString();
////				System.out.println(output);
//				
//				engine.eval(new java.io.FileReader("D:\\R_DATA\\SCRIPT\\test.R"));
//				engine.eval("print(bse_dt_set)");
//				engine.eval("print(TEST_DATA)");
//				
//				
//				/**
//				 * DataFrame getOut
//				 */
////				AttributeMap am = new AttributeMap();
//				
////				ListVector input = new ListVector(values, attributes)
////				SEXP se2 = (SEXP) engine.eval("int.raw.full.a100");
////				System.ot.println(se2.getTypeName());u
//				
////				Vector vData = model.getElementAsVector("BSE_DT");
////				System.out.println(vData.getVectorType());
////				System.out.println("row cnt :"+vData.length());
//				
//				
//				for(int i =0 ; i < sResultData.length ; i++) {
//					SEXP se = (SEXP) engine.eval(sResultData[i]);
//					System.out.println(se.getTypeName());
//					switch(se.getTypeName()) {
//						case "list":
//							System.out.println("+++++++++++++"+sResultData[i]+" Type is LIST - ");
//							ListVector model = (ListVector)engine.eval(sResultData[i]);
////							model.getIndexByName(arg0)
//							System.out.println(model.getTypeName());
//							
////							AttributeMap am = model.getAttributes();
////							StringVector sv = am.getNames();
//							System.out.println(" size = : "+model.length());
//							for(int ami = 0 ; ami < model.length() ; ami ++) {
//								System.out.print(model.getName(ami)+" || ");
//							}
//							System.out.println();
//							break;
//							
//						case "character":
//							System.out.println("+++++++++++++"+sResultData[i]+" Type is CHARACTER - ");
//							break;
//							
//						case "double":
//							System.out.println("+++++++++++++"+sResultData[i]+" Type is DOUBLE - ");
//							break;
//						default:
//							break;
//					}
//					
//				}
//				
////				String output = outpuw.toString();
////				System.out.println(output);
////				StringWriter outpuw = new StringWriter();
////				engine.getContext().setWriter(outpuw);
////				engine.eval("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))");
////				engine.eval("print(test)");
////				engine.eval("print(test2)");
////				String output = outpuw.toString();
////				System.out.println(output);
////				engine.eval(new java.io.FileReader("script.R"));
////				engine
//			}catch(ScriptException | FileNotFoundException se) {
//				se.printStackTrace();
//			}
//			
////			StringWriter outpuw = new StringWriter();
//			
////		    String sql = "select a from Bond a" ;
////		    
////		    List<Bond> rst = session.createQuery(sql).getResultList();
////		    for(Bond aa : rst){
////		    	logger.info("aaa : {},{},{}", aa.getMvId(), aa.getIssuerId());
////		    }
//		    
////			Bindings b = new 
////			engine.setsetBindings(bindings, ScriptContext.ENGINE_SCOPE);
//		   
//		    
//		    HibernateUtil.shutdown();
//		  }
//
	 
	
}
