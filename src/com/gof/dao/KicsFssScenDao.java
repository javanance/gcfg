package com.gof.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Session;

import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsFssScen;
import com.gof.enums.EIrScenario;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KicsFssScenDao {	
		
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrCurveHis> getEntities(String baseYm, EIrScenario enumScen, String irModelId) {		
		return getEntities(baseYm, enumScen.getScenNum(), irModelId);
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String baseYm, EIrScenario enumScen, String irModelId) {		
		return getEntitiesMap(baseYm, enumScen.getScenNum(), irModelId);
	}	

	
	public static List<IrCurveHis> getEntities(String baseYm, Integer scenNum, String fssCrveCd) {		
		
		String yyyymm = (baseYm != null) ? getMaxBaseYm(baseYm) : getMaxBaseYm(TimeUtil.toYearMonth(LocalDate.now()));		
		
		String query = "select a from KicsFssScen a        "
	               	 + " where 1=1                         "
		             + "   and a.baseYm     = :baseYm      "
		             + "   and a.scenNo     = :scenNo      "
		             + "   and a.fssCrveCd  = :fssCrveCd   "
		             + "   and a.matCd     <> 'M0000'      "
		             + "   order by a.matCd                "
		;

		List<KicsFssScen> fssScenario = session.createQuery(query, KicsFssScen.class)
				                                   .setParameter("baseYm", yyyymm)
				                                   .setParameter("scenNo", scenNum)
				                                   .setParameter("fssCrveCd", fssCrveCd)				                                   
				                                   .getResultList();		
		
		List<IrCurveHis> irCurveHis = new ArrayList<IrCurveHis>();
		
		for(KicsFssScen fss : fssScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(TimeUtil.toEndOfMonth(fss.getBaseYm()));
			curveHis.setIrCurveId(GeneralUtil.concatenate(fss.getFssCrveCd(), fss.getScenNo().toString()));
			curveHis.setMatCd(fss.getMatCd());
			curveHis.setIntRate(GeneralUtil.objectToPrimitive(fss.getRate()));		

			irCurveHis.add(curveHis);			
		}		
		return irCurveHis;		
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String baseYm, Integer scenNum, String fssCrveCd) {	
		
		String yyyymm = (baseYm != null) ? getMaxBaseYm(baseYm) : getMaxBaseYm(TimeUtil.toYearMonth(LocalDate.now()));		
		
		String query = "select a from KicsFssScen a        "
	               	 + " where 1=1                         "
		             + "   and a.baseYm     = :baseYm      "
		             + "   and a.scenNo     = :scenNo      "
		             + "   and a.fssCrveCd  = :fssCrveCd   "
		             + "   and a.matCd     <> 'M0000'      "
		             //+ "   and rownum <= 20                "
		             + "   order by a.matCd                "
		;		

		List<KicsFssScen> fssScenario = session.createQuery(query, KicsFssScen.class)
				                                   .setParameter("baseYm", yyyymm)
				                                   .setParameter("scenNo", scenNum)
				                                   .setParameter("fssCrveCd", fssCrveCd)				                                   
				                                   .getResultList();		
		
		Map<String, IrCurveHis> irCurveHisMap = new TreeMap<String, IrCurveHis>();
		
		for(KicsFssScen fss : fssScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(TimeUtil.toEndOfMonth(fss.getBaseYm()));
			
			/**
			 * TODO: In setIrCurveId Method, IrCurveId is also contained [ScenNo] intentionally.
			 */
			curveHis.setIrCurveId(GeneralUtil.concatenate(fss.getFssCrveCd(), fss.getScenNo().toString()));
			curveHis.setMatCd(fss.getMatCd());
			curveHis.setIntRate(GeneralUtil.objectToPrimitive(fss.getRate()));			

			irCurveHisMap.put(curveHis.getMatCd(), curveHis);	
		}		
		
		//log.info("irCurveHisMap: {}", irCurveHisMap);
		return irCurveHisMap;		
	}	
	
//	private static String bucketToMatCd(LocalDate asOfDate, LocalDate bucketEndDate) {		
//		//return Constant.TIME_UNIT_MONTH + String.format("%04d", TimeUtil.monthBetween(asOfDate, bucketEndDate) + 1);		
//		return TimeUtil.monthBetweenMatCd(asOfDate, bucketEndDate);
//	}		
	

	private static String getMaxBaseYm(String baseYm) {		
		
		String query = "select max(a.baseYm)          "
				+ "		  from KicsFssScen a          "
				+ "		 where 1=1                    "		
				+ "	       and a.baseYm <= :baseYm    "
		;
		
		String maxYm =  (String) session.createQuery(query).setParameter("baseYm", baseYm).uniqueResult();		
		
		if(maxYm == null) {
			log.warn("No Data found in FSS Interest Rate Scenario at {}", baseYm);
			return baseYm;
		}
		return maxYm;
	}	
	
}
