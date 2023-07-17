package com.gof.test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.DaoUtil;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.KicsAssetFideDao;
import com.gof.dao.KicsFssScenDao;
import com.gof.entity.*;
import com.gof.enums.EInstrument;
import com.gof.interfaces.Instrument;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

public class DeTest2 {
	
	public DeTest2() {}	

	private final static Logger logger = LoggerFactory.getLogger(DeTest2.class.getSimpleName());	
	
	public static void main(String[] args) throws Exception {		
		
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    session.beginTransaction();	    
	    
	    //session.createNativeQuery("TRUNCATE TABLE QCM.KICS_RESULT_DE").executeUpdate();
//	    session.createQuery("Delete KicsAssetResult").executeUpdate();
	    //session.getTransaction().commit();	    

//    	session.createQuery("Delete KicsResultDe a where a.lastModifiedBy like \'DT%\'").executeUpdate();
//    	session.createQuery("Delete KicsResultDe where Instrument_ID like \'%0000000013570%\'").executeUpdate();	    
	    
	    String bseDt = "20171231";	    
	    String sqlHisDate = "select max(a.baseDate) from IrCurveHis a where a.baseDate <= '" + bseDt + "'";	    
	    List<String> irHisDateList = session.createQuery(sqlHisDate).getResultList();
	    String irHisDate = irHisDateList.get(0);	    
	    
	    session.enableFilter("BSE_DT").setParameter("bseDt", bseDt);
	    session.enableFilter("BASE_DATE").setParameter("baseDate", irHisDate);
//	    session.enableFilter("BASE_DATE").setParameter("baseDate", "20171229");
	    System.out.println("BSE_DT = " + bseDt + " | BASE_DATE = " + irHisDate);
	    
//	    session.disableFilter("dgsNo");
	    //filter  = session.enableFilter(filterName).setParameter("baseDate", "20171231").setParameter("dgsNo", "0000000020522");
	    //filter  = session.enableFilter(filterName).setParameter("baseDate", "20171231").setParameter("dgsNo", "0000000013570");
	    //filter  = session.enableFilter(filterName).setParameter("baseDate", "20171231").setParameter("dgsNo", "0000000020117");
	    
	    LocalTime start = LocalTime.now();    	
    	boolean cmd = false;
	    
  	    //Filter filter  = session.enableFilter(filterName).setParameter("bseDate", LocalDate.of(2017, 12, 31));
	    //Filter filter  = session.enableFilter(filterName).setParameter("baseYymm", "201712");
	    
        String entity = "KicsAssDe";        
        
        String where = "";
        //where = "where a.expoId like 'F%' and a.invmPdcTpcd = '52A'";
        //where = "where a.invmPdcTpcd = '78H' or a.invmPdcTpcd = '62H' or a.invmPdcTpcd = '80H'";
        //where = "where a.expoId like 'FP%' and a.pchsIrCurveId <> 'RF_USD' and a.sllIrCurveId = 'RF_USD' and a.invmPdcTpcd <> '62A'";
        //where = "where a.expoId like 'FP%' and a.invmPdcTpcd = '62A' AND rownum <=3";
        
        
        //where = "where a.isinCd = '165MC000'";
        //where = "where 1=1 and a.invmPdcTpcd = '62B' and rownum <= 1";
        //where = "where 1=1 and rownum <= 10";
        //where = "where a.expoId = 'DE000000002039683000080'";
        //where = "where a.dgsNo = '0000000013851'";
        //where = "where 1=1 and a.sllIrCurveId = 'RF_USD'";
        //where = "where 1=1 and a.invmPdcTpcd = '60A' or a.invmPdcTpcd = '62B'";
        //where = "where 1=1 and a.dgsNo = '0000000020522'";
        //where = "where 1=1 and a.invmPdcTpcd = '700'";
        //where = "where 1=1 and a.invmPdcTpcd = '60A' and rownum <= 2";	        
        //where = "where 1=1 and a.invmPdcTpcd = '62B' and rownum <= 2";
        
        
	    String sql = "select a from " + entity + " a " + where;
//	    LocalDate bseDate = LocalDate.of(2017, 12, 28);    
//	    LocalDate sysDate = LocalDate.of(2018, 2, 28);
//	    
//	    System.out.println(ChronoUnit.DAYS.between(bseDate, sysDate));
//	    System.out.println(ChronoUnit.DAYS.between(sysDate, bseDate));

//	    System.out.println(TimeUtil.monthBetween(bseDate, sysDate));
//	    System.out.println(TimeUtil.monthBetweenDouble(bseDate, sysDate));
//	    System.out.println(bseDate.plusMonths(2));
//	    System.out.println(sysDate.minusMonths(2));	    
//	    
//		Long month = ChronoUnit.MONTHS.between(bseDate, sysDate) + 1;
//		
//		String monthStr = Constant.TIME_UNIT_MONTH + String.format("%04d", TimeUtil.monthBetween(bseDate, sysDate));			
//		System.out.println(monthStr);
	    
	    //double[] values = this.irCurveHis.get(0).values().stream().map(IrCurveHis::getIntRate).mapToDouble(Double::doubleValue).toArray();
	    //List<Double> list = Arrays.asList(1.0,3.0,2,0,4.0);
//	    List<String> list = Arrays.asList("1.0","3.0","2.0","4.0");
//		
//		//double[] aa = list.stream().mapToDouble(Double::parseDouble).forEach(System.out::println);;
//	    list.stream().filter(i -> i == "1.01").mapToDouble(Double::parseDouble).forEach(System.out::println);
//	    
//	    List<List<String>> list2 = new ArrayList<List<String>>();	    
//	    list2.add(Arrays.asList("1.0", "0.1", "3.0"));
//	    list2.add(Arrays.asList("3.0", "0.3"));
//	    list2.add(Arrays.asList("2.0", "0.2"));	    
//	    list2.stream().filter(i -> Double.valueOf(i.size()) == 2).forEach(System.out::println);


	    if(cmd) {	    	
	    
		    //List<KicsAssDe> rst = session.createQuery(sql).getResultList();	    	
	    	//List<KicsAssetFide> rst = KicsAssetFideDao.getEntities("20171231");
	    	List<KicsAssetFide> rst = new ArrayList<KicsAssetFide>();
		    
		    int iii = 0;
		    int jjj = 0;
		    int kkk = 0;
		    List<KicsAssetFide> errInstrument = new ArrayList<KicsAssetFide>();
		    Map<String, String> exceptions = new HashMap<String, String>();
		    
		    for(KicsAssetFide aa : rst){    	
//		    	logger.info("aaa : {},{},{},{},{},{}", aa);
//		    	
//		    	for(IrCurve ir : aa.getIrCurve()) {
//		    		logger.info("aaa : {},{},{},{},{},{}", ir.getIrCurveId());	
//		    	}
		    	
		    	try {
		    		
//			    	InstrumentCreator factory = new InstrumentCreator();			    	
//			        Derivatives deInst = factory.setInstrumentModel(aa.getInvmPdcTpcd()); 
//			        deInst.setDerivativesEntities(aa);
			    				    	
			        //Derivatives deInst = EInstrumentOld.getEInstrument(aa.getInvmPdcTpcd()).getInstrumentModel();		    		
		    		Instrument deInst = EInstrument.getEInstrument(aa.getInstTpcd()).getInstrumentModel();
			        deInst.setInstrumentEntities(aa);		        

//			    	System.out.println(deInst.toString());	    	
//			    	
//			    	KicsCashflowDeTest result = deInst.getValuationTest(false);	    	
//			    	System.out.println(result.toString());
//			    	KicsCashflowDeTest result2 = deInst.getValuationTest(true);	    	
//			    	System.out.println(result2.toString());	    	
		    		
			    	List<KicsAssetResult> cflist = deInst.getValuation(true);
			    	
			    	for(KicsAssetResult cf : cflist) {		    		
//			    		logger.info("in DeTest : {},{},{},{},{},{}", cf);
				    				    	
			    		session.save(cf);	    		
			    	}		    	
			    	iii++;
		    	}
			    catch(Exception ex) {
					ex.printStackTrace();
					
					errInstrument.add(aa);					
			        jjj++;
			        
//			        try {		    		 		    	
//				        Derivatives deInst2 = EInstrumentOld.getEInstrument("NA").getInstrumentModel();				        
//
//				        deInst2.setInstrumentEntities(aa);
//			    		
//				    	List<KicsResultDe> cflist2 = deInst2.getValuation(false);
//				    	
//				    	for(KicsResultDe cf : cflist2) {
//					    	cf.setLastModifiedBy(aa.getInvmPdcTypNm());			    	
//				    		session.save(cf);	    		
//				    	}
//				    	kkk++;			        	
//			        }
//			        catch(Exception ex2) {
//			        	ex2.printStackTrace();
//			        }
			    }
		    }
		    
		    session.getTransaction().commit();
		    session.close();
		    HibernateUtil.shutdown();		    
		    
		    //for(KicsAssDe err : errInstrument) logger.info("RecCurve , PayCurve, Expo Info : {},{},{}", err.getPchsIrCurveId(), err.getSllIrCurveId(), err);
		    for(Map.Entry<String, String> elem : exceptions.entrySet()) logger.info("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());	    
		    
		    logger.info("Inforce , Success, Failure(1st), Success(BS): {},{},{},{},{},{}", rst.size(), iii, jjj, kkk);
		    logger.info("Time Elapsed : {} seconds", start.until(LocalTime.now(),ChronoUnit.MILLIS) / 1000.0);		    

        }	    
	    
	    
	    try {	    	
	    	
//	    	List<IrCurveHis> rst = IrCurveHisDao.getEntities("A100", "M0003", LocalDate.of(2017,  9,  30), LocalDate.of(2017, 12, 31));		    
//		    for(IrCurveHis aa : rst) logger.info("aaa : {},{},{},{},{},{}", aa);

//	    	List<IrCurveHis> rst = KicsFssScenarioDao.getEntities("20171231", 1, "10");		    
//		    logger.info("aaa : {},{},{},{},{},{}", rst);
		    
    		Map<String, Double> fxScenario = new HashMap<String, Double>();
    		fxScenario.put("M", 1.0);
    		fxScenario.put("A", 500.);
    		
    		logger.info("1st: {}", fxScenario.entrySet());
    		fxScenario.entrySet().stream().forEach(s -> logger.info("1st: {}", GeneralUtil.setScenarioValueString(s.getKey(), s.getValue())));

//	    	Map<String, Object> param = new HashMap<String, Object>();
//	    	param.put("scenNum", 1);	    	
//	    	param.put("aplyEndDate", "99991231");
//			
//			logger.info("param : {}", Arrays.toString(param.keySet().stream().toArray(String[]::new)));
//	    	
//	    	List<KicsSceChngMst> rst = DaoUtil.getEntities("KicsSceChngMst", param);
//	    	for(KicsSceChngMst aa : rst) logger.info("aaa : {},{},{},{},{},{}", aa);
	    }	    	
	    catch(Exception ex) {
			ex.printStackTrace();
	    }	    

	}
	
}