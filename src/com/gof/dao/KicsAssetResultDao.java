package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.KicsAssetResult;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KicsAssetResultDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from "
			                         + KicsAssetResultDao.class.getSimpleName().split("Dao")[0]			                         			                         
			                         + " a where 1=1 "
			                         ;	
	
	private static String deleteQuery = "delete "
                                     + KicsAssetResultDao.class.getSimpleName().split("Dao")[0]			                         
                                     + " a where 1=1 "
                                     ;	
	private static String deleteQuery1 = "delete KicsAssetResul a where a.baseDate = :baseDate"
            
            ;	
	
	public static void deleteEntities() {		
		
		StringBuilder sb = new StringBuilder();		
		sb.append(deleteQuery);
		
	    session.beginTransaction();		
		session.createQuery(sb.toString()).executeUpdate();		
		
		session.getTransaction().commit();
		log.info("{} Entities have been Deleted", log.getName());
	}

	
	public static void deleteEntities(String baseDate) {
		
		
		StringBuilder sb = new StringBuilder();		
		if(baseDate == null) sb.append(deleteQuery);
		else sb.append(deleteQuery).append(" and ").append(" baseDate ").append(" = ").append(baseDate);
		
		
		

	    session.beginTransaction();		
		session.createQuery(sb.toString()).executeUpdate();		
		//session.createQuery("Delete KicsAssetResult").executeUpdate();		
		//session.createQuery(deleteQuery, KicsAssetResult.class).setParameter("baseDate", baseDate).executeUpdate();
		
		
		
		session.getTransaction().commit();
		log.info("{} Entities have been Deleted: [BASE_DATE: {}]", log.getName(), baseDate);
	}
		
	
	public static void deleteEntities(String baseDate, int scenNum) {
		
		StringBuilder sb = new StringBuilder();		
		sb.append(deleteQuery)
		  .append(" and ").append(" baseDate ").append(" = ").append(baseDate)
		  .append(" and ").append(" scenNum ").append(" = ").append(scenNum)
		  ;		

	    session.beginTransaction();		
		session.createQuery(sb.toString()).executeUpdate();		
		//session.createQuery("Delete KicsAssetResult").executeUpdate();		
		//session.createQuery(deleteQuery, KicsAssetResult.class).setParameter("baseDate", baseDate).executeUpdate();
		
		session.getTransaction().commit();
		log.info("{} Entities have been Deleted: [BASE_DATE: {}]", log.getName(), baseDate);		
	}

	
	public static List<KicsAssetResult> getEntities(String baseDate) {				
		
		StringBuilder sb = new StringBuilder();		
		sb.append(baseQuery).append(" and ").append(" baseDate ").append(" = ").append(baseDate);

		return session.createQuery(baseQuery, KicsAssetResult.class).getResultList();
		
	}
	
}
