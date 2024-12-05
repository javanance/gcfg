package com.gof.instrument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetAcco;
import com.gof.entity.KicsAssetFide;
import com.gof.entity.KicsAssetLoan;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsAssetSecr;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.KicsAsset;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@Slf4j
public abstract class InstrumentAbstract implements Instrument {	
	
	public abstract String getExpoId() throws Exception;	
	
	public void setInstrumentEntities(KicsAsset entity) throws Exception {

		if(     entity instanceof KicsAssetSecr) setInstrumentEntities((KicsAssetSecr) entity);		
		else if(entity instanceof KicsAssetFide) setInstrumentEntities((KicsAssetFide) entity);
		else if(entity instanceof KicsAssetLoan) setInstrumentEntities((KicsAssetLoan) entity);
		else if(entity instanceof KicsAssetAcco) setInstrumentEntities((KicsAssetAcco) entity);
		else {
			log.error("Check the Asset Entity: {}", entity.getExpoId());
			throw new Exception("Check the Asset Entity");
		}
	}
	
//	public abstract void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;
//	public abstract void setIrScenarioFxCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;
	
	// 24.11.22 sy add 시나리오, 통화커브별 금리시나리오
	public abstract void setIrScenarioEntities(Integer scenNum,String crnyCd, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;
	
	public abstract void setFxScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception;	
	
	public abstract void setStockScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception;	
	
	public void setImpliedMaturity(Double impliedMaturityDays) throws Exception {}	
	
	public abstract List<KicsAssetResult> getValuation(boolean toOriginalCurrency) throws Exception;	

	public List<KicsAssetResult> getValuation() throws Exception {return getValuation(TO_ORIGINAL_CURRENCY);}	
	
	protected void setInstrumentEntities(KicsAssetSecr entity) throws Exception {}
	
	protected void setInstrumentEntities(KicsAssetFide entity) throws Exception {}
	
	protected void setInstrumentEntities(KicsAssetLoan entity) throws Exception {}
	
	protected void setInstrumentEntities(KicsAssetAcco entity) throws Exception {}	
	
	
	protected KicsAssetResult cloneEntity(KicsAssetResult sourceEntity) throws CloneNotSupportedException {
		
		KicsAssetResult cloneEntity = sourceEntity.clone();
		return cloneEntity;				
	}
	
	/**
	 * 자산 인스트루먼트의 속성에 따라 연산에 필요한 날짜 배열을 미리 생성.
	 *
	 * @param issueDt 발행일
	 * @param matDt 만기일
	 * @param pmtTerm 지급주기
	 * @param pmtTermType 지급주기 유형
	 * @param direction 지급 방향 (true: 선지급(발행일부터 +1*지급주기 ), false: 후지급(만기일부터 -1* 지급주기 ))
	 * @return 연산에 필요한 날짜 배열
	 * @throws IllegalArgumentException 발행일이 만기일보다 늦을 경우
	 */
	protected List<LocalDate> generateCashflowArray(LocalDate issueDt, LocalDate matDt, int pmtTerm, char pmtTermType, boolean direction) throws Exception {		

		List<LocalDate> cfDate = new ArrayList<LocalDate>();
		
	    if(issueDt.isAfter(matDt)) {
	    	log.error("IssueDate cannot be larger than MaturityDate: [{}]", this.getExpoId());	    	
	    	return null;
	    }
	    if(pmtTerm <= 0) pmtTerm = Integer.MAX_VALUE;
	    
	    if(direction) {		// 이자지급방식: 선지급방식 (발행일로부터 이자를 계산함)     
	    	int cnt =1;
	    	LocalDate thisDate = issueDt;
	    	cfDate.add(thisDate);
		    switch(pmtTermType) {		    
		    
			    case TIME_UNIT_MONTH:			    	
//			    	while(!thisDate.plusMonths(pmtTerm).isAfter(matDt)) {		    		
//			    		thisDate = thisDate.plusMonths(pmtTerm);
//			    		cfDate.add(thisDate);		    		
//			    	}
			    	
			    	while(!issueDt.plusMonths(pmtTerm * cnt).isAfter(matDt)) {
			    		cfDate.add(matDt.plusMonths(pmtTerm * cnt));
			    		cnt = cnt +1;
			    	}
			    	
			    	if(!cfDate.contains(matDt)) cfDate.add(matDt);
			    	return cfDate;
			    	
			    case TIME_UNIT_YEAR:
			    	while(!thisDate.plusYears(pmtTerm).isAfter(matDt)) {		    		
			    		thisDate = thisDate.plusYears(pmtTerm);
			    		cfDate.add(thisDate);		    		
			    	}
			    	if(!cfDate.contains(matDt)) cfDate.add(matDt);
			    	return cfDate;			    			    	

			    default:
			    	log.warn("Unidentified Payment Term Type [{}]: [{}]", pmtTermType, this.getExpoId());
			    	return cfDate;			    			    	
		    }	    			    
	    }
	    else { // 이자지급방식: 후지급방식 (만기일로부터 거꾸로 이자를 계산함)     
	    	int cnt =1;
	    	LocalDate thisDate = matDt;
	    	cfDate.add(thisDate);
		    switch(pmtTermType) {		    
		    
			    case TIME_UNIT_MONTH:			    	
//			    	while(!thisDate.minusMonths(pmtTerm).isBefore(issueDt)) {
//			    		thisDate = thisDate.minusMonths(pmtTerm);
//			    		cfDate.add(thisDate);		    		
//			    	}
			    	while(!matDt.minusMonths(pmtTerm * cnt).isBefore(issueDt)) {
			    		cfDate.add(matDt.minusMonths(pmtTerm * cnt));
			    		cnt = cnt +1;
			    	}
			    	if(!cfDate.contains(issueDt)) cfDate.add(issueDt);
			    	return cfDate.stream().sorted().collect(Collectors.toList());
			    	
			    case TIME_UNIT_YEAR:
			    	while(!thisDate.minusYears(pmtTerm).isBefore(issueDt)) {		    		
			    		thisDate = thisDate.minusYears(pmtTerm);
			    		cfDate.add(thisDate);		    		
			    	}
			    	if(!cfDate.contains(issueDt)) cfDate.add(issueDt);
			    	return cfDate.stream().sorted().collect(Collectors.toList());

			    default:			    	
			    	log.warn("Unidentified Payment Term Type [{}]: [{}]", pmtTermType, this.getExpoId());
			    	return cfDate;		    	
		    }	    		    	
	    }	    
	}
	
	/**
	 * 자산 인스트루먼트의 속성에 따라 연산에 필요한 날짜 배열을 미리 생성. firstCouponDate 로부터 만기까지.
	 * 
	 * @param issueDt 발행일
	 * @param matDt 만기일
	 * @param firstCouponDate **첫번째 쿠폰 날짜**
	 * @param pmtTerm 지급 주기
	 * @param pmtTermType 지급 주기 유형
	 * @param direction 방향 (기본적으로 true 발행일부터 순차적으로 처리하나 firstCouponDate의 날짜가 유효하지 않은 경우 direction 설정에 따라 산출함. )
	 * @return 날짜 배열
	 * @throws Exception 예외 발생 시
	 */
	protected List<LocalDate> generateCashflowArray(LocalDate issueDt, LocalDate matDt, LocalDate firstCouponDate, int pmtTerm, char pmtTermType, boolean direction) throws Exception {		
		
		List<LocalDate> cfDate = new ArrayList<LocalDate>();
		
	    if(issueDt.isAfter(matDt)) {	    	
	    	log.error("IssueDate cannot be larger than MaturityDate: [{}]", this.getExpoId());
	    	return null;
	    }
	    // firstCouponDate 데이터 오류 (발행일 이전에 쿠폰발행, 만기 이후에 쿠폰발행) : firstCouponDate를 무시하고 현금흐름 날짜 배열 생성 (default) 
	    if(firstCouponDate == null || issueDt.isAfter(firstCouponDate) || matDt.isBefore(firstCouponDate)) {
	    	return generateCashflowArray(issueDt, matDt, pmtTerm, pmtTermType, direction);
	    }
	    
	    // 지급기간이 0 이하인 경우 (데이터 오류) default : 1200 
	    if(pmtTerm <= 0) pmtTerm = 1200;	    	    	
	    	
    	cfDate.add(issueDt);
    	
    	LocalDate thisDate = firstCouponDate;
    	cfDate.add(thisDate);
	    switch(pmtTermType) {		    
	    
		    case TIME_UNIT_MONTH:			    	
		    	while(!thisDate.plusMonths(pmtTerm).isAfter(matDt)) {		    		
		    		thisDate = thisDate.plusMonths(pmtTerm);
		    		cfDate.add(thisDate);		    		
		    	}
		    	if(!cfDate.contains(matDt)) cfDate.add(matDt);
		    	return cfDate;
		    	
		    case TIME_UNIT_YEAR:
		    	while(!thisDate.plusYears(pmtTerm).isAfter(matDt)) {		    		
		    		thisDate = thisDate.plusYears(pmtTerm);
		    		cfDate.add(thisDate);		    		
		    	}
		    	if(!cfDate.contains(matDt)) cfDate.add(matDt);
		    	return cfDate;			    			    	

		    default:		    	
		    	log.warn("Unidentified Payment Term Type [{}]: [{}]", pmtTermType, this.getExpoId());
		    	return cfDate;
	    }	    
	}
	
	/**
	 * 만기별 할인율 배열 기반으로 현금흐름의 현재가치 산출.
	 *
	 * @param matTerm 만기 배열 []
	 * @param dcntRate 할인율 배열 []
	 * @param baseDate 기준일자 
	 * @param dates 현금흐름이 발생하는 날짜 배열 []
	 * @param cashflows 현금흐름 배열 []
	 * @param dcntCmpdMtd 할인율 복리계산 방식
	 * @param dcntCmpdPeriod 할인율 복리계산 기간
	 * @param dcntCmpdPeriodType 할인율 복리계산 기간 단위
	 * @param dayCountBasis 일수 계산 방식
	 * @return 현재가치
	 */
	protected double getPresentValue(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, double[] cashflows, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) throws Exception {
		
		double pv = 0.0;
		double[] discountFactor = TimeUtil.getDiscountFactor(matTerm, dcntRate, baseDate, dates, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
		
//		log.info("PV aaaaaaa : {}", discountFactor);
		
		for(int i=0; i<discountFactor.length; i++) {
			
			if(dates[i].isAfter(baseDate)) {
				pv += discountFactor[i] * cashflows[i];				
			}			
//			log.info("PV in Inst 1 :  {}, {}, {}, {}, {},{},{}",  i, dates[i], cashflows[i],dcntRate[i], discountFactor[i] ,  pv);
//			log.info(  "PV in Inst 1 :  i: {}, date: {}, cashflow: {}, dcntRate: {}, discountFactor: {}, pv: {}",  i, dates[i], cashflows[i], dcntRate[i], discountFactor[i], pv);
			if (i != 0) {
//			    log.info("i: {}, date: {}, cashflow: {}, dcntRate: {}, discountFactor: {}, pv: {}", i, dates[i], cashflows[i], dcntRate[i], discountFactor[i], pv);
			}
		}		
		
//		log.info("PV in Inst : {}", pv);
		
		return pv;
    }    
	
	/**
	 * ytm 기반으로 현금흐름의 현재가치 산출.
	 *
	 * @param ytm 할인율(YTM) - 할인에 사용할 Yield to Maturity 값
	 * @param baseDate 기준일자 
	 * @param dates 현금흐름이 발생하는 날짜 배열 []
	 * @param cashflows 현금흐름 배열 []
	 * @param dcntCmpdMtd 할인율 복리계산 방식
	 * @param dcntCmpdPeriod 할인율 복리계산 기간
	 * @param dcntCmpdPeriodType 할인율 복리계산 기간 단위
	 * @param dayCountBasis 일수 계산 방식
	 * @return 현재가치
	 */
    protected double getPresentValue(double ytm, LocalDate baseDate, LocalDate[] dates, double[] cashflows, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) throws Exception {
    	
    	double pvYtm = 0.0;
    	double[] discountFactor = TimeUtil.getDiscountFactor(ytm, baseDate, dates, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
    	
    	for(int i=0; i<discountFactor.length; i++) {
    	
    		if(dates[i].isAfter(baseDate)) {
    			pvYtm += discountFactor[i] * cashflows[i];
    		}
    	}
//    	log.info("PV : {}", pvYtm);
    	return pvYtm;    	
    }
    
	
	protected double getEffectiveDuration(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, double[] cashflows, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) throws Exception {
	    
	    double pvZero     = getPresentValue(matTerm, dcntRate, baseDate, dates, cashflows, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);	    
	    double pvPositive = getPresentValue(matTerm, addSpread(dcntRate,  EFFECTIVE_DURATION_BP), baseDate, dates, cashflows, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);	    	    
	    double pvNegative = getPresentValue(matTerm, addSpread(dcntRate, -EFFECTIVE_DURATION_BP), baseDate, dates, cashflows, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);	    

	    if(Math.abs(pvZero) < ZERO_DOUBLE) {
	    	
	    	log.warn("Check the Instrument in calculating EffectiveDuration: [{}]", this.getExpoId());	    	
	    	return 0.0;
	    }
	    return (pvPositive - pvNegative) / (-2.0 * pvZero * EFFECTIVE_DURATION_BP);		
	}	
	
	
	protected double getMacaulayDuration(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, double[] cashflows, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) throws Exception {

		double numerator   =  getTimeWeightedPresentValue(matTerm, dcntRate, baseDate, dates, cashflows, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
		double denominator =  getPresentValue(matTerm, dcntRate, baseDate, dates, cashflows, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
		
		if(Math.abs(denominator) < ZERO_DOUBLE) {	
			
			log.warn("Check the Instrument in calculating MacaulayDuration: [{}]", this.getExpoId());
			return 0.0;
		}
		return numerator / denominator;
	}
	
	
	protected double getTimeWeightedPresentValue(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, double[] cashflows, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) throws Exception {

		double timePv = 0.0;
		double[] weightedDiscountFactor = TimeUtil.getTimeWeightedDiscountFactor(matTerm, dcntRate, baseDate, dates, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
		
		for(int i=0; i<weightedDiscountFactor.length; i++) {
			
			if(dates[i].isAfter(baseDate)) {
				timePv += weightedDiscountFactor[i] * cashflows[i];
			}
		}		
		return timePv;
	}
    
    
	protected double getEffectiveMaturity(LocalDate baseDate, LocalDate[] dates, double[] cashflows, int dayCountBasis) throws Exception {
    	
    	double numerator   = 0.0;
    	double denominator = 0.0;
    	
    	double[] timeFactor = TimeUtil.getTimeFactor(baseDate, dates, dayCountBasis);
    	
		for(int i=0; i<timeFactor.length; i++) {			
			
			if(timeFactor[i] > ZERO_DOUBLE) {
				numerator   += timeFactor[i] * cashflows[i];
				denominator += cashflows [i];
			}
		}		
		
		if(Math.abs(denominator) < ZERO_DOUBLE) {			
			log.warn("Check the cashflows in calculating Effective Maturity: [{}]", this.getExpoId());
			return 0.0;
		}				
		//return Math.max(numerator / denominator, IMPLIED_MAT_TOL);		
		return numerator / denominator;
    }
  
	
    protected double getAccruedInterest(LocalDate baseDate, LocalDate[] dates, double[] cashflows, int dayCountBasis) throws Exception {		
    	
    	double accruedInterest = 0.0;
		double tfNumerator     = 0.0;
		double tfDenominator   = 0.0;
		int   addOneDay        = 1;
		
		if(baseDate.isBefore(dates[0])) return 0.0;		
		
		for(int i=1; i<dates.length; i++) {			
			
		    if(baseDate.isBefore(dates[i])) {		    	
		    	//tfNumerator = TimeUtil.getTimeFactor(dates[i-1].minusDays(0), baseDate.plusDays(0), dayCountBasis);		    	
		    	//if((dayCountBasis == EDayCountBasis.A30_360.getDcbCode() || dayCountBasis == EDayCountBasis.E30_360.getDcbCode()) && baseDate.lengthOfMonth() == 31) addOneDay = 1;
		    	//if(baseDate.lengthOfMonth() == 31) addOneDay = 1;
		    	
		    	tfNumerator   = TimeUtil.getTimeFactor(dates[i-1].minusDays(0), baseDate.plusDays(addOneDay), dayCountBasis);
		    	tfDenominator = TimeUtil.getTimeFactor(dates[i-1], dates[i], dayCountBasis);		    	
		    	
		    	if(Math.abs(tfDenominator) < ZERO_DOUBLE) {
		    		log.warn("Check the cashflows in calculating Accrued Interest: [{}]", this.getExpoId());
		    		return 0.0;
		    	}		    	
		    	accruedInterest = cashflows[i] * tfNumerator / tfDenominator;		    	
		    	return accruedInterest;
		    }		    
		}		
    	return accruedInterest;
    }    
    
    
	protected String setResultDefineMap(Integer resultCode) {

		Map<Integer, String> resultMap = new LinkedHashMap<Integer, String>();		
		
		resultMap.put(FE_PRINCIPAL_CF,          "PRINCIPAL_CF"         );
		resultMap.put(FE_INTEREST_CF,           "INTEREST_CF"          );
		resultMap.put(FE_YIELD_TO_MATURITY,     "YTM"                  );
		resultMap.put(FE_PV_DIRTY,              "PV_DIRTY"             );
		resultMap.put(FE_ACCRUED_INTEREST,      "ACCRUED_INT"          );
		resultMap.put(FE_PV_CLEAN,              "PV_CLEAN"             );
		resultMap.put(FE_EFFECTIVE_MATURITY,    "EFFE_MATURITY"        );
		resultMap.put(FE_EFFECTIVE_DURATION,    "EFFE_DURATION"        );
		resultMap.put(FE_MODIFIED_DURATION,     "MODI_DURATION"        );								
		resultMap.put(FE_BS_AMOUNT,             "BS_AMOUNT"            );
		resultMap.put(FE_ACCRUED_INT_BS,        "BS_ACCRUED"           );
		resultMap.put(FE_IRATE_EXPOSURE,        "IRATE_EXPO"           );
		resultMap.put(FE_FXRT_EXPOSURE,         "FXRT_EXPO"            );
		resultMap.put(FE_STOCK_EXPOSURE,        "STOCK_EXPO"           );
		resultMap.put(FE_IMPLIED_SPREAD,        "IMP_SPREAD"           );
		resultMap.put(FE_IMPLIED_MATURITY,      "IMP_MAT"              );
		resultMap.put(FE_IMPLIED_SIGMA,         "IMP_SIGMA"            );
		
		resultMap.put(FE_FWDFUT_PRICE,          "FORWARD_PRICE"        );
		resultMap.put(FE_KTB_FORWARD_RATE,      "KTB_FWDRATE"          );		
		resultMap.put(FE_DPDR,                  "DPDR"                 );		
				
		return (resultMap.get(resultCode) != null ? resultMap.get(resultCode) : null);		
    }	
	
	
	protected String setLegDefineMap(Integer legCode) {

		Map<Integer, String> legMap = new LinkedHashMap<Integer, String>();
		
		legMap.put(REC_LEG_KEY, REC_LEG_NAME);
		legMap.put(PAY_LEG_KEY, PAY_LEG_NAME);    	
		legMap.put(NET_LEG_KEY, NET_LEG_NAME);
				
		return (legMap.get(legCode) != null ? legMap.get(legCode) : null);		
    }
	
	
//    protected double getRangeAccrualFactor(int indexType, double floorRate, double ceilRate, LocalDate baseDate, LocalDate[] dates, double[] cashflows, int dayCountBasis) {    	
//    	
//    	int inRange = 0;
//    	double rangeAccuralFactor = 0.0;    	
//		LocalDate lastIntDate = null;
//		LocalDate nextIntDate = null;
//
//		if(baseDate.isBefore(dates[0])) return 0.0;		
//		
//		for(int i=1; i<dates.length; i++) {			
//			
//		    if(baseDate.isBefore(dates[i])) {
//		    	lastIntDate = dates[i-1];
//		    	nextIntDate = dates[i];		    	
//		    }
//		}				
//		if(lastIntDate == null || nextIntDate == null) return rangeAccuralFactor;    	
//		
//    	switch(indexType) {
//    	
//	    	case 1: {	    		
//		    	List<IrCurveHis> rst = IrCurveHisDao.getEntities("A100", "M0003", lastIntDate, nextIntDate);
//		    	
//		    	for(IrCurveHis aa : rst) {
//		    		if(aa.getIntRate() <= ceilRate && aa.getIntRate() >= floorRate) inRange++;		    		
//		    	}
//		    	return inRange; 		
//	    	}
//	    	default: return rangeAccuralFactor;
//    	}	    			
//    }    
    
    /**
     * add 내재스프레드 
     * */
	protected double[] addSpread(double[] dcntRate, double spread) {    	
    	
    	double[] newDcntRate = new double[dcntRate.length];    	
    	for(int i=0; i<dcntRate.length; i++) newDcntRate[i] = dcntRate[i] + spread;
    	
    	return newDcntRate;
    }	

	
	protected boolean isCorrectDate(String dateString) throws Exception {
		
		LocalDate date;		
		LocalDate maxDate = TimeUtil.stringToDate(ABSOLUTE_MAX_DATE);
		LocalDate minDate = TimeUtil.stringToDate(ABSOLUTE_MIN_DATE);		
		
		if (TimeUtil.isSettableDay(dateString)) {
			date = TimeUtil.stringToDate(dateString);
		}
		else return false;
		
		if(date.isBefore(minDate) || date.isAfter (maxDate)) {
			log.debug("Input Date is inadequate in [EXPO_ID: {}]", this.getExpoId());
			return false;
		}		
		return true;
	}
	
	
	protected boolean isCorrectDate(LocalDate date) throws Exception {
		
		LocalDate maxDate = TimeUtil.stringToDate(ABSOLUTE_MAX_DATE);
		LocalDate minDate = TimeUtil.stringToDate(ABSOLUTE_MIN_DATE);		
		
		if(date == null) return false;
		
		if(date.isBefore(minDate) || date.isAfter (maxDate)) {
			log.debug("Input Date is inadequate in [EXPO_ID: {}]", this.getExpoId());
			return false;
		}		
		return true;
	}	
	
	
	protected LocalDate setDate(String dateString, LocalDate defaultDate) throws Exception {
		
		if(isCorrectDate(dateString)) {
			return TimeUtil.stringToDate(dateString);
		}
		else if (isCorrectDate(defaultDate)) {
			return defaultDate;
		}
		else {
			return LocalDate.now();
		}
	}
	
	
	protected LocalDate setDate(String dateString) throws Exception {
		
		if(isCorrectDate(dateString)) {
			return TimeUtil.stringToDate(dateString);		
		}
		else {
			return LocalDate.now();
		}
	}
	
	
	protected LocalDate setDateOrNull(String dateString) throws Exception {
		
		if(isCorrectDate(dateString)) return TimeUtil.stringToDate(dateString);
		else return null;
	}	
	
}