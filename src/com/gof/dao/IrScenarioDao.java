package com.gof.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Session;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrScenario;
import com.gof.enums.EIrScenario;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IrScenarioDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrCurveHis> getEntities(String baseDate, EIrScenario enumScen, String irModelId) {		
		return getEntities(baseDate, enumScen.getScenNum(), irModelId);
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String baseDate, EIrScenario enumScen, String irModelId) {		
		return getEntitiesMap(baseDate, enumScen.getScenNum(), irModelId);
	}				
	
	
	public static List<IrCurveHis> getEntities(String baseDate, Integer scenNum, String irModelId) {		
		
		String query = "select a from IrScenario a         "
	               	 + " where 1=1                         "
		             + "   and a.baseDate   = :baseDate    "
		             + "   and a.scenNum    = :scenNum     "		             
		;

		List<IrScenario> irShockScenario = session.createQuery(query, IrScenario.class)
				                                  .setParameter("baseDate", baseDate)				                                  
				                                  .setParameter("scenNum", scenNum.toString())				                                  				                                 
				                                  .getResultList();		
		
		List<IrCurveHis> irCurveHis = new ArrayList<IrCurveHis>();
		
		for(IrScenario scen : irShockScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(baseDate);
			curveHis.setIrCurveId("1111111");			
			curveHis.setMatCd(scen.getMatCd());
			curveHis.setIntRate(scen.getIntRate());
			
			irCurveHis.add(curveHis);			
		}		
		
//		Collections.sort(irCurveHis, new Comparator<IrCurveHis>() {			
//			public int compare(IrCurveHis curve1, IrCurveHis curve2) {
//				if(Integer.valueOf(curve1.getMatCd().split("M")[1]) > Integer.valueOf(curve2.getMatCd().split("M")[1])) return 1;
//				else if(Integer.valueOf(curve1.getMatCd().split("M")[1]) < Integer.valueOf(curve2.getMatCd().split("M")[1])) return -1;
//				else return 0;				
//			}
//		});
//		
//		irCurveHis.stream().filter(s -> Integer.valueOf(s.getMatCd().split("M")[1]) <= 5).forEach(s -> logger.info("irCurveHis: {}", s));		
		
		return irCurveHis;		
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String baseDate, Integer scenNum, String irModelId) {		
		
		baseDate = (baseDate != null) ? getMaxBaseDate(baseDate) : getMaxBaseDate(TimeUtil.dateToString(LocalDate.now()));		
		
		String query = "select a from IrScenario a         "		
	               	 + " where 1=1                         "
		             + "   and a.baseDate   = :baseDate    "
		             + "   and a.scenNum    = :scenNum     "	
		             //+ "   and a.irModelId  = :irModelId   "
		             + "   order by a.matCd                "
		;

		List<IrScenario> irScenario = session.createQuery(query, IrScenario.class)
				                                  .setParameter("baseDate", baseDate)
				                                  .setParameter("scenNum", scenNum.toString())
				                                  .getResultList();		

		Map<String, IrCurveHis> irCurveHisMap = new TreeMap<String, IrCurveHis>();
		
		for(IrScenario scen : irScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(scen.getBaseDate());
			curveHis.setIrCurveId("1111111");			
			curveHis.setMatCd(scen.getMatCd());
			curveHis.setIntRate(scen.getIntRate());
						
			irCurveHisMap.put(curveHis.getMatCd(), curveHis);			
		}				
		//irCurveHisMap.entrySet().stream().filter(s -> Integer.valueOf(s.getKey().split("M")[1]) <= 10).forEach(s -> logger.info("irCurveHisMap: {}, {}", s.getKey(), s.getValue().getIntRate()));		
		
		return irCurveHisMap;		
	}
	
	/**
	 * TODO: KTB처리를 위하여 query 내용 수정
	 */
	private static String getMaxBaseDate(String baseDate) {		
		
		String query = "select max(a.baseDate)                "
				+ "		from IrScenario a                     "
				+ "		where 1=1                             "
				+ "		and a.baseDate <= :baseDate "
				//+ "     and a.irModelId  = :irModelId         "
				//+ "		and substr(a.baseDate,1,6)  <= :bseDt "
				//+ "		and a.baseDate  >= :bseDt       "
		;		
		
		String maxDate =  (String) session.createQuery(query)
				                     	  .setParameter("baseDate", baseDate)
				                     	  //.setParameter("irModelId", irModelId)
			            				  .uniqueResult();		
		
		if(maxDate == null) {
			log.error("IR Scenario Data is not found at {}!!!", baseDate);
			return baseDate;
		}
		return maxDate;
	}	

}
