package com.gof.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.gof.entity.KicsInstCurve;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KicsInstCurveDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	private static String curveId = "1111111";
	private static String user    = "111";
	private static LocalDate date = LocalDate.now();

//	private static String baseQuerySecr = " select a.EXPO_ID             "
//	                                    + " , '" + curveId + "' , '" + user + "' , '" + date + "' "  										
//			                            + " from Q_IC_ASSET_SECR a        "			                            
//			                            + " where 1=1                   "			            
//			                            + "	and a.BASE_DATE = :baseDate  "
//			                            ;
	
	
	private static String baseQuerySecr = " select a.expoId             "
            + " , '" + curveId + "' , '" + user + "' , '" + date + "' "  										
            + " from KicsAssetSecr a        "			                            
            + " where 1=1                   "			            
            + "	and a.baseDate = :baseDate  "
            + " and rownum <= 10 "
            ;
	

//	private static String baseQueryLoan = " select a.expoId             "
//			+ " , '" + curveId + "' , '" + user + "' , '" + date + "' "
//							            + " from KicsAssetLoan a        "			                            
//							            + " where 1=1                   "			            
//							            + "	and a.baseDate = :baseDate  "
//							            ;
//	
//	private static String baseQueryFide = " select a.expoId             "
//			+ " , '" + curveId + "' , '" + user + "' , '" + date + "' "				            
//										+ " from KicsAssetFide a        "			                            
//							            + " where 1=1                   "			            
//							            + "	and a.baseDate = :baseDate  "
//                                        ;
//
//	private static String baseQueryAcco = " select a.expoId             "
//			+ " , '" + curveId + "' , '" + user + "' , '" + date + "' "
//							            + " from KicsAssetAcco a        "			                            
//							            + " where 1=1                   "			            
//							            + "	and a.baseDate = :baseDate  "
//                                        ;	

	private static String deleteQuery = "delete "
            						 + KicsInstCurveDao.class.getSimpleName().split("Dao")[0]			                         
            						 + " a where 1=1 "
                                     ;
	
	@SuppressWarnings("unchecked")
	public static void insertEntities(String baseDate) {		
		
		List<KicsInstCurve> instCurve;
		List<KicsInstCurve> instCurvePool = new ArrayList<KicsInstCurve>();
		
		session.beginTransaction();		
		//log.info("{}, {}", session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList().stream().toString());		
		
		//session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList().stream().forEach(s -> instCurve.add((KicsInstCurve) s));		
		//session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList().stream().forEach(s -> log.info("instCurve: {}", s));
		
	    instCurve = session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList();
	    
	    for(int i=0; i<instCurve.size(); i++) {
	    	log.info("{}", (String) instCurve.get(i).getExpoId());
	    }
//	    for(Object ic : instCurve) {
//	    	instCurvePool.add((KicsInstCurve) ic);	    	
//	    }
	    //instCurvePool.addAll(instCurve);

//	    log.info("{}", instCurvePool.get(0).getExpoId());
//	    log.info("{}", instCurvePool.get(0).getIrateCurveId());
//	    log.info("{}", instCurvePool.get(0).getLastModifiedBy());
	    
	    //if(instCurvePool.get(0) instanceof KicsInstCurve) log.info("^^^^^^^^^^^^^^^^^^^^");
	    //if(instCurvePool.get(0) instanceof Object) log.info("^^^^^^^^^^^^^^^^^^^^");
	    log.info("{}, {}", instCurve.size(), instCurvePool.size());
	    
	    
//	    KicsInstCurve aaa = (KicsInstCurve) instCurvePool.get(0);
//	    log.info("{}", aaa.getExpoId());
//	    instCurve = session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList();	    
//	    instCurvePool.addAll(instCurve);
//
//	    instCurve = session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList();	    
//	    instCurvePool.addAll(instCurve);
//
//	    instCurve = session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList();	    
//	    instCurvePool.addAll(instCurve);

		
//		instCurve = session.createNativeQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList();
//		instCurve.stream().forEach(s -> log.info("instCurve: {}", s));
		
//		instCurve.addAll(session.createQuery(baseQuerySecr).setParameter("baseDate", baseDate).getResultList());		
//		instCurve.addAll(session.createQuery(baseQueryLoan).setParameter("baseDate", baseDate).getResultList());
//		instCurve.addAll(session.createQuery(baseQueryFide).setParameter("baseDate", baseDate).getResultList());
//		instCurve.addAll(session.createQuery(baseQueryAcco).setParameter("baseDate", baseDate).getResultList());
//
//		for(KicsInstCurve ic : instCurvePool) {
//			//log.info("{}", ic.toString());
//			//session.save(ic);
//		}
		session.getTransaction().commit();		
		log.info("{} Entities have been Inserted", log.getName());
	}	
	

	
	public static void deleteEntities() {		
	
		StringBuilder sb = new StringBuilder();		
		sb.append(deleteQuery);
		
		session.beginTransaction();		
		session.createQuery(sb.toString()).executeUpdate();		
		
		session.getTransaction().commit();
		log.info("{} Entities have been Deleted", log.getName());
	}
	
	

}
