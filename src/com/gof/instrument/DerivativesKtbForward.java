package com.gof.instrument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetResult;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EFinElements;
import com.gof.enums.EKtbFuturesInfo;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class DerivativesKtbForward extends DerivativesAbstract {
	
	
	
	protected double      faceValue;	
	protected int         ktbPmtCyc;
	protected double      appliedBaseRate;
	
	protected double      recKtbSpotPrice;
	protected double      payKtbSpotPrice;
	protected double      intRate;
	protected int         paymentFreq;
	protected int         cfSize;
	
	
	protected double      fwdFaceValue;
	protected double      fwdSpotPrice;
	protected double      fwdDuration;
	
	
	protected int         ktbPaymentFreq;
	protected LocalDate   ktbMaturityDate;
	protected double	  ktbCouponRate;
	protected double      ktbFaceValue;
	protected double      ktbSpotPrice;
	protected double      ktbDuration;
	
	
	
	protected LocalDate[] ktbPayoffDate;
	protected LocalDate[] fwdPayoffDate;
	
	
	protected double[] ktbPrinPayoffAmt;
	protected double[] fwdPrinPayoffAmt;
	
	protected double[] ktbIntPayoffAmt;
	protected double[] fwdIntPayoffAmt;

	
	public DerivativesKtbForward(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;
	}	

	public DerivativesKtbForward(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public DerivativesKtbForward() {
		super();		
		this.financialElements = EFinElements.KTB_FWD.getEFinElementList();
	}	
	
	
	protected void setAttributes() throws Exception {		 
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;
		this.paymentTermType    = TIME_UNIT_MONTH;		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		
		
		this.ktbPmtCyc          = (this.isLongPosition == INTEGER_TRUE) ? this.recPaymentTerm : this.payPaymentTerm;
		this.ktbMaturityDate    = this.maturityDate.plusYears(this.ktbMaturityYear);

		if(this.ktbMaturityYear == NULL_INT || this.ktbPmtCyc == NULL_INT) {			
			log.warn("Check the Instrument Info of KTB Futures: EXPO_ID = [{}]", this.expoId);
			
			if(this.isinCd != null && this.isinCd.trim().length() == LENGTH_ISIN_CD) {				
				this.ktbMaturityYear = EKtbFuturesInfo.getEKtbFuturesInfo(this.isinCd.substring(3,6)).getMatYear();
				this.ktbPmtCyc       = EKtbFuturesInfo.getEKtbFuturesInfo(this.isinCd.substring(3,6)).getPmtTerm();				
			}			
		}
		
		
		this.ktbPaymentFreq		= 12 / this.ktbPmtCyc;
		this.cfSize             = this.ktbMaturityYear * this.ktbPaymentFreq;		
		
		this.ktbCouponRate      = (this.isLongPosition == INTEGER_TRUE) ? this.recIRate: this.payIRate;
		
		this.ktbFaceValue	    = (this.isLongPosition == INTEGER_TRUE) ? this.recAmt : this.payAmt ;
		this.ktbDuration	    = (this.isLongPosition == INTEGER_TRUE) ? this.recExtDura : this.payExtDura ;
		this.ktbSpotPrice	    = (this.isLongPosition == INTEGER_TRUE) ? this.recExtUprc : this.payExtUprc ;
		
		this.fwdFaceValue	    = (this.isLongPosition == INTEGER_TRUE) ? this.payAmt : this.recAmt ;
		this.fwdDuration	    = (this.isLongPosition == INTEGER_TRUE) ? this.payExtDura : this.recExtDura ;
		this.fwdSpotPrice	    = (this.isLongPosition == INTEGER_TRUE) ? this.payExtUprc : this.recExtUprc ;
		
		this.faceValue          = ktbFaceValue;
		this.spotPrice			= this.recExtUprc - this.payExtUprc;
		
		
		this.dcntCmpdPeriod     = this.ktbPmtCyc;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
		
	}
	
	protected void setPayoffDate() throws Exception {			
		List<LocalDate> temp = new ArrayList<LocalDate>();
		temp.add(this.issueDate);
		temp.add(this.maturityDate);
		
		this.fwdPayoffDate = temp.stream().toArray(LocalDate[]::new);
		this.ktbPayoffDate = generateCashflowArray(this.maturityDate, this.ktbMaturityDate, ktbPmtCyc, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		log.info("ktb payoff Date : {},{}", fwdPayoffDate.length,  ktbPayoffDate.length);
		
		this.recPayoffDate = new LocalDate[ktbPayoffDate.length +1] ; 
		this.payPayoffDate = new LocalDate[ktbPayoffDate.length +1] ; 
		this.netPayoffDate = new LocalDate[ktbPayoffDate.length +1] ; 
		
		
		recPayoffDate[0] = this.issueDate;
		payPayoffDate[0] = this.issueDate;
		netPayoffDate[0] = this.issueDate;
		
		for(int j =0; j< ktbPayoffDate.length; j++) {
			recPayoffDate[j+1] = ktbPayoffDate[j];
			payPayoffDate[j+1] = ktbPayoffDate[j];
			netPayoffDate[j+1] = ktbPayoffDate[j];
		}
	}	
	
	protected void setPayoffAmount() throws Exception {	    
	    
		log.info("Set payoff amt in ktb forward: {},{},{}" , isLongPosition, ktbPayoffDate.length, fwdPayoffDate.length);
		log.info("Set payoff amt in ktb forward1: {},{},{},{}" , ktbPaymentFreq, ktbFaceValue, fwdFaceValue, ktbCouponRate);
		
		recPrincipalPayoffAmount = new double[ktbPayoffDate.length+1];   	
    	recInterestPayoffAmount  = new double[ktbPayoffDate.length+1];    	
    	payPrincipalPayoffAmount = new double[ktbPayoffDate.length+1];
    	payInterestPayoffAmount  = new double[ktbPayoffDate.length+1];
    	
    	
    	recPayoffAmount =  new double[ktbPayoffDate.length+1];
    	payPayoffAmount =  new double[ktbPayoffDate.length+1];
    	netPayoffAmount =  new double[ktbPayoffDate.length+1];
    	
		
		//log.info("KTB =  POTN:{}, REC:{}, PAY:{}", this.isLongPosition, this.recKtbSpotPrice, this.payKtbSpotPrice);		
    	// n+1 		
    	
    	for(int i=0; i<ktbPayoffDate.length+1; i++) {
    		recPrincipalPayoffAmount[i] =0.0;    	
    		recInterestPayoffAmount[i]  =0.0;    	
    		payPrincipalPayoffAmount[i] =0.0;
    		payInterestPayoffAmount[i]  =0.0;
    		
    		if( this.isLongPosition == INTEGER_TRUE ) {
		    	
    			if(i==0) {
    				
    			}
    			else if(i==1) {
    		    	payPrincipalPayoffAmount[i] =fwdFaceValue;
    			}
    			else if(i < ktbPayoffDate.length ) {
    		    	recInterestPayoffAmount[i]   = ktbFaceValue * ktbCouponRate / ktbPaymentFreq;    	
    			}
    			else if(i == ktbPayoffDate.length ) {
    				recPrincipalPayoffAmount[i] = ktbFaceValue;    	
    		    	recInterestPayoffAmount[i]  = ktbFaceValue * ktbCouponRate / ktbPaymentFreq;    	
    			}
    			
    		}
    		else {
    			if(i==0) {
    				
    			}
    			else if(i==1) {
    		    	recPrincipalPayoffAmount[i] =fwdFaceValue;
    			}
    			else if(i < ktbPayoffDate.length ) {
    		    	payInterestPayoffAmount[i]   = ktbFaceValue * ktbCouponRate / ktbPaymentFreq;    	
    			}
    			else if(i == ktbPayoffDate.length ) {
    				payPrincipalPayoffAmount[i] = ktbFaceValue;    	
    		    	payInterestPayoffAmount[i]  = ktbFaceValue * ktbCouponRate / ktbPaymentFreq;    	
    			}
    		}
    		
    		recPayoffAmount[i] =  recPrincipalPayoffAmount[i] + recInterestPayoffAmount[i];
    		payPayoffAmount[i] =  payPrincipalPayoffAmount[i] + payInterestPayoffAmount[i];
    		netPayoffAmount[i] =  recPayoffAmount[i] - payPayoffAmount[i] ;
    	}
    	
		
	    log.info("Payoff in KtbFwd : {},{},{},{},{}, {}", this.ktbSpotPrice, this.fwdSpotPrice,  netPayoffDate, this.recPayoffAmount, this.payPayoffAmount);
    }    

    
    
    
	/**
	 * This method is applied to Two Cases
	 * 1. In Calibration Process, Called internally from Method Calculating implied Spread with baseRate
	 * 2. After Calibration Process, Used directly in finding KTB FUTURES price according to each scenario with static implied Spread 
	 */
//    private double getKtbFwdUnitPrice(double ytm) throws Exception {
//    	
//    	double pvYtm = 0.0;
//    	
//    	for(int i=1; i<=this.cfSize; i++) {
//    		pvYtm += (this.intRate * 100.0 / this.paymentFreq) / Math.pow((1.0 + ytm / this.paymentFreq), i);
//    	}
//    	pvYtm += 100.0 / Math.pow((1.0 + ytm / this.paymentFreq), cfSize);
//    	
//    	return pvYtm;    	
//    }
    
    
//    private double getKtbFwdPrice(double ytm) throws Exception {
//    	
//    	double pvYtm = 0.0;
//    	
//    	for(int i=1; i<=this.cfSize; i++) {
//    		pvYtm += (this.intRate * 100.0 / this.paymentFreq) / Math.pow((1.0 + ytm / this.paymentFreq), i);
//    	}
//    	pvYtm += 100.0 / Math.pow((1.0 + ytm / this.paymentFreq), cfSize);
//    	
//    	return pvYtm;    	
//    }
    
//    @Override
//	protected double getPresentValue(Integer legType) throws Exception {
//		
//		switch(legType) {		
//		
//		    case REC_LEG_KEY: return this.recExtUprc 
//		    case PAY_LEG_KEY: return this.payExtUprc 
//		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY);		    
//		    default         : return 0.0;
//		}		
//	}
//    
//    @Override
//    protected double getPresentValueClean(Integer legType) throws Exception {
//
//    	switch(legType) {		
//		
//		    case REC_LEG_KEY: return this.recPayoffAmount[1];
//		    case PAY_LEG_KEY: return this.payPayoffAmount[1];		    	    
//		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY) - this.getVltAmt();
//		    default         : return 0.0;
//		}		    	
//    }
    
//    @Override
//    protected double getEffectiveMaturity1(Integer legType) throws Exception {
//    	return TimeUtil.getTimeFactor(this.baseDate, this.baseDate.plusDays(1), EDayCountBasis.DEFAULT.getDcbCode());
//    }    

    /**
     * this.spotPriceBase 는 국채선물의 경우 현재 시장가격이다. 이 시장가를 기준으로 현재 적용되는 내재 YTM(적용선도금리)를 도출하는 과정을 거친다.
     * this.spotPrice/PriceBase는 주식충격과 무관한 값이다. KTB FUTURES의 경우 100원으로 환산된 시장가격임.
     */
    
    @Override
    protected double getImpliedSpread() throws Exception {
//    	log.info("Imp in Ktb fwd: {}", spotPrice);
    	return getImpliedSpread(this.spotPrice);
    }    
    
    @Override
    protected double getImpliedSpread(double targetUnitPrice) throws Exception {
    	
    	log.info("Imp in Ktb fwd1: {},{}", spotPrice, targetUnitPrice);
    	return getImpliedSpread(targetUnitPrice, INITIAL_GUESS);    	
    }    
    
    @Override
    protected double getKtbForwardRate() throws Exception {
    	return this.getAppliedBaseRate() + this.getImpliedSpread();
    }
    
    
//    @Override
    public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {
		
		log.info("getValuation in KTB FWD: {},{}", currencyType);
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
}
