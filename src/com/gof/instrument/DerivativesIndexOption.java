package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EFinElements;
import com.gof.util.PricingUtil;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class DerivativesIndexOption extends DerivativesAbstract {	
	
	protected double      faceValue;
	protected boolean     isCallOption;
	protected double      appliedBaseRate;
	protected double      appliedTimeFactor;
	protected double      recOptionSpotPrice;
	protected double      payOptionSpotPrice;

	
	public DerivativesIndexOption(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;	
	}	
	
	public DerivativesIndexOption(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);		
	}	
	
	public DerivativesIndexOption() {
		super();
		this.financialElements = EFinElements.FIDE_EXCG.getEFinElementList();
	}

	
	protected void setAttributes() throws Exception {		
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
		
		this.faceValue          = this.contractSize * this.contractMultiple;
		this.isCallOption       = this.optTpcd.equals(CALL_OPTION) ? true : false;		
				
		this.appliedBaseRate    = TimeUtil.getDiscountRate(this.recMatTerm, this.recMatTermIntRate, this.issueDate, this.maturityDate);
	   	this.appliedTimeFactor  = TimeUtil.getTimeFactor(baseDate, maturityDate, this.recDayCountBasis);
		
	   	//TODO: optionVol은 시나리오별로도 유지가 되어야함..그래야 여기에 option Shock을 줄수가 있음...
	   	this.optionVol          = getOptionImpliedSigma(); 
	}	
		
    
    protected void setPayoffDate() throws Exception {		
			
		this.recPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		this.payPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		this.netPayoffDate = this.recPayoffDate.clone();
		
		if(this.recPayoffDate == null || this.recPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);
		if(this.payPayoffDate == null || this.payPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);
    }
    
	
    protected void setPayoffAmount() throws Exception {	    
	    
    	recPrincipalPayoffAmount = new double[recPayoffDate.length];   	
    	recInterestPayoffAmount  = new double[recPayoffDate.length];    	
    	payPrincipalPayoffAmount = new double[payPayoffDate.length];
    	payInterestPayoffAmount  = new double[payPayoffDate.length];    	
    	
		if(this.isLongPosition == INTEGER_TRUE) {			
			this.payOptionSpotPrice = this.contractPrice;			
			this.recOptionSpotPrice = (this.isStockShock == INTEGER_TRUE) ? getOptionPrice() : this.spotPrice;
		}
		else if(this.isLongPosition == INTEGER_FALSE){			
			this.recOptionSpotPrice = this.contractPrice;
			this.payOptionSpotPrice = (this.isStockShock == INTEGER_TRUE) ? getOptionPrice() : this.spotPrice;			
		}		
		else log.error("INDEX OPTION MUST HAVE Long/Short Position Information: EXPO_ID: [{}]", this.expoId);
		
		for(int i=0; i<recPayoffDate.length; i++) {
    		
    		if(i == 0) {
    			recPrincipalPayoffAmount[0] = 0.0;
    			recInterestPayoffAmount [0] = 0.0;
    			
    			payPrincipalPayoffAmount[0] = 0.0;
    			payInterestPayoffAmount [0] = 0.0;
    		}
    		else {    			
    			recPrincipalPayoffAmount[i] = (i == recPayoffDate.length-1) ? this.faceValue * this.recOptionSpotPrice : 0.0;    			
    			recInterestPayoffAmount [i] = 0.0;
    			
    			payPrincipalPayoffAmount[i] = (i == payPayoffDate.length-1) ? this.faceValue * this.payOptionSpotPrice : 0.0;    			
    			payInterestPayoffAmount [i] = 0.0;
    		}
    	}	    
	    
    	recPayoffDate   = TimeUtil.mergeCashflows(recPayoffDate, recPrincipalPayoffAmount, recPayoffDate, recInterestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	recPayoffAmount = TimeUtil.mergeCashflows(recPayoffDate, recPrincipalPayoffAmount, recPayoffDate, recInterestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();

    	payPayoffDate   = TimeUtil.mergeCashflows(payPayoffDate, payPrincipalPayoffAmount, payPayoffDate, payInterestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payPayoffAmount = TimeUtil.mergeCashflows(payPayoffDate, payPrincipalPayoffAmount, payPayoffDate, payInterestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();   	
    	
    	netPayoffDate   = TimeUtil.mergeCashflows(false, recPayoffDate, recPayoffAmount, recFxRate, payPayoffDate, payPayoffAmount, payFxRate).keySet().stream().toArray(LocalDate[]::new);
    	netPayoffAmount = TimeUtil.mergeCashflows(false, recPayoffDate, recPayoffAmount, recFxRate, payPayoffDate, payPayoffAmount, payFxRate).values().stream().mapToDouble(Double::doubleValue).toArray();
    	
		if(this.recPayoffAmount == null || this.recPayoffAmount.length < 1) log.warn("Check the Instrument Infomation(Payoff Amount): [{}]", this.expoId);
		if(this.payPayoffAmount == null || this.payPayoffAmount.length < 1) log.warn("Check the Instrument Infomation(Payoff Amount): [{}]", this.expoId);		
	    
	    //log.info("{},{},{},{},{}", this.faceValue, this.recOptionSpotPrice, this.recPrincipalPayoffAmount, this.payOptionSpotPrice, this.payPrincipalPayoffAmount);	    
    }    
    
    @Override
	protected double getPresentValue(Integer legType) {
		
		switch(legType) {		
		
		    case REC_LEG_KEY: return this.recPayoffAmount[1];
		    case PAY_LEG_KEY: return this.payPayoffAmount[1];
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY);		    
		    default         : return 0.0;
		}		
	}
    
    @Override
    protected double getPresentValueClean(Integer legType) {

    	switch(legType) {		
		
		    case REC_LEG_KEY: return this.recPayoffAmount[1];
		    case PAY_LEG_KEY: return this.payPayoffAmount[1];
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY) - this.getVltAmt();		    
		    default         : return 0.0;
		}		    	
    }
    
    @Override
    protected double getEffectiveMaturity(Integer legType) {
    	return TimeUtil.getTimeFactor(this.baseDate, this.baseDate.plusDays(1), EDayCountBasis.DEFAULT.getDcbCode());
    }    
    
    @Override
    protected double getOptionImpliedSigma() throws Exception {
    	return PricingUtil.getImpliedSigma(isCallOption, this.underlyingSpotPrice, this.underlyingExercisePrice, this.appliedBaseRate, this.appliedTimeFactor, this.spotPrice);
    }
    

    protected double getOptionPrice() throws Exception {
    	return PricingUtil.optionPrice(isCallOption,  this.underlyingSpotPrice, this.underlyingExercisePrice, this.appliedBaseRate, this.optionVol, this.appliedTimeFactor);
    }
    
}
