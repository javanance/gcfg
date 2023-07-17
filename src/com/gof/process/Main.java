package com.gof.process;

import java.awt.color.ICC_ColorSpace;
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

import com.gof.dao.DaoUtil;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.KicsAssetAccoDao;
import com.gof.dao.KicsAssetFideDao;
import com.gof.dao.KicsAssetLoanDao;
import com.gof.dao.KicsAssetResultDao;
import com.gof.dao.KicsAssetSecrDao;
import com.gof.dao.KicsFssScenDao;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveHis2;
import com.gof.entity.IrmodelResult;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsShckStk;
import com.gof.enums.EIrScenario;
import com.gof.enums.EScenarioAttr;
import com.gof.enums.ERunArgument;
import com.gof.enums.EBoolean;
import com.gof.enums.EInstrument;
import com.gof.interfaces.Constant;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.Irmodel;
import com.gof.interfaces.KicsAsset;
import com.gof.irmodel.IrmodelNelsonSeigelD;
import com.gof.irmodel.IrmodelSmithWilson;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * INSTRUMENT ÀÇ Main Å¬·¡½º. Process ÆÐÅ°Áö¿¡ µî·ÏµÈ ÀÛ¾÷ÀÇ ±¸µ¿ ¿©ºÎ, ±¸µ¿ ¼ø¼­¸¦ °ü¸®ÇÔ.
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
	
	private static Session session;	
	private static String baseDate;
	private static String JOB_GCFG_ID;
	private static String today = TimeUtil.dateToString(LocalDate.now());
	private static int singleScenarioIndex = 1;
	
	private static List<KicsAsset> instruments = new ArrayList<KicsAsset>();
    private static List<KicsAsset> errInstrument = new ArrayList<KicsAsset>();
    private static Map<String, String> exceptions = new HashMap<String, String>();
    private static List<KicsAssetResult> results;    
    
	
	private static List<Integer> irScenarioIndexList = new ArrayList<Integer>();
	//private static List<Integer> irScenarioIndexList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);	
	
	private static Map<Integer, Map<String, IrCurveHis>> scenarioCurveHisMap = new HashMap<Integer, Map<String, IrCurveHis>>();
	private static Map<String, IrCurveHis> calibrationCurveHis = new HashMap<String, IrCurveHis>();
	
	private static Map<Integer, Map<String, Map<String, IrCurveHis>>> scenarioCurveHisFxMap = new HashMap<Integer, Map<String, Map<String, IrCurveHis>>>();
	private static Map<String, Map<String, IrCurveHis>> calibrationCurveHisFxMap = new HashMap<String, Map<String, IrCurveHis>>();	
	private static Map<String, IrCurveHis> calibrationCurveHisFx  = new HashMap<String, IrCurveHis>();	
	
	private static Map<String, Double> impliedSpreadMap = new HashMap<String, Double>();    
	private static Map<String, Double> impliedMaturityMap = new HashMap<String, Double>();
	//private static Map<String, Double> impliedSigmaMap = new HashMap<String, Double>();
	
	//private static String stkModelId;
	private static String stkShockDirection;
	private static String KOSPI_INDEX_ID;
	private static Map<String, KicsShckStk> stkScenarioMap = new HashMap<String, KicsShckStk>();	
	
	//////////////////////////////////////
	private static String irModelId;
	private static String irScenarioFxYn = "N";
	private static String irScenarioFxDefault; 
	
	private static String irScenarioFxCurrency;
	private static List<String> irScenarioFxCurrencyList = new ArrayList<String>();	
	//////////////////////////////////////	
	
	private static boolean fx = false;

	private static String expoId = null;	
	
//	private static String expoId = "'SECR_O_4000000000635G000000001'";             //FOREIGN bond
//	private static String expoId = "'SECR_O_4000000000744G000000001'";             //FOREIGN bond
//	private static String expoId = "'SECR_O_4000000000446G000000001'";             //FRN
//	private static String expoId = "'SECR_O_4000000000663G000000001'";             //FRN
//	private static String expoId = "'SECR_O_4000000000842G000000001'";             //FRN q
//	private static String expoId = "'FIDE_O_1000000025216'";             			//bond forward not ok
//	private static String expoId = "'FIDE_O_1000000025672'";             			//bond forward ok
//	private static String expoId = "'FIDE_L_G000KR1510717041_999999999999_010'";             //ktb 
    //private static String expoId = "'SECR_O_G110KR103502G5C60142016011800001'";             //ktb 
    //private static String expoId = "'SECR_O_G110KR3822047V600162008062000001'";             //embedded bond	--> ¿ì¸®ÀºÇà ÈÄ¼øÀ§Ã¤.. matPremium patch ÀÌÈÄ ÇØ°á -> ÇÒÀÎ±â°£(1³âvs3°³¿ù)¿¡ µû¶ó Á¶Á¤¸¸±â Â÷ÀÌ¹ß»ý!
	//private static String expoId = "'SECR_O_A130KRB50801G4830132014082200001_12714363'";    //MBS --> imp_mat_tol -> to be more bigger // (ytmÀÌ À½¼ö³×? ¼ö·Åµµ ¾ÈµÊ)-> mat PatchÀÌÈÄ ÇØ°á
	//private static String expoId = "'SECR_O_A130XS13045707050132015101900003_12714691'";    //USD ibrd
	//private static String expoId = "'SECR_O_G110FR00132512040142017041200002_12722674'";    //FR0013251204	//ÇÁ¶û½º¿¹º¸(CDC) 2.62 04/12/2047 -> ¿É¼Ç±¸ºÐ¼Ó¼ºÀÌ 08ÀÌÁö¸¸, ¿É¼ÇÇà»ç½ÃÀÛÀÏÀº ¾øÀ½
    //private static String expoId = "'SECR_O_G110KRC0350C20660142010102600001_12720177'";    //±¹°íÃ¤ ¸¸±âº¸À¯ ÀÌÇ¥½ºÆ®¸³  // KRC0350C2066
    //private static String expoId = "'SECR_O_G110KR103502G7920142017091900001_12720172'";    //±¹°íÃ¤ ¸¸±âº¸À¯ ¿øÃ¤±Ç       // KR103502G792
	//private static String expoId = "'SECR_O_A130KR101501D7690132017070700001_12714335'";    //±¹¹ÎÁÖÅÃ1Á¾  // KR101501D769 --> compound class¾È¿¡¼­ ºÐ·ù°¡ Àß¸øµÇ¸é °ªÀÌ Æ²¾îÁú¼öÀÖÀ½(default:Simple)
	//private static String expoId = "'SECR_O_P110XS13340799580132016012900001_12717488'";    //zero callable(simple) // XS1334079958
	//private static String expoId = "'SECR_O_P110KR20010147380132017032900001_12716749'";    //º¹5´Ü2 // KR2001014738	 // cmpdTypeÀß¸øµÇ¸é °æ°úÀÌÀÚ Æ²¾îÁü // º¹5´Ü2 ¸»°í ´Ù¸¥ Å¸ÀÔµµ Á¸Àç°¡´É!! YYCT = 3ÀÎ °ÍÀº ÇØ¿ÜÃ¤±Ç µµ È®ÀÎÇÊ¿ä
	//private static String expoId = "'SECR_O_G110KR72066600030129999123100000_12713865'";    //stock	
	//private static String expoId = "'ACCO_267008140000'";                                   //acco	//
	//private static String expoId = "'FIDE_O_G110SWAP170720020112017072000001'";             //KOGAS CRS (2018) // SWAP17072002
	//private static String expoId = "'FIDE_O_G110IRS1105170010122011051700001'";             //IRS  	IRS110517001
	//private static String expoId = "'FIDE_O_G110SNDO171019010112017101900001'";             //FX //	SNDO17101901	
	//private static String expoId = "'LOAN_0129361100037'";    //LOAN 1(INDIVIDUALS (not CORP)) //	0129361100037
	//private static String expoId = "'FIDE_O_G110KR4101N300020222017121300002_02'";          //KR4101N30002 //KOSPI200 FUTURES
	//private static String expoId = "'FIDE_O_G251KR4167M600020122017040500001_01'";          //KR4167M60002 //KTB FUTURES	(Long)
	//private static String expoId = "'FIDE_O_G251KR4165M600060222017040600001_02'";          //KR4165M60006 //KTB FUTURES  (Short)
	//private static String expoId = "'FIDE_O_G271KR4167M600020122017032000001_01'";          //KR4167M60002 //KTB FUTURES	(Long)
	//private static String expoId = "'FIDE_O_G280KR4165M600060122017042400001_01'";          //KR4165M60006 //KTB FUTURES	(Long)
	
	//private static String expoId = "'FIDE_O_G110SWAP150909010112015091000001_12724376'";      // net YTMÀÌ Àß ¾ÈµÇ´Âµ¥, ¿ÞÂÊÀÇ ÄÉÀÌ½º´Â REC PAYµµ ¹®Á¦°¡ ÀÖ¾úÀ½ --> ½ÇÁ¦ ¿øÈ­/¿ÜÈ­ ¸í¸ñ¿ø±Ý°ú IDHKRH_SWAP_INFO »óÀÇ otbl_prcp, bgn_prcp_excg_amt µÎ°³ÀÇ Â÷ÀÌ°¡ Å­(5¹é¸¸ºÒ vs 80¾ï) --> ÇòÁö´ë»óÀÎ ÀÚ»êÀº Àå±âÃ¤±ÇÀÌ¸ç, ÀÌÀÇ °øÁ¤°¡Ä¡ÀÎµí
	//private static String expoId = "'FIDE_O_P110SNDO160122010112016012700001_12717616'";      // net YTMÀÌ Àß ¾ÈµÇ´Âµ¥, ¿ÞÂÊÀÇ ÄÉÀÌ½º´Â REC PAYµµ ¹®Á¦°¡ ÀÖ¾úÀ½ --> ½ÇÁ¦ ¿øÈ­/¿ÜÈ­ ¸í¸ñ¿ø±Ý°ú IDHKRH_SWAP_INFO »óÀÇ otbl_prcp, bgn_prcp_excg_amt µÎ°³ÀÇ Â÷ÀÌ°¡ Å­(5¹é¸¸ºÒ vs 80¾ï) --> ÇòÁö´ë»óÀÎ ÀÚ»êÀº Àå±âÃ¤±ÇÀÌ¸ç, ÀÌÀÇ °øÁ¤°¡Ä¡ÀÎµí

	//private static String expoId = "'SECR_O_G110USFC000001010112013012900001'";  // ±¸Á¶È­¿¹±Ý5
	//private static String expoId = "'FIDE_O_G110SWAP141008010112014101400001'";    //CRS
	
	//private static String expoId = "'SECR_L_A140KR5107A01857_KR3561044065_035'";    //KTB FUTURES LT	
	//private static String expoId = null;	

	//private static String expoId = "'SECR_O_G110US478160BU720142016041200002'";     //USD
	//private static String expoId = "'SECR_O_G110XS15802313030142017032100001'";     //SEK
	//private static String expoId = "'SECR_O_G110BE00085440830142019012300003'";     //EUR
	
	
	
	/**
	 * TODO:
	 */
	public static void main(String[] args) throws Exception {		

	    try {
	    	
// **************************************************************************************************************************
			if(!init(args)) {
				log.error("Today is {} and BaseDate is {}", today, baseDate == null ? "NULL" : "Incorrect! [" + baseDate + "]");
				System.exit(0);
			}		
			
//			hiberTest();

// **************************************************************************************************************************
//			log.info("init after : {},{},{}", baseDate, expoId);
			
			deleteResult(baseDate);
			//deleteResult();
			setInstCurve(baseDate);		
			loadInstrument(expoId);
		
// **************************************************************************************************************************
		
			//Standard Valuation with Base Rate
			if(jobList.contains("SD")) {
				//jobValuationStandard();
			}
			
			//Discount Rate Calibration
			if(jobList.contains("CL")) {
				setIrScenarioForCalibration();
				if(calibrationCurveHis != null && !calibrationCurveHis.isEmpty()) {
					jobValuationForCalibration();				
				}			
				else {
					log.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.CL.getAlias(), baseDate);					
					System.exit(0);
				}				
			}		
	
			//Valuation with IR Shock Scenario
			if(jobList.contains("IR")) {
//				setIrScenario(1);			
				setIrScenario(irScenarioIndexList);
				if(scenarioCurveHisMap != null && !scenarioCurveHisMap.isEmpty()) {
					jobValuationIrScenario();
				}
				else {
					log.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.IR.getAlias(), baseDate);
					System.exit(0);
				}
			}
			
			//Valuation with STOCK Shock Scenario
			if(jobList.contains("ST")) {			
//				setStockScenario();	
//				if(stkScenarioMap != null && !stkScenarioMap.isEmpty()) {
//					jobValuationStockScenario();
//				}			
//				else {
//					log.error("Job_Valuation is Failed: [JOB TYPE: {}]. Check the Scenario Data at [{}]", EScenarioAttr.ST.getAlias(), baseDate);
//					System.exit(0);
//				}				
			}
			
			if(jobList.contains("SW")) {
				calSw();
			}
			
			if(jobList.contains("NS")) {
				calDns();
			}		
		

// **************************************************************************************************************************		
			//error instrument
		    for(Map.Entry<String, String> elem : exceptions.entrySet()) log.warn("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
		
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
//			session = HibernateUtil.getSessionFactory().openSession();
		}
		catch (Exception e) {
			session = HibernateUtil.getSessionFactory().openSession();
			log.error("Error in Properties File Loading: {}", e);
		}		
		
		try {		
			JOB_GCFG_ID = properties.getOrDefault("JobId", "KICS_CFG").toString().trim().toUpperCase();
			
			//String jobString = properties.getOrDefault("JobList", "SD,CL,IR,ST").toString().trim().toUpperCase();		
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
			
			
			//log.info("{}, {}, {}, {}, {}, {}, {}", irModelId, irScenarioIndex, irScenarioIndexList, irScenarioFxYn, irScenarioFxCurrency, irScenarioFxCurrencyList, irScenarioFxDefault);
		}
		catch (Exception e) {			
			e.printStackTrace();
			log.error("Error in Properties File Loading: {}", e);			
			System.exit(0);
		}		

		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public static void testSql() {
		
	    String sqlHisDate = "select max(a.baseDate) from IrCurveHis a where a.baseDate <= '" + baseDate + "'";	    
	    List<String> irHisDateList = session.createQuery(sqlHisDate).getResultList();
	    log.info("testSql: {}", irHisDateList.get(0));		
	}
	
	
	public static void hiberTest() {		
		
//		session.beginTransaction();	    

		List<IrCurve> curve;		
		Map<String, Object> curveParam2 = new HashMap<String, Object>();		
		//curveParam2.put("irCurveId", irModelId);		
		curveParam2.put("irCurveId", "1111111");
		
		curve = DaoUtil.getEntities("IrCurve", curveParam2);	    
		curve.stream().forEach(s -> log.info("{}, {}, {}", s.getIrCurveId(), s.getIrCurveNm(), s.getIrCurveHis()));
	    
	    if(curve.isEmpty()) log.info("^^^^");		
	}

	
		
	public static void deleteResult() {
		KicsAssetResultDao.deleteEntities();
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
	
	
	public static void deleteResult(String baseDate, int idx) {		
		KicsAssetResultDao.deleteEntities(baseDate, idx);		
	}	


	public static void setInstCurve(String baseDate) {
		//KicsInstCurveDao.deleteEntities();
		//KicsInstCurveDao.insertEntities(baseDate);		
	}
	
	
	public static void jobValuationForCalibration() {
	
			log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
			session.beginTransaction();
			LocalTime startTime = LocalTime.now();
			
		    int iii = 0, jjj = 0;		
		    Integer count = 1;
		    
		    for(KicsAsset inst : instruments){
		    	try {	    		
		    		Instrument fiInst = EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstrumentModel();    		
		    		Integer instCode  = GeneralUtil.objectToPrimitive(EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstCode());
		    		
		    		//inst.setIsRealNumber(true);
		    		fiInst.setInstrumentEntities(inst);
		    		//log.info("type: {}, Class: {}, fiInst: {}",inst.getInstTpcd() + inst.getInstDtlsTpcd(), fiInst.getClass().getSimpleName(), fiInst.getExpoId());	    		   
		    		
		    		//For Applying Foreign Currency Interest Rate Scenario
		    		if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse() 
		    		   && (instCode.intValue() >= Instrument.INST_BOND_ZCB && instCode.intValue() <= Instrument.INST_BOND_NON) 
		    		   && !GeneralUtil.objectToPrimitive(inst.getCrnyCd(), Instrument.DEF_CURRENCY).equals(Instrument.DEF_CURRENCY)) {	    			
	
//		    			log.info("In Bond And Foregin : {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    			
		    			int itr = 0;	    			
		    			for(Map.Entry<String, Map<String, IrCurveHis>> clCurveHis : calibrationCurveHisFxMap.entrySet()) {	    				
		    				
//		    				log.info("In Calib His Map : {},{},{}", clCurveHis.getKey(), clCurveHis.getValue().size());
		    				
		    				if(inst.getCrnyCd().equals(clCurveHis.getKey())) {
//		    					log.info("In Bond And Foregin 0 : {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    					
		    					if(!clCurveHis.getValue().isEmpty()) {
		    						//log.info("here matching");
		    						
//		    						log.info("In Bond And Foregin 1 : {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    						
		    						fiInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), clCurveHis.getValue(), null);
		    						
		    						itr = itr + 1;
		    						break;
		    					}
		    				}
		    			}
		    			
		    			if (itr == 0) {	    				
		    				//log.info("here default");
		    				
		    				log.info("In Default: {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    				
		    				fiInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHisFxMap.get(irScenarioFxDefault), null);
		    			} 	    			
		    		}
	//	    		
		    		//For Applying KRW Interest Rate Scenario	    		
		    		else {	    			
		    			//log.info("here krw");
//		    			log.info("In KRW : {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    			
		    			fiInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHis, null);
		    		}
		    		
	//	    		Additional fxCurve for fxSwap and Crs 
		    		if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse() 
		 	    		   && (instCode.intValue() == Instrument.INST_FIDE_CCSWAP || instCode.intValue() == Instrument.INST_FIDE_FXSWAP)){	    			
		    				
//		    				log.info("In Derivatives : {},{},{}", instCode, inst.getCrnyCd(), inst.getExpoId());
		    			
		    				for(Map.Entry<String, Map<String, IrCurveHis>> entry : calibrationCurveHisFxMap.entrySet()) {
		    					if(!entry.getKey().equals("KRW")) {
		    						
		    						log.info("zzzz : {}", entry.getKey());
		    						
		    						fiInst.setIrScenarioFxCurveEntities(EIrScenario.CALIBRATION.getScenNum(), entry.getValue(), 0.0);
		    						break;
		    					}
		    				}
		 	    	}
		    		
//		    		log.info("Before Valuation : {},{}", fiInst.getExpoId(), fx);
		    	    results = fiInst.getValuation(fx);
		    	    
		    	    if(results == null || results.size() < 1) { 
		    	    	log.warn("No Asset Result in [{}]", fiInst.getExpoId());
		    	    	break;
		    	    }
		    	    
		    	    for(KicsAssetResult rst : results) {
	
		    	    	rst.setScenNum(EIrScenario.CALIBRATION.getScenNum());
		    	    	rst.setScenName(EIrScenario.CALIBRATION.name());
		    	    	rst.setScenType(EIrScenario.CALIBRATION.getScenType());
		    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
		    	    	rst.setLastUpdateDate(LocalDate.now());
		    	    	
		    	    	session.save(rst);	    	   
		    	    	if(count % 10000 == 0) {
		    	    		session.flush();
		    	    		session.clear();
		    	    		count = 1;
		    	    	}
		    	    	
		    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_SPREAD))) {
		    	    		impliedSpreadMap.put(inst.getExpoId(), rst.getValue());
		    	    		if(Math.abs(rst.getValue()) > 0.02) {
		    	    			//log.info("Implied Spread  : {}: VAL: {}, TP: {}, CLASS: {}", inst.getExpoId(), format(rst.getValue()), inst.getProdTpcd(), setType(inst.getInstTpcd(), inst.getInstDtlsTpcd()));	
		    	    		}	    	    		
		    	    	}
		    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_MATURITY))) {
		    	    		impliedMaturityMap.put(inst.getExpoId(), rst.getValue());
		    	    		//log.info("Implied Maturity: {}: VAL: {}, TP: {}, CLASS: {}", inst.getExpoId(), format(rst.getValue()), inst.getProdTpcd(), setType(inst.getInstTpcd(), inst.getInstDtlsTpcd()));
		    	    	}
	//	    	    	if(rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_SIGMA))) {
	//	    	    		impliedSigmaMap.put(inst.getExpoId(), rst.getValue());
	//	    	    		log.info("Implied Sigma   : {}: VAL: {}, TP: {}, CLASS: {}", inst.getExpoId(), format(rst.getValue()), inst.getProdTpcd(), setType(inst.getInstTpcd(), inst.getInstDtlsTpcd()));
	//	    	    	}	    	    	
		    	    	count++;
		    	    }	    		    	    
		    	    iii++;	    	    
		    	}		 
		    	catch(Exception ex) {
		    		ex.printStackTrace();
		    		log.error("ERROR: EXPO_ID : [{}], {}", inst.getExpoId(), ex);
		    		
				    errInstrument.add(inst);			    
				    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.CL.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());			    
	    	        jjj++;
		    	}	    	
		    	if(iii % 100 == 0) log.info("Instrument is processed {}/{}", iii, instruments.size());	    	
		    }
			session.getTransaction().commit();		
			log.info("Instrument is processed {}/{}", iii, instruments.size());
	
			log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
			log.info("Inforce: {}, Success: {}, Failure: {}", instruments.size(), iii, jjj);
		    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
			
		}


	public static void jobValuationIrScenario() {
			
			log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.IR.getAlias());
			session.beginTransaction();	    
			LocalTime startTime = LocalTime.now();		
		
		    int iii = 0, jjj = 0;	    
		    Integer count = 1;
		    
		    for(KicsAsset inst : instruments){
		    	
		    	try {	    		
		    		Instrument fiInst = EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstrumentModel();	    		
		    		Integer instCode  = GeneralUtil.objectToPrimitive(EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstCode());
		    		
		    		//inst.setIsRealNumber(true);    		    
	    		    fiInst.setInstrumentEntities(inst);    		    
	    		    
		            for(Map.Entry<Integer, Map<String, IrCurveHis>> scenarioCurveHis : scenarioCurveHisMap.entrySet()) {	          
//		            	log.info("Valuation in Main : {},{}", scenarioCurveHis.getKey(), scenarioCurveHis.getValue().get("M0012"));
	
			    		if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse()
			    		   && (instCode.intValue() >= Instrument.INST_BOND_ZCB && instCode.intValue() <= Instrument.INST_BOND_NON) 
				 	       && !GeneralUtil.objectToPrimitive(inst.getCrnyCd(), Instrument.DEF_CURRENCY).equals(Instrument.DEF_CURRENCY)) {
			    			
			    			int itr = 0;
		    				for(Map.Entry<String, Map<String, IrCurveHis>> scenCurveHisFx : scenarioCurveHisFxMap.get(scenarioCurveHis.getKey()).entrySet()) {	
				 	    				
			    				if(inst.getCrnyCd().equals(scenCurveHisFx.getKey())) {
			    					
			    					if(!scenCurveHisFx.getValue().isEmpty()) {
			    						//log.info("here matching");	
				    					fiInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenCurveHisFx.getValue(), impliedSpreadMap.get(inst.getExpoId()));
				    					itr = itr + 1;
				    					break;
			    					}
			    				}
			    			}
		    				
		    				if(itr == 0) {	    					
		    					if(scenarioCurveHisFxMap.get(scenarioCurveHis.getKey()).containsKey(irScenarioFxDefault)
		    					   && !scenarioCurveHisFxMap.get(scenarioCurveHis.getKey()).get(irScenarioFxDefault).isEmpty()) {	    						
		    						//log.info("here default");	    						
		    						fiInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHisFxMap.get(scenarioCurveHis.getKey()).get(irScenarioFxDefault), impliedSpreadMap.get(inst.getExpoId()));
		    					}
		    					else {	    				
		    						//log.info("here krw1");
		    						fiInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHis.getValue(), impliedSpreadMap.get(inst.getExpoId()));		    					    					
		    					}
		    				}	    				
			    		}
				    	else {			    		
				    		//log.info("here krw2");
				    		fiInst.setIrScenarioCurveEntities(scenarioCurveHis.getKey(), scenarioCurveHis.getValue(), impliedSpreadMap.get(inst.getExpoId()));
				    	}
		            	
	//		    		Additional fxCurve for fxSwap and Crs 
			    		if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse() 
			 	    		   && (instCode.intValue() == Instrument.INST_FIDE_CCSWAP || instCode.intValue() == Instrument.INST_FIDE_FXSWAP)){	    			
			    			
			    				for(Map.Entry<String, Map<String, IrCurveHis>> entry : scenarioCurveHisFxMap.get(scenarioCurveHis.getKey()).entrySet()) {
			    					if(!entry.getKey().equals("KRW")) {
			    						
//			    						log.info("zzzz : {}", entry.getKey());
			    						
			    						fiInst.setIrScenarioFxCurveEntities(scenarioCurveHis.getKey(), entry.getValue(), 0.0);
			    						break;
			    					}
			    				}
			 	    	}
			    		
			    		
			    		
		            	fiInst.setImpliedMaturity(impliedMaturityMap.get(inst.getExpoId()));
		            	//fiInst.setStockScenarioEntities(impliedSigmaMap.get(inst.getExpoId()));
		            	//seInst.setImpliedMaturity(impliedMaturityMap.getOrDefault(inst.getExpoId(), 1.0));
			    	    results = fiInst.getValuation(fx);	    	    
			    	    
			    	    if(results == null || results.size() < 1) { 
					    	log.warn("No Asset Result in [{}]", fiInst.getExpoId());
					    	break;
					    }
	
			    	    for(KicsAssetResult rst : results) {
			    	    	
			    	    	rst.setScenNum(scenarioCurveHis.getKey());		    	    			
	
			    	    	//rst.setScenName(null);
			    	    	//rst.setScenType("IR");
			    	    	//log.info("{}, {}, {}", scenarioCurveHis.getKey(), EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).getScenNum(), EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).name());
			    	    	rst.setScenName(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).name());
			    	    	rst.setScenType(EIrScenario.getEIrScenario(scenarioCurveHis.getKey()).getScenType());		    	    	
			    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
			    	    	rst.setLastUpdateDate(LocalDate.now());
	
			    	    	//log.info("Result: {}", rst.toString());
			    	    	session.save(rst);
			    	    	if(count % 10000 == 0) {
			    	    		session.flush();
			    	    		session.clear();
			    	    		count = 0;
			    	    	}
			    	    	count++;
			    	    }		    	    
		            }	            	           	            
		    	    iii++;	    		
		    	}
		    	catch(Exception ex) {
		    		ex.printStackTrace();
		    		log.error("ERROR: EXPO_ID : [{}], {}", inst.getExpoId(), ex);
		    		
				    errInstrument.add(inst);			    
				    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.IR.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());
	    	        jjj++;	    		
		    	}
		    	if(iii % 100 == 0) log.info("Instrument is processed {}/{}", iii, instruments.size());	    	
		    }
		    log.info("Instrument is processed {}/{}", iii, instruments.size());
			session.getTransaction().commit();				
			
			log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.IR.getAlias());		
		    log.info("Inforce: {}, Success: {}, Failure: {}", instruments.size(), iii, jjj);
		    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
			
		}


	public static void jobValuationStockScenario() {
		
		log.info("Job_Valuation Start: [JOB TYPE: {}]", EScenarioAttr.ST.getAlias());
		session.beginTransaction();	    
		LocalTime startTime = LocalTime.now();		
	
	    int iii = 0, jjj = 0;	    
	    Integer count = 1;
	    
	    for(KicsAsset inst : instruments){
	    	
	    	try {	    		
	    		Instrument fiInst = EInstrument.getEInstrument(inst.getInstTpcd() + inst.getInstDtlsTpcd()).getInstrumentModel();
	    		
	    		//inst.setIsRealNumber(true);    		    
			    fiInst.setInstrumentEntities(inst);    		    
	
	        	fiInst.setIrScenarioCurveEntities(EIrScenario.CALIBRATION.getScenNum(), calibrationCurveHis, impliedSpreadMap.get(inst.getExpoId()));	            	
	        	fiInst.setImpliedMaturity(impliedMaturityMap.get(inst.getExpoId()));
	        	fiInst.setStockScenarioEntities(singleScenarioIndex, String.valueOf(Constant.MULTIPLY), (stkShockDirection.equals("UP") ? 1.0 : -1.0) * stkScenarioMap.get(KOSPI_INDEX_ID).getShckVal());
	        	//fiInst.setStockScenarioEntities(impliedSigmaMap.get(inst.getExpoId()));
	    	    results = fiInst.getValuation(fx);	    	    
	    	    
			    if(results == null || results.size() < 1) { 
			    	log.warn("No Asset Result in [{}]", fiInst.getExpoId());
			    	break;
			    }    		    
	    	    
	    	    for(KicsAssetResult rst : results) {		    	    	
	    	    	
	    	    	rst.setScenNum(singleScenarioIndex);
	    	    	rst.setScenName(EScenarioAttr.ST.getAlias());
	    	    	rst.setScenType(EScenarioAttr.ST.name());
	    	    	rst.setLastModifiedBy(JOB_GCFG_ID);	    
	    	    	rst.setLastUpdateDate(LocalDate.now());
	
	    	    	//log.info("Result: {}", rst.toString());
	    	    	session.save(rst);
	    	    	count++;
	    	    }	            	           	            
	    	    iii++;	  
	    	}	    	
	    	catch(Exception ex) {
	    		ex.printStackTrace();
			    errInstrument.add(inst);
			    exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.ST.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());
		        jjj++;	    		
	    	}
	    	if(iii % 100 == 0) log.info("Instrument is processed {}/{}", iii, instruments.size());	    	
	    }
	    log.info("Instrument is processed {}/{}", iii, instruments.size());
		session.getTransaction().commit();				
		
		log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.ST.getAlias());		
	    log.info("Inforce: {}, Success: {}, Failure: {}", instruments.size(), iii, jjj);
	    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
		
	}


	private static void setIrScenarioForCalibration() {		
		
		try {
			//calibrationCurveHis = IrScenarioDao.getEntitiesMap(baseDate, EIrScenario.CALIBRATION.getScenNum(), irModelId);
			calibrationCurveHis = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), EIrScenario.CALIBRATION.getScenNum(), irModelId);		
			
//			calibrationCurveHis.entrySet().forEach(s-> log.info("cal Curve : {},{}", s.getKey(), s.getValue()));
			
			if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse()) {				
				
				for(String fx : irScenarioFxCurrencyList) {			
					
					String fxCurveId =  irModelId.substring(0,1) + fx + irModelId.substring(4, 8);					
					calibrationCurveHisFx = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), EIrScenario.CALIBRATION.getScenNum(), fxCurveId);
					
					log.info("FX Curve Id and size : {}, {}", fxCurveId, calibrationCurveHisFx.size());
					
					if(calibrationCurveHisFx.isEmpty()) {
						log.warn("Set IR Calibration: Interest Rate Shock Scenario of Foreign Currency [{}] is Empty", fx);
					}
					else calibrationCurveHisFxMap.put(fx, calibrationCurveHisFx);
				}
				
				if(calibrationCurveHisFxMap.isEmpty()) {					
					log.warn("Set IR Calibration: Interest Rate Shock Scenario of Foreign Currency is Empty [{}]", irScenarioFxCurrencyList);
					irScenarioFxYn = "N";
				}
				if(!calibrationCurveHisFxMap.containsKey(irScenarioFxDefault)) {
					log.warn("Set IR Calibration: Interest Rate Shock Scenario of Default Foreign Currency [{}] is Empty", irScenarioFxDefault);
					irScenarioFxYn = "N";						
				}
			}						
			//log.info("{}, {}, {}", irScenarioFxYn, calibrationCurveHisFxMap, calibrationCurveHisFxMap.keySet());
		}
		catch (Exception e) {
			log.error("Set IR Calibration: Check the TABLE [Q_IC_FSS_SCEN]: {}", e);			
			System.exit(0);
		}
		
//		calibrationCurveHisFxMap.entrySet().forEach(s->log.info("zzzzzz : {},{}", s.getKey(), s.getValue()));
	}

	
	private static void setIrScenario(List<Integer> irScenarioIndexList) {
		
		for(Integer idx : irScenarioIndexList) {
			setIrScenario(idx.intValue());		
		}
	}

	
	private static void setIrScenario(int idx) {				
		
		try {
			
			Map<String, IrCurveHis> scenarioCurveHis = new HashMap<String, IrCurveHis>(); 
			scenarioCurveHis = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), idx, irModelId);
			
		    if (!scenarioCurveHis.isEmpty() && scenarioCurveHis.size() > 0) scenarioCurveHisMap.put(idx, scenarioCurveHis);		    
		    
		    Map<String, Map<String, IrCurveHis>> scenarioCurveHisFxSet = new HashMap<String, Map<String, IrCurveHis>>();
		    Map<String, IrCurveHis> scenarioCurveHisFx = new HashMap<String, IrCurveHis>();			
		    
			if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse()) {				
				
				for(String fx : irScenarioFxCurrencyList) {						
					
					String fxCurveId =  irModelId.substring(0,1) + fx + irModelId.substring(4, 8);
					scenarioCurveHisFx = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), idx, fxCurveId);
					
//					for(Map.Entry<String, IrCurveHis> entry : scenarioCurveHisFx.entrySet()) {
//						log.info(" Set IrSce :  {},{},{},{}", fxCurveId,idx, entry.getKey(), entry.getValue().getIntRate());
//					}
					
					if(scenarioCurveHisFx.isEmpty()) {
						log.warn("Set IR Valuation: Interest Rate Shock Scenario of Foreign Currency [{}] is Empty in ScenNum [{}]", fx, idx);
					}
					else scenarioCurveHisFxSet.put(fx, scenarioCurveHisFx);
				}
				
				if(scenarioCurveHisFxSet.isEmpty()) {					
					log.warn("Set IR Valuation: Interest Rate Shock Scenario of Foreign Currency is Empty in ScenNum [{}]", idx);
					irScenarioFxYn = "N";
				}
				if(!scenarioCurveHisFxSet.containsKey(irScenarioFxDefault)) {
					log.warn("Set IR Valuation: Interest Rate Shock Scenario of Default Foreign Currency [{}] is Empty in ScenNum [{}]", irScenarioFxDefault, idx);
					irScenarioFxYn = "N";
				}				
				
				//log.info("{}", scenarioCurveHisFxSet);
				scenarioCurveHisFxMap.put(idx, scenarioCurveHisFxSet);				
			}
			//log.info("{}, {}, {}, {}", irScenarioFxYn, scenarioCurveHisFxMap, scenarioCurveHisFxMap.keySet(), scenarioCurveHisFxSet.keySet());
			
		}		
		catch (Exception e) {
			log.error("Interest Rate Scenario: Check the TABLE [Q_IC_FSS_SCEN]: {}", e);			
			System.exit(0);			
		}
	}	

	
	private static void setStockScenario() {		
		//stkScenarioMap = KicsShckStkDao.getEntitiesMap(baseDate, stkModelId);		
		//for(Map.Entry<String, KicsShckStk> scen : stkScenarioMap.entrySet()) System.out.println(scen.toString());		
	}		

	/**
	 * TODO:
	 */
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
	
	
	public static String setType(String instType, String instDtls) {
		
		if(instType == null || instDtls == null) {
			return EInstrument.INST_NON.toString();
		}		
		//return GeneralUtil.leftPad(instType,  1) + GeneralUtil.leftPad(instDtls, 2);
		return instType + instDtls;
	}
	
	
	public static double round(double number, int decimalDigit) {
		if(decimalDigit < 0) return Math.round(number);
		return Double.parseDouble(String.format("%." + decimalDigit + "f", number));
	}		
	
	
	public static String format(Double number) {		
		return ((number > 0) ? "+" : "") + String.valueOf(round(number, 4));
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
	    	log.info("------------------------");
	    	Map<String, Map<String, Double>> hisMap4 = new TreeMap<String, Map<String, Double>>();
	    	hisMap4 = IrCurveHisDao.getEntitiesMap3("1111111", "20170101", "20171231");
	    	//hisMap4 = IrCurveHisDao.getEntitiesMap3("1111111", "20171226", "20171228"); 
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
