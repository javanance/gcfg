package com.gof.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.KicsShckStk;
import com.gof.util.HibernateUtil;


public class KicsShckStkDao {	
	
	private final static Logger logger = LoggerFactory.getLogger(KicsShckStkDao.class.getSimpleName());	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	private static final String kicsShckStkName = "KICS";
	
	public static List<KicsShckStk> getEntities(String bseDt, String stkModelId) {
		
		if(stkModelId.equals(kicsShckStkName)) return getEntities(bseDt);
		else {
			logger.error("Stock Shock Scenario[{}] Value is not defined Yet!", stkModelId);
			return null;
		}
	}
	
	
	public static Map<String, KicsShckStk> getEntitiesMap(String bseDt, String stkModelId) {		
		
		if(stkModelId.equals(kicsShckStkName)) return getEntitiesMap(bseDt);
		else {
			logger.error("Stock Shock Scenario[{}] Value is not defined Yet!", stkModelId);
			return null;
		}		
	}				
	
	
	public static List<KicsShckStk> getEntities(String bseDt) {		
		
		String query = "select a from KicsShckStk a        "
	               	 + " where 1=1                         "
		             + "   and a.aplyStrtDate <= :baseDate "
		             + "   and a.aplyEndDate  >= :baseDate "
		;

		List<KicsShckStk> stkShockScenario = session.createQuery(query, KicsShckStk.class)
				                                    .setParameter("baseDate", bseDt)
				                                    .getResultList();
		
		if(stkShockScenario == null || stkShockScenario.isEmpty()) logger.error("Stock Shock Scenario is not found at {}", bseDt);
		return stkShockScenario;		
	}
	
	
	public static Map<String, KicsShckStk> getEntitiesMap(String bseDt) {	
		
		List<KicsShckStk> stkShockScenario = getEntities(bseDt);		
		Map<String, KicsShckStk> stkShockScenarioMap = new TreeMap<String, KicsShckStk>();
		
		for(KicsShckStk scen : stkShockScenario) {
			stkShockScenarioMap.put(scen.getStkKndCd() + scen.getKicsCrdGrd(), scen);
		}		
		return stkShockScenarioMap;		
	}	
	
}
