package com.gof.process;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Session;

import com.gof.dao.KicsFssScenDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsShckStk;
import com.gof.enums.EIrScenario;
import com.gof.enums.EScenarioAttr;
import com.gof.instrument.DerivativesAbstract;
import com.gof.enums.ERunArgument;
import com.gof.enums.EBoolean;
import com.gof.enums.EInstrument;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.KicsAsset;
import com.gof.util.GeneralUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JobProcessor {
	
	private static Session session = Main.session;	
	private static String baseDate = Main.baseDate;
	private static String JOB_GCFG_ID = Main.JOB_GCFG_ID;
	
    private static List<KicsAsset> errInstrument = new ArrayList<KicsAsset>();
    private static Map<String, String> exceptions = new HashMap<String, String>();
//    private static List<KicsAssetResult> results;    
	
	private static List<Integer> irScenarioIndexList = Main.irScenarioIndexList;
	
	private static Map<Integer, Map<String, IrCurveHis>> scenarioCurveHisMap = new HashMap<Integer, Map<String, IrCurveHis>>();
	
	private static Map<Integer, Map<String, Map<String, IrCurveHis>>> scenarioCurveHisFxMap = new HashMap<Integer, Map<String, Map<String, IrCurveHis>>>();
	
	private static Map<String, Double> impliedSpreadMap = new HashMap<String, Double>();    
	private static Map<String, Double> impliedMaturityMap = new HashMap<String, Double>();
	
	//////////////////////////////////////
	private static String irModelId = Main.irModelId;
	private static String irScenarioFxYn = Main.irScenarioFxYn;
	private static String irScenarioFxDefault = Main.irScenarioFxDefault; 	
	private static List<String> irScenarioFxCurrencyList = Main.irScenarioFxCurrencyList;	
	//////////////////////////////////////	
	
	private static boolean fx = false;

	
  public static void jobForCalibration(List<KicsAsset> instruments) {
	  // 1. base 금리시나리오 가져오기 
	  setIrScenario(EIrScenario.CALIBRATION.getScenNum());	
	  
	  // 2.평가로직 
	  int iii = 0, jjj = 0;        
	  Integer count = 1;
	  LocalTime startTime = LocalTime.now();
	
	  for (KicsAsset inst : instruments) {
	      try {
	          Instrument fiInst = getFinancialInstrument(inst);
	          
	          // 자산유형별 금리 커브 설정 
	          Integer instCode  = GeneralUtil.objectToPrimitive(EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstCode());

	          // calibration  금리시나리오 적용 
	          applyIrScenario( inst,  fiInst, EIrScenario.CALIBRATION.getScenNum(), instCode);
	          List<KicsAssetResult> results = fiInst.getValuation(fx);
	
	          if (results == null || results.size() < 1) {
	              log.warn("No Asset Result in [{}]", fiInst.getExpoId());
	              break;
	          }
	          // calibration 산출결과 
	          saveResults(results, count, inst, EIrScenario.CALIBRATION);
	          
	          // 내재스프레드산출 
	          saveImpliedResults(results, count, inst);
	          
	          iii++;
	      } catch (Exception ex) {
	          handleException(inst, ex);
	          jjj++;
	      }
	      if (iii % 100 == 0) 
		      log.info("Instrument is processed {}/{}", iii, instruments.size());
	  	}
	      log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.CL.getAlias());
	      log.info("Inforce: {}, Success: {}, Failure: {}", instruments.size(), iii, jjj);
	      log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(), ChronoUnit.MILLIS) * 0.001);
	      
	      for(Map.Entry<String, String> elem : exceptions.entrySet()) log.warn("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
  	}

	public static void jobForIrScenario(List<KicsAsset> instruments) {
		
		// 1.금리시나리오 가져오기 
			setIrScenario(irScenarioIndexList);	
			
		// 2.평가로직
		    int iii = 0, jjj = 0;	    
		    Integer count = 1;
		    LocalTime startTime = LocalTime.now();
		    
		    for(KicsAsset inst : instruments){
		    	
		    	try {	    		
		    		Instrument fiInst = getFinancialInstrument(inst);
		    		
		    		// 자산유형별 금리 커브 설정 
		    		Integer instCode  = GeneralUtil.objectToPrimitive(EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstCode());
		    		
		            for(Map.Entry<Integer, Map<String, IrCurveHis>> scenarioCurveHis : scenarioCurveHisMap.entrySet()) {	          
//		            	log.info("Valuation in Main : {},{}", scenarioCurveHis.getKey(), scenarioCurveHis.getValue().get("M0012"));
	
		            	// inst 유형별 금리 시나리오 설정.
		            	applyIrScenario( inst,  fiInst, scenarioCurveHis.getKey(), instCode);
			    	
		            	fiInst.setImpliedMaturity(impliedMaturityMap.get(inst.getExpoId()));
		            	List<KicsAssetResult> results = fiInst.getValuation(fx);	    	    
			    	    
			    	    if(results == null || results.size() < 1) { 
					    	log.warn("No Asset Result in [{}]", fiInst.getExpoId());
					    	break;
					    }
			    	    saveResults(results, count, inst,EIrScenario.getEIrScenario(scenarioCurveHis.getKey()));
			    	    	    	    
		            }	            	           	            
		    	    iii++;	    		
		    	}
		    	catch(Exception ex) {
		    		handleException(inst, ex);
	    	        jjj++;	    		
		    	}
		    	if(iii % 100 == 0) 
		    	log.info("Instrument is processed {}/{}", iii, instruments.size());	    	
		    }
			log.info("Job_Valuation is Completed !: [JOB TYPE: {}]", EScenarioAttr.IR.getAlias());		
		    log.info("Inforce: {}, Success: {}, Failure: {}", instruments.size(), iii, jjj);
		    log.info("Time Elapsed: {} seconds", startTime.until(LocalTime.now(),ChronoUnit.MILLIS) * 0.001);		
			
		    for(Map.Entry<String, String> elem : exceptions.entrySet()) log.warn("ExpoId , Exception: {},{}", elem.getKey(), elem.getValue());
		}
	
	private static void setIrScenario(List<Integer> irScenarioIndexList) {
		
		for(Integer scenNum : irScenarioIndexList) {
			setIrScenario(scenNum.intValue());		
		}
	}

	
	private static void setIrScenario(int scenNum) {				
		
		try {
			
			Map<String, IrCurveHis> scenarioCurveHis = new HashMap<String, IrCurveHis>(); 
			scenarioCurveHis = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), scenNum, irModelId);
			
		    if (!scenarioCurveHis.isEmpty() && scenarioCurveHis.size() > 0) scenarioCurveHisMap.put(scenNum, scenarioCurveHis);		    
		    
		    Map<String, Map<String, IrCurveHis>> scenarioCurveHisFxSet = new HashMap<String, Map<String, IrCurveHis>>();
		    Map<String, IrCurveHis> scenarioCurveHisFx = new HashMap<String, IrCurveHis>();		
		    Map<String, IrCurveHis> defaultScenarioCurveHisFx = new HashMap<String, IrCurveHis>();		
		    
			if(EBoolean.valueOf(irScenarioFxYn).isTrueFalse()) {
				
				// 디폴트 통화 커브 ID 및 데이터 조회
				String defaultFxCurveId = irModelId.substring(0, 1) + irScenarioFxDefault + irModelId.substring(4, 8);
				defaultScenarioCurveHisFx = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), scenNum, defaultFxCurveId);
				
				for(String fx : irScenarioFxCurrencyList) {						
					
					String fxCurveId =  irModelId.substring(0,1) + fx + irModelId.substring(4, 8);
					scenarioCurveHisFx = KicsFssScenDao.getEntitiesMap(TimeUtil.toYearMonth(baseDate), scenNum, fxCurveId);
					
					if(scenarioCurveHisFx.isEmpty()) {
						log.warn("Set IR Valuation: Interest Rate Shock Scenario of Foreign Currency [{}] is Empty in ScenNum [{}]. Applying default FX Curve [{}] for Foreign Currency [{}]", fx, scenNum,irScenarioFxDefault, fx );
						scenarioCurveHisFxSet.put(fx, defaultScenarioCurveHisFx);
					}
					else scenarioCurveHisFxSet.put(fx, scenarioCurveHisFx);
				}
				
				if(scenarioCurveHisFxSet.isEmpty()) {					
					log.warn("Set IR Valuation: Interest Rate Shock Scenario of Foreign Currency is Empty in ScenNum [{}]", scenNum);
					irScenarioFxYn = "N";
				}
				if(!scenarioCurveHisFxSet.containsKey(irScenarioFxDefault)) {
					log.warn("Set IR Valuation: Interest Rate Shock Scenario of Default Foreign Currency [{}] is Empty in ScenNum [{}]", irScenarioFxDefault, scenNum);
					irScenarioFxYn = "N";
				}				
				
//				log.info("{}", scenarioCurveHisFxSet);
				scenarioCurveHisFxMap.put(scenNum, scenarioCurveHisFxSet);
				//log 
//		        for (Map.Entry<Integer, Map<String, Map<String, IrCurveHis>>> entry : scenarioCurveHisFxMap.entrySet()) {
//		            Integer scen = entry.getKey();
//		            Set<String> crnyCd = entry.getValue().keySet();
//		            int count = entry.getValue().size();
//		            log.info("aaaa [{}], CRNY_CD : {}, Count: {}", scen, crnyCd, count);
//		        }
		        
		    }
		}		
		catch (Exception e) {
			log.error("Interest Rate Scenario: Check the TABLE [Q_IC_FSS_SCEN]: {}", e);			
			System.exit(0);			
		}
	}	
  
  private static Instrument getFinancialInstrument(KicsAsset inst) throws Exception {
      Instrument fiInst = EInstrument.getEInstrument(setType(inst.getInstTpcd(), inst.getInstDtlsTpcd())).getInstrumentModel();
      fiInst.setInstrumentEntities(inst);
  //    log.info("type: {}, Class: {}, fiInst: {}", inst.getInstTpcd() + inst.getInstDtlsTpcd(), fiInst.getClass().getSimpleName(), fiInst.getExpoId());
      return fiInst;
  }
  

  private static void saveResults(List<KicsAssetResult> results, int count, KicsAsset inst, EIrScenario scen) {
      for (KicsAssetResult rst : results) {
          rst.setScenNum(scen.getScenNum());
          rst.setScenName(scen.name());
          rst.setScenType(scen.getScenType());
//          rst.setLastModifiedBy(JOB_GCFG_ID);
          rst.setLastModifiedBy(inst.getInstTpcd()+inst.getInstDtlsTpcd());
          rst.setLastUpdateDate(LocalDate.now());

          session.save(rst);
          if (count % 10000 == 0) {
              session.flush();
              session.clear();
              count = 1;
          }
          count++;
      }
  }

  private static void saveImpliedResults(List<KicsAssetResult> results, int count, KicsAsset inst) {
      for (KicsAssetResult rst : results) {

          session.save(rst);
          if (count % 10000 == 0) {
              session.flush();
              session.clear();
              count = 1;
          }

          if (rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_SPREAD))) {
              impliedSpreadMap.put(inst.getExpoId(), rst.getValue());
          }
          if (rst.getResultType().equals(String.valueOf(Instrument.FE_IMPLIED_MATURITY))) {
              impliedMaturityMap.put(inst.getExpoId(), rst.getValue());
          }
          count++;
      }
  }
  private static void handleException(KicsAsset inst, Exception ex) {
	  ex.printStackTrace();
      log.error("ERROR: EXPO_ID : [{}], {}", inst.getExpoId(), ex);
      errInstrument.add(inst);
      exceptions.put(GeneralUtil.concatenate('|', EScenarioAttr.CL.getAlias(), inst.getExpoId(), inst.getInstDtlsTpcd(), inst.getIsinCd()), ex.toString());
  }
  


	
	private static String setType(String instType, String instDtls) {
		
		if(instType == null || instDtls == null) {
			return EInstrument.INST_NON.toString();
		}		
		return instType + instDtls;
	}
	
	private static void applyIrScenario(KicsAsset inst, Instrument fiInst, int scenNum, Integer instCode) {
	    boolean isFxScenApply = EBoolean.valueOf(irScenarioFxYn).isTrueFalse();
	    boolean isBondInst = instCode.intValue() >= Instrument.INST_BOND_ZCB && instCode.intValue() <= Instrument.INST_BOND_NON;
	    boolean isDefaultCurrency = GeneralUtil.objectToPrimitive(inst.getCrnyCd(), Instrument.DEF_CURRENCY).equals(Instrument.DEF_CURRENCY);


	    // 해당 인스트루먼트에 필요한 커브 설정.
	    // 1.  외화 & 채권 =>irCurveFromHisFxMap
	    if (isFxScenApply && isBondInst && !isDefaultCurrency) {
	        setIrCurveFromHisFxMap(inst, fiInst, scenNum);
	    	}
    	else {
    	// 2. 원화 => IrCurveFromHisMap
			setIrCurveFromHisMap(inst, fiInst, scenNum);
		}
	    	    
         if (isDerivativesScenApply(instCode)) { // 파생상품인 경우 포지션에 따른 할인율 설정을 여기에서 추가로 함.  
        	setIrCurveFromHisFxMapByPosition(inst, fiInst, scenNum);
         }
	}

	private static boolean isDerivativesScenApply(Integer instCode) {
	   return EBoolean.valueOf(irScenarioFxYn).isTrueFalse() 
	          && (instCode == Instrument.INST_FIDE_CCSWAP || instCode == Instrument.INST_FIDE_FXSWAP);
	  }
	
	private static void setIrCurveFromHisMap(KicsAsset inst, Instrument fiInst, int scenNum) {
	    Map<String, IrCurveHis> curveHis = scenarioCurveHisMap.get(scenNum);

	    try { // KDSP1000_1 : default인 경우만 설정함.
	        fiInst.setIrScenarioCurveEntities(scenNum, curveHis, impliedSpreadMap.get(inst.getExpoId()));
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	        log.error("Error setting IR Scenario Curve Entities for {}: {}", inst.getExpoId(), e);
	    }
	}

	private static void setIrCurveFromHisFxMap(KicsAsset inst, Instrument fiInst, int scenNum) {
	    String crnyCd = inst.getCrnyCd();

	    // scenarioCurveHisFxMap에서 해당 통화코드 확인
	    boolean crnyYn = false;
	    for (Map.Entry<String, Map<String, IrCurveHis>> scenCurveHisFx : scenarioCurveHisFxMap.get(scenNum).entrySet()) {
	        if (crnyCd.equals(scenCurveHisFx.getKey()) && !scenCurveHisFx.getValue().isEmpty()) {
	            crnyYn = true; // scenCurveHisFx에 해당 통화가 있으면 curveHis 에 채우기 
	            Map<String, IrCurveHis> curveHis = scenCurveHisFx.getValue();
	            try { // 할인율에 내재스프레드 반영 
					fiInst.setIrScenarioCurveEntities(scenNum, curveHis, impliedSpreadMap.get(inst.getExpoId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            break;
	        }
	    }

	    if (!crnyYn) {
	        // scenCurveHisFx에 해당 통화가 없는 경우, scenarioCurveHisFxMap에서 curveHis 가져오기 
	        Map<String, IrCurveHis> curveHis = scenarioCurveHisFxMap.get(scenNum).containsKey(irScenarioFxDefault)
	                && !scenarioCurveHisFxMap.get(scenNum).get(irScenarioFxDefault).isEmpty()
	                ? scenarioCurveHisFxMap.get(scenNum).get(irScenarioFxDefault)
	                : scenarioCurveHisMap.get(scenNum);

	        try { // 할인율에 내재 스프레드 반영 
				fiInst.setIrScenarioCurveEntities(scenNum, curveHis, impliedSpreadMap.get(inst.getExpoId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

	private static void setIrCurveFromHisFxMapByPosition(KicsAsset inst, Instrument fiInst, int scenNum) {
		DerivativesAbstract ddd = (DerivativesAbstract) fiInst;
		log.info("payCurrency :{}, recCurrency:{}",ddd.getPayCurrency(), ddd.getRecCurrency()) ;
		
	    for (Map.Entry<String, Map<String, IrCurveHis>> entry : scenarioCurveHisFxMap.get(scenNum).entrySet()) {
	    	log.info("Processing FX Curve: {}", entry.getKey());
	    	
	        if (!entry.getKey().equals("KRW") && (entry.getKey().equals(ddd.getPayCurrency()) || entry.getKey().equals(ddd.getRecCurrency()))) {
	            log.info("Processing FX Curve: {}", entry.getKey());
	            try {
					fiInst.setIrScenarioFxCurveEntities(scenNum, entry.getValue(), 0.0); 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("Error setting IR Scenario FX Curve Entities for {}: {}", inst.getExpoId(), e);
				}
	            break;
	        }
	    }
	}

}
