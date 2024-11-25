package com.gof.process;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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
	          
	          // calibration  금리시나리오 적용 
//	          applyIrScenario( inst,  fiInst, EIrScenario.CALIBRATION.getScenNum(), instCode);
		      //  24.11.22 sy add 시나리오, 통화커브별 금리시나리오(케이스에 상관없이 통화별 커브를 채워두기 )
	      		setIrCurveFromScenFxHisMap (inst, fiInst, EIrScenario.CALIBRATION.getScenNum());
	          
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
		    		
		            for(Map.Entry<Integer, Map<String, IrCurveHis>> scenarioCurveHis : scenarioCurveHisMap.entrySet()) {	          
//		            	log.info("Valuation in Main : {},{}", scenarioCurveHis.getKey(), scenarioCurveHis.getValue().get("M0012"));
	
		        		//  24.11.22 sy add 시나리오, 통화커브별 금리시나리오(케이스에 상관없이 통화별 커브를 채워두기 )
		        		setIrCurveFromScenFxHisMap (inst, fiInst, scenarioCurveHis.getKey());
			    	
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

	//  24.11.22 sy add 시나리오, 통화커브별 금리시나리오 
	private static void setIrCurveFromScenFxHisMap (KicsAsset inst, Instrument fiInst, int scenNum) {
		
		// 평가에 필요한 통화코드 세트 가져오기 (자산유형에 따라 1개 이상의 서로 다른 통화코드별 세트가 필요할 수 있음 )
		Set<String> crnySet = inst.getCrnySet();
	    
		// 통화별로 가져오기 
		for (String crnyCd : crnySet) {
			
			Map<String, IrCurveHis> curveHis = scenarioCurveHisFxMap.getOrDefault(scenNum, Collections.emptyMap()).get(crnyCd);
			Map<String, IrCurveHis> curveHisAply = (curveHis == null || curveHis.isEmpty()) 
				    ? scenarioCurveHisFxMap.getOrDefault(scenNum, Collections.emptyMap()).get(irScenarioFxDefault) //Instrument.DEF_CURRENCY  
				    : curveHis;
			
			if (curveHisAply != null && !curveHisAply.isEmpty()) {
			    try {
			        // 커브를 설정
			        fiInst.setIrScenarioEntities(scenNum, crnyCd, curveHisAply, impliedSpreadMap.get(inst.getExpoId()));
			    } catch (Exception e) {
			        e.printStackTrace(); 
			    }
			}	
		}
	}
}
