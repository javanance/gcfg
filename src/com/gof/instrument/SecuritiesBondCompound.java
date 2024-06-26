package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.enums.EInstrument;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class SecuritiesBondCompound extends SecuritiesBondAbstract {	

	private LocalDate transDate;

	public SecuritiesBondCompound(LocalDate baseDate) {		
		super();		
		this.baseDate = baseDate;
	}
	
	public SecuritiesBondCompound(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public SecuritiesBondCompound() {
		super();		
	}
	
	
	protected void setAttributes() throws Exception {				
		
		this.cmpdPeriod         = this.paymentTerm;		
		this.cmpdPeriodType     = TIME_UNIT_MONTH;	
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		switch(EInstrument.getEInstrument(this.instTpcd + String.valueOf(this.instDtlsTpcd)).getInstCode()) {		
		
			case INST_BOND_SCB : { // 102
				this.cmpdMethod = CMPD_MTD_SIMP;
				break;    
			}
			case INST_BOND_DCB : { // 103
				this.cmpdMethod = CMPD_MTD_DISC; 
				break;			
			}
			case INST_BOND_C5S2: { // 110
				this.cmpdMethod = CMPD_MTD_EXOTIC;
				this.transDate = this.issueDate.plusYears(5);
				break;
			}
			default: {
				log.warn("Check the Compound Method of Compound Type Bond: [EXPO_ID: {}]", this.expoId);
				this.cmpdMethod = CMPD_MTD_SIMP;
				break;
			}
		}	
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
	}
	
	
	protected void setPayoffDate() throws Exception {
		
		this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		
		if(this.payoffDate == null || this.payoffDate.length < 1) log.warn("Check the Instrument Infomation: [{}]", this.expoId);
	}	
	
	
    protected void setPayoffAmount() throws Exception {
    	
    	principalPayoffAmount = new double[payoffDate.length];   	
    	interestPayoffAmount  = new double[payoffDate.length];    	

    	double timeFactor       = 0.0;    	
    	double timeFactorSplit1 = 0.0;
    	double timeFactorSplit2 = 0.0;
    	
    	for(int i=0; i<payoffDate.length; i++) {
    		
    		if(i == 0) {    			
    			principalPayoffAmount[0] = 0.0;
    			interestPayoffAmount[0]  = 0.0;    			
    		}
    		else {    			    			    			
    			principalPayoffAmount[i] = (i == payoffDate.length-1) ? this.notionalCurrent * this.maturityPremium: 0.0;
    			
    			if(this.cmpdMethod != CMPD_MTD_EXOTIC) {
    				timeFactor  = TimeUtil.getTimeFactor(payoffDate[i-1], payoffDate[i], this.dayCountBasis);
        			interestPayoffAmount[i]  = this.notionalCurrent 
			                                   * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, timeFactor);    				
    			}
    			else {
    				timeFactorSplit1 = TimeUtil.getTimeFactor(payoffDate[i-1], transDate, this.dayCountBasis);    				
        			interestPayoffAmount[i]  = this.notionalCurrent 
			                                   * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, CMPD_MTD_DISC, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, timeFactorSplit1);
        			
        			timeFactorSplit2 = TimeUtil.getTimeFactor(transDate, payoffDate[i],   this.dayCountBasis);
        			interestPayoffAmount[i] += this.notionalCurrent
			                  	               * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, CMPD_MTD_SIMP, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, timeFactorSplit2);
    			}
    			//log.info("{},{}", this.principalPayoffAmount[i], this.interestPayoffAmount[i]);
    		}    	    		
    	}    	    	
    	payoffDate   = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payoffAmount = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();    	
    	
    	if(this.payoffAmount == null || this.payoffAmount.length < 1) log.warn("Check the Instrument Information: [{}]", this.expoId);
    }
    
    
    @Override
    protected double getAccruedInterest(LocalDate baseDate, LocalDate[] dates, double[] cashflows, int dayCountBasis) throws Exception {		
    	
    	double accruedInterest = 0.0;
		double tfNumerator     = 0.0;    	
    	double tfSplit1        = 0.0;
    	double tfSplit2        = 0.0;

		int addOneDay = 1;
		
		if(baseDate.isBefore(dates[0])) return 0.0;		
		
		for(int i=1; i<dates.length; i++) {			
			
		    if(baseDate.isBefore(dates[i])) {		    	
		    	
		    	if(this.cmpdMethod != CMPD_MTD_EXOTIC) {
			    	tfNumerator = TimeUtil.getTimeFactor(dates[i-1].minusDays(0), baseDate.plusDays(addOneDay), dayCountBasis);
			    	accruedInterest = this.notionalCurrent 
			                          * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, tfNumerator);
			    	//log.info("DCB: {}, date[i-1]: {}, baseDate+1 : {}, tfNumerator: {}, accrued: {}", dayCountBasis, dates[i-1], baseDate.plusDays(addOneDay), tfNumerator, accruedInterest);
		    	}
		    	else {
		    		if(this.baseDate.isBefore(this.transDate)) {
		    			tfSplit1 = TimeUtil.getTimeFactor(dates[i-1].minusDays(0), baseDate.plusDays(addOneDay), dayCountBasis);
				    	accruedInterest = this.notionalCurrent 
				                          * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, CMPD_MTD_DISC, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, tfSplit1);		    			
		    		}
		    		else {
		    			tfSplit1 = TimeUtil.getTimeFactor(dates[i-1].minusDays(0), transDate.plusDays(0), dayCountBasis);
		    			tfSplit2 = TimeUtil.getTimeFactor(transDate.minusDays(0),  baseDate.plusDays(addOneDay), dayCountBasis);
				    	accruedInterest  = this.notionalCurrent 
                                           * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, CMPD_MTD_DISC, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, tfSplit1);
				    	accruedInterest += this.notionalCurrent
				    			           * TimeUtil.getIntFactor(ODD_COUPON, this.iRate, CMPD_MTD_SIMP, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, tfSplit2);
		    		}
		    	}		    	
		    	return accruedInterest;
		    }		    
		}	
    	return accruedInterest;
    }

}

