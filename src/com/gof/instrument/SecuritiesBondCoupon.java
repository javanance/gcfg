package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class SecuritiesBondCoupon extends SecuritiesBondAbstract {	

	public SecuritiesBondCoupon(LocalDate baseDate) {		
		super();		
		this.baseDate = baseDate;
	}
	
	public SecuritiesBondCoupon(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public SecuritiesBondCoupon() {
		super();		
	}
	
	/**
	 * TODO: this.dcntCmpdPeriod     = this.paymentTerm;  --> 3개월이표채의 경우 12개월로 할인한경우와 비교할 때 adjMaturityDate에도 영향을 미칠 수 있음(duration차이)
	 */
	protected void setAttributes() throws Exception {			
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		//this.dcntCmpdPeriod     = this.paymentTerm;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;		
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
	}
	

	protected void modifyingFirstCouponDate() throws Exception {
		
		if(this.firstCouponDate != null && this.maturityDate.getDayOfMonth() != this.firstCouponDate.getDayOfMonth()) {
			this.firstCouponDate = TimeUtil.setDays(this.firstCouponDate, this.maturityDate);			
		}		
	}
	
	
	protected void setPayoffDate() throws Exception {
		
		boolean usingFirstCouponDate = false;		
		//modifyingFirstCouponDate();		
		
		if(this.firstCouponDate == null) {
			usingFirstCouponDate = false;		
		}
		else if(!TimeUtil.toYearMonth(this.issueDate.plusMonths(this.paymentTerm)).equals(TimeUtil.toYearMonth(this.firstCouponDate))) {
			
			if(this.firstCouponDate.isBefore(this.baseDate)) {
				usingFirstCouponDate = false;
				this.cfDirection = true;
				this.issueDate = this.firstCouponDate;
			}
			else if(this.firstCouponDate.isAfter(this.baseDate) && this.firstCouponDate.minusMonths(2*this.paymentTerm).isBefore(this.issueDate)) {
				usingFirstCouponDate = true;				
			}
			else usingFirstCouponDate = false;			
		}
		else {
			usingFirstCouponDate = false;			
		}		
		
		if(usingFirstCouponDate) {
		    this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.firstCouponDate, this.paymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		}
		else {
		    this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.paymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		}	    
		
		if(this.payoffDate == null || this.payoffDate.length < 1) log.warn("Check the Instrument Infomation: [{}]", this.expoId);
	}	
	
	
    protected void setPayoffAmount() throws Exception {
    	
    	principalPayoffAmount = new double[payoffDate.length];   	
    	interestPayoffAmount  = new double[payoffDate.length];    	
    	
    	boolean isOddCoupon = false;
    	double timeFactor = 0.0;    	
    	
    	for(int i=0; i<payoffDate.length; i++) {
    		
    		if(i == 0) {    			
    			principalPayoffAmount[0] = 0.0;
    			interestPayoffAmount[0]  = 0.0;    			
    		}
    		else {    			
    			isOddCoupon = TimeUtil.isOddCouponDate(payoffDate[i-1], payoffDate[i], this.paymentTerm, this.paymentTermType);    			
    			timeFactor  = TimeUtil.getTimeFactor(payoffDate[i-1], payoffDate[i], this.dayCountBasis);
    			if(timeFactor > DEF_TIME_FACTOR) isOddCoupon = true;

    			principalPayoffAmount[i] = (i == payoffDate.length-1) ? this.notionalCurrent * this.maturityPremium: 0.0;    			
    			interestPayoffAmount[i]  = this.notionalCurrent 
    					                   * TimeUtil.getIntFactor(isOddCoupon, this.iRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, timeFactor);
    		}    	    		
    	}    	    	
    	payoffDate   = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payoffAmount = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();    	
    	
    	if(this.payoffAmount == null || this.payoffAmount.length < 1) log.warn("Check the Instrument Information: [{}]", this.expoId);
    	
    	//for(int i=0; i<payoffDate.length; i++) log.info("{}, {}, {}", i, this.payoffDate[i], this.payoffAmount[i]);
    }

}

