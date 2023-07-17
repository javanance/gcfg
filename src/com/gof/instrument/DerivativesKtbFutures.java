package com.gof.instrument;

import java.time.LocalDate;
import java.util.Map;

import com.gof.entity.IrCurveHis;
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
public class DerivativesKtbFutures extends DerivativesAbstract {
	
	protected double      faceValue;	
	protected int         ktbPmtCyc;	
	protected double      appliedBaseRate;
	protected double      recKtbSpotPrice;
	protected double      payKtbSpotPrice;
	protected double      intRate;
	protected int         paymentFreq;
	protected int         cfSize;	

	
	public DerivativesKtbFutures(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;
	}	

	public DerivativesKtbFutures(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super(scenNum, scenarioCurveHis);			
	}	 

	public DerivativesKtbFutures() {
		super();		
		this.financialElements = EFinElements.KTB_FUTURES.getEFinElementList();
	}	
	
	
	protected void setAttributes() throws Exception {		 
		
		this.cmpdPeriod         = DEF_CMPD_PERIOD_YEAR;
		this.cmpdPeriodType     = TIME_UNIT_YEAR;		
		this.cmpdMethod         = CMPD_MTD_SIMP;		
		this.paymentTermType    = TIME_UNIT_MONTH;		
		
		this.ktbPmtCyc          = (this.isLongPosition == INTEGER_TRUE) ? this.recPaymentTerm : this.payPaymentTerm;

		if(this.ktbMaturityYear == NULL_INT || this.ktbPmtCyc == NULL_INT) {			
			log.warn("Check the Instrument Info of KTB Futures: EXPO_ID = [{}]", this.expoId);
			
			if(this.isinCd != null && this.isinCd.trim().length() == LENGTH_ISIN_CD) {				
				this.ktbMaturityYear = EKtbFuturesInfo.getEKtbFuturesInfo(this.isinCd.substring(3,6)).getMatYear();
				this.ktbPmtCyc       = EKtbFuturesInfo.getEKtbFuturesInfo(this.isinCd.substring(3,6)).getPmtTerm();				
			}			
		}
		
		this.paymentFreq        = 12 / this.ktbPmtCyc;
		this.cfSize             = this.ktbMaturityYear * this.paymentFreq;		
		this.faceValue          = this.contractSize * this.contractMultiple;		
		
		this.dcntCmpdMtd        = CMPD_MTD_DISC;
		this.dcntCmpdPeriod     = this.paymentFreq;
		this.dcntCmpdPeriodType = TIME_UNIT_MONTH;
	}	
		

	protected void setPayoffDate() throws Exception {			
		
		this.recPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		this.payPayoffDate = generateCashflowArray(this.issueDate, this.maturityDate, NON_COUPON_PMT_TERM, this.paymentTermType, this.cfDirection).stream().toArray(LocalDate[]::new);
		this.netPayoffDate = this.recPayoffDate.clone();
		
		if(this.recPayoffDate == null || this.recPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);
		if(this.payPayoffDate == null || this.payPayoffDate.length < 1) log.warn("Check the Instrument Infomation(Payoff Date): [{}]", this.expoId);		
	}	
    
    /**
     * TODO: protected double getKtbForwardRate() {return getAppliedBaseRate() + getImpliedSpread();}
     * this.baseRate -> 국채선물 내재선도금리를 구하는 과정에서 내재선도금리 자체보다는 기준시나리오(Rf Spot)을 기준으로 하는 spread를 구하기 위한 목적으로 도입하였음
     * 이후 금리시나리오에서 Rf시나리오값에 결정된 spread를 더하는 식으로 진행 --> 결국 이것이 implied Spread와 동일한 개념이 될 것임 (spread가 고정이지 baseRate가 고정은 아니므로)  
     * 캘리브레이션 수행후 impliedSpread 는 DerivativesAbstract를 거치며, 실제 할인금리값에 더해져서 세팅됨. 즉, appliedBaseRate + impliedSpread (from DerivativesAbstarct)가 함께 unit price계산에 들어감
     */
	protected void setPayoffAmount() throws Exception {	    
	    
    	recPrincipalPayoffAmount = new double[recPayoffDate.length];   	
    	recInterestPayoffAmount  = new double[recPayoffDate.length];    	
    	payPrincipalPayoffAmount = new double[payPayoffDate.length];
    	payInterestPayoffAmount  = new double[payPayoffDate.length];    	
    	
		if(this.isLongPosition == INTEGER_TRUE) {
			
			this.appliedBaseRate = TimeUtil.getDiscountRate(this.recMatTerm, this.recMatTermIntRate, this.issueDate, this.maturityDate);			
			this.intRate         = this.recIRate;			
			this.recKtbSpotPrice = (this.isRecIrCalibration == INTEGER_TRUE) ? this.getKtbUnitPrice(this.appliedBaseRate) : this.spotPriceForNonStock;
			this.payKtbSpotPrice = this.contractPrice;
		}
		else if(this.isLongPosition == INTEGER_FALSE) {
			
			this.appliedBaseRate = TimeUtil.getDiscountRate(this.payMatTerm, this.payMatTermIntRate, this.issueDate, this.maturityDate);
			this.intRate         = this.payIRate;
			this.recKtbSpotPrice = this.contractPrice;						
			this.payKtbSpotPrice = (this.isPayIrCalibration == INTEGER_TRUE) ? this.getKtbUnitPrice(this.appliedBaseRate) : this.spotPriceForNonStock;
		}
		else {				
			log.error("KTB FUTURES MUST HAVE Long/Short Position Information: EXPO_ID = [{}]", this.expoId);
			this.recKtbSpotPrice = this.spotPriceForNonStock;
			this.payKtbSpotPrice = this.contractPrice;
		}		
		//log.info("KTB =  POTN:{}, REC:{}, PAY:{}", this.isLongPosition, this.recKtbSpotPrice, this.payKtbSpotPrice);		
		
		for(int i=0; i<recPayoffDate.length; i++) {
    		
    		if(i == 0) {
    			recPrincipalPayoffAmount[0] = 0.0;
    			recInterestPayoffAmount [0] = 0.0;
    			
    			payPrincipalPayoffAmount[0] = 0.0;
    			payInterestPayoffAmount [0] = 0.0;
    		}
    		else {    			
    			recPrincipalPayoffAmount[i] = (i == recPayoffDate.length-1) ? this.faceValue * this.recKtbSpotPrice : 0.0;    			
    			recInterestPayoffAmount [i] = 0.0;
    			
    			payPrincipalPayoffAmount[i] = (i == payPayoffDate.length-1) ? this.faceValue * this.payKtbSpotPrice : 0.0;    			
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
		
	    log.info("Payoff in KtbFuture : {},{},{},{},{}", this.faceValue, this.recKtbSpotPrice, this.recPrincipalPayoffAmount, this.payKtbSpotPrice, this.payPrincipalPayoffAmount);
    }    
    
	/**
	 * This method is applied to Two Cases
	 * 1. In Calibration Process, Called internally from Method Calculating implied Spread with baseRate
	 * 2. After Calibration Process, Used directly in finding KTB FUTURES price according to each scenario with static implied Spread 
	 */
    private double getKtbUnitPrice(double ytm) throws Exception {
    	
    	double pvYtm = 0.0;
    	
    	for(int i=1; i<=this.cfSize; i++) {
    		pvYtm += (this.intRate * 100.0 / this.paymentFreq) / Math.pow((1.0 + ytm / this.paymentFreq), i);
    	}
    	pvYtm += 100.0 / Math.pow((1.0 + ytm / this.paymentFreq), cfSize);
    	
    	return pvYtm;    	
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

    /**
     * this.spotPriceBase 는 국채선물의 경우 현재 시장가격이다. 이 시장가를 기준으로 현재 적용되는 내재 YTM(적용선도금리)를 도출하는 과정을 거친다.
     * this.spotPrice/PriceBase는 주식충격과 무관한 값이다. KTB FUTURES의 경우 100원으로 환산된 시장가격임.
     */
    @Override
    protected double getImpliedSpread() throws Exception {    	
    	return getImpliedSpread(this.spotPriceForNonStock);
    }    
    
    @Override
    protected double getImpliedSpread(double targetUnitPrice) throws Exception {    	
    	return getImpliedSpread(targetUnitPrice, INITIAL_GUESS);    	
    }    
    
    @Override
    protected double getImpliedSpread(double targetUnitPrice, double initGuess) throws Exception {
    	
    	if(this.isRecIrCalibration != INTEGER_TRUE && this.isPayIrCalibration != INTEGER_TRUE) return 0.0;
    	
    	double spreadPos   = 0.0;
    	double spreadNeg   = 0.0;
    	double spreadNew   = 0.0;
    	double fnSpreadPos = 0.0;
    	double fnSpreadNeg = 0.0;
    	double fnSpreadNew = 0.0;
    	
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	spreadPos = Math.min( initGuess,  1.0);
    	spreadNeg = Math.max(-initGuess, -0.9);    	    	

    	fnSpreadPos = getKtbUnitPrice(this.appliedBaseRate + spreadPos) - targetUnitPrice;
    	fnSpreadNeg = getKtbUnitPrice(this.appliedBaseRate + spreadNeg) - targetUnitPrice;    	
    	
    	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) {
    		
    		for(int i=0; i<MAX_INITIAL_GUESS; i++) {    			    			
    			
	        	spreadPos  *=  2.0;
	        	spreadNeg   = -0.9;	        	
	        	fnSpreadPos = getKtbUnitPrice(this.appliedBaseRate + spreadPos) - targetUnitPrice;
	        	fnSpreadNeg = getKtbUnitPrice(this.appliedBaseRate + spreadNeg) - targetUnitPrice;    	    	
	        	
	        	if(fnSpreadPos * fnSpreadNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnSpreadPos) || Double.isNaN(fnSpreadNeg)) return 0.0;
	        	
	        	if(i==0) log.warn("Changing Initial Guess in Calculating Implied Spread: EXPO_ID = [{}]", this.getExpoId());
    		}    		    		
        	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) return 0.0;
    	}

    	spreadNew = 0.5 * (spreadPos + spreadNeg);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		
    		
    		fnSpreadNew = getKtbUnitPrice(this.appliedBaseRate + spreadNew) - targetUnitPrice;    		

    		if(fnSpreadNeg * fnSpreadNew < 0) {
    			
    			fnSpreadPos = fnSpreadNew;
    			spreadPos   = spreadNew;
    			spreadNew   = 0.5 * (spreadPos + spreadNeg);    			
    			if(Math.abs(spreadNew - spreadPos) < ZERO_DOUBLE) return spreadNew;
    		}
        	else if(fnSpreadPos * fnSpreadNew < 0){
        		
        		fnSpreadNeg = fnSpreadNew;
        		spreadNeg   = spreadNew;
        		spreadNew   = 0.5*(spreadPos + spreadNeg);        		
        		if(Math.abs(spreadNew - spreadNeg) < ZERO_DOUBLE) return spreadNew;
        	}
        	else return spreadNew;
    	}
    	log.warn("Calculating Implied Spread is failed: [{}]", this.getExpoId());    	
    	return 0.0;
    }
    
    @Override
    protected double getKtbForwardRate() throws Exception {
    	return this.getAppliedBaseRate() + this.getImpliedSpread();
    }
    
}
