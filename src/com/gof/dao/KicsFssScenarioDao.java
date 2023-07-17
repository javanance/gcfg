package com.gof.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsFssScenario;
import com.gof.enums.EFssScenario;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;


public class KicsFssScenarioDao {	
	
	private final static Logger logger = LoggerFactory.getLogger(KicsFssScenarioDao.class.getSimpleName());	
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<IrCurveHis> getEntities(String bseDt, EFssScenario enumScen) {		
		return getEntities(bseDt, enumScen.getSceNo(), enumScen.getFssScenTyp());
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String bseDt, EFssScenario enumScen) {		
		return getEntitiesMap(bseDt, enumScen.getSceNo(), enumScen.getFssScenTyp());
	}				
	
	
	public static List<IrCurveHis> getEntities(String bseDt, Integer scenNum, String fssScenTyp) {		
		
		LocalDate asOfDate = (bseDt != null) ? getMaxBseDt(bseDt) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
		LocalDate inOneMonth = asOfDate.plusMonths(1);
		
		String query = "select a from KicsFssScenario a    "
	               	 + " where 1=1                         "
		             + "   and a.asOfDate   = :asOfDate    "
		             + "   and a.scenNum    = :scenNum     "
		             + "   and a.fssScenTyp = :fssScenTyp  "
		             + "   and a.bucketStartDate not between :asOfDate and :inOneMonth "
		             //+ "   and rownum <= 10                "
		             + "   order by a.bucketStartDate      "
		;

		List<KicsFssScenario> fssScenario = session.createQuery(query, KicsFssScenario.class)
				                                   .setParameter("asOfDate", asOfDate)
				                                   .setParameter("scenNum", scenNum)
				                                   .setParameter("fssScenTyp", fssScenTyp)
				                                   .setParameter("inOneMonth", inOneMonth)
				                                   .getResultList();		
		
		List<IrCurveHis> irCurveHis = new ArrayList<IrCurveHis>();
		
		for(KicsFssScenario fss : fssScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(TimeUtil.dateToString(fss.getAsOfDate()));
			curveHis.setIrCurveId(GeneralUtil.concatenate(fss.getFssScenTyp(), fss.getScenNum().toString()));
			curveHis.setMatCd(bucketToMatCd(fss.getAsOfDate().plusDays(1), fss.getBucketEndDate()));
			curveHis.setIntRate(fss.getValue());
			
			//System.out.println(TimeUtil.monthBetween(fss.getAsOfDate(), fss.getBucketEndDate()));			
			irCurveHis.add(curveHis);			
		}		
		
//		Collections.sort(irCurveHis, new Comparator<IrCurveHis>() {			
//			public int compare(IrCurveHis curve1, IrCurveHis curve2) {
//				if(Integer.valueOf(curve1.getMatCd().split("M")[1]) > Integer.valueOf(curve2.getMatCd().split("M")[1])) return 1;
//				else if(Integer.valueOf(curve1.getMatCd().split("M")[1]) < Integer.valueOf(curve2.getMatCd().split("M")[1])) return -1;
//				else return 0;				
//			}
//		});
		
		//irCurveHis.stream().filter(s -> Integer.valueOf(s.getMatCd().split("M")[1]) <= 5).forEach(s -> logger.info("irCurveHis: {}", s));		

		
		return irCurveHis;		
	}
	
	
	public static Map<String, IrCurveHis> getEntitiesMap(String bseDt, Integer scenNum, String fssScenTyp) {		
		
		LocalDate asOfDate = (bseDt != null) ? getMaxBseDt(bseDt) : getMaxBseDt(TimeUtil.dateToString(LocalDate.now()));
		LocalDate inOneMonth = asOfDate.plusMonths(1);
		
		String query = "select a from KicsFssScenario a    "
	               	 + " where 1=1                         "
		             + "   and a.asOfDate   = :asOfDate    "
		             + "   and a.scenNum    = :scenNum     "
		             + "   and a.fssScenTyp = :fssScenTyp  "
		             + "   and a.bucketStartDate not between :asOfDate and :inOneMonth "
		             //+ "   and rownum <= 10                "
		             + "   order by a.bucketStartDate      "
		;

		List<KicsFssScenario> fssScenario = session.createQuery(query, KicsFssScenario.class)
				                                   .setParameter("asOfDate", asOfDate)
				                                   .setParameter("scenNum", scenNum)
				                                   .setParameter("fssScenTyp", fssScenTyp)
				                                   .setParameter("inOneMonth", inOneMonth)
				                                   .getResultList();		
		
		//Map<String, IrCurveHis> irCurveHisMap = new HashMap<String, IrCurveHis>();
		Map<String, IrCurveHis> irCurveHisMap = new TreeMap<String, IrCurveHis>();
		
		for(KicsFssScenario fss : fssScenario) {
			
			IrCurveHis curveHis = new IrCurveHis();
			curveHis.setBaseDate(TimeUtil.dateToString(fss.getAsOfDate()));
			curveHis.setIrCurveId(GeneralUtil.concatenate(fss.getFssScenTyp(), fss.getScenNum().toString()));
			curveHis.setMatCd(bucketToMatCd(fss.getAsOfDate().plusDays(1), fss.getBucketEndDate()));
			curveHis.setIntRate(fss.getValue());
			
			//System.out.println(TimeUtil.monthBetween(fss.getAsOfDate(), fss.getBucketEndDate()));			
			irCurveHisMap.put(curveHis.getMatCd(), curveHis);			
		}		
		
		//irCurveHisMap.entrySet().stream().filter(s -> Integer.valueOf(s.getKey().split("M")[1]) <= 5).forEach(s -> logger.info("irCurveHisMap: {}, {}", s.getKey(), s.getValue().getIntRate()));
		
		return irCurveHisMap;		
	}

	
	private static String bucketToMatCd(LocalDate asOfDate, LocalDate bucketEndDate) {		
		//return Constant.TIME_UNIT_MONTH + String.format("%04d", TimeUtil.monthBetween(asOfDate, bucketEndDate) + 1);		
		return TimeUtil.monthBetweenMatCd(asOfDate, bucketEndDate);
	}		
	

	private static LocalDate getMaxBseDt(String bseDt) {		
		
		String query = "select max(a.asOfDate)      "
				+ "		from KicsFssScenario a      "
				+ "		where 1=1                   "		
				+ "		and a.asOfDate <= :bseDt    "
		;
		
		LocalDate maxDate =  (LocalDate) session.createQuery(query)		
				                           	    .setParameter("bseDt", TimeUtil.stringToDate(bseDt))            				                          
			            					    .uniqueResult();		
		
		if(maxDate == null) {
			logger.error("No Data found in FSS Interest Rate Scenario at {}", bseDt);
			return TimeUtil.stringToDate(bseDt);
		}
		return maxDate;
	}	
	
}
