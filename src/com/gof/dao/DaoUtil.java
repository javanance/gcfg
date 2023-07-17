package com.gof.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;


/**
 *  <p>단일 테이블에 대한 단순한 추출 조건인 경우에 적용하는 범용적인 DAO (Data Access Object) 임.      
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DaoUtil {
	
	//private final static Logger logger = LoggerFactory.getLogger("DAO");
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getEntities(String entityName) {
		try {
			return (List<T>) getEntities(Class.forName("com.gof.entity." + entityName));
		} catch (Exception e) {
			log.error("Class Name Error : {}", e);
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public static <T> List<T> getEntities(String entityName, Map<String, Object> param) {
		try {
			return (List<T>) getEntities(Class.forName("com.gof.entity." + entityName), param);
		} catch (Exception e) {
			log.error("Class Name Error : {}", e);
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> getEntityStream(String entityName, Map<String, Object> param) {
		try {
			return (Stream<T>) getEntityStream(Class.forName("com.gof.entity." + entityName), param);
		} catch (Exception e) {
			log.error("Class Name Error : {}", e);
		}
		return null;
	}
	
	public static <T> List<T> getEntities(Class<T> klass) {
		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;		

		Query<T> q = session.createQuery(builder.toString(), klass);		
		List<T> rst = q.getResultList();
		return rst;
	}

	
	/** 
	*  Entity Class 에 대한 Equal 조건의 매개변수를 적용하여 데이터를 추출하는 Method 임  
	*  @param klass 	   Entity Class 의 클래스 메타
	*  @param <T>		   메타 클래스의 실제 클래스를 의미함.
	*  @param param		Equal 조건의 조건구문을 지정하는 Map 형식 ( 예시 : ("baseDate", "201712") 의 (key, value) 구조로써 baseDate = '201712' 를 만족하는 데이터를 추출하는 경우임) 
	*  @return          입력한 Entity Class 중 조건을 만족하는 instance 를 리턴함.   
	*/ 
	public static <T> List<T> getEntities(Class<T> klass, Map<String, Object> param) {
		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;				
		
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
//		log.info("query : {}", builder.toString());
		Query<T> q = session.createQuery(builder.toString(), klass);

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		
		List<T> rst = q.getResultList();
		return rst;
	}

	
	public static <T> List<T> getEntities(Class<T> klass, Map<String, Object> param, Map<String, Map<String, Object>> filter) {
		Filter f ; 
		for(Map.Entry<String, Map<String, Object>> entry : filter.entrySet()) {
			f = session.enableFilter(entry.getKey());
			for(Map.Entry<String, Object> zz : entry.getValue().entrySet()) {
				f.setParameter(zz.getKey(), zz.getValue());
//				log.info("filter: {},{},{},{}", klass.getName(), entry.getKey(), zz.getKey(), zz.getValue());
			}
		}
		//		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);

		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
		
		Query<T> q = session.createQuery(builder.toString(), klass);

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		
//		log.info("Query : {},{}", klass.getSimpleName(), builder.toString());
//		q.stream().forEach(s -> log.info("GetEntities : {},{},{},{}", s.toString()));

		List<T> rst = q.getResultList();
//		for(Map.Entry<String, Map<String, Object>> entry : filter.entrySet()) {
//			session.disableFilter(entry.getKey());
//		}
		return rst;
	}
	
	
	public static <T> Stream<T> getEntityStream(Class<T> klass, Map<String, Object> param) {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		session.beginTransaction();

		StringBuilder builder = new StringBuilder();
		builder.append("select a from ")
			   .append(klass.getSimpleName())
			   .append(" a where 1=1")
			   ;

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			builder.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
		}
		
		Query<T> q = session.createQuery(builder.toString(), klass);
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
//		log.info("Query : {},{}", klass.getSimpleName(), builder.toString());

		return q.stream();
	}	

}
