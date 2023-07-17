package com.gof.instrument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.dao.RefRateDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.RefRateCoef;
import com.gof.entity.RefRateMap;
import com.gof.util.FwdRateUtil;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public class SecuritiesBondFrnCoupon extends SecuritiesBondAbstract {	

	private String bssd;
	private List<RefRateMap> refRateList;
	private List<RefRateCoef> refRateCoefList;
	private String refRateId;
	private double intercept;
	private double slope;
	private Map<Long,Double> ts = new HashMap<Long, Double>(); 
	
	public SecuritiesBondFrnCoupon(LocalDate baseDate) {		
		super();		
		this.baseDate = baseDate;
	}
	
	public SecuritiesBondFrnCoupon(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public SecuritiesBondFrnCoupon() {
		super();		
	}
	
	/**
	 * TODO: this.dcntCmpdPeriod     = this.paymentTerm;  --> 3개월이표채의 경우 12개월로 할인한경우와 비교할 때 adjMaturityDate에도 영향을 미칠 수 있음(duration차이)
	 */
	protected void setAttributes() throws Exception {			
		
		log.info("Set Attr Frn : {},{}");
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		//this.dcntCmpdPeriod     = this.paymentTerm;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;		
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
		
		this.bssd = this.baseDate.format(DateTimeFormatter.BASIC_ISO_DATE).toString();
		this.refRateList 		= RefRateDao.getRefRateMap(bssd, this.expoId.split("SECR_O_")[1]);
		this.refRateCoefList 	= RefRateDao.getRefRateCoef(bssd);
		
		this.refRateId 		= refRateList.size()>0 ? refRateList.get(0).getBaseIrateCd(): "KTB_3M";
		
		this.intercept		= 0.0;
		this.slope			= 1.0;
		
		for(RefRateCoef aa : refRateCoefList) {
			if(aa.getBaseIrateCd().equals(refRateId)) {
				this.intercept		= aa.getIntercept();
				this.slope			= aa.getSlope();
				break;
			}
		}
		
		for(Map.Entry<String, IrCurveHis> entry : scenarioCurveHis.entrySet()) {
			
//			log.info("sce Curve : {}, {}, {}, {}", entry.getValue().getIrCurveId(),  Long.valueOf(entry.getKey().substring(1, 5)), entry.getValue().getIntRate());
			ts.put(Long.valueOf(entry.getKey().substring(1, 5)), entry.getValue().getIntRate());
		}
	}
	

	protected void modifyingFirstCouponDate() throws Exception {
		
		if(this.firstCouponDate != null && this.maturityDate.getDayOfMonth() != this.firstCouponDate.getDayOfMonth()) {
			this.firstCouponDate = TimeUtil.setDays(this.firstCouponDate, this.maturityDate);			
		}		
	}
	
	
	protected void setPayoffDate() throws Exception {
		
		log.info("Set Payoff Date Frn : {},{},{},{},{}", firstCouponDate, issueDate, maturityDate);
		
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
		
		log.info("Set Payoff Date Frn1 : {},{},{},{},{}", usingFirstCouponDate, cfDirection, this.paymentTermType);
		
		if(usingFirstCouponDate) {
		    this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.firstCouponDate, this.paymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		}
		else {
		    this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, this.paymentTerm, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		}	    
		
		if(this.payoffDate == null || this.payoffDate.length < 1) log.warn("Check the Instrument Infomation: [{}]", this.expoId);
		
//		for(int i=0; i<payoffDate.length; i++) {
//			log.info("payoff Date :  {}", payoffDate[i]);
//		}
	}	
	
	
    protected void setPayoffAmount() throws Exception {
    	
    	principalPayoffAmount = new double[payoffDate.length];   	
    	interestPayoffAmount  = new double[payoffDate.length];    	
    	
    	boolean isOddCoupon = false;
    	double timeFactor = 0.0;    	
    	
    	double fwdRate		 = 0.0;
    	double frnCouponRate = 0.0;
    	
    	log.info("in Set Payoff in Frn :  {}, {}", this.scenNum); 
    	
    	for(int i=0; i<payoffDate.length; i++) {
    		
//    		if(i <= 1) {
//    			frnCouponRate = this.iRate;
//    			log.info("Frn coupon :{},{},{}", i, payoffDate[i], iRate);
//    		}
//    		else {
//    			fwdRate = FwdRateUtil.getForwardRate(bssd, paymentTerm, payoffDate[i], ts);
//    			frnCouponRate = slope * fwdRate + intercept + this.spreadOverCurve;
//    			
//    			log.info("Frn coupon1 :{},{},{},{},{},{},{}", i, payoffDate[i], frnCouponRate, fwdRate, slope, intercept);
//
//    		}
    		
    		if(payoffDate[i].isBefore(baseDate.plusMonths(paymentTerm))) {
				frnCouponRate = this.iRate;
//				log.info("Frn coupon :{},{},{},{},{},{}", expoId, i, payoffDate[i], iRate);
			}
			else {
				fwdRate = FwdRateUtil.getForwardRate(bssd, paymentTerm, payoffDate[i], ts);
				frnCouponRate = slope * fwdRate + intercept + this.spreadOverCurve;
				
//				log.info("Frn coupon1 :{},{},{},{},{},{},{},{},{}", expoId, i, payoffDate[i], frnCouponRate, fwdRate, slope, intercept, refRateId);
	
			}    		
    		if(i == 0) {    			
    			principalPayoffAmount[0] = 0.0;
    			interestPayoffAmount[0]  = 0.0;    			
    		}
    		else {    			
//    			isOddCoupon = TimeUtil.isOddCouponDate(payoffDate[i-1], payoffDate[i], this.paymentTerm, this.paymentTermType);  
    			isOddCoupon = false;
    			timeFactor  = TimeUtil.getTimeFactor(payoffDate[i-1], payoffDate[i], this.dayCountBasis);
    			if(timeFactor > DEF_TIME_FACTOR) isOddCoupon = true;

    			principalPayoffAmount[i] = (i == payoffDate.length-1) ? this.notionalCurrent * this.maturityPremium: 0.0;    			
    	
    			interestPayoffAmount[i]  = this.notionalCurrent 
    					                   * TimeUtil.getIntFactor(isOddCoupon, frnCouponRate, this.cmpdMethod, this.cmpdPeriod, this.cmpdPeriodType, this.paymentTerm, this.paymentTermType, timeFactor);

//    			for validation 
//    			log.info("int cf in Frn : {},{},{},{}", isOddCoupon, payoffDate[i], frnCouponRate, timeFactor, interestPayoffAmount[i]);
    		}    	    		
    	}    	    	
    	payoffDate   = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payoffAmount = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();    	
    	
    	if(this.payoffAmount == null || this.payoffAmount.length < 1) log.warn("Check the Instrument Information: [{}]", this.expoId);
    	
    	//for(int i=0; i<payoffDate.length; i++) log.info("{}, {}, {}", i, this.payoffDate[i], this.payoffAmount[i]);
    }

}

