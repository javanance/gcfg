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
     * this.baseRate -> ��ä���� ���缱���ݸ��� ���ϴ� �������� ���缱���ݸ� ��ü���ٴ� ���ؽó�����(Rf Spot)�� �������� �ϴ� spread�� ���ϱ� ���� �������� �����Ͽ���
     * ���� �ݸ��ó��������� Rf�ó��������� ������ spread�� ���ϴ� ������ ���� --> �ᱹ �̰��� implied Spread�� ������ ������ �� ���� (spread�� �������� baseRate�� ������ �ƴϹǷ�)  
     * Ķ���극�̼� ������ impliedSpread �� DerivativesAbstract�� ��ġ��, ���� ���αݸ����� �������� ���õ�. ��, appliedBaseRate + impliedSpread (from DerivativesAbstarct)�� �Բ� unit price��꿡 ��
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
     * this.spotPriceBase �� ��ä������ ��� ���� ���尡���̴�. �� ���尡�� �������� ���� ����Ǵ� ���� YTM(���뼱���ݸ�)�� �����ϴ� ������ ��ģ��.
     * this.spotPrice/PriceBase�� �ֽ���ݰ� ������ ���̴�. KTB FUTURES�� ��� 100������ ȯ��� ���尡����.
     *  this.spotPriceBase는 기준 가격을 나타냅니다. 현재 가격인 this.spotPrice가 기준 가격에서 얼마나 벗어났는지를 나타냅니다. 
     *  이는 현재 가격이 기준 가격에 비해 얼마나 낮거나 높은지를 나타내며, 이는 현재 가격이 기준 가격을 기준으로 한 YTM(만기수익률)에 기반하여 모델링되었습니다.
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
