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
	 * ���⺸�� ����ä�� �� ��Ʈ���� ���ؼ� �̽��� �߻��� (�ܺδܰ��� dirty�� clean�� �����ٵ�(����ä) �̼������� �����ϴ� ��Ȳ��(�系�� �ƴϰ� Ÿ���)
	 * SECR_G110KRC0350C20660142010102600001_12720177    // ����ä ���⺸�� ��ǥ��Ʈ��  // KRC0350C2066 (Ÿ�縸 �̼����� �ִ� ����ä)
     * SECR_G110KR103502G7920142017091900001_12720172    // ����ä ���⺸�� ��ä��       // KR103502G792 (�ڻ� +Ÿ�� �̼����� ��ǥä)
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
	 * TODO: �̼����� �� �������ǹ̼����� Ȯ���� �ʿ� ���� �������δ� ������ڴ� ������ 0�̰�, ������ ��������ε� 0�� �ٸ�...bs���ߴ� �������� ���䰡 �ʿ��غ���
	 * ����ä�� �ܺ��򰡴ܰ��� clean? dirty? ������ �ƴѰ�? ��λ� �������ǹ̼����ڰ� �����ϸ�....�ܺ��򰡴ܰ��� ����� �ݾ��� clean�� �ƴѵ�?
	 * SECR_G110KRC0350C20660142010102600001_12720177(coupon strip)
	 * SECR_G110KR103502G7920142017091900001_12720172(general frb)
	 * ������ ���⺸������ �� ����ä��(��ǥä ��)�� ������� ��� ���� �� ���� ����
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

