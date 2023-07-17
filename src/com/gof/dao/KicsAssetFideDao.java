package com.gof.dao;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;

import com.gof.interfaces.KicsAsset;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KicsAssetFideDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from "
			                         + KicsAssetFideDao.class.getSimpleName().split("Dao")[0]
			                         + " a where 1=1 "
			                         ;
	
	
//	public static List<KicsAssetFide> getEntities(String bseDt, String expoId) {		
//		
//		StringBuilder sb = new StringBuilder();		
//		sb.append(baseQuery).append(" and ").append(" expoId ").append(" = ").append(expoId);		
//
//		String irCurveHisBaseDate = (bseDt != null) ? getMaxBseDt(bseDt) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
//		
//		session.enableFilter("BSE_DT").setParameter("bseDt", bseDt);
//		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);		
//		
//		return session.createQuery(sb.toString(), KicsAssetFide.class).getResultList();
//		//return session.createQuery(baseQuery, KicsAssDe.class).setParameter("bseDt", bseDt).getResultList();
//	}
//	
//	
//	public static List<KicsAssetFide> getEntitiesForTest(String bseDt, String invmPdcTpcd) {		
//		
//		StringBuilder sb = new StringBuilder();		
//		sb.append(baseQuery).append(" and ").append(" invmPdcTpcd ").append(" = ").append(invmPdcTpcd);		
//
//		String irCurveHisBaseDate = (bseDt != null) ? getMaxBseDt(bseDt) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
//		
//		session.enableFilter("BSE_DT").setParameter("bseDt", bseDt);
//		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);
//		
//		return session.createQuery(sb.toString(), KicsAssetFide.class).getResultList();
//		//return session.createQuery(baseQuery, KicsAssDe.class).setParameter("bseDt", bseDt).getResultList();
//	}	
//
//	
//	
//	public static List<KicsAssetFide> getEntities(String bseDt) {		
//		
//		String irCurveHisBaseDate = (bseDt != null) ? getMaxBseDt(bseDt) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
//		
//		session.enableFilter("BSE_DT").setParameter("bseDt", bseDt);
//		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);
//		
//		return session.createQuery(baseQuery, KicsAssetFide.class).getResultList();
//		//return session.createQuery(baseQuery, KicsAssDe.class).setParameter("bseDt", bseDt).getResultList();
//	}	
	

	public static List<KicsAsset> getEntities(String baseDate, String expoId) {		
		
		StringBuilder sb = new StringBuilder();		
		sb.append(baseQuery).append(" and ").append(" expoId ").append(" = ").append(expoId);		

		String irCurveHisBaseDate = (baseDate != null) ? getMaxBseDt(baseDate) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
		
		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);
		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);		
		
		return session.createQuery(sb.toString(), KicsAsset.class).getResultList();
		//return session.createQuery(baseQuery, KicsAssDe.class).setParameter("bseDt", bseDt).getResultList();
	}	
	
	
	public static List<KicsAsset> getEntities(String baseDate) {		
		
		String irCurveHisBaseDate = (baseDate != null) ? getMaxBseDt(baseDate) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
		
		session.enableFilter("BASE_DATE_ASSET").setParameter("baseDate", baseDate);
		session.enableFilter("BASE_DATE").setParameter("baseDate", irCurveHisBaseDate);		
		
		return session.createQuery(baseQuery, KicsAsset.class).getResultList();
		//return session.createQuery(baseQuery, KicsAssDe.class).setParameter("bseDt", bseDt).getResultList();
	}	

	
	private static String getMaxBseDt(String baseDate) {		
		
		String query = "select max(a.baseDate) "
				+ "		from IrCurveHis a "
				+ "		where 1=1 "		
				+ "		and a.baseDate <= :baseDate "
		;
		
		String maxDate =  (String) session.createQuery(query)		
				            			  .setParameter("baseDate", baseDate)								 
								          .uniqueResult();
		
		if(maxDate == null || maxDate.length() != 8) {
			log.warn("Q_IC_IR_CURVE_HIS Data is not found at {}!!!", baseDate);
			//log.error("IR Curve History Data is not found at {}!!!", baseDate);
			return baseDate;
		}
		return maxDate;
	}		

	
//	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveId){
//		String query = "select a from IrCurveHis a "
//				+ "		where 1=1 "
//				+ "		and a.irCurveId =:irCurveId "
//				+ "		and a.baseDate  = :bssd	"
//				+ "     order by a.matCd"
//				;
//		
//		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
//				.setParameter("irCurveId", irCurveId)
//				.setParameter("bssd", getMaxBaseDate(bssd, irCurveId))
//				.getResultList();
//		
////		log.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
//		return curveRst;
//	}	
	
//	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
//		String query = "select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
//				+ "		from IrCurveHis a "
//				+ "		where 1=1 "
//				+ "		and a.baseDate <= :bssd	"
//				+ "		and a.irCurveId =:param1 "
//				+ "     and a.matCd in (:param2, :param3) "
//				+ "		group by substr(a.baseDate,1,6), a.matCd "
//				;
//		
//		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
//				.setParameter("param1", "A100")
//				.setParameter("param2", matCd1)
//				.setParameter("param3", matCd2)
//				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
//				.getResultList();		
//		return curveRst;
//	}
	
	
//	public static List<IrCurveHis> getCurveHisBetween(String bssd, String stBssd,  String curveId){
//		String query = "select a from IrCurveHis a "
//				+ "		where 1=1 "
//				+ "		and a.baseDate <= :bssd	"
//				+ "		and a.baseDate >= :stBssd "
//				+ "		and a.irCurveId =:param1 "
//				+ "		and a.matCd not in (:matCd1, :matCd2, :matCd3) "
//				+ "     order by a.baseDate"
//				;
//		
//		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
//				.setParameter("param1", curveId)
//				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
//				.setParameter("stBssd", stBssd)
//				.setParameter("matCd1", "M0018")
//				.setParameter("matCd2", "M0030")
//				.setParameter("matCd3", "M0048")
//				.getResultList();		
//		
////		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
////				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
////		curveMap.entrySet().forEach(s -> log.info("aaa : {},{},{}", s.getKey(), s.getValue()));
//		return curveRst;
//	}
	
	
//	public static Map<String, List<IrCurveHis>> getIrCurveListTermStructure(String bssd, String stBssd, String irCurveId){
//		String query =" select a from IrCurveHis a " 
//					+ " where a.irCurveId =:irCurveId "			
//					+ "	and a.baseDate >= :stBssd "
//					+ "	and a.baseDate <= :bssd "
//					+ "	and a.matCd in (:matCdList)"
//					+ " order by a.baseDate, a.matCd "
//					;
//		
//		return session.createQuery(query, IrCurveHis.class)
//				.setParameter("irCurveId", irCurveId)
//				.setParameter("stBssd", stBssd)
//				.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
//				.stream()
////				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
//				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
//				;
//	}
	
}
