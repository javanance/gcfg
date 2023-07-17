package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EFinElements;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class DerivativesIndexFutures extends DerivativesAbstract {	
	
	protected double      faceValue;
	protected double      recFuturesSpotPrice;
	protected double      payFuturesSpotPrice;

	
	public DerivativesIndexFutures(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;	
	}
	
	public DerivativesIndexFutures(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);		
	}	
	
	public DerivativesIndexFutures() {
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
			
			this.recFuturesSpotPrice = this.spotPrice;			
			this.payFuturesSpotPrice = this.contractPrice;			
		}
		else if(this.isLongPosition == INTEGER_FALSE){	
			
			this.recFuturesSpotPrice = this.contractPrice;			 
			this.payFuturesSpotPrice = this.spotPrice;
		}		
		else log.error("INDEX FUTURES Instrument MUST HAVE Long/Short Position Information: EXPO_ID = [{}]", this.expoId);		
		
		for(int i=0; i<recPayoffDate.length; i++) {
    		
    		if(i == 0) {
    			recPrincipalPayoffAmount[0] = 0.0;
    			recInterestPayoffAmount [0] = 0.0;
    			
    			payPrincipalPayoffAmount[0] = 0.0;
    			payInterestPayoffAmount [0] = 0.0;
    		}
    		else {    			
    			recPrincipalPayoffAmount[i] = (i == recPayoffDate.length-1) ? this.faceValue * this.recFuturesSpotPrice : 0.0;    			
    			recInterestPayoffAmount [i] = 0.0;
    			
    			payPrincipalPayoffAmount[i] = (i == payPayoffDate.length-1) ? this.faceValue * this.payFuturesSpotPrice : 0.0;    			
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
	    
	    //log.info("{},{},{},{},{}", this.faceValue, this.recFuturesSpotPrice, this.recPrincipalPayoffAmount, this.payFuturesSpotPrice, this.payPrincipalPayoffAmount);
    }        

    @Override
	protected double getPresentValue(Integer legType) throws Exception {
		
		switch(legType) {		
		
		    case REC_LEG_KEY: return this.recPayoffAmount[1];
		    case PAY_LEG_KEY: return this.payPayoffAmount[1];
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY);		    
		    default         : return 0.0;
		}		
	}
    
    @Override
    protected double getPresentValueClean(Integer legType) throws Exception {

    	switch(legType) {		
		
		    case REC_LEG_KEY: return this.recPayoffAmount[1];
		    case PAY_LEG_KEY: return this.payPayoffAmount[1];
		    case NET_LEG_KEY: return getPresentValue(REC_LEG_KEY) - getPresentValue(PAY_LEG_KEY) - this.getVltAmt();		    
		    default         : return 0.0;
		}		    	
    }
    
    @Override
    protected double getEffectiveMaturity(Integer legType) throws Exception {
    	return TimeUtil.getTimeFactor(this.baseDate, this.baseDate.plusDays(1), EDayCountBasis.DEFAULT.getDcbCode());
    }    

}
