package com.gof.process;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.dao.KicsAssetAccoDao;
import com.gof.dao.KicsAssetFideDao;
import com.gof.dao.KicsAssetLoanDao;
import com.gof.dao.KicsAssetResultDao;
import com.gof.dao.KicsAssetSecrDao;
//import com.gof.dao.KicsFssScenDao;
//import com.gof.entity.IrCurveHis;
//import com.gof.enums.EIrScenario;
import com.gof.enums.EScenarioAttr;
import com.gof.enums.ERunArgument;
//import com.gof.enums.EBoolean;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.KicsAsset;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * INSTRUMENT의 Main 클래스. Process 패키지에 등록된 업무의 구조 여부, 구조 순서를 관리함.
 * 
 * @author skahn@gofconsulting.co.kr
 * @version 1.0
 *
 */
@Slf4j
@Data
public class Main {	
	
	private static Properties properties = new Properties();
	private static Map<ERunArgument, String> argMap = new HashMap<ERunArgument, String>();	
	private static List<String> jobList = new ArrayList<String>();				 
	
	public static Session session;	
	public static String baseDate;
	public static String JOB_GCFG_ID;
	private static String today = TimeUtil.dateToString(LocalDate.now());
	
	private static List<KicsAsset> instruments = new ArrayList<KicsAsset>();
//    private static List<KicsAsset> errInstrument = new ArrayList<KicsAsset>();
    private static Map<String, String> exceptions = new HashMap<String, String>();
//    private static List<KicsAssetResult> results;    
    
	public static List<Integer> irScenarioIndexList = new ArrayList<Integer>();
//	public static Map<String, Double> impliedSpreadMap = new HashMap<String, Double>();
	
	//////////////////////////////////////
	public static String irModelId;
	public static String irScenarioFxYn = "N";
	public static String irScenarioFxDefault; 
	
	private static String irScenarioFxCurrency;
	public static List<String> irScenarioFxCurrencyList = new ArrayList<String>();	
	//////////////////////////////////////	
	
//	private static boolean fx = false;

	private static String expoId = null;	
	
	
	public static void main(String[] args) throws Exception {		

	    try {
	    	
// **************************************************************************************************************************
			if(!init(args)) {
				log.error("Today is {} and BaseDate is {}", today, baseDate == null ? "NULL" : "Incorrect! [" + baseDate + "]");
				System.exit(0);
			}		

// **************************************************************************************************************************
			
			deleteResult(baseDate);
			loadInstrument(expoId);
		
// **************************************************************************************************************************
		
			//Discount Rate Calibration
			if(jobList.contains("CL")) {
				  // 1. 초기 설정 및 트랜잭션 시작
				  initializeJob(EScenarioAttr.CL.getAlias());
				  // 2. CL Process
				  JobProcessor.jobForCalibration(instruments);
				  // 3. 트랜잭션 커밋 및 최종 로깅
			      session.getTransaction().commit();
			}		
	
			//Valuation with IR Shock Scenario
			if(jobList.contains("IR")) {
				// 1. 초기 설정 및 트랜잭션 시작
				initializeJob(EScenarioAttr.IR.getAlias());
				// 2. IR Process 
				JobProcessor.jobForIrScenario(instruments);
				// 3. 트랜잭션 커밋 및 최종 로깅
				session.getTransaction().commit();	
			}
			
// **************************************************************************************************************************		
			//error instrument
//		    for(Map.Entry<String, String> elem : exceptions.entrySet()) log.warn("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
		
// *********************************************************** End Job ******************************************************		
			HibernateUtil.shutdown();
			System.exit(0);
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	log.error("Error in Main Process of Valuation: {}", e);			
	    	System.exit(0);
	    }
	}	


	private static boolean init(String[] args) {
		
		if (args == null || args.length == 0) {
			baseDate = null;
			return false;			
		}
		
		for (String arg : args) {
			log.info("Input Arguments : {}", arg);
			for(ERunArgument ea : ERunArgument.values()) {				
				if(arg.split("=")[0].toLowerCase().contains(ea.name())) {
					argMap.put(ea, arg.split("=")[1]);					
					break;
				}
			}			
		}
		if(argMap.get(ERunArgument.time) == null) return false;		
		baseDate = argMap.get(ERunArgument.time).replace("-", "").replace("/", "").trim().substring(0, 8);			
		if(argMap.get(ERunArgument.expoid) != null) expoId = "'" + argMap.get(ERunArgument.expoid).replace("'", "").trim() + "'";		
	
		try {
			FileInputStream fis = new FileInputStream(argMap.get(ERunArgument.properties));
			properties.load(new BufferedInputStream(fis));
			session = HibernateUtil.getSessionFactory(properties).openSession();
		}
		catch (Exception e) {
			session = HibernateUtil.getSessionFactory().openSession();
			log.error("Error in Properties File Loading: {}", e);
		}		
		
		try {		
			JOB_GCFG_ID = properties.getOrDefault("JobId", "KICS_CFG").toString().trim().toUpperCase();
			
			String jobString = properties.getOrDefault("JobList", "CL,IR").toString().trim().toUpperCase();
			jobList = Arrays.stream(jobString.split(",")).map(s -> s.trim()).collect(Collectors.toList());
			
			irModelId = properties.getOrDefault("IrScenarioId", "KDSP1000").toString().trim().toUpperCase();
			
			String irScenarioIndex = properties.getOrDefault("IrScenarioList", "1,2,3,4,5,6,7,8,9,10").toString();		
			irScenarioIndexList = Arrays.stream(irScenarioIndex.split(",")).map(s -> s.trim()).map(Integer::parseInt).collect(Collectors.toList());
			
			irScenarioFxYn = properties.getOrDefault("IrScenarioFxYn", "N").toString().trim().toUpperCase();			
			
			irScenarioFxCurrency = properties.getOrDefault("IrScenarioFxCurrency", Instrument.DEF_CURRENCY).toString().trim().toUpperCase();
			irScenarioFxCurrencyList = Arrays.stream(irScenarioFxCurrency.split(",")).map(s -> s.trim()).collect(Collectors.toList());
			if(!irScenarioFxCurrencyList.contains(Instrument.DEF_CURRENCY)) irScenarioFxCurrencyList.add(Instrument.DEF_CURRENCY);
			
			irScenarioFxCurrencyList.forEach(s-> log.info("IrScenario Fx : {}", s));
			
			irScenarioFxDefault = properties.getOrDefault("IrScenarioFxDefault", Instrument.DEF_CURRENCY).toString().trim().toUpperCase();
			
			
			log.info("{}, {}, {}, {}, {}, {}, {}", irModelId, irScenarioIndex, irScenarioIndexList, irScenarioFxYn, irScenarioFxCurrency, irScenarioFxCurrencyList, irScenarioFxDefault);
		}
		catch (Exception e) {			
			e.printStackTrace();
			log.error("Error in Properties File Loading: {}", e);			
			System.exit(0);
		}		

		return true;
	}
		
	public static void deleteResult(String baseDate) {
		
		try {
			KicsAssetResultDao.deleteEntities(baseDate);
		}
		catch (Exception e) {
			log.error("Check the TABLE [Q_IC_ASSET_RSLT]: {}", e);
			System.exit(0);
		}
	}
	
	  private static void initializeJob(String jobType) {
	      log.info("==============Job_Valuation Start: [JOB TYPE: {}]==============", jobType );
	      session.beginTransaction();
	  }
	

	private static void loadInstrument(String expoId) {
		
		try {
			if(expoId != null) {
		    	instruments = KicsAssetSecrDao.getEntities(baseDate, expoId);
		    	if(instruments.isEmpty()) {
		    		instruments = KicsAssetAccoDao.getEntities(baseDate, expoId);	    		
		    	}
		    	if(instruments.isEmpty()) {
		    		instruments = KicsAssetFideDao.getEntities(baseDate, expoId);
		    	}
		    	if(instruments.isEmpty()) {
		    		instruments = KicsAssetLoanDao.getEntities(baseDate, expoId);
		    	}	    	
		        log.info("Instrument Info: {}", instruments);
			}
			else {
		    	instruments = KicsAssetSecrDao.getEntities(baseDate);
		    	instruments.addAll(KicsAssetAccoDao.getEntities(baseDate));
		    	instruments.addAll(KicsAssetFideDao.getEntities(baseDate));
		    	//instruments.addAll(KicsAssetLoanDao.getEntities(baseDate));
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error(    "Check TABLES related to Asset Exposure, [Q_IC_ASSET_SECR, Q_IC_ASSET_FIDE, Q_IC_ASSET_ACCO] "
				    	+ "or TABLES related to Interest Rate, [Q_IC_INST_CURVE, Q_CM_IRC, Q_IC_IR_CURVE_HIS]: {}", e);			
			System.exit(0);			
		}		
	}
	
	
	public static double round(double number, int decimalDigit) {
		if(decimalDigit < 0) return Math.round(number);
		return Double.parseDouble(String.format("%." + decimalDigit + "f", number));
	}		
	
	
	public static String format(Double number) {		
		return ((number > 0) ? "+" : "") + String.valueOf(round(number, 4));
	}
		
	
}
