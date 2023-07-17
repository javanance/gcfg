package com.gof.process;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.DaoUtil;
import com.gof.dao.IrScenarioDao;
import com.gof.dao.KicsAssetResultDao;
import com.gof.dao.KicsShckStkDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetFide;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsSceChngMst;
import com.gof.entity.KicsShckStk;
import com.gof.enums.EInstrument;
import com.gof.enums.EIrScenario;
import com.gof.enums.EScenarioAttr;
import com.gof.enums.ERunArgument;
import com.gof.interfaces.Constant;
import com.gof.interfaces.Instrument;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

/**
 * INSTRUMENT 의 Main 클래스. Process 패키지에 등록된 작업의 구동 여부, 구동 순서를 관리함.
 * 
 * @author skahn@gofconsulting.co.kr
 * @version 1.0
 *
 */
public class Main_old {
	
	private final static Logger logger = LoggerFactory.getLogger("JOB_DEVLUT");
	private static Properties properties = new Properties();
	private static Map<ERunArgument, String> argMap = new HashMap<ERunArgument, String>();	
	private static List<String> jobList = new ArrayList<String>();				 
	
	private static Session session;	
	private static String bseDt;
	private static String JOB_DEVLUT_ID;
	private static String today = TimeUtil.dateToString(LocalDate.now());
	private static int singleScenarioIndex = 1;
	
    private static List<KicsAssetFide> errInstrument = new ArrayList<KicsAssetFide>();
    private static Map<String, String> exceptions = new HashMap<String, String>();
    private static List<KicsAssetResult> results;
	
	private static String irModelId;
	private static List<Integer> irScenarioIndexList = new ArrayList<Integer>();
	//private static List<Integer> irScenarioIndexList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);	
	
	private static Map<Integer, Map<String, IrCurveHis>> scenarioCurveHisMap = new HashMap<Integer, Map<String, IrCurveHis>>();
	private static Map<String, IrCurveHis> calibrationCurveHis = new HashMap<String, IrCurveHis>();
	private static Map<String, Double> impliedSpreadMap = new HashMap<String, Double>();
    
	private static Map<String, Object> fxScenarioParam = new HashMap<String, Object>();	
	private static List<KicsSceChngMst> fxScenarioList = new ArrayList<KicsSceChngMst>();
	//private static List<Integer> fxScenarioIndexList = new ArrayList<Integer>();
	private static Map<Integer, KicsSceChngMst> fxScenarioListMap = new HashMap<Integer, KicsSceChngMst>();	

	private static String stkModelId;
	private static String stkShockDirection;
	private static String KOSPI_INDEX_ID;
	private static Map<String, KicsShckStk> stkScenarioMap = new HashMap<String, KicsShckStk>();	
    
    public static String expoId = null;    
    //private static String expoId = "'DE000000000457883000080'";
    //private static String expoId = "'DE000000001545283100080'";
    //private static String expoId = "'DE000000001491383100080'";  //CHECK THIS!!1    
    //private static String expoId = "'DT301G524583000033TA004B0503A004L4F8C7C3086D800EEE10080020A0A4132'";  //OPTION    
    //private static String expoId = "'DT101H910000033TA020_0902A020S51C82DA30AA5204EE10080020A0A4621'";     //FUTURES
    

	
	public static void main(String[] args) {		

// **************************************************************************************************************************
		if(!init(args)) {
			logger.error("Today is {} and BaseDate is {}", today, bseDt == null ? "NULL" : "Incorrect! [" + bseDt + "]");
			System.exit(0);
		}

// **************************************************************************************************************************
		deleteResult(bseDt);
		
// **************************************************************************************************************************

		//Discount Rate Calibration
		if(jobList.contains("CL")) {
			setIrScenarioForCalibration();
			if(calibrationCurveHis != null && !calibrationCurveHis.isEmpty()) {
				jobValuationForCalibration();				
			}			
			else logger.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.CL.getAlias(), bseDt);
		}
		
		//Standard Valuation with Base Rate(CRS/A100)
		if(jobList.contains("SD")) {
			jobValuationStandard();
		}	

		//Valuation with IR Shock Scenario
		if(jobList.contains("IR")) {			
			setIrScenario(irScenarioIndexList);			
			if(scenarioCurveHisMap != null && !scenarioCurveHisMap.isEmpty()) {
				jobValuationIrScenario();
			}
			else logger.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.IR.getAlias(), bseDt);			
		}		
		
		//Valuation with STOCK Shock Scenario
		if(jobList.contains("ST")) {			
			setStockScenario();	
			if(stkScenarioMap != null && !stkScenarioMap.isEmpty()) {
				jobValuationStockScenario();
			}			
			else logger.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.ST.getAlias(), bseDt);			
		}		

		//Valuation with Foreign Exchange Rate Scenario
		if(jobList.contains("FX")) {
			setFxScenario();
			if(fxScenarioList != null && !fxScenarioList.isEmpty()) {
				jobValuationFxScenario();
			}
			else logger.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.FX.getAlias(), bseDt);
		}

// **************************************************************************************************************************		
		//error instrument
	    for(Map.Entry<String, String> elem : exceptions.entrySet()) logger.info("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
		
// *********************************************************** End Job ******************************************************		
		HibernateUtil.shutdown();
		System.exit(0);

	}

	
	private static boolean init(String[] args) {
		
		if (args == null || args.length == 0) {
			bseDt = null;
			return false;			
		}
		
		for (String arg : args) {
			logger.info("Input Arguments : {}", arg);
			for(ERunArgument ea : ERunArgument.values()) {				
				if(arg.split("=")[0].toLowerCase().contains(ea.name())) {
					argMap.put(ea, arg.split("=")[1]);					
					break;
				}
			}			
		}
		if(argMap.get(ERunArgument.time) == null) return false;		
		bseDt = argMap.get(ERunArgument.time).replace("-", "").replace("/", "").trim().substring(0, 8);			
		if(argMap.get(ERunArgument.expoid) != null) expoId = "'" + argMap.get(ERunArgument.expoid).replace("'", "").trim() + "'";		
	
		try {
			FileInputStream fis = new FileInputStream(argMap.get(ERunArgument.properties));
			properties.load(new BufferedInputStream(fis));
			session = HibernateUtil.getSessionFactory(properties).openSession();
		}
		catch (Exception e) {
			session = HibernateUtil.getSessionFactory().openSession();
			logger.warn("Error in Properties File Loading: {}", e);
		}		
		
		JOB_DEVLUT_ID = properties.getOrDefault("JobDevlutID", "JOB_DEVLUT").toString().trim().toUpperCase();		
		String jobString = properties.getOrDefault("JobList", "SD,CL,IR,ST").toString().trim().toUpperCase();		
		jobList = Arrays.stream(jobString.split(",")).map(s -> s.trim()).collect(Collectors.toList());		
		
		irModelId = properties.getOrDefault("IrScenarioId", "FSS").toString().trim().toUpperCase();
		String irScenarioIndex = properties.getOrDefault("IrScenarioList", "1,2,3,4,5,6,7,8,9,10").toString();		
		irScenarioIndexList = Arrays.stream(irScenarioIndex.split(",")).map(s -> s.trim()).map(Integer::parseInt).collect(Collectors.toList());		
                
		stkModelId = properties.getOrDefault("StScenarioId", "KICS").toString().trim().toUpperCase();
		stkShockDirection = properties.getOrDefault("StShockDirection", "DOWN").toString().trim().toUpperCase();
		KOSPI_INDEX_ID = properties.getOrDefault("KOSPI_INDEX_ID", "10").toString().trim().toUpperCase();

		return true;
	}	
	

	private static void setStockScenario() {		
		
		stkScenarioMap = KicsShckStkDao.getEntitiesMap(bseDt, stkModelId);		
		//for(Map.Entry<String, KicsShckStk> scen : stkScenarioMap.entrySet()) System.out.println(scen.toString());		
	}		

	
	private static void setFxScenario() {		
			
//		Map<String, Double> fxScenario = new HashMap<String, Double>();
//		fxScenario.put("M", 1.0);
//		fxScenarioMap.put(0, fxScenario);
//		fxScenarioIndexList.add(0);
			
		//fxScenarioParam.put("scenNum", 1);
		fxScenarioParam.put("aplyEndDate", "99991231");
    	
		fxScenarioList = DaoUtil.getEntities("KicsSceChngMst", fxScenarioParam);
    	
    	for(KicsSceChngMst fxScenario : fxScenarioList) {    		
    		//fxScenarioIndexList.add(fxScenarioList.indexOf(fxScenario) + 1);
    		fxScenarioListMap.put(fxScenarioList.indexOf(fxScenario) + 1, fxScenario);
    		logger.info("FX Scenario Attributes: {}, [{}]", fxScenarioList.indexOf(fxScenario) + 1, GeneralUtil.setScenarioValueString(fxScenario.getFxChngTypCd(), fxScenario.getFxChngVal()));
    	}    	
	}		
	

	private static void setIrScenarioForCalibration() {		
		calibrationCurveHis = IrScenarioDao.getEntitiesMap(bseDt, EIrScenario.CALIBRATION.getScenNum(), irModelId);
	}

	
	private static void setIrScenario(List<Integer> irScenarioIndexList) {		
		for(Integer idx : irScenarioIndexList) {
			setIrScenario(idx.intValue());		
		}
	}	


	private static void setIrScenario(int idx) {				
		Map<String, IrCurveHis> scenarioCurveHis = IrScenarioDao.getEntitiesMap(bseDt, EIrScenario.getEIrScenario(idx), irModelId);		
	    if (scenarioCurveHis != null && scenarioCurveHis.size() != 0) scenarioCurveHisMap.put(idx, scenarioCurveHis);
	}	
	
	
	public static void deleteResult(String bseDt, int idx) {		
		KicsAssetResultDao.deleteEntities(bseDt, idx);		
	}
	
	
	public static void deleteResult(String bseDt) {
		KicsAssetResultDao.deleteEntities(bseDt);
	}	

	//to be determined whether using Ir Calibration Spot Curve. Considering IR Scen #1 and ST Scen #1, it will be using it.
	private static void jobValuationStockScenario() {		
		
		logger.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.ST.getAlias());		
		
		session.beginTransaction();						
	    LocalTime startTime = LocalTime.now();    	
	    
	    List<KicsAssetFide> instruments;
	    instruments = new ArrayList<KicsAssetFide>();
//	    if(expoId != null) instruments = KicsAssetFideDao.getEntities(bseDt, expoId);
//	    else instruments = KicsAssetFideDao.getEntities(bseDt);
		
	    int iii = 0, jjj = 0;		
	    for(KicsAssetFide inst : instruments){
	    	
	    	try {
	    		Instrument deInst = EInstrument.getEInstrument(inst.getInstTpcd()).getInstrumentModel();
	            deInst.setInstrumentEntities(inst);	            
	            
	            deInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHis, impliedSpreadMap.get(inst.getExpoId()));	            
            	deInst.setStockScenarioEntities(singleScenarioIndex, String.valueOf(Constant.MULTIPLY), (stkShockDirection.equals("UP") ? 1.0 : -1.0) * stkScenarioMap.get(KOSPI_INDEX_ID).getShckVal());	            		            	
	    	    results = deInst.getValuation();	            
	    	    
	    	    for(KicsAssetResult rst : results) {
	    	    	
	    	    	rst.setScenNum(singleScenarioIndex);
	    	    	rst.setScenName(EScenarioAttr.ST.getAlias());
	    	    	rst.setScenType(EScenarioAttr.ST.name());		    	    	
	    	    	rst.setLastModifiedBy(JOB_DEVLUT_ID);	    	    	
	    	    	session.save(rst);	    	    	
	    	    }
	    	    iii++;
	    	}	    			 
	    	catch(Exception ex) {			
	    		ex.printStackTrace();
			    errInstrument.add(inst);			    
    	        jjj++;
	    	}
	    }
		session.getTransaction().commit();		
		
		logger.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.ST.getAlias());
		logger.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    logger.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
	}	
	
	
	private static void jobValuationForCalibration() {
		
		logger.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
		session.beginTransaction();
		LocalTime startTime = LocalTime.now();    	
		//LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), 
	    
	    List<KicsAssetFide> instruments;	    
	    instruments = new ArrayList<KicsAssetFide>();
//	    if(expoId != null) instruments = KicsAssetFideDao.getEntities(bseDt, expoId);
//	    else instruments = KicsAssetFideDao.getEntities(bseDt);
		
	    int iii = 0, jjj = 0;
	    boolean fx = false;
	    fx = true;
		
	    for(KicsAssetFide inst : instruments){
	    	
	    	try {
	    		Instrument deInst = EInstrument.getEInstrument(inst.getInstTpcd()).getInstrumentModel();
	            deInst.setInstrumentEntities(inst);

            	deInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHis, null);
	    	    results = deInst.getValuation(fx);	    	    
	    	    
	    	    for(KicsAssetResult rst : results) {

	    	    	rst.setScenNum(EIrScenario.CALIBRATION.getScenNum());
	    	    	rst.setScenName(EIrScenario.CALIBRATION.name());
	    	    	rst.setScenType(EIrScenario.CALIBRATION.getScenType());
	    	    	rst.setLastModifiedBy(JOB_DEVLUT_ID);	    	    	
	    	    	session.save(rst);	    	    	
	    	    	
	    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_SPREAD))) {
	    	    		impliedSpreadMap.put(inst.getExpoId(), rst.getValue());
	    	    		//logger.info("Residual Spread: {}: {}", inst.getExpoId(), rst.getValue());
	    	    	}
	    	    }	    	    
	    	    iii++;	    	    
	    	}		 
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);			    			    
    	        jjj++;
	    	}	    	
	    	//if(iii % 10 == 0) logger.info("Derivatives Instrument is processed {}/{}", iii, instruments.size());	    	
	    }
		session.getTransaction().commit();		

		logger.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
		logger.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    logger.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
	}
	
	
	private static void jobValuationIrScenario() {		
		
		logger.info("Job_Valuation Start: [JOB TYPE: {}, SCEN NUMBER: {}]", EScenarioAttr.IR.getAlias(), irScenarioIndexList);		
		session.beginTransaction();						
	    LocalTime startTime = LocalTime.now();    	
	    
	    List<KicsAssetFide> instruments;	    
	    instruments = new ArrayList<KicsAssetFide>();
//	    if(expoId != null) instruments = KicsAssetFideDao.getEntities(bseDt, expoId);
//	    else instruments = KicsAssetFideDao.getEntities(bseDt);
		
	    int iii = 0, jjj = 0;
	    boolean fx = false;
	    fx = true;
		
	    for(KicsAssetFide inst : instruments){
	    	
	    	try {   
	    		Instrument deInst = EInstrument.getEInstrument(inst.getInstTpcd()).getInstrumentModel();
	            deInst.setInstrumentEntities(inst);
	            
	            for(Map.Entry<Integer, Map<String, IrCurveHis>> scenarioCurveHis : scenarioCurveHisMap.entrySet()) {	            	

	            	deInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHis.getValue(), impliedSpreadMap.get(inst.getExpoId()));
	            	//deInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHis.getValue(), null);
		    	    results = deInst.getValuation(fx);	    	    
		    	    
		    	    for(KicsAssetResult rst : results) {		    	    	
		    	    	
		    	    	rst.setScenNum(scenarioCurveHis.getKey());
		    	    	rst.setScenName(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).name());
		    	    	rst.setScenType(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).getScenType());
		    	    	rst.setLastModifiedBy(JOB_DEVLUT_ID);	    	    	
		    	    	session.save(rst);
		    	    }
	            }	            	           	            
	    	    iii++;	    	    
	    	}		 
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);			    
    	        jjj++;
	    	}
	    }	    
		session.getTransaction().commit();		
		
		logger.info("Job_Valuation is Completed !: [JOB TYPE: {}, SCEN NUMBER: {}]", EScenarioAttr.IR.getAlias(), irScenarioIndexList);	    
		logger.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    logger.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);
	}	

	
	private static void jobValuationFxScenario() {		
		
		logger.info("Job_Valuation Start: [JOB TYPE: {}, SCEN_TYPE: {}]", EScenarioAttr.FX.getAlias(), fxScenarioListMap.keySet());		
		
		session.beginTransaction();						
	    LocalTime startTime = LocalTime.now();    	
	    
	    List<KicsAssetFide> instruments;	    
	    instruments = new ArrayList<KicsAssetFide>();
//	    if(expoId != null) instruments = KicsAssetFideDao.getEntities(bseDt, expoId);
//	    else instruments = KicsAssetFideDao.getEntities(bseDt);
		
	    int iii = 0, jjj = 0;
	    boolean fx = false;
	    fx = true;	    
		
	    for(KicsAssetFide inst : instruments){
	    	
	    	try {
	    		Instrument deInst = EInstrument.getEInstrument(inst.getInstTpcd()).getInstrumentModel();
	            deInst.setInstrumentEntities(inst);

	            for(Map.Entry<Integer, KicsSceChngMst> fxScenario : fxScenarioListMap.entrySet()) {
	            	
	            	deInst.setFxScenarioEntities(fxScenario.getKey(), fxScenario.getValue().getFxChngTypCd(), fxScenario.getValue().getFxChngVal());	            	
		    	    results = deInst.getValuation(fx);	    	    
		    	    
		    	    for(KicsAssetResult rst : results) {		    	    	
		    	    	
		    	    	rst.setScenNum(fxScenario.getKey());
		    	    	rst.setScenName(GeneralUtil.setScenarioValueString(fxScenario.getValue().getFxChngTypCd(), fxScenario.getValue().getFxChngVal()));
		    	    	rst.setScenType(EScenarioAttr.FX.name());		    	    	
		    	    	rst.setLastModifiedBy(JOB_DEVLUT_ID);	    	    	
		    	    	session.save(rst);
		    	    }
	            }	            	           	            
	    	    iii++;	    	    
	    	}		 
	    	catch(Exception ex) {			
	    		ex.printStackTrace();
			    errInstrument.add(inst);			    
    	        jjj++;
	    	}
	    }
		session.getTransaction().commit();		
		
		logger.info("Job_Valuation is Completed !: [JOB TYPE: {}, SCEN_TYPE: {}]", EScenarioAttr.FX.getAlias(), fxScenarioListMap.keySet());
		logger.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    logger.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
	}	

	
	private static void jobValuationStandard() {		
		
		logger.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.SD.getAlias());		
		session.beginTransaction();				
	    LocalTime startTime = LocalTime.now();    	
	    
	    List<KicsAssetFide> instruments;	    
	    instruments = new ArrayList<KicsAssetFide>();
//	    if(expoId != null) instruments = KicsAssetFideDao.getEntities(bseDt, expoId);
//	    else instruments = KicsAssetFideDao.getEntities(bseDt);
	    
		//// for check exception !! ///////////////////
	    int iii = 0, jjj = 0;	    
	    for(KicsAssetFide inst : instruments){
	    	
	    	try {   
	    		Instrument deInst = EInstrument.getEInstrument(inst.getInstTpcd()).getInstrumentModel();
	            deInst.setInstrumentEntities(inst);
	    	    results = deInst.getValuation();	    	    
	    	    
	    	    for(KicsAssetResult rst : results) {	    	    	
	    	    	
	    	    	rst.setScenNum(singleScenarioIndex);
	    	    	rst.setScenName(EScenarioAttr.SD.getAlias());
	    	    	rst.setScenType(EScenarioAttr.SD.name());	    	    	
	    	    	rst.setLastModifiedBy(JOB_DEVLUT_ID);	    	    	
	    	    	session.save(rst);
	    	    }	            
	    	    iii++;	    	    
	    	}		 
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);
    	        jjj++;
	    	}
	    }	    
		session.getTransaction().commit();				
		
		logger.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.SD.getAlias());		
	    logger.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    logger.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);	    
	}	
	
}
