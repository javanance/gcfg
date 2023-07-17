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
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.IrScenarioDao;
import com.gof.dao.KicsAssetResultDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveHis2;
import com.gof.entity.IrmodelResult;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsAssetSecr;
import com.gof.enums.EIrScenario;
import com.gof.enums.EScenarioAttr;
import com.gof.enums.ERunArgument;
import com.gof.enums.EInstrument;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.Irmodel;
import com.gof.irmodel.IrmodelNelsonSeigelD;
import com.gof.irmodel.IrmodelSmithWilson;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * INSTRUMENT 의 Main 클래스. Process 패키지에 등록된 작업의 구동 여부, 구동 순서를 관리함.
 * 
 * @author skahn@gofconsulting.co.kr
 * @version 1.0
 *
 */
@Slf4j
@Data
public class TestSecurities {	
	
	private static Properties properties = new Properties();
	private static Map<ERunArgument, String> argMap = new HashMap<ERunArgument, String>();	
	private static List<String> jobList = new ArrayList<String>();				 
	
	private static Session session;	
	private static String baseDate;
	private static String JOB_GCFG_ID;
	private static String today = TimeUtil.dateToString(LocalDate.now());
	private static int singleScenarioIndex = 1;
	
    private static List<KicsAssetSecr> errInstrument = new ArrayList<KicsAssetSecr>();
    private static Map<String, String> exceptions = new HashMap<String, String>();
    private static List<KicsAssetResult> results;    
    
	private static String irModelId;
	private static List<Integer> irScenarioIndexList = new ArrayList<Integer>();
	//private static List<Integer> irScenarioIndexList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);	
	
	private static Map<Integer, Map<String, IrCurveHis>> scenarioCurveHisMap = new HashMap<Integer, Map<String, IrCurveHis>>();
	private static Map<String, IrCurveHis> calibrationCurveHis = new HashMap<String, IrCurveHis>();
	private static Map<String, Double> impliedSpreadMap = new HashMap<String, Double>();    
	private static Map<String, Double> impliedMaturityMap = new HashMap<String, Double>();
    
    //private static String expoId = null;
    //private static String expoId = "'SECR_G110KR103502G5C60132016011800003_12720100'";    //ktb 
    //private static String expoId = "'SECR_G110KR3822047V600162008062000001_12720689'";    //emb bond
	//private static String expoId = "'SECR_G110KR72066600030129999123100000_12713865'";    //stock
	//private static String expoId = "'SECR_A130KRB50801G4830132014082200001_12714363'";    //MBS --> imp_mat_tol -> to be more bigger
	//private static String expoId = "'SECR_A130XS13045707050132015101900003_12714691'";    //USD ibrd
    //private static String expoId = "'SECR_G110KRC0350C20660142010102600001_12720177'";    // 국고채 만기보유 이표스트립  // KRC0350C2066
    //private static String expoId = "'SECR_G110KR103502G7920142017091900001_12720172'";    // 국고채 만기보유 원채권       // KR103502G792
	//private static String expoId = "'SECR_A130KR101501D7690132017070700001_12714335'";    // 국민주택1종  // KR101501D769
	//private static String expoId = "'SECR_P110XS13340799580132016012900001_12717488'";    // zero callable(simple) // XS1334079958
	//private static String expoId = "'SECR_G110C21KR17100010112017101600001_12714119'";    // 정기예금(단리) // KR1710001011
	//private static String expoId = "'SECR_P110KR20010147380132017032900001_12716749'";    // 복5단2 // KR2001014738
	public static String expoId = "'SECR_G110KR72066600030129999123100000_12713865'";    //stock
	
	
	public static void main(String[] args) {		

// **************************************************************************************************************************
		if(!init(args)) {
			log.error("Today is {} and BaseDate is {}", today, baseDate == null ? "NULL" : "Incorrect! [" + baseDate + "]");
			System.exit(0);
		}		

// **************************************************************************************************************************
		//deleteResult(baseDate);
		deleteResult();
		
// **************************************************************************************************************************

		//Discount Rate Calibration
		if(jobList.contains("CL")) {
			setIrScenarioForCalibration();
			if(calibrationCurveHis != null && !calibrationCurveHis.isEmpty()) {
				jobValuationForCalibration();				
			}			
			else log.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.CL.getAlias(), baseDate);
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
			else log.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.IR.getAlias(), baseDate);			
		}		
		
		
		if(jobList.contains("SW")) {
			calSw();
		}
		
		if(jobList.contains("NS")) {
			calDns();
		}
		
		

// **************************************************************************************************************************		
		//error instrument
	    for(Map.Entry<String, String> elem : exceptions.entrySet()) log.info("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
		
// *********************************************************** End Job ******************************************************		
		HibernateUtil.shutdown();
		System.exit(0);
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
			log.warn("Error in Properties File Loading: {}", e);
		}		
		
		JOB_GCFG_ID = properties.getOrDefault("JobId", "JOB_CFG").toString().trim().toUpperCase();		
		String jobString = properties.getOrDefault("JobList", "SD,CL,IR,ST").toString().trim().toUpperCase();		
		jobList = Arrays.stream(jobString.split(",")).map(s -> s.trim()).collect(Collectors.toList());
		
		irModelId = properties.getOrDefault("IrScenarioId", "FSS").toString().trim().toUpperCase();
		String irScenarioIndex = properties.getOrDefault("IrScenarioList", "1,2,3,4,5,6,7,8,9,10").toString();		
		irScenarioIndexList = Arrays.stream(irScenarioIndex.split(",")).map(s -> s.trim()).map(Integer::parseInt).collect(Collectors.toList());		

		return true;
	}	

	
	@SuppressWarnings("unchecked")
	public static void testSql() {
		
	    String sqlHisDate = "select max(a.baseDate) from IrCurveHis a where a.baseDate <= '" + baseDate + "'";	    
	    List<String> irHisDateList = session.createQuery(sqlHisDate).getResultList();
	    log.info("testSql: {}", irHisDateList.get(0));		
	}
	
	
	public static void deleteResult() {
		KicsAssetResultDao.deleteEntities();
	}

	
	public static void deleteResult(String baseDate) {
		KicsAssetResultDao.deleteEntities(baseDate);
	}
	
	
	public static void deleteResult(String baseDate, int idx) {		
		KicsAssetResultDao.deleteEntities(baseDate, idx);		
	}	


	private static void setIrScenarioForCalibration() {
		calibrationCurveHis = IrScenarioDao.getEntitiesMap(baseDate, EIrScenario.CALIBRATION.getScenNum(), irModelId);
	}

	
	private static void setIrScenario(List<Integer> irScenarioIndexList) {		
		for(Integer idx : irScenarioIndexList) {
			setIrScenario(idx.intValue());		
		}
	}

	private static void setIrScenario(int idx) {				
		Map<String, IrCurveHis> scenarioCurveHis = IrScenarioDao.getEntitiesMap(baseDate, EIrScenario.getEIrScenario(idx), irModelId);		
	    if (scenarioCurveHis != null && scenarioCurveHis.size() != 0) scenarioCurveHisMap.put(idx, scenarioCurveHis);
	}
	
	
	public static void jobValuationForCalibration() {

		log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
		session.beginTransaction();
		LocalTime startTime = LocalTime.now();    	
		//LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),	    
		
	    List<KicsAssetSecr> instruments = new ArrayList<KicsAssetSecr>();
//	    if(expoId != null) instruments = KicsAssetSecrDao.getEntities(baseDate, expoId);
//	    else instruments = KicsAssetSecrDao.getEntities(baseDate);
		
	    int iii = 0, jjj = 0;
	    boolean fx = false;
	    fx = true;
		
	    Integer count = 1;
	    for(KicsAssetSecr inst : instruments){
	    	
	    	try {	    		
	    		Instrument fiInst = EInstrument.getEInstrument(inst.getInstTpcd() + inst.getInstDtlsTpcd()).getInstrumentModel();
	    		
	    		//inst.setEmbeddedOption("1");	    		
	    		inst.setIsRealNumber(true);
    		    fiInst.setInstrumentEntities(inst);    		    
    		    
    		    fiInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHis, null);
    		    //log.info("cal_curve_his: {}", calibrationCurveHis);
	    	    results = fiInst.getValuation(fx);
	    	    
	    	    for(KicsAssetResult rst : results) {

	    	    	rst.setScenNum(EIrScenario.CALIBRATION.getScenNum());
	    	    	rst.setScenName(EIrScenario.CALIBRATION.name());
	    	    	rst.setScenType(EIrScenario.CALIBRATION.getScenType());
	    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
	    	    	rst.setLastUpdateDate(LocalDate.now());  	
	    	    	session.save(rst);	    	   
//	    	    	if(count % 10000 == 0) {
//	    	    		session.flush();
//	    	    		session.clear();
//	    	    		count = 1;
//	    	    	}
	    	    	
	    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_SPREAD))) {
	    	    		impliedSpreadMap.put(inst.getExpoId(), rst.getValue());
	    	    		log.info("Implied Spread: {}: {}, {}, {}", inst.getExpoId(), rst.getValue(), inst.getProdTpcd(), inst.getInstDtlsTpcd());
	    	    	}
	    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_MATURITY))) {
	    	    		impliedMaturityMap.put(inst.getExpoId(), rst.getValue());
	    	    		log.info("Implied Maturity: {}: {}, {}, {}", inst.getExpoId(), rst.getValue(), inst.getProdTpcd(), inst.getInstDtlsTpcd());
	    	    	}
	    	    	count++;
	    	    	//if(count % 1000 == 0) log.info("count: {}", count);
	    	    }	    		    	    
	    	    iii++;	    	    
	    	}		 
	    	catch(Exception ex) {
	    		//ex.printStackTrace();
			    errInstrument.add(inst);
			    log.info("ERROR !! : EXPO_ID : [{}]", inst.getExpoId());
			    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.CL.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());			    
    	        jjj++;
	    	}	    	
	    	if(iii % 100 == 0) log.info("Securities Instrument is processed {}/{}", iii, instruments.size());	    	
	    }
	    log.info("Securities Instrument is processed {}/{}", iii, instruments.size());
		session.getTransaction().commit();		

		log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
		log.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
		
		
	}

	public static void jobValuationIrScenario() {
		
		log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.IR.getAlias());
		session.beginTransaction();	    
		LocalTime startTime = LocalTime.now(); 
		
	    List<KicsAssetSecr> instruments = new ArrayList<KicsAssetSecr>();
//	    if(expoId != null) instruments = KicsAssetSecrDao.getEntities(baseDate, expoId);
//	    else instruments = KicsAssetSecrDao.getEntities(baseDate);
		
	    int iii = 0, jjj = 0;
	    boolean fx = false;
	    fx = true;	    
	    
	    for(KicsAssetSecr inst : instruments){
	    	
	    	try {   
	    		Instrument fiInst = EInstrument.getEInstrument(inst.getInstTpcd() + inst.getInstDtlsTpcd()).getInstrumentModel();	    		
	    		
	    		inst.setIsRealNumber(true);    		    
    		    fiInst.setInstrumentEntities(inst);
	            
	            for(Map.Entry<Integer, Map<String, IrCurveHis>> scenarioCurveHis : scenarioCurveHisMap.entrySet()) {	            	

	            	fiInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHis.getValue(), impliedSpreadMap.get(inst.getExpoId()));
	            	fiInst.setImpliedMaturity(impliedMaturityMap.get(inst.getExpoId()));
	            	//seInst.setImpliedMaturity(impliedMaturityMap.getOrDefault(inst.getExpoId(), 1.0));
		    	    results = fiInst.getValuation(fx);	    	    
		    	    
		    	    for(KicsAssetResult rst : results) {		    	    	
		    	    	
		    	    	rst.setScenNum(scenarioCurveHis.getKey());
		    	    	rst.setScenName(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).name());
		    	    	rst.setScenType(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).getScenType());
		    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
		    	    	rst.setLastUpdateDate(LocalDate.now());
		    	    	session.save(rst);
		    	    }
	            }	            	           	            
	    	    iii++;	    		
	    	}
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);
			    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.IR.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());
    	        jjj++;	    		
	    	}
	    }	    
		session.getTransaction().commit();				
		
		log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.IR.getAlias());		
	    log.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
		
	}
	
	
	public static void jobValuationStandard() {
		
		log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.SD.getAlias());
		session.beginTransaction();	    
		LocalTime startTime = LocalTime.now(); 
		
	    List<KicsAssetSecr> instruments = new ArrayList<KicsAssetSecr>();
//	    if(expoId != null) instruments = KicsAssetSecrDao.getEntities(baseDate, expoId);
//	    else instruments = KicsAssetSecrDao.getEntities(baseDate);
		
		//// for check exception !! ///////////////////
	    int iii = 0, jjj = 0;	    
	    for(KicsAssetSecr inst : instruments){
	    	
	    	try {   
	    		Instrument fiInst = EInstrument.getEInstrument(inst.getInstTpcd() + inst.getInstDtlsTpcd()).getInstrumentModel();
	    		
	    		inst.setIsRealNumber(false);
    		    fiInst.setInstrumentEntities(inst);	            
	    	    results = fiInst.getValuation();	    		
	    		log.info("{}, {}, {}, {}, {}, {}, {}", inst.getExpoId(), inst.getMatrDate(), inst.getIntPayCyc(), inst.getIrate(), inst.getIrate() * inst.getBsAmt(), inst.getBsAmt());	    		
	    		
	    		for(KicsAssetResult rst : results) {  
	    	    	rst.setScenNum(singleScenarioIndex);
	    	    	rst.setScenName(EScenarioAttr.SD.getAlias());
	    	    	rst.setScenType(EScenarioAttr.SD.name());	    	    	
	    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
	    	    	rst.setLastUpdateDate(LocalDate.now());
	    	    	session.save(rst);
	    	    	//log.info("Type: {}, Date: {}, Value:{}", rst.getResultName(), TimeUtil.stringToDate(rst.getResultDate()), rst.getValue());
	    	    }	            
	    	    iii++;	    	    	    			
	    	}
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);
			    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.SD.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());
    	        jjj++;	    		
	    	}
	    }	    
		session.getTransaction().commit();				
		
		log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.SD.getAlias());		
	    log.info("Inforce: {}, Success: {}, Failure(): {}", instruments.size(), iii, jjj);
	    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);	    		
	}
	
	
	@SuppressWarnings("unchecked")
	public static void calDns() {

		try {		
			session.beginTransaction();
		    
//		    List<IrCurveHis2> curveHis;	    
//		    curveHis = IrCurveHisDao.getEntities("1111111", "20181226", "20181228");		    
//		    
//		    log.info("{}, {}", mapT.keySet().size(), mapT.values().size());
//		    
//	    	for(IrCurveHis2 his : curveHis)	{
//	    		//log.info("{}, {}, {}", his.idMap(), mapT, his.idMap().equals(mapT));	    		
//	    		//log.info("{}, {}", id, his);
//	    		//hisMap2.put( (TreeMap<String, Object>) his.idMap(), his.getIntRate());
//	    		//log.info("{}, {}, {}", his.idMap().get("baseDate"), his.idMap().get("matCd"), his.getIntRate());
//	    	}
//	    	
//	    	for(Map<String, Object> map : hisMap2.keySet()) {
//	    		//log.info("{}, {}, {}", map, map.get("baseDate"));	    		
//	    		//Map<String, Map<Double, Double>>
//	    		//Object obj = map.get("baseDate");	    		
//	    	}
	    	
	    	Map<String, Map<String, Double>> hisMap4 = new TreeMap<String, Map<String, Double>>();
	    	//hisMap4 = IrCurveHisDao.getEntitiesMap3("1111111", "20170101", "20171231");
	    	hisMap4 = IrCurveHisDao.getEntitiesMap3("1111111", "20171226", "20171228"); 
	    	//log.info("testClass:{}, {}, {}", hisMap4);  
	    	
	    	
	    	Map<String, Map<Double, Double>> hisMap5 = new TreeMap<String, Map<Double, Double>>();
	    	for(Map.Entry<String, Map<String, Double>> his5 : hisMap4.entrySet()) {
	    		
	    		TreeMap<Double, Double> dbVal = new TreeMap<Double, Double>();
	    		for(Map.Entry<String, Double> hisval : his5.getValue().entrySet()) {
	    			dbVal.put(Double.parseDouble(hisval.getKey().substring(1, 5)), hisval.getValue());
	    			//dbVal.put(Double.parseDouble(hisval.getKey().substring(1, 5)) / 12.0, hisval.getValue() * 0.01);
//	    			log.info("{}, {}, {}", his5.getKey(), hisval.getKey(), hisval.getValue());	    			
//	    			log.info("{}, {}, {}", his5.getKey(), (Double.parseDouble(hisval.getKey().substring(1, 5)) / 12.0),  hisval.getValue() * 0.01);
	    		}
	    		hisMap5.put(his5.getKey(), (TreeMap<Double, Double>) dbVal.clone());	    		
	    	}
	    	//log.info("{}, {}, {}", hisMap5.keySet().size(), ((TreeMap<String, Map<Double, Double>>) hisMap5).firstEntry().getValue().size(), hisMap5);
	    	log.info("testClass2:{}, {}, {}", hisMap5);  
	    	
	    	IrmodelNelsonSeigelD dns = new IrmodelNelsonSeigelD(TimeUtil.stringToDate(baseDate), hisMap5, hisMap5.size());
	    	
			dns.setTimeUnit(Irmodel.TIME_UNIT_MONTH);
			dns.setDayCountBasis(9);
			dns.setDecimalDigit(10);
			//dns.setRealNumber(true);
			//dns.setCmpdType(Irmodel.CMPD_MTD_CONT);
			//dns.setPrjTimeUnit(Irmodel.TIME_UNIT_YEAR);		

			//log.info("{}, {}, {}", tsMap, testTenor, testRate);
			//log.info("{}", sw.getTermStructure().isEmpty());
			
			Map<String, Map<String, IrmodelResult>> dnsMap = dns.getIrmodelResult();
			log.info("DNS Result : {}", dnsMap);
					

//			for(Map.Entry<String, IrmodelResult> ts1 : dnsMap.get(baseDate).entrySet()) {
//				//log.info("term: {}, {}, irRate: {}, {}, {}, {}", ts.getKey().toString(), ts.getValue().getMatDate(), ts.getValue().getSpotCont(), ts.getValue().getFwdCont(), ts.getValue().getSpotDisc(), ts.getValue().getFwdDisc());			
//				log.info("term: {}, {}, {}, {}", ts1.getKey().toString(), ts1.getValue().getMatDate(), ts1.getValue().getSpotDisc(), ts1.getValue().getMatTerm() );						
//			}
			dns.paramToString();	
	    	
			
//	    	Map<String, Map<String, Double>> hisMap6 = new TreeMap<String, Map<String, Double>>();
//	    	hisMap6 = IrCurveHisDao.getEntitiesMap2("1111111", "20171226", "20171228");
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static void calSw() {
		
		Map<Double, Double> tsMap = new TreeMap<Double, Double>();		
		
//		double[] testTenor = new double[] {0.25, 0.50, 0.75, 1.00, 1.50, 2.00, 2.50, 3.00, 4.00, 5.00, 7.00, 10.0, 20.0};
//		//double[] testTenor = new double[] {   3,    6,    9,   12,   18,   24,   30,   36,   48,   60,   84,  120,  240};
//		double[] testRate  = new double[] {1.52, 1.66, 1.79, 1.87, 2.01, 2.09, 2.15, 2.15, 2.31, 2.38, 2.44, 2.46, 2.44}; //FY2017 RF
//		//double[] testRate  = new double[] {1.33, 1.47, 1.52, 1.56, 1.61, 1.63, 1.66, 1.65, 1.81, 1.86, 2.02, 2.10, 2.16}; //FY2016 RF 
//		for(int i=0; i<testTenor.length; i++) tsMap.put(testTenor[i],  testRate[i]);
		
		session.beginTransaction();	    
		List<IrCurveHis2> curveHis;		
	    curveHis = IrCurveHisDao.getEntities("1111111", baseDate);		
	    //curveHis.stream().forEach(s -> log.info("{}, {}", Double.parseDouble(s.getMatCd().substring(1, 5)), s.getIntRate()));
		curveHis.stream().forEach(s -> tsMap.put(Double.parseDouble(s.getMatCd().substring(1, 5)), s.getIntRate()));		
		
		//log.info("{}", tsMap);
		IrmodelSmithWilson sw = new IrmodelSmithWilson(TimeUtil.stringToDate(baseDate), tsMap);
		
		//IrmodelSmithWilson sw = new IrmodelSmithWilson(TimeUtil.stringToDate(baseDate));				
		
//		sw.setTermStructure(tsMap);
//		log.info("{}", sw.getTermStructure());
//		log.info("{}, {}", sw.getTenor(), sw.getiRate());
		sw.setTimeUnit(Irmodel.TIME_UNIT_MONTH);
		sw.setDayCountBasis(9);
		sw.setDecimalDigit(10);
		sw.setRealNumber(false);
//		sw.setPrjYear(150);
//		//sw.setLtfrCont(0.027);
//		sw.setLtfr(0.045);
//		sw.setLtfrPoint(60);
		
		//sw.setCmpdType(Irmodel.CMPD_MTD_CONT);
		//sw.setPrjTimeUnit(Irmodel.TIME_UNIT_YEAR);		

		//log.info("{}, {}, {}", tsMap, testTenor, testRate);
		//log.info("{}", sw.getTermStructure().isEmpty());
		
		Map<String, Map<String, IrmodelResult>> swMap = sw.getIrmodelResult();		
		if(swMap.containsKey(baseDate)) {
			for(Map.Entry<String, IrmodelResult> ts : swMap.get(baseDate).entrySet()) {
				//log.info("term: {}, {}, irRate: {}, {}, {}, {}", ts.getKey().toString(), ts.getValue().getMatDate(), ts.getValue().getSpotCont(), ts.getValue().getFwdCont(), ts.getValue().getSpotDisc(), ts.getValue().getFwdDisc());			
				log.info("term: {}, {}, {}, {}", ts.getKey().toString(), ts.getValue().getMatDate(), ts.getValue().getSpotDisc(), ts.getValue().getMatTerm() );
			}			
		}
		sw.paramToString();		
		
//		if(sw.getIrmodelResult().containsKey(baseDate)) {
//			for(Map.Entry<String, IrmodelResult> ts : sw.getIrmodelResult().get(baseDate).entrySet()) {
//				//log.info("term: {}, {}, irRate: {}, {}, {}, {}", ts.getKey().toString(), ts.getValue().getMatDate(), ts.getValue().getSpotCont(), ts.getValue().getFwdCont(), ts.getValue().getSpotDisc(), ts.getValue().getFwdDisc());			
//				log.info("term: {}, {}, {}, {}", ts.getKey().toString(), ts.getValue().getMatDate(), ts.getValue().getSpotDisc(), ts.getValue().getMatTerm() );
//			}			
//		}
	}	
	
}
