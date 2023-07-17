package com.gof.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveHis2;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IrCurveHisDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	public static List<IrCurveHis> getEntities(String irCurveId, String matCd, LocalDate startDate, LocalDate endDate) {		
		
		String query = "select a from IrCurveHis a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "
		+ "		and a.matCd     =  :matCd     "
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "
		+ "     order by a.baseDate           "
		;

		List<IrCurveHis> curveHis = session.createQuery(query, IrCurveHis.class)		
				                           .setParameter("irCurveId",  irCurveId)
				                           .setParameter("matCd", matCd)
				                           .setParameter("startDate", TimeUtil.dateToString(startDate))
				                           .setParameter("endDate", TimeUtil.dateToString(endDate))
				                           .getResultList();
		
		log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, curveHis.size());
		curveHis.stream().forEach(s -> log.info("Curve History: {}, {}, {}, {}", s.getBaseDate(), s.getIrCurveId(), s.getMatCd(), s.getIntRate()));
		
		return curveHis;
	}
	
	public static List<IrCurveHis> getEntities(String irCurveId, String matCd, LocalDate baseDate) {		
		
		String query = "select a from IrCurveHis a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "
		+ "		and a.matCd     =  :matCd     "
		+ "		and a.baseDate  =  :baseDate  "
		;

		List<IrCurveHis> curveHis = session.createQuery(query, IrCurveHis.class)
				                           .setParameter("irCurveId",  irCurveId)
				                           .setParameter("matCd", matCd)
				                           .setParameter("baseDate", TimeUtil.dateToString(baseDate))
				                           .getResultList();
		
		log.info("baseDate: {}, CurveHistory: {}", baseDate, curveHis.size());
		curveHis.stream().forEach(s -> log.info("Curve History: {}, {}, {}, {}", s.getBaseDate(), s.getIrCurveId(), s.getMatCd(), s.getIntRate()));
		
		return curveHis;
	}
	
	
	//////////////////////////////////////////////////////////////////
	// Below Code for IrCurveHis2 (Simple Query(not for instrument))
    //////////////////////////////////////////////////////////////////
	
	public static List<IrCurveHis2> getEntities(String irCurveId, String baseDate) {		
		
		String maxBaseDate = (baseDate != null) ? getMaxBaseDate(baseDate) : getMaxBaseDate(TimeUtil.dateToString(LocalDate.now()));
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  =  :baseDate  "
		;

		List<IrCurveHis2> curveHis = session.createQuery(query, IrCurveHis2.class)
				                           .setParameter("irCurveId",  irCurveId)				                           
				                           .setParameter("baseDate", maxBaseDate)
				                           .getResultList();
		
		log.info("baseDate: {}, CurveHistory: {}", baseDate, curveHis.size());
		//curveHis.stream().forEach(s -> log.info("Curve History: {}, {}", s.getMatCd(), s.getIntRate()));
		
		return curveHis;
	}
	
	
	public static List<IrCurveHis2> getEntities(String irCurveId, String startDate, String endDate) {		
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "		
		+ "     order by a.baseDate, a.matCd  "
		;

		List<IrCurveHis2> curveHis = session.createQuery(query, IrCurveHis2.class)
				                           .setParameter("irCurveId",  irCurveId)				                           
				                           .setParameter("startDate", startDate)
				                           .setParameter("endDate", endDate)
				                           .getResultList();
		
		//log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, curveHis.size());
		//curveHis.stream().forEach(s -> log.info("Curve History: {}, {}, {}, {}", s.getBaseDate(), s.getIrCurveId(), s.getMatCd(), s.getIntRate()));
		
		return curveHis;
	}		
	
	
	@SuppressWarnings("unchecked")
	//bug version...just footprint
	public static Map<String, Map<String, Double>> getEntitiesMap0(String irCurveId, String startDate, String endDate) {		
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "		
		+ "     order by a.baseDate, a.matCd  "
		;

		List<IrCurveHis2> curveHis = session.createQuery(query, IrCurveHis2.class)
				                           .setParameter("irCurveId",  irCurveId)				                           
				                           .setParameter("startDate", startDate)
				                           .setParameter("endDate", endDate)
				                           .getResultList();

		Map<String, Map<String, Double>> irCurveHisMap = new TreeMap<String, Map<String, Double>>();
		
		String _baseDate = null;
		TreeMap<String, Double> ts = new TreeMap<String, Double>();			
		
		for(IrCurveHis2 his : curveHis)	{
			
			if(_baseDate == null || _baseDate == his.getBaseDate()) {	    			
				ts.put(his.getMatCd().toString(), his.getIntRate());
				 _baseDate = his.getBaseDate();
			}
			if(_baseDate != his.getBaseDate()) {
				irCurveHisMap.put(_baseDate, (TreeMap<String, Double>) ts.clone());	    			
				//ts.clear();	    			
				ts.put(his.getMatCd().toString(), his.getIntRate());
				_baseDate = his.getBaseDate();
			}
		}
		//log.info("{}, {}, {}", irCurveHisMap.keySet(), irCurveHisMap.values());	
		log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, curveHis.size());

		return irCurveHisMap;
	}

	
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, Double>> getEntitiesMap1(String irCurveId, String startDate, String endDate) {		
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "		
		+ "     order by a.baseDate, a.matCd  "
		;

		List<IrCurveHis2> curveHis = session.createQuery(query, IrCurveHis2.class)
				                           .setParameter("irCurveId",  irCurveId)				                           
				                           .setParameter("startDate", startDate)
				                           .setParameter("endDate", endDate)
				                           .getResultList();

		Map<String, Map<String, Double>> irCurveHisMap = new TreeMap<String, Map<String, Double>>();
		TreeMap<String, Double> ts = new TreeMap<String, Double>();		
		String _baseDate = "";					
		
		for(IrCurveHis2 his : curveHis)	{
			
			if(_baseDate.equals("") || _baseDate.equals(his.getBaseDate())) {	    			
				ts.put(his.getMatCd().toString(), his.getIntRate());				 
			}
			if(!_baseDate.equals("") && !_baseDate.equals(his.getBaseDate())) {
				irCurveHisMap.put(_baseDate, (TreeMap<String, Double>) ts.clone());	    			
				ts.clear();				
				ts.put(his.getMatCd().toString(), his.getIntRate());
			}
			_baseDate = his.getBaseDate();			
		}
		irCurveHisMap.put(_baseDate, (TreeMap<String, Double>) ts.clone());
		//log.info("{}, {}, {}", irCurveHisMap.keySet(), irCurveHisMap.values());	
		log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, curveHis.size());

		return irCurveHisMap;
	}
	
	
	public static Map<String, Map<String, Double>> getEntitiesMap2(String irCurveId, String startDate, String endDate) {		
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "		
		+ "     order by a.baseDate, a.matCd  "
		;		 
		
    	Map<String, List<IrCurveHis2>> tsMap = session.createQuery(query, IrCurveHis2.class)
                                                      .setParameter("irCurveId",  irCurveId)				                           
                                                      .setParameter("startDate", startDate)
                                                      .setParameter("endDate", endDate)
                                                      .getResultList()
                                                      .stream().collect(Collectors.groupingBy(s -> s.getBaseDate(), TreeMap::new, Collectors.toList()));

        Map<String, Map<String, Double>> irCurveHisMap = new TreeMap<String, Map<String, Double>>();
        
        for(Map.Entry<String, List<IrCurveHis2>> ts : tsMap.entrySet()) {        	
        	irCurveHisMap.put(ts.getKey(), ts.getValue().stream().collect(Collectors.toMap(IrCurveHis2::getMatCd, IrCurveHis2::getIntRate)));
        }
				                        		  
		//log.info("{}, {}, {}", irCurveHisMap.keySet(), irCurveHisMap.values());	
		log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, tsMap.values());

		return irCurveHisMap;
	}	


	/**
	 * TODO: toMap부분 s -> s.getMatcd(), s.getIntRate() 등으로 처리가능한지 확인필요
	 * 
	 */
	public static Map<String, Map<String, Double>> getEntitiesMap3(String irCurveId, String startDate, String endDate) {		
		
		String query = "select a from IrCurveHis2 a "
		+ "		where 1=1 "
		+ "		and a.irCurveId =  :irCurveId "		
		+ "		and a.baseDate  >= :startDate "
		+ "		and a.baseDate  <  :endDate   "		
		+ "     order by a.baseDate, a.matCd  "
		;		 
		
    	Map<String, Map<String, Double>> irCurveHisMap = session.createQuery(query, IrCurveHis2.class)
                                                             .setParameter("irCurveId",  irCurveId)				                           
                                                             .setParameter("startDate", startDate)
                                                             .setParameter("endDate", endDate)
                                                             .getResultList()
                                                             //.stream().collect(Collectors.groupingBy(IrCurveHis2::getBaseDate
                                                             .stream().collect(Collectors.groupingBy(s -> s.getBaseDate()
                                                            		           , TreeMap::new, Collectors.toMap( IrCurveHis2::getMatCd, IrCurveHis2::getIntRate)));
				                        		  
    //Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
    //    	, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
    //.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
    //.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))

//    	log.info("{}, {}, {}", irCurveHisMap, irCurveHisMap.keySet(), irCurveHisMap.values());	
//		log.info("startDate: {}, endDate: {}, CurveHistory: {}", startDate, endDate, tsMap.values());

		return irCurveHisMap;
	}	
	
	
	private static String getMaxBaseDate(String baseDate) {		
		
		String query = "select max(a.baseDate) "
				+ "		from IrCurveHis2 a "
				+ "		where 1=1 "		
				+ "		and a.baseDate <= :baseDate "
		;
		
		String maxDate =  (String) session.createQuery(query)		
				            			  .setParameter("baseDate", baseDate)								 
								          .uniqueResult();
		
		if(maxDate == null || maxDate.length() != 8) {
			log.error("IR Curve History Data is not found at {}!!!", baseDate);
			return baseDate;
		}
		return maxDate;
	}	
	

}
