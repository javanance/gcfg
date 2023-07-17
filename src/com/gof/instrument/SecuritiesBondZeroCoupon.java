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
public class SecuritiesBondZeroCoupon extends SecuritiesBondAbstract {	

	public SecuritiesBondZeroCoupon(LocalDate baseDate) {		
		super();		
		this.baseDate = baseDate;
	}
	
	public SecuritiesBondZeroCoupon(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public SecuritiesBondZeroCoupon() {
		super();		
	}
	
	/**
	 * 만기보유 국고채권 중 스트립에 대해서 이슈가 발생함 (외부단가는 dirty와 clean이 같을텐데(할인채) 미수수익이 존재하는 상황임(사내는 아니고 타사분)
	 * SECR_G110KRC0350C20660142010102600001_12720177    // 국고채 만기보유 이표스트립  // KRC0350C2066 (타사만 미수수익 있는 할인채)
     * SECR_G110KR103502G7920142017091900001_12720172    // 국고채 만기보유 원채권       // KR103502G792 (자사 +타사 미수수익 이표채)
     * this.paymentTerm is not necessary
	 */
	protected void setAttributes() throws Exception {				
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;
		this.paymentTermType    = TIME_UNIT_MONTH;
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		this.dcntCmpdPeriod     = MONTH_IN_YEAR;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;		

		//this.bsAmt              = this.bsAmtBase + (this.currency.equals(DEFAULT_CURRENCY) ? this.accrAmt - this.uernAmt : 0);
	}
	
	
	protected void setPayoffDate() throws Exception {
		
		this.payoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);		

		if(this.payoffDate == null || this.payoffDate.length < 1) log.warn("Check the Instrument Infomation: [{}]", this.expoId);
	}	
	
	/**
	 * TODO: 미수이자 중 유가증권미수이자 확인이 필요 현재 로직으로는 경과이자는 무조건 0이고, 실제로 전산상으로도 0임 다만...bs맞추는 과정에서 검토가 필요해보임
	 * 할인채의 외부평가단가는 clean? dirty? 같은거 아닌가? 장부상에 유가증권미수이자가 존재하며....외부평가단가를 고려한 금액은 clean이 아닌듯?
	 * SECR_G110KRC0350C20660142010102600001_12720177(coupon strip)
	 * SECR_G110KR103502G7920142017091900001_12720172(general frb)
	 * 유사한 만기보유증권 중 국고채권(이표채 등)을 살펴봐야 결론 지을 수 있을 듯함
	 */
    protected void setPayoffAmount() throws Exception {
    	
    	principalPayoffAmount = new double[payoffDate.length];   	
    	interestPayoffAmount  = new double[payoffDate.length];    	
    	
    	for(int i=0; i<payoffDate.length; i++) {
    		
    		if(i == 0) {    			
    			principalPayoffAmount[0] = 0.0;
    			interestPayoffAmount [0] = 0.0;    			
    		}
    		else {    			
    			principalPayoffAmount[i] = (i == payoffDate.length-1) ? this.notionalCurrent * this.maturityPremium: 0.0;    			
    			interestPayoffAmount [i] = 0.0;
    		}    	    		
    	}    	    	
    	payoffDate   = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).keySet().stream().toArray(LocalDate[]::new);
    	payoffAmount = TimeUtil.mergeCashflows(payoffDate, principalPayoffAmount, payoffDate, interestPayoffAmount).values().stream().mapToDouble(Double::doubleValue).toArray();    	
    	
    	if(this.payoffAmount == null || this.payoffAmount.length < 1) log.warn("Check the Instrument Information: [{}]", this.expoId);
    	
    	//for(int i=0; i<payoffDate.length; i++) log.info("{}, {}, {}", i, this.payoffDate[i], this.payoffAmount[i]);
    }

}

