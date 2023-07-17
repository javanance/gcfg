package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.enums.EFinElements;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class DerivativesCcsForward extends DerivativesAbstract {	
	
	public DerivativesCcsForward(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;
	}
	
	public DerivativesCcsForward(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public DerivativesCcsForward() {
		super();		
		this.financialElements = EFinElements.SWAP.getEFinElementList();
	}
	
	
	protected void setAttributes() throws Exception {		
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		//this.dcntCmpdPeriod     = this.paymentTerm;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;		
		
		//this.maturityDate = LocalDate.of(2042, 1, 20);
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
		else if(!TimeUtil.toYearMonth(this.issueDate.plusMonths(this.recPaymentTerm)).equals(TimeUtil.toYearMonth(this.firstCouponDate))) {
			
			if(this.firstCouponDate.isBefore(this.baseDate)) {
				usingFirstCouponDate = false;
				this.cfDirection = true;
				this.issueDate = this.firstCouponDate;
			}
			else if(this.firstCouponDate.isAfter(this.baseDate) && this.firstCouponDate.minusMonths(2*this.recPaymentTerm).isBefore(this.issueDate)) {
				usingFirstCouponDate = true;				
			}
			else usingFirstCouponDate = false;			
		}
		else {
			usingFirstCouponDate = false;			
			
		}		
		
		if(usingFirstCouponDate) {
		    this.recPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.firstCouponDate, this.recPaymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		    this.payPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.firstCouponDate, this.payPaymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		    this.netPayoffDate = this.recPayoffDate.clone();
		}
		else {
		    this.recPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.recPaymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		    this.payPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.payPaymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		    this.netPayoffDate = this.recPayoffDate.clone();			
		}	    
		
		if(this.recPayoffDate == null || this.recPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);
		if(this.payPayoffDate == null || this.payPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);
	}	
	
	
    protected void setPayoffAmount() throws Exception {
    	
    	recPrincipalPayoffAmount = new double[recPayoffDate.length];   	
    	recInterestPayoffAmount  = new double[recPayoffDate.length];    	
    	payPrincipalPayoffAmount = new double[payPayoffDate.length];
    	payInterestPayoffAmount  = new double[payPayoffDate.length];
    	
    	boolean isOddCoupon = false;
    	double timeFactor = 0.0;
    	
    	for(int i=0; i<recPayoffDate.length; i++) {
    		
    		if(i == 0) {    			
    			recPrincipalPayoffAmount[0] = 0.0;
    			recInterestPayoffAmount [0] = 0.0;    			
    		}
    		else {    			
    			isOddCoupon = TimeUtil.isOddCouponDate(recPayoffDate[i-1], recPayoffDate[i], this.recPaymentTerm, this.paymentTermType);
    			timeFactor  = TimeUtil.getTimeFactor  (recPayoffDate[i-1], recPayoffDate[i], this.recDayCountBasis);
    			
    			recPrincipalPayoffAmount[i] = (i == recPayoffDate.length-1) ? this.recAmt : 0.0;
    			recInterestPayoffAmount [i] = this.recAmt * TimeUtil.getIntFactor
	                                           (isOddCoupon, this.recIRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.recPaymentTerm, this.paymentTermType, timeFactor);    			
    		}    		
    	}    	
    	
    	if(this.isPrincipalSwap == INTEGER_FALSE) for(int i=0; i<recPrincipalPayoffAmount.length; i++) recPrincipalPayoffAmount[i] = 0.0;    	
    	
    	recPayoffDate   = TimeUtil.mergeCashflows(recPayoffDate, recPrincipalPayoffAmount, recPayoffDate, recInterestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	recPayoffAmount = TimeUtil.mergeCashflows(recPayoffDate, recPrincipalPayoffAmount, recPayoffDate, recInterestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();

    	
    	for(int i=0; i<payPayoffDate.length; i++) {
    		
    		if(i == 0) {    			
    			payPrincipalPayoffAmount[0] = 0.0;
    			payInterestPayoffAmount [0] = 0.0;    			
    		}
    		else {    			
    			isOddCoupon = TimeUtil.isOddCouponDate(payPayoffDate[i-1], payPayoffDate[i], this.payPaymentTerm, this.paymentTermType);
    			timeFactor  = TimeUtil.getTimeFactor  (payPayoffDate[i-1], payPayoffDate[i], this.payDayCountBasis);
    			
    			payPrincipalPayoffAmount[i] = (i == payPayoffDate.length-1) ? this.payAmt : 0.0;
    			payInterestPayoffAmount [i] = this.payAmt * TimeUtil.getIntFactor
	                                           (isOddCoupon, this.payIRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.payPaymentTerm, this.paymentTermType, timeFactor);    			
    		}    		
    	}    	
    	
    	if(this.isPrincipalSwap == INTEGER_FALSE) for(int i=0; i<payPrincipalPayoffAmount.length; i++) payPrincipalPayoffAmount[i] = 0.0;
    	
    	payPayoffDate   = TimeUtil.mergeCashflows(payPayoffDate, payPrincipalPayoffAmount, payPayoffDate, payInterestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payPayoffAmount = TimeUtil.mergeCashflows(payPayoffDate, payPrincipalPayoffAmount, payPayoffDate, payInterestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();   	
    	
    	netPayoffDate   = TimeUtil.mergeCashflows(false, recPayoffDate, recPayoffAmount, recFxRate, payPayoffDate, payPayoffAmount, payFxRate).keySet().stream().toArray(LocalDate[]::new);
    	netPayoffAmount = TimeUtil.mergeCashflows(false, recPayoffDate, recPayoffAmount, recFxRate, payPayoffDate, payPayoffAmount, payFxRate).values().stream().mapToDouble(Double::doubleValue).toArray();
    	
		if(this.recPayoffAmount == null || this.recPayoffAmount.length < 1) log.warn("Check the Instrument Infomation(Payoff Amount): [{}]", this.expoId);
		if(this.payPayoffAmount == null || this.payPayoffAmount.length < 1) log.warn("Check the Instrument Infomation(Payoff Amount): [{}]", this.expoId);
    	
    	//for(int i=0; i<netPayoffDate.length; i++) log.info("{}, {}, {}", i, this.netPayoffDate[i], this.netPayoffAmount[i]);
    }    
    
}

