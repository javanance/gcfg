package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.interfaces.KicsAsset;
import com.gof.util.HibernateUtil;


public class KicsAssetAccoDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from "
			                         + KicsAssetAccoDao.class.getSimpleName().split("Dao")[0]
			                         + " a where 1=1 "
			                         ;
	
	
//	public static List<KicsAssetAcco> getEntities(String baseDate, String expoId) {		
//		
//		StringBuilder sb = new StringBuilder();		
//		sb.append(baseQuery).append(" and ").append(" expoId ").append(" = ").append(expoId);		
//		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);				
//		
//		return session.createQuery(sb.toString(), KicsAssetAcco.class).getResultList();
//	}
//	
//	
//	public static List<KicsAssetAcco> getEntities(String baseDate) {		
//				
//		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);		
//		
//		return session.createQuery(baseQuery, KicsAssetAcco.class).getResultList();
//	}	
		

	public static List<KicsAsset> getEntities(String baseDate, String expoId) {		
		
		StringBuilder sb = new StringBuilder();		
		sb.append(baseQuery).append(" and ").append(" expoId ").append(" = ").append(expoId);		
		
		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);				
		
		return session.createQuery(sb.toString(), KicsAsset.class).getResultList();
	}
	
	
	public static List<KicsAsset> getEntities(String baseDate) {		
				
		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);		
		
		return session.createQuery(baseQuery, KicsAsset.class).getResultList();
	}	

}
