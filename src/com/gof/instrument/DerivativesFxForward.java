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
public class DerivativesFxForward extends DerivativesAbstract {
	
	public DerivativesFxForward(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;	
	}	
	
	public DerivativesFxForward(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}
	
	public DerivativesFxForward() {
		super();		
		this.financialElements = EFinElements.SWAP.getEFinElementList();
	}
	
	
	protected void setAttributes() throws Exception {
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;				
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
    	
    	for(int i=0; i<recPayoffDate.length; i++) {
    		
    		if(i == 0) {
    			recPrincipalPayoffAmount[0] = 0.0;
    			recInterestPayoffAmount [0] = 0.0;
    			
    			payPrincipalPayoffAmount[0] = 0.0;
    			payInterestPayoffAmount [0] = 0.0;
    		}
    		else {    			
    			recPrincipalPayoffAmount[i] = (i == recPayoffDate.length-1) ? this.recAmt : 0.0;    			
    			recInterestPayoffAmount [i] = 0.0;
    			
    			payPrincipalPayoffAmount[i] = (i == payPayoffDate.length-1) ? this.payAmt : 0.0;    			
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
		
		//for(int i=0; i<netPayoffDate.length; i++) log.info("{}, {}, {}", i, this.netPayoffDate[i], this.netPayoffAmount[i]);
    }   
  
}
