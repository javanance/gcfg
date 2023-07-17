package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.RefRateCoef;
import com.gof.entity.RefRateMap;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RefRateDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	public static List<RefRateMap> getRefRateMap(String baseDate) {		
		
		String query = "select a from RefRateMap a "
		+ "		where 1=1 "
		+ "		and a.baseDate =  :baseDate "
		;

		List<RefRateMap> refRateMapList = session.createQuery(query, RefRateMap.class)		
				                           .setParameter("baseDate",  baseDate)
				                           .getResultList();
		
		return refRateMapList;
	}
	

	public static List<RefRateMap> getRefRateMap(String baseDate, String positionId) {		
		
		String query = "select a from RefRateMap a "
		+ "		where 1=1 "
		+ "		and a.baseDate =  :baseDate "
		+ "     and a.positionId = :positionId"
		;

		List<RefRateMap> refRateMapList = session.createQuery(query, RefRateMap.class)		
				                           .setParameter("baseDate",  baseDate)
				                           .setParameter("positionId",  positionId)
				                           .getResultList();
		
		return refRateMapList;
	}


	public static List<RefRateCoef> getRefRateCoef(String baseDate) {		
		
		String query = "select a from RefRateCoef a "
		+ "		where 1=1 "
		+ "		and a.baseDate =  :baseDate "
		;

		List<RefRateCoef> refRateCoefList = session.createQuery(query, RefRateCoef.class)		
				                           .setParameter("baseDate",  baseDate)
				                           .getResultList();
		
		return refRateCoefList;
	}

}
