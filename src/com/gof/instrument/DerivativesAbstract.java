package com.gof.instrument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.gof.dao.KicsFssScenDao;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetFide;
import com.gof.entity.KicsAssetResult;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EInstrument;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public abstract class DerivativesAbstract extends InstrumentAbstract {
	
	private Session session = HibernateUtil.getSessionFactory().openSession();

	protected LocalDate   baseDate;	
	protected String      expoId;
	protected String      fundCd;
	protected String      prodTpcd;
	protected String      accoCd;
	protected String      accoNm;
	protected String      isinCd;	
	protected String      isinNm;	
	protected String      contId;
	protected String      deptCd;
	protected String      instTpcd;
	protected int         instDtlsTpcd;
	//protected String      potnTpcd;
	protected String      optTpcd;
	protected int         isLongPosition;
	protected int         isPrincipalSwap;	
	
	protected LocalDate   issueDate;
	protected LocalDate   maturityDate;	
	protected LocalDate   firstCouponDate;
	protected int         ktbMaturityYear;	
	
	protected String      recCurrency;	
	protected double      recFxRate;
	protected double      recFxRateBase;
	protected int         recDayCountBasis;			
	protected double      recAmt;	
	protected double      recIRate;
	protected String      recIrCurveId;	
	protected double      recSpreadOverCurve;
	protected int         recPaymentTerm;
	protected String      recIrateDvCd;
	protected String      recIrateTpCd;
	protected double      recAccruedInterest;
	protected double      recDividendYield;
	
	protected String      payCurrency;
	protected double      payFxRate;
	protected double      payFxRateBase;
	protected int         payDayCountBasis;	
		
	protected double      payAmt;	
	protected double      payIRate;
	protected String      payIrCurveId;
	protected double      paySpreadOverCurve;	
	protected int         payPaymentTerm;
	protected String      payIrateDvCd;
	protected String      payIrateTpCd;	
	protected double      payAccruedInterest;
	protected double      payDividendYield;
	
	protected boolean     isRealNumber;
	protected double      toRealScale;
	protected double      contractSize;
	protected double      contractMultiple;
	protected double      contractPrice;	
	protected double      spotPrice;
	protected double      spotPriceBase;
	protected double      spotPriceForNonStock;
	protected double      dividendYield;
	protected double      underlyingExercisePrice;
	protected double      underlyingSpotPrice;
	protected double      underlyingSpotPriceBase;
	protected double      optionVol;
	protected double      optionVolBase;
	
	//TODO: to be deleted...
//	protected double      recSpotPrice;
//	protected double      paySpotPrice;	
//	protected double      recSpotPriceBase;
//	protected double      paySpotPriceBase;
	
	//protected double      netBasis; //netBasis = spotPrice - theoreticalPrice

	protected double      bsAmt;
	protected double      vltAmt;
    protected double      fairBsAmt;
	protected double      accrAmt;
	protected double      uernAmt;	
	
	protected double      extUprc;
	protected double      extUprcUnit;
	protected double      extDura;    
	protected double      recExtUprc;
	protected double      payExtUprc;
	protected double      recExtDura;
	protected double      payExtDura;
	
	protected List<IrCurve>                            irCurve;
	protected Map<String, Map<String, IrCurveHis>>     irCurveHis;
	
	protected Map<String, IrCurveHis>                  scenarioCurveHis;
	protected Map<String, IrCurveHis>                  scenarioForeignCurveHis;
	
	protected Integer                                  scenNum;
	
	protected int         isRecIrCalibration;
	protected int         isPayIrCalibration;
	protected int         isStockShock;
	protected String      fxTypCd;
	protected Double      fxVal;		
	
	protected List<Integer>   financialElements;
	protected KicsAssetResult result = new KicsAssetResult();	
	
	protected LocalDate[] recPayoffDate;
	protected double[]    recPrincipalPayoffAmount;
	protected double[]    recInterestPayoffAmount;
	protected double[]    recPayoffAmount;	
	
	protected LocalDate[] payPayoffDate;
	protected double[]    payPrincipalPayoffAmount;
	protected double[]    payInterestPayoffAmount;
	protected double[]    payPayoffAmount;
	
	protected LocalDate[] netPayoffDate;                      
	protected double[]    netPrincipalPayoffAmount;
	protected double[]    netInterestPayoffAmount;
	protected double[]    netPayoffAmount;	
	                      
	protected String[]    recMatTerm;		 
	protected double[]    recMatTermIntRate;	
	protected String[]    payMatTerm;		 
	protected double[]    payMatTermIntRate;
	
	//TODO: to be deleted...
	protected String      matCd;	
	protected double      recIrCurveYield;
	protected double      payIrCurveYield;
	
	protected boolean     cfDirection;	
	protected boolean     isForward;
	
	protected int         cmpdPeriod;	
	protected char        cmpdPeriodType;
	protected char        cmpdMethod;	
	protected char        paymentTermType;
	
	protected double      impliedSpread;
	protected double      recDiscountSpread;	
	protected double      payDiscountSpread;
	protected char        dcntCmpdMtd;
	protected int         dcntCmpdPeriod;
	protected char        dcntCmpdPeriodType;	
	protected String[]    dcntRecMatTerm;		 
	protected double[]    dcntRecMatTermIntRate;	
	protected String[]    dcntPayMatTerm;		 
	protected double[]    dcntPayMatTermIntRate;
	
	
	public DerivativesAbstract(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super();
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;		
	}
	
	public DerivativesAbstract() {		
		super();
		this.scenNum          = 0;
		this.scenarioCurveHis = null;
		this.fxTypCd          = null;
		this.fxVal            = null;
	}	

//	/** TODO: 포지션별 커브를 가져와야 하는 경우 KRW (default currency가 아니면 설정을 못하고 있음.)수정 필요.
//	 * {@link DerivativesAbstract#setIrCurve()} */
//	@Override
//	public void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) {
//		
//		this.scenNum          = scenNum;
//		this.scenarioCurveHis = scenarioCurveHis;
//		this.impliedSpread    = GeneralUtil.objectToPrimitive(spread);		
//		this.setIrCurve();				
//		//log.info("IR Scenario Curve Entities have been set! SCEN_NUM: {}", this.scenNum);
//	}
	
	
//	@Override
//	public void setIrScenarioFxCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception{
//		scenarioForeignCurveHis = scenarioCurveHis;
//		
//		if(!recCurrency.equals("KRW")) {
//			setScenarioFxCurveHis("REC");
////			setIrFxCurveHis("REC");
//		}
//		if(!payCurrency.equals("KRW")) {
//			setScenarioFxCurveHis("PAY");
////			setIrFxCurveHis("PAY");
//		}
//	}
	// 24.11.22 sy add 시나리오, 통화커브별 금리시나리오 
	@Override
	public void setIrScenarioEntities(Integer scenNum, String crnyCd, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception{
		scenarioForeignCurveHis = scenarioCurveHis;
		this.impliedSpread = spread ;
		
	    if (recCurrency.equals(crnyCd)) {
	        setScenarioFxCurveHis("REC");
	        log.info("IR Scenario REC Curve Entities have been set! SCEN_NUM: {}", this.scenNum);
	    }

	    if (payCurrency.equals(crnyCd)) {
	        setScenarioFxCurveHis("PAY");
	        log.info("IR Scenario  PAY Curve Entities have been set! SCEN_NUM: {}", this.scenNum);
	    }
	}

	public void setFxScenarioEntities(Integer scenNum, String typCd, Double val) {
		
		this.scenNum = scenNum;
		this.fxTypCd = typCd;
		this.fxVal = val;
		this.setFxValues();
	}

	public void setStockScenarioEntities(Integer scenNum, String typCd, Double val) {		
		
		this.scenNum = scenNum;
		this.isStockShock = INTEGER_TRUE;
		
		if(this.instTpcd.equals(EInstrument.FIDE_IDXFUT.getLegacyCode())) {
			this.spotPrice = GeneralUtil.setScenarioValue(this.spotPriceBase, typCd, val);			
		}
		if(this.instTpcd.equals(EInstrument.FIDE_IDXOPTC.getLegacyCode()) || this.instTpcd.equals(EInstrument.FIDE_IDXOPTP.getLegacyCode())) {
			this.underlyingSpotPrice = GeneralUtil.setScenarioValue(this.underlyingSpotPriceBase, typCd, val);
		}
	
	}

	/**
	 * TODO: 
	 */
	public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {
		
		log.info("getValuation in derivAbs: {},{}", currencyType);
		evaluateCashflow();				
		
		List<KicsAssetResult> cflist = new ArrayList<KicsAssetResult>();
	    double recFxRatio = (currencyType ? DEF_FX_RATE : this.recFxRate);
	    double payFxRatio = (currencyType ? DEF_FX_RATE : this.payFxRate);
		
		List<Integer> legType = new ArrayList<Integer>();
		legType.add(REC_LEG_KEY);
		legType.add(PAY_LEG_KEY);
		legType.add(NET_LEG_KEY);
		
	    this.result.setBaseDate(TimeUtil.dateToString(this.baseDate));		
		this.result.setExpoId(this.expoId);
	    this.result.setFundCd(this.fundCd);
		this.result.setProdTpcd(this.prodTpcd);
		this.result.setAccoCd(this.accoCd);
		this.result.setDeptCd(this.deptCd);
	
		for(Integer leg : legType) {
			
			this.result.setLegType(setLegDefineMap(leg));		
			
			switch(leg.intValue()) {			
			
			    case REC_LEG_KEY: {		    	
			    	this.result.setCurrency(currencyType ? this.recCurrency : DEF_CURRENCY);
			    	
			    	for(Integer fe : financialElements) {			    		
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {
						    case FE_PRINCIPAL_CF: {
						    	for(int i=0; i<recPayoffDate.length; i++) {						    		
						    		this.result.setResultDate(TimeUtil.dateToString(recPayoffDate[i]));		
						    		this.result.setValue(Math.floor(this.recPrincipalPayoffAmount[i] * recFxRatio));						    		
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_INTEREST_CF: {				
						    	for(int i=0; i<recPayoffDate.length; i++) {
						    		this.result.setResultDate(TimeUtil.dateToString(recPayoffDate[i]));
						    		this.result.setValue(Math.floor(this.recInterestPayoffAmount[i] * recFxRatio));						    		
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_YIELD_TO_MATURITY: {			
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getYieldToMaturity(REC_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));			    	
						    	break;			    	
						    }						    
						    case FE_PV_DIRTY: {				
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));						    	
						    	this.result.setValue(Math.floor(getPresentValue(REC_LEG_KEY) * recFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_ACCRUED_INTEREST: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(Math.floor(getAccruedInterest(REC_LEG_KEY) * recFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_PV_CLEAN: {						    
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(REC_LEG_KEY) * recFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_MATURITY: {		
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getEffectiveMaturity(REC_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						
						    case FE_EFFECTIVE_DURATION: {			
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getEffectiveDuration(REC_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }			
						    case FE_MODIFIED_DURATION: {						    
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getModifiedDuration(REC_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_ACCRUED_INT_BS: {						    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
						    	this.result.setValue(getAccrAmt() / (currencyType && !this.recCurrency.equals(DEF_CURRENCY) ? this.recFxRate : DEF_FX_RATE));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						
						    case FE_IRATE_EXPOSURE: {						    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(REC_LEG_KEY) * recFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_FXRT_EXPOSURE: {		
						    	if(this.recCurrency != null && !this.recCurrency.equals(DEF_CURRENCY)) {
							    	this.result.setCurrency(this.recCurrency);
							    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
							    	this.result.setValue(Math.floor(getPresentValueClean(REC_LEG_KEY) * 1.0));
							    	cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_STOCK_EXPOSURE: {						
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(0 * recFxRatio));
						    	if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    default: break;						
			    		}			    	    
			    	}
			    	break;  //switch -> REC_LEG_KEY
			    }			    
			    case PAY_LEG_KEY: {			    	
			    	this.result.setCurrency(currencyType ? this.payCurrency : DEF_CURRENCY);
			    	
			    	for(Integer fe : financialElements) {			    		
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {			    		
						    case FE_PRINCIPAL_CF: {						   
						    	for(int i=0; i<payPayoffDate.length; i++) {						    		
						    		this.result.setResultDate(TimeUtil.dateToString(payPayoffDate[i]));
						    		this.result.setValue(Math.floor(this.payPrincipalPayoffAmount[i] * payFxRatio));						    		
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_INTEREST_CF: {						    	
						    	for(int i=0; i<payPayoffDate.length; i++) {						    		
						    		this.result.setResultDate(TimeUtil.dateToString(payPayoffDate[i]));				
						    		this.result.setValue(Math.floor(this.payInterestPayoffAmount[i] * payFxRatio));
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_YIELD_TO_MATURITY: {			    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getYieldToMaturity(PAY_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));			    	
						    	break;			    	
						    }						    
						    case FE_PV_DIRTY: {						    				
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValue(PAY_LEG_KEY) * payFxRatio));			    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_ACCRUED_INTEREST: {						  
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getAccruedInterest(PAY_LEG_KEY) * payFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_PV_CLEAN: {						    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(PAY_LEG_KEY) * payFxRatio));						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_EFFECTIVE_MATURITY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getEffectiveMaturity(PAY_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_DURATION: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getEffectiveDuration(PAY_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }			
						    case FE_MODIFIED_DURATION: {						    				
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getModifiedDuration(PAY_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_ACCRUED_INT_BS: {						    							
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
						    	this.result.setValue(getUernAmt() / (currencyType && !this.recCurrency.equals(DEF_CURRENCY) ? this.payFxRate : DEF_FX_RATE));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						
						    case FE_IRATE_EXPOSURE: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(PAY_LEG_KEY) * payFxRatio));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_FXRT_EXPOSURE: {		
						    	if(this.payCurrency != null && !this.payCurrency.equals(DEF_CURRENCY)) {
							    	this.result.setCurrency(this.payCurrency);
							    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
							    	this.result.setValue(Math.floor(getPresentValueClean(PAY_LEG_KEY) * 1.0));
							    	cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }					
						    case FE_STOCK_EXPOSURE: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(0 * recFxRatio));
						    	if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }									    
						    default: break;				
				    	}
			    	}
				    break;  //switch -> PAY_LEG_KEY
			    }
			    case NET_LEG_KEY: {		    	
			    	this.result.setCurrency(DEF_CURRENCY);
			    	
			    	for(Integer fe : financialElements) {			    		
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {
						    case FE_PRINCIPAL_CF: {				
						    	for(int i=0; i<netPayoffDate.length; i++) {
						    		this.result.setResultDate(TimeUtil.dateToString(netPayoffDate[i]));
						    		this.result.setValue(Math.floor(this.recPrincipalPayoffAmount[i] * this.recFxRate - this.payPrincipalPayoffAmount[i] * this.payFxRate)); 
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));						    		
						    	}
						    	break;
						    }
						    case FE_INTEREST_CF: {						    							    	
						    	for(int i=0; i<netPayoffDate.length; i++) {
						    		this.result.setResultDate(TimeUtil.dateToString(netPayoffDate[i]));
						    		this.result.setValue(Math.floor(this.recInterestPayoffAmount[i] * this.recFxRate - this.payInterestPayoffAmount[i] * this.payFxRate)); 
						    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    case FE_YIELD_TO_MATURITY: {			    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getYieldToMaturity(NET_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));			    	
						    	break;			    	
						    }						    
						    case FE_PV_DIRTY: {						    		
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValue(NET_LEG_KEY)));
						    	cflist.add(cloneEntity(this.result));					    	
						    	break;
						    }
						    case FE_ACCRUED_INTEREST: {					
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(Math.floor(getAccruedInterest(NET_LEG_KEY)));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_PV_CLEAN: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(NET_LEG_KEY)));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_MATURITY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));		
						    	this.result.setValue(getEffectiveMaturity(NET_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_DURATION: {					
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getEffectiveDuration(NET_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_MODIFIED_DURATION: {						
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
						    	this.result.setValue(getModifiedDuration(NET_LEG_KEY));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_BS_AMOUNT: {						    			
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getFairBsAmt());
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_ACCRUED_INT_BS: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
						    	this.result.setValue(getAccrAmt() - getUernAmt());
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }			
						    case FE_IRATE_EXPOSURE: {						    				
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(NET_LEG_KEY)));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_FXRT_EXPOSURE: {		
						    	break;
						    }
						    case FE_STOCK_EXPOSURE: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(Math.floor(getPresentValueClean(NET_LEG_KEY) * 0));
						    	if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_IMPLIED_SPREAD: {						    							    	
						    	log.debug("NET calc FE : {} FE_IMPLIED_SPREAD", fe );
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getImpliedSpread());						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_KTB_FORWARD_RATE: {						    					
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getKtbForwardRate());						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_IMPLIED_SIGMA: {						    			
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getOptionImpliedSigma());						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    
						    default: break;						
			    		}			    	    
			    	}
			    	break;  //switch -> NET_PAYOFF_KEY
			    }			    
			}
		}		
		return cflist;
	}

	@Override
	protected void setInstrumentEntities(KicsAssetFide entity) throws Exception {
		
		this.baseDate     = setDate(entity.getBaseDate());
		this.expoId       = entity.getExpoId();		
		this.fundCd       = entity.getFundCd();		
		this.prodTpcd     = entity.getProdTpcd();		
		this.accoCd       = entity.getAccoCd();				
		this.isinCd       = entity.getIsinCd();		
		this.deptCd       = entity.getDeptCd();
		this.instTpcd     = entity.getInstTpcd();		
		this.instDtlsTpcd = GeneralUtil.objectToPrimitive(entity.getInstDtlsTpcd());		
		
		switch(EInstrument.getEInstrument(this.instTpcd + String.valueOf(this.instDtlsTpcd)).getInstCode()) {			
			
		    case INST_FIDE_IDXOPTC: this.optTpcd = CALL_OPTION; break;			
			case INST_FIDE_IDXOPTP: this.optTpcd = PUT_OPTION; 	break;
			default               : this.optTpcd = null;        break;
		}
		
		this.isLongPosition          = GeneralUtil.stringComparator(entity.getPotnTpcd(), POSITION_LONG);
		
		this.issueDate               = setDate(entity.getIssuDate(), this.baseDate);		
		this.maturityDate            = setDate(entity.getMatrDate(), this.baseDate.plusDays(1));		 
		if(this.maturityDate.isBefore(this.issueDate.plusDays(1))) {
			this.maturityDate        = this.issueDate.plusDays(1);
			log.warn("MaturityDate must be after IssueDate in [{}]", this.expoId);			
		}		
		if(this.maturityDate.isBefore(this.baseDate.plusDays(1))) { 
			this.maturityDate        = this.baseDate.plusDays(1);
			log.warn("MaturityDate must be after BaseDate in [{}]", this.expoId);			
		}		
		this.firstCouponDate         = setDateOrNull(entity.getFrstIntDate());		
		if(this.firstCouponDate != null && this.firstCouponDate.isBefore(this.issueDate)) this.firstCouponDate = null;		
		
		this.isPrincipalSwap         = GeneralUtil.stringComparator(entity.getPrinExcgYn(), PRINCIPAL_SWAP);
		this.ktbMaturityYear         = GeneralUtil.objectToPrimitive(entity.getKtbMatYear());				
		this.cfDirection             = false;		
		this.isForward               = true;
		
		this.contractSize            = GeneralUtil.objectToPrimitive(entity.getContQnty());
		this.contractMultiple        = GeneralUtil.objectToPrimitive(entity.getContMult());
		this.contractPrice           = GeneralUtil.objectToPrimitive(entity.getContPrc());				
		this.spotPrice               = GeneralUtil.objectToPrimitive(entity.getSpotPrc());
		this.spotPriceBase           = this.spotPrice;
		this.spotPriceForNonStock    = this.spotPrice;
		this.isStockShock            = INTEGER_FALSE;
		this.optionVol               = GeneralUtil.objectToPrimitive(entity.getOptVolt());
		this.optionVolBase           = this.optionVol; 
		this.underlyingExercisePrice = GeneralUtil.objectToPrimitive(entity.getUndlExecPrc());
		this.underlyingSpotPrice     = GeneralUtil.objectToPrimitive(entity.getUndlSpotPrc());
		this.underlyingSpotPriceBase = this.underlyingSpotPrice;		
		
		this.recCurrency             = GeneralUtil.objectToPrimitive(entity.getRecCrnyCd(),   DEF_CURRENCY);
		this.recFxRate               = GeneralUtil.objectToPrimitive(entity.getRecCrnyFxrt(), DEF_FX_RATE);		
		this.recFxRateBase           = this.recFxRate;		
		this.recDayCountBasis        = EDayCountBasis.getEDayCountBasis(entity.getRecDcbCd()).getDcbCode();		
		this.recAmt                  = GeneralUtil.objectToPrimitive(entity.getRecNotlAmt());		
		this.recIRate                = GeneralUtil.objectToPrimitive(entity.getRecIrate());		
		this.recPaymentTerm          = GeneralUtil.objectToPrimitive(entity.getRecIntPayCyc(), DEF_PMT_TERM_MONTH);		
		this.recAccruedInterest      = GeneralUtil.objectToPrimitive(entity.getAccrAmt());
		this.recDiscountSpread       = 0.0;
		this.isRecIrCalibration      = INTEGER_FALSE;		
		
		this.payCurrency             = GeneralUtil.objectToPrimitive(entity.getPayCrnyCd(),   DEF_CURRENCY);		
		this.payFxRate               = GeneralUtil.objectToPrimitive(entity.getPayCrnyFxrt(), DEF_FX_RATE);		
		this.payFxRateBase           = this.payFxRate;		
		this.payDayCountBasis        = EDayCountBasis.getEDayCountBasis(entity.getPayDcbCd()).getDcbCode();		
		this.payAmt                  = GeneralUtil.objectToPrimitive(entity.getPayNotlAmt());		
		this.payIRate                = GeneralUtil.objectToPrimitive(entity.getPayIrate());
		this.payPaymentTerm          = GeneralUtil.objectToPrimitive(entity.getPayIntPayCyc(), DEF_PMT_TERM_MONTH);		
		this.payAccruedInterest      = GeneralUtil.objectToPrimitive(entity.getUernAmt());
		this.payDiscountSpread       = 0.0;
		this.isPayIrCalibration      = INTEGER_FALSE;		
		               
	    this.bsAmt                   = GeneralUtil.objectToPrimitive(entity.getBsAmt());
	    this.vltAmt                  = GeneralUtil.objectToPrimitive(entity.getVltAmt());
	    this.fairBsAmt               = GeneralUtil.objectToPrimitive(entity.getFairBsAmt());		
		this.accrAmt                 = GeneralUtil.objectToPrimitive(entity.getAccrAmt());
		this.uernAmt                 = GeneralUtil.objectToPrimitive(entity.getUernAmt());
		this.recExtUprc              = GeneralUtil.objectToPrimitive(entity.getRecExtUprc(), this.recAmt);
		this.payExtUprc              = GeneralUtil.objectToPrimitive(entity.getPayExtUprc(), this.payAmt);
		this.recExtDura              = GeneralUtil.objectToPrimitive(entity.getRecExtDura());
		this.payExtDura              = GeneralUtil.objectToPrimitive(entity.getPayExtDura());		
				
		this.isRealNumber            = GeneralUtil.objectToPrimitive(entity.getIsRealNumber());
		this.isRealNumber            = true;
		//log.info("real number:{}", this.isRealNumber);
		this.irCurveHis              = new HashMap<String, Map<String, IrCurveHis>>();
		this.recIrCurveId            = DEFAULT_KRW_RFCURVE_ID;
		this.payIrCurveId            = DEFAULT_KRW_RFCURVE_ID;
		this.irCurve                 = null;
	
		if(entity.getIrCurve() == null || entity.getIrCurve().isEmpty()) {			
//			log.warn("IrCurve Data is not found at {}!!!", baseDate);		
		}
		else if(!entity.getIrCurve().isEmpty() && !entity.getIrCurve().get(0).getIrCurveHis().isEmpty()) {			
				
				this.recIrCurveId            = entity.getIrCurve().get(0).getIrCurveId();
				this.payIrCurveId            = entity.getIrCurve().get(0).getIrCurveId();				
				this.irCurve                 = entity.getIrCurve();				
				for(IrCurve irc : irCurve) irCurveHis.put(irc.getIrCurveId(), irc.getIrCurveHis());
//				log.info("{}, {}, {}", entity.getIrCurve().get(0).getIrCurveId(), entity.getIrCurve(), entity.getIrCurve().get(0));				
				
				setIrCurve();
		}
		else {
			log.warn("Q_IC_IR_CURVE_HIS Data is not found at {}!!!", baseDate);
		    //log.warn("IrCurveHis Data is not found at {}!!!", baseDate);
		}		
	}
	
	private void setIrCurve() {		
		
		if(scenarioCurveHis != null && !scenarioCurveHis.isEmpty()) {			
			this.setRecScenarioCurveHis();
			this.setPayScenarioCurveHis(); // 여기에서 EUR 못채움 
		}
		else {
			this.setRecIrCurveHis();
			this.setPayIrCurveHis();			
		}
	}	
	
	
	private void setFxValues() {		
		
		if(recCurrency != null && !recCurrency.equals(DEF_CURRENCY)) this.recFxRate = GeneralUtil.setScenarioValue(this.recFxRateBase, this.fxTypCd, this.fxVal);				
		if(payCurrency != null && !payCurrency.equals(DEF_CURRENCY)) this.payFxRate = GeneralUtil.setScenarioValue(this.payFxRateBase, this.fxTypCd, this.fxVal);		
	}	


	/**
	 * TODO: 이 부분의 코드를 수정해야 한다. 현재 로직/구조 상에서 적합하지 않다. 즉, 환율 시나리오 아래의 특정 if문에서 빠져 나간다. (DEF_CURRENCY가 아닌 경우)
	 * isRecIrCalibration은 FALSE(0)로 기본값이 초기화되어 있다.
	 */
	private void setRecScenarioCurveHis() {
		
		if(this.recIrCurveId != null && this.recCurrency.equals(DEF_CURRENCY)) {		
			log.info("rec in set IR: {}", scenNum, recIrCurveId);
			
			this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
			
			this.recDiscountSpread = this.impliedSpread;
			this.isRecIrCalibration = INTEGER_TRUE;			

			recMatTerm = scenarioCurveHis.keySet().stream().toArray(String[]::new);			
			recMatTermIntRate = addSpread(scenarioCurveHis.values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.recDiscountSpread);
			
		}
		else setRecIrCurveHis();		
		
//		log.info("rec dis rate : {},{},{}", scenNum, recDiscountSpread, recMatTermIntRate);
//		log.info("rec dis rate aaa: {},{},{}", scenNum, recDiscountSpread, recMatTerm);
		dcntRecMatTerm = recMatTerm;		 
		dcntRecMatTermIntRate = recMatTermIntRate;
	}
	
	private void setPayScenarioCurveHis() {		
		log.info("pay in set IR: {},{},{}", payIrCurveId, payCurrency, DEF_CURRENCY);
		
//		if(this.payIrCurveId != null) {
		if(this.payIrCurveId != null && this.payCurrency.equals(DEF_CURRENCY)) {
//		if(this.payIrCurveId != null && this.payCurrency.equals(DEF_CURRENCY) && this.isRecIrCalibration == INTEGER_FALSE) {
			
			log.info("pay in setIr: {},{}", scenNum, payIrCurveId, isRecIrCalibration);
			this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
			
			this.payDiscountSpread = this.impliedSpread;
			this.isPayIrCalibration = INTEGER_TRUE;			

			payMatTerm = scenarioCurveHis.keySet().stream().toArray(String[]::new);			
			payMatTermIntRate = addSpread(scenarioCurveHis.values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.payDiscountSpread);			
			
		}
		else setPayIrCurveHis();
		
//		log.info("pay dis rate : {},{},{}", scenNum,  payDiscountSpread, payMatTermIntRate);
		dcntPayMatTerm = payMatTerm;		 
		dcntPayMatTermIntRate = payMatTermIntRate;
	}

	private void setScenarioFxCurveHis(String legType) {		
		
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
		
		if(legType.equals("REC")) {
			this.recDiscountSpread = this.impliedSpread;
			this.isRecIrCalibration = INTEGER_TRUE;			

			recMatTerm = scenarioForeignCurveHis.keySet().stream().toArray(String[]::new);			
			recMatTermIntRate = addSpread(scenarioForeignCurveHis.values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.recDiscountSpread);
		
			dcntRecMatTerm = recMatTerm;		 
			dcntRecMatTermIntRate = recMatTermIntRate;
			log.info("rec curve : {},{},{}", recCurrency,this.isRecIrCalibration ,recMatTermIntRate);
		}
		else {
			
			
	    	// TODO : CHECK!!!  기존 엔진 로직 확인  
	    	// 채권선도 , 국채선물은 REC / PAY 모두 고려하여 내재스프레드를 산출 & 적용 INST_DTLS_TPCD =7 
	    	// 그외 파생 (통화 /외환 스왑)은 REC cf 만을 고려하여 내재스프레드를 산출 & 적용하고 있음 
			
			if (this.instDtlsTpcd == 7) {
				this.payDiscountSpread = this.impliedSpread;
				this.isPayIrCalibration = INTEGER_TRUE;
			}
			
			payMatTerm = scenarioForeignCurveHis.keySet().stream().toArray(String[]::new);			
			payMatTermIntRate = addSpread(scenarioForeignCurveHis.values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.payDiscountSpread );			
			
			dcntPayMatTerm = payMatTerm;		 
			dcntPayMatTermIntRate = payMatTermIntRate;
			log.info("Pay curve : {},{},{}",  payCurrency, this.isPayIrCalibration ,payMatTermIntRate);
		}	
	}
	
//	private void setIrFxCurveHis(String legType) {		
//		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
//		
//		if(legType.equals("REC")) {
//			
//			recMatTerm = this.irCurveHis.get(this.recIrCurveId).keySet().stream().toArray(String[]::new);		 
//			recMatTermIntRate = addSpread(this.irCurveHis.get(this.recIrCurveId).values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), 0.0);
//			
//			dcntRecMatTerm = recMatTerm;		 
//			dcntRecMatTermIntRate = recMatTermIntRate;		
//			log.info("foreign rec curve : {},{},{}", payMatTermIntRate);
//		}
//		else {
//			payMatTerm = this.irCurveHis.get(this.payIrCurveId).keySet().stream().toArray(String[]::new);		 
//			payMatTermIntRate = addSpread(this.irCurveHis.get(this.payIrCurveId).values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), 0.0);
//			
//			dcntPayMatTerm = payMatTerm;		 
//			dcntPayMatTermIntRate = payMatTermIntRate;
//			log.info("foreign Pay curve : {},{},{}", payMatTermIntRate);
//		}		
//		
//	}

	private void setRecIrCurveHis() {		
		
		//this.toRealScale = TO_REAL_INT_RATE;		
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
		
		if(this.irCurveHis.get(this.recIrCurveId) != null && !this.irCurveHis.get(this.recIrCurveId).isEmpty()) {			
			recMatTerm = this.irCurveHis.get(this.recIrCurveId).keySet().stream().toArray(String[]::new);		 
			recMatTermIntRate = addSpread(this.irCurveHis.get(this.recIrCurveId).values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.recDiscountSpread);
		}
		else if(this.irCurveHis.get(this.recIrCurveId) == null) log.error("REC IR CURVE ID is NULL in {}", this.expoId);
		else if(this.irCurveHis.get(this.recIrCurveId).isEmpty()) log.error("REC IR CURVE DATA is Empty in {}", this.expoId);
		else log.error("Check the REC IR CURVE DATA in {}", this.expoId);
		
		dcntRecMatTerm = recMatTerm;		 
		dcntRecMatTermIntRate = recMatTermIntRate;		
		//log.info("REC HIS: {}, {}, {}", this.irCurveHis, recMatTerm, recMatTermIntRate);
	}
	
	private void setPayIrCurveHis() {
		
		//this.toRealScale = TO_REAL_INT_RATE;
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
		
		if(this.irCurveHis.get(this.payIrCurveId) != null && !this.irCurveHis.get(this.payIrCurveId).isEmpty()) {
			payMatTerm = this.irCurveHis.get(this.payIrCurveId).keySet().stream().toArray(String[]::new);		 
			payMatTermIntRate = addSpread(this.irCurveHis.get(this.payIrCurveId).values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.payDiscountSpread);
			
		}		
		else if(this.irCurveHis.get(this.payIrCurveId) == null) log.error("PAY IR CURVE ID is NULL in {}", this.expoId);
		else if(this.irCurveHis.get(this.payIrCurveId).isEmpty()) log.error("PAY IR CURVE DATA is Empty in {}", this.expoId);
		else log.error("Check the PAY IR CURVE DATA in {}", this.expoId);		
		
		dcntPayMatTerm = payMatTerm;		 
		dcntPayMatTermIntRate = payMatTermIntRate;
		log.info("PAY HIS: {}, {}, {}", this.irCurveHis, payMatTerm, payMatTermIntRate);
	}
	
	
	protected void evaluateCashflow() throws Exception {
		
		log.info("evaluateCashflow in DerivAbs:{},{}");
		
		setAttributes();		
		setPayoffDate();
		setPayoffAmount();
		
		log.info("evaluateCashflow in DerivAbs End:{},{}");
	}
	
	
	protected abstract void setAttributes()  throws Exception;
	
	protected abstract void setPayoffDate() throws Exception;
	
	protected abstract void setPayoffAmount() throws Exception;
	
	
	protected double getPresentValue(Integer legType) throws Exception {
		
		switch(legType) {		
		
		    case REC_LEG_KEY: return getPresentValue(REC_LEG_KEY, this.recMatTermIntRate);
		    case PAY_LEG_KEY: return getPresentValue(PAY_LEG_KEY, this.payMatTermIntRate);
		    //case NET_LEG_KEY: log.warn("Using getPresentValueBsCrny(Integer legType) instead this method in EXPO_ID = [{}]", this.getExpoId());
		    case NET_LEG_KEY: return getPresentValueBsCrny(NET_LEG_KEY);
		    default         : return 0.0;
		}		
	}
	
	
	protected double getPresentValueBsCrny(Integer legType) throws Exception {
		
		switch(legType) {
		
		    case REC_LEG_KEY: return getPresentValue(REC_LEG_KEY) * this.recFxRate;
		    case PAY_LEG_KEY: return getPresentValue(PAY_LEG_KEY) * this.payFxRate;
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) * this.recFxRate - getPresentValue(PAY_LEG_KEY) * this.payFxRate;
		    default         : return 0.0;
		}
	}

	
	protected double getPresentValue(Integer legType, double[] dcntRate) throws Exception {
		
//		log.info("PV in Der Abs0 : {},{},{},{},{}", legType, dcntRate.length, baseDate, recMatTerm);
//		log.info("PV in Der Abs1 : {},{},{},{},{}", dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, recDayCountBasis);
//		for(int i=0; i< recPayoffDate.length ; i++) {
//			log.info("PV in Der Abs2 : {},{},{},{},{}",  recPayoffDate[i], payPayoffDate[i]);
//		}
		for(int i=0; i< 10 ; i++) {
//			log.info("PV in Der Abs2 : {},{},{},{},{}",  scenNum, i, dcntRate[i], recPayoffDate[i], payPayoffDate[i]);
		}
//		log.info("PV in Der Abs3 : {},{},{},{},{}", recPayoffAmount);
//		log.info("PV in Der Abs3 : {},{},{},{},{}", payPayoffAmount);
		
		switch(legType) {		

			case REC_LEG_KEY: return getPresentValue(this.recMatTerm, dcntRate, this.baseDate, this.recPayoffDate, this.recPayoffAmount
	    						   			       , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.recDayCountBasis);
	    	
	    	case PAY_LEG_KEY: return getPresentValue(this.payMatTerm, dcntRate, this.baseDate, this.payPayoffDate, this.payPayoffAmount
	    		                                   , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.payDayCountBasis);
	    	
	    	case NET_LEG_KEY: log.warn("Same Discount Rate can not be applied to Both two SWAP Legs in EXPO_ID = [{}]", this.getExpoId());
	    	default         : return 0.0;
		}
	}	
	
	
	protected double getPresentValueBsCrny(Integer legType, double[] dcntRate) throws Exception {
		
		switch(legType) {
		
		    case REC_LEG_KEY: return getPresentValue(REC_LEG_KEY, dcntRate) * this.recFxRate;
		    case PAY_LEG_KEY: return getPresentValue(PAY_LEG_KEY, dcntRate) * this.payFxRate;
		    case NET_LEG_KEY: log.warn("Same Discount Rate can not be applied to Both two SWAP Legs in EXPO_ID = [{}]", this.getExpoId());
		    default         : return 0.0;
		}
	}
	

    protected double getPresentValue(Integer legType, double ytm) throws Exception {
    	
    	switch(legType) {
    		
    		case REC_LEG_KEY: return getPresentValue(ytm, this.baseDate, this.recPayoffDate, this.recPayoffAmount
    				                               , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.recDayCountBasis);
    		case PAY_LEG_KEY: return getPresentValue(ytm, this.baseDate, this.payPayoffDate, this.payPayoffAmount
                                                   , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.payDayCountBasis);
	    	//case NET_LEG_KEY: log.warn("GetPresentValue for YTM of NET_LEG_KEY has not been implemented yet in EXPO_ID = [{}]", this.getExpoId());
    		case NET_LEG_KEY: return getPresentValueBsCrny(NET_LEG_KEY, ytm);    		
    		default         : return 0.0;
    	}    		
    }

	
	protected double getPresentValueBsCrny(Integer legType, double ytm) throws Exception {
		
		switch(legType) {
		
		    case REC_LEG_KEY: return getPresentValue(REC_LEG_KEY, ytm) * this.recFxRate;
		    case PAY_LEG_KEY: return getPresentValue(PAY_LEG_KEY, ytm) * this.payFxRate;
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY, ytm) * this.recFxRate - getPresentValue(PAY_LEG_KEY, ytm) * this.payFxRate;
		    default         : return 0.0;
		}
	}
	
	
    protected double getPresentValueClean(Integer legType) throws Exception {    	
    	return getPresentValue(legType) - getAccruedInterest(legType);    	
    }
	
	
	protected double getEffectiveDuration(Integer legType) throws Exception {
		
		switch(legType) {		
		
		    case REC_LEG_KEY: return getEffectiveDuration(REC_LEG_KEY, this.recMatTermIntRate);
		    case PAY_LEG_KEY: return getEffectiveDuration(PAY_LEG_KEY, this.payMatTermIntRate);		    
		    case NET_LEG_KEY: return getEffectiveDuration(REC_LEG_KEY, this.recMatTermIntRate) - getEffectiveDuration(PAY_LEG_KEY, this.payMatTermIntRate); 
		    default:          return 0.0;
		}
	}
	

	protected double getEffectiveDuration(Integer legType, double[] dcntRate) throws Exception {		
		
		switch(legType) {
	    	
		    case REC_LEG_KEY: return getEffectiveDuration(this.recMatTerm, dcntRate, this.baseDate, this.recPayoffDate, this.recPayoffAmount
	    			                                    , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.recDayCountBasis);
	    	case PAY_LEG_KEY: return getEffectiveDuration(this.payMatTerm, dcntRate, this.baseDate, this.payPayoffDate, this.payPayoffAmount
	    			                                    , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.payDayCountBasis);
	    	case NET_LEG_KEY: log.warn("Using getEffectiveDuration(Integer legType) instead this method in EXPO_ID = [{}]", this.getExpoId());
	    	default:          return 0.0;
		}	
	}	
	
	/**
	 * Macaulay Duration���� �����෹�̼����� �Ѿ�� �������� paymentTerm�� ���°� �ƴѰ�...compoundPeriod�̾���ϳ�?...
	 */
	protected double getModifiedDuration(Integer legType) throws Exception {
		
		if(legType == NET_LEG_KEY) return getMacaulayDuration(NET_LEG_KEY);	
		
		//return getMacaulayDuration() / (1.0 + getYieldToMaturity());
		else return getMacaulayDuration(legType) / ( 1.0 + getYieldToMaturity(legType) / TimeUtil.getCompoundFrequency(this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType) );
	}

	
	protected double getMacaulayDuration(Integer legType) throws Exception {

		switch(legType) {
		    
			case REC_LEG_KEY: return getMacaulayDuration(REC_LEG_KEY, this.recMatTermIntRate);
		    case PAY_LEG_KEY: return getMacaulayDuration(PAY_LEG_KEY, this.payMatTermIntRate);		    
		    case NET_LEG_KEY: return getMacaulayDuration(REC_LEG_KEY, this.recMatTermIntRate) - getMacaulayDuration(PAY_LEG_KEY, this.payMatTermIntRate);
		    default:          return 0.0;
		}	
	}
	

	protected double getMacaulayDuration(Integer legType, double[] dcntRate) throws Exception {
		
		switch(legType) {
	
	    	case REC_LEG_KEY: return getMacaulayDuration(this.recMatTerm, dcntRate, this.baseDate, this.recPayoffDate, this.recPayoffAmount
	    										       , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.recDayCountBasis);
	    	case PAY_LEG_KEY: return getMacaulayDuration(this.payMatTerm, dcntRate, this.baseDate, this.payPayoffDate, this.payPayoffAmount
	    		                                       , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.payDayCountBasis);
	    	case NET_LEG_KEY: log.warn("Using getMacaulayDuration(Integer legType) instead this method in EXPO_ID = [{}]", this.getExpoId());	    	
	    	default         : return 0.0;
		}
	}

	
	protected double getTimeWeightedPresentValue(Integer legType) throws Exception {	
		
		switch(legType) {
		
		    case REC_LEG_KEY: return getTimeWeightedPresentValue(REC_LEG_KEY, this.recMatTermIntRate);
		    case PAY_LEG_KEY: return getTimeWeightedPresentValue(PAY_LEG_KEY, this.payMatTermIntRate);
		    case NET_LEG_KEY: log.warn("getTimeWeightedPresentValue of NET_LEG_KEY has not been implemented yet in EXPO_ID = [{}]", this.getExpoId());
		    default:          return 0.0;
		}
	}
	

	protected double getTimeWeightedPresentValue(Integer legType, double[] dcntRate) throws Exception {
		
		switch(legType) {
		
	    	case REC_LEG_KEY: return getTimeWeightedPresentValue(this.recMatTerm, dcntRate, this.baseDate, this.recPayoffDate, this.recPayoffAmount
	    										               , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.recDayCountBasis);
	    	case PAY_LEG_KEY: return getTimeWeightedPresentValue(this.payMatTerm, dcntRate, this.baseDate, this.payPayoffDate, this.payPayoffAmount
	    		                                               , this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.payDayCountBasis);
	    	case NET_LEG_KEY: log.warn("Using getTimeWeightedPresentValue(Integer legType) instead this method in EXPO_ID = [{}]", this.getExpoId());
	    	default:          return 0.0;
		}		
	}
    
	/**
	 * NET_LEG�� CF�� ���⼺�� ����/������ �ƴϹǷ�, ���ſ� ����ϰ� �����Ͽ� [REC-PAY]�� ����ó������
	 */    
    protected double getYieldToMaturity(Integer legType) throws Exception {
    	
    	switch(legType) {
    	
    		case REC_LEG_KEY: return getYieldToMaturity(legType, this.recExtUprc + getAccruedInterestBsCrny(REC_LEG_KEY));
    		case PAY_LEG_KEY: return getYieldToMaturity(legType, this.payExtUprc + getAccruedInterestBsCrny(PAY_LEG_KEY));
    		//case NET_LEG_KEY: return getYieldToMaturity(legType, this.fairBsAmt  + getAccruedInterestBsCrny(NET_LEG_KEY));
    		case NET_LEG_KEY: return getYieldToMaturity(REC_LEG_KEY, this.recExtUprc + getAccruedInterestBsCrny(REC_LEG_KEY))
    				               - getYieldToMaturity(PAY_LEG_KEY, this.payExtUprc + getAccruedInterestBsCrny(PAY_LEG_KEY));    		
    		default:          return 0.0;
    	}
    }
	
    
    protected double getYieldToMaturity(Integer legType, double price) throws Exception {
    	return getYieldToMaturity(legType, price, INITIAL_GUESS);    	
    }
	
	
    private double getYieldToMaturity(Integer legType, double targetBsAmt, double initGuess) throws Exception {
    	
    	double ratePos   = 0.0;
    	double rateNeg   = 0.0;
    	double rateNew   = 0.0;
    	double fnRatePos = 0.0;
    	double fnRateNeg = 0.0;
    	double fnRateNew = 0.0;    	
    	
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	ratePos = Math.min(initGuess,   1.0);    	
    	rateNeg = Math.max(-initGuess, -0.9);    	
    	
    	fnRatePos = getPresentValueBsCrny(legType, ratePos) - targetBsAmt;		
    	fnRateNeg = getPresentValueBsCrny(legType, rateNeg) - targetBsAmt;    	
    	if(Double.isNaN(fnRatePos) || Double.isNaN(fnRateNeg)) return 0.0;    	
    	
    	//log.info("{}, rec:{}, pay:{}, net:{}, target:{}, PV_POS:{}, PV_NEG:{}, POS:{}, NEG:{}", setLegDefineMap(legType), this.recExtUprc, this.payExtUprc, this.fairBsAmt, targetBsAmt, getPresentValueBsCrny(legType, ratePos), getPresentValueBsCrny(legType, rateNeg), getPresentValueBsCrny(legType, ratePos) - targetBsAmt, getPresentValueBsCrny(legType, rateNeg) - targetBsAmt);
    	if(fnRatePos * fnRateNeg > ZERO_DOUBLE) {
    		
    		for(int i=0; i<MAX_INITIAL_GUESS; i++) {    			    			
    			
    			ratePos  *=  2.0;
    			rateNeg   = -0.9;
    	    	fnRatePos = getPresentValueBsCrny(legType, ratePos) - targetBsAmt;		
    	    	fnRateNeg = getPresentValueBsCrny(legType, rateNeg) - targetBsAmt;	        	

	        	if(fnRatePos * fnRateNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnRatePos) || Double.isNaN(fnRateNeg)) return 0.0;
	        	
	        	if(i==0) log.warn("Changing Initial Guess in Calculating YTM [LEG: {}]: EXPO_ID = [{}]", setLegDefineMap(legType), this.getExpoId());
    		}    		    		
        	if(fnRatePos * fnRateNeg > ZERO_DOUBLE) return 0.0;
    	}    	

    	rateNew = 0.5 * (ratePos + rateNeg);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		

    		fnRateNew = getPresentValueBsCrny(legType, rateNew) - targetBsAmt;    		

    		if(fnRateNeg * fnRateNew < 0) {
    			
    			fnRatePos = fnRateNew;
    			ratePos   = rateNew;
    			rateNew   = 0.5 * (ratePos + rateNeg);    			
    			if(Math.abs(rateNew - ratePos) < ZERO_DOUBLE) return rateNew;
    		}
        	else if(fnRatePos * fnRateNew < 0) {
        		
        		fnRateNeg = fnRateNew;
        		rateNeg   = rateNew;
        		rateNew   = 0.5 * (ratePos + rateNeg);        		
        		if(Math.abs(rateNew - rateNeg) < ZERO_DOUBLE) return rateNew;
        	}
        	else return rateNew;
    	}    	
    	log.warn("Calculating Yield to Maturity is failed: [{}]", this.getExpoId());
    	return 0.0;    	
    }    
    
    
	protected double getEffectiveMaturity(Integer legType) throws Exception {    	
		
//		log.info("Eff Dur in Der Abs : {},{},{},{},{}", legType, recDayCountBasis, recPayoffDate, recPayoffAmount );
		
		switch(legType) {
		
		    case REC_LEG_KEY: return getEffectiveMaturity(this.baseDate, this.recPayoffDate, this.recPayoffAmount, this.recDayCountBasis);
		    case PAY_LEG_KEY: return getEffectiveMaturity(this.baseDate, this.payPayoffDate, this.payPayoffAmount, this.payDayCountBasis);		
		    case NET_LEG_KEY: return getEffectiveMaturity(this.baseDate, this.recPayoffDate, this.recPayoffAmount, this.recDayCountBasis)
		    		               - getEffectiveMaturity(this.baseDate, this.payPayoffDate, this.payPayoffAmount, this.payDayCountBasis);
		    default         : return 0.0;
		}
    }
		
    
    protected double getAccruedInterest(Integer legType) throws Exception {
    	
		switch(legType) {		
		
		    case REC_LEG_KEY: return getAccruedInterest(this.baseDate, this.recPayoffDate, this.recInterestPayoffAmount, this.recDayCountBasis);
		    case PAY_LEG_KEY: return getAccruedInterest(this.baseDate, this.payPayoffDate, this.payInterestPayoffAmount, this.payDayCountBasis);
		    case NET_LEG_KEY: return getAccruedInterestBsCrny(NET_LEG_KEY);
		    default         : return 0.0;
		}
    }
    
    
    protected double getAccruedInterestBsCrny(Integer legType) throws Exception {

    	switch(legType) {		
		
		    case REC_LEG_KEY: return getAccruedInterest(REC_LEG_KEY) * this.recFxRate;
		    case PAY_LEG_KEY: return getAccruedInterest(PAY_LEG_KEY) * this.payFxRate;		
		    case NET_LEG_KEY: return getAccruedInterest(REC_LEG_KEY) * this.recFxRate - getAccruedInterest(PAY_LEG_KEY) * this.payFxRate;
		    default         : return 0.0;
		}
    }
    
    
    protected double getImpliedSpread() throws Exception {
    	
    	double targetBsAmt = this.fairBsAmt + getAccruedInterest(NET_LEG_KEY);    	
    	return getImpliedSpread(targetBsAmt);
    }    
    
    
    protected double getImpliedSpread(double targetBsAmt) throws Exception {    	
    	return getImpliedSpread(targetBsAmt, INITIAL_GUESS);    	
    }    
    
    
    protected double getImpliedSpread(double targetBsAmt, double initGuess) throws Exception {
    	
    	if(this.isRecIrCalibration != INTEGER_TRUE && this.isPayIrCalibration != INTEGER_TRUE) return 0.0;
    	
    	if(this.isRecIrCalibration == INTEGER_TRUE && this.isPayIrCalibration == INTEGER_TRUE) {
    		log.warn("Both REC and PAY Legs will be calibrated. It may not be converged: [{}]", this.getExpoId());    		
    	}
    	
//    	log.info("Implied In Der ABS : {},{},{}, {}", targetBsAmt, initGuess, recDiscountSpread, payDiscountSpread);
//    	log.info("Implied In Der ABS0 : {},{},{}, {}", isRecIrCalibration, isPayIrCalibration);
//    	log.info("Implied In Der ABS1 : {},{},{}", recMatTermIntRate, payMatTermIntRate);
    	
    	double spreadPos   = 0.0;
    	double spreadNeg   = 0.0;
    	double spreadNew   = 0.0;
    	double fnSpreadPos = 0.0;
    	double fnSpreadNeg = 0.0;
    	double fnSpreadNew = 0.0;    	
    	
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	
    	spreadPos = Math.min( initGuess,  1.0);
    	spreadNeg = Math.max(-initGuess, -0.9);    	 

    	fnSpreadPos =   getPresentValueBsCrny(REC_LEG_KEY, addSpread(this.recMatTermIntRate, spreadPos * this.isRecIrCalibration)) 
    			      - getPresentValueBsCrny(PAY_LEG_KEY, addSpread(this.payMatTermIntRate, spreadPos * this.isPayIrCalibration))
    			      - targetBsAmt;
    	fnSpreadNeg =   getPresentValueBsCrny(REC_LEG_KEY, addSpread(this.recMatTermIntRate, spreadNeg * this.isRecIrCalibration)) 
     			      - getPresentValueBsCrny(PAY_LEG_KEY, addSpread(this.payMatTermIntRate, spreadNeg * this.isPayIrCalibration))
	    		      - targetBsAmt;    	
    	
//    	log.info("Implied Spread11111 : {},{}",  fnSpreadNeg, fnSpreadPos);
    	
    	if(Double.isNaN(fnSpreadPos) || Double.isNaN(fnSpreadNeg)) return 0.0;
    	
    	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) {
    		
    		for(int i=0; i<MAX_INITIAL_GUESS; i++) {    			    			
    			
	        	spreadPos  *=  2.0;
	        	spreadNeg   = -0.9;	        	
	        	fnSpreadPos =   getPresentValueBsCrny(REC_LEG_KEY, addSpread(this.recMatTermIntRate, spreadPos * this.isRecIrCalibration)) 
	    			          - getPresentValueBsCrny(PAY_LEG_KEY, addSpread(this.payMatTermIntRate, spreadPos * this.isPayIrCalibration))
	    			          - targetBsAmt;
	    	    fnSpreadNeg =   getPresentValueBsCrny(REC_LEG_KEY, addSpread(this.recMatTermIntRate, spreadNeg * this.isRecIrCalibration)) 
	     			          - getPresentValueBsCrny(PAY_LEG_KEY, addSpread(this.payMatTermIntRate, spreadNeg * this.isPayIrCalibration))
		    		          - targetBsAmt;

	        	if(fnSpreadPos * fnSpreadNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnSpreadPos) || Double.isNaN(fnSpreadNeg)) return 0.0;
	        	
	        	if(i==0) log.warn("Changing Initial Guess in Calculating Implied Spread: EXPO_ID = [{}]", this.getExpoId());
    		}    		    		
        	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) return 0.0;
    	}    	

    	spreadNew = 0.5 * (spreadPos + spreadNeg);
//    	log.info("Implied Spread0 : {},{}",  spreadNew);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		
    		
//    		log.info("Implied Spread : {},{}", i, spreadNew);
    		fnSpreadNew =   getPresentValueBsCrny(REC_LEG_KEY, addSpread(this.recMatTermIntRate, spreadNew * this.isRecIrCalibration)) 
  			              - getPresentValueBsCrny(PAY_LEG_KEY, addSpread(this.payMatTermIntRate, spreadNew * this.isPayIrCalibration))
  			              - targetBsAmt;    		

    		if(fnSpreadNeg * fnSpreadNew < 0) {
    			
    			fnSpreadPos = fnSpreadNew;
    			spreadPos   = spreadNew;
    			spreadNew   = 0.5 * (spreadPos + spreadNeg);    			
    			if(Math.abs(spreadNew - spreadPos) < ZERO_DOUBLE) return spreadNew;
    		}
        	else if(fnSpreadPos * fnSpreadNew < 0) {
        		
        		fnSpreadNeg = fnSpreadNew;
        		spreadNeg   = spreadNew;
        		spreadNew   = 0.5*(spreadPos + spreadNeg);        		
        		if(Math.abs(spreadNew - spreadNeg) < ZERO_DOUBLE) return spreadNew;
        	}
        	else return spreadNew;
    	}    	
    	log.warn("Calculating Implied Spread is failed: [{}]", this.getExpoId());
    	return 0.0;
    }
    
    
    protected double getKtbForwardRate() throws Exception {
    	return 0.0;
    }    
    
    
    protected double getOptionImpliedSigma() throws Exception {
    	return 0.0;
    }
	
	
	
}
		