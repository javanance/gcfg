package com.gof.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsAssetSecr;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EFinElements;
import com.gof.util.GeneralUtil;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public abstract class SecuritiesBondAbstract extends SecuritiesAbstract {			
	
	public SecuritiesBondAbstract(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super();
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;		
	}		
	
	public SecuritiesBondAbstract() {				
		super();
		this.scenNum              = 0;
		this.scenarioCurveHis     = null;
		this.fxTypCd              = null;
		this.fxVal                = null;
		this.adjMaturityDate      = null;
		this.isFirstValuation     = true;
		this.toBeAdjustedMaturity = true;
		this.impDura              = 0.0;				
		this.financialElements    = EFinElements.BOND.getEFinElementList();		
	}
	
	
	
	@Override
	protected void setInstrumentEntities(KicsAssetSecr entity) throws Exception {

		this.baseDate         = setDate(entity.getBaseDate());
		this.expoId           = entity.getExpoId();		
		this.fundCd           = entity.getFundCd();		
		this.prodTpcd         = entity.getProdTpcd();		
		this.accoCd           = entity.getAccoCd();				
		this.isinCd           = entity.getIsinCd();		
		this.deptCd           = entity.getDeptCd();
		this.instTpcd         = entity.getInstTpcd();		
		this.instDtlsTpcd     = GeneralUtil.objectToPrimitive(entity.getInstDtlsTpcd());		
		
		this.issueDate        = setDate(entity.getIssuDate(), this.baseDate);		
		this.maturityDate     = setDate(entity.getMatrDate(), this.baseDate.plusDays(1));		
		if(this.maturityDate.isBefore(this.issueDate.plusDays(1))) { 
			this.maturityDate = this.issueDate.plusDays(1);
			//this.maturityDate = this.issueDate.plusYears(1);
			log.warn("MaturityDate must be after IssueDate in [{}]", this.expoId);			
		}		
		if(this.maturityDate.isBefore(this.baseDate.plusDays(1))) { 
			this.maturityDate = this.baseDate.plusDays(1);
			log.warn("MaturityDate must be after BaseDate in [{}]", this.expoId);			
		}		
		this.orgMaturityDate  = this.maturityDate;
		this.adjMaturityDate  = this.maturityDate;
		this.firstCouponDate  = setDateOrNull(entity.getFrstIntDate());		
		if(this.firstCouponDate != null && this.firstCouponDate.isBefore(this.issueDate)) this.firstCouponDate = null;				
				
		this.cfDirection             = false;		
		this.isStockShock            = INTEGER_FALSE;
		this.underlyingExercisePrice = GeneralUtil.objectToPrimitive(entity.getUndlExecPrc());
		this.underlyingSpotPrice     = GeneralUtil.objectToPrimitive(entity.getUndlSpotPrc());
		this.underlyingSpotPriceBase = this.underlyingSpotPrice;
		this.embeddedOption          = GeneralUtil.objectToPrimitive(entity.getOptEmbTpcd());
		this.optStrDate              = setDateOrNull(entity.getOptStrDate());
		this.optEndDate              = setDateOrNull(entity.getOptEndDate());
		if(this.optStrDate != null) {
			if(!this.maturityDate.isAfter(this.optStrDate) || !this.baseDate.isBefore(this.optStrDate)) this.optStrDate = null; 
		}						
		
		this.currency                = GeneralUtil.objectToPrimitive(entity.getCrnyCd(),   DEF_CURRENCY);
		this.fxRate                  = GeneralUtil.objectToPrimitive(entity.getCrnyFxrt(), DEF_FX_RATE);		
		this.spotPrice               = GeneralUtil.objectToPrimitive(entity.getCrnyFxrt(), DEF_UNIT_PRICE);
		this.fxRateBase              = this.fxRate;
		this.spotPriceBase           = this.spotPrice;
		this.dayCountBasis           = EDayCountBasis.getEDayCountBasis(entity.getDcbCd()).getDcbCode();
		                             
		this.iRate                   = GeneralUtil.objectToPrimitive(entity.getIrate());		
		this.paymentTerm             = GeneralUtil.objectToPrimitive(entity.getIntPayCyc(), DEF_PMT_TERM_MONTH);
		                             
		this.maturityPremium         = GeneralUtil.objectToPrimitive(entity.getMatrPrmu(),  DEF_UNIT_PRICE);
		this.maturityPremiumOrg      = this.maturityPremium;
		this.amortTerm               = GeneralUtil.objectToPrimitive(entity.getAmortTerm(), DEF_PMT_TERM_MONTH);
		this.amortAmt                = GeneralUtil.objectToPrimitive(entity.getAmortAmt());
				                     
		this.spreadOverCurve         = GeneralUtil.objectToPrimitive(entity.getAddSprd());
		this.discountSpread          = 0.0;
		this.isIrCalibration         = INTEGER_FALSE;		

		this.notionalStart           = GeneralUtil.objectToPrimitive(entity.getNotlAmtOrg());
        this.notionalCurrent         = GeneralUtil.objectToPrimitive(entity.getNotlAmt());                                     
        this.bsAmt                   = GeneralUtil.objectToPrimitive(entity.getBsAmt());
        this.fairBsAmt               = GeneralUtil.objectToPrimitive(entity.getFairBsAmt());		
        if(Math.abs(this.fairBsAmt) > ZERO_DOUBLE) {
        	this.notionalCurrent = GeneralUtil.objectToPrimitive(entity.getNotlAmt(), this.fairBsAmt);
        }        
        
		this.accrAmt                 = GeneralUtil.objectToPrimitive(entity.getAccrAmt());
		this.uernAmt                 = GeneralUtil.objectToPrimitive(entity.getUernAmt());
		                             
		this.extUprc                 = GeneralUtil.objectToPrimitive(entity.getExtUprc());
		this.extUprc1                = GeneralUtil.objectToPrimitive(entity.getExtUprc1());
		this.extUprc2                = GeneralUtil.objectToPrimitive(entity.getExtUprc2());
		this.extUprcUnit             = GeneralUtil.objectToPrimitive(entity.getExtUprcUnit());
		this.extDura                 = GeneralUtil.objectToPrimitive(entity.getExtDura());
		this.extDura1                = GeneralUtil.objectToPrimitive(entity.getExtDura1());
		this.extDura2                = GeneralUtil.objectToPrimitive(entity.getExtDura2());		

		this.isRealNumber            = GeneralUtil.objectToPrimitive(entity.getIsRealNumber());
		this.isRealNumber            = true;
		//log.info("real number:{}", this.isRealNumber);
		this.irCurveHis              = new HashMap<String, Map<String, IrCurveHis>>();		
		this.irCurveId               = DEFAULT_KRW_RFCURVE_ID;
		this.irCurve                 = null;

		if(entity.getIrCurve() == null || entity.getIrCurve().isEmpty()) {			
			log.warn("IrCurve Data is not found at {}!!!", baseDate);		
		}
		else if(!entity.getIrCurve().isEmpty() && !entity.getIrCurve().get(0).getIrCurveHis().isEmpty()) {			
			
				this.irCurveId               = entity.getIrCurve().get(0).getIrCurveId();
				this.irCurve                 = entity.getIrCurve();				
				for(IrCurve irc : irCurve) irCurveHis.put(irc.getIrCurveId(), irc.getIrCurveHis());
				//log.info("{}, {}, {}", entity.getIrCurve().get(0).getIrCurveId(), entity.getIrCurve(), entity.getIrCurve().get(0));				
				
				setIrCurve();
		}
		else {
			log.warn("Q_IC_IR_CURVE_HIS Data is not found at {}!!!", baseDate);
		    //log.warn("IrCurveHis Data is not found at {}!!!", baseDate);
		}		
		
		log.info("Set Instrument  in BondAbstract");
	}	
	

	
    @Override
	public void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception {
		
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;
		this.impSpread        = GeneralUtil.objectToPrimitive(spread);		
		this.setIrCurve();				
		log.info("IR Scenario Curve Entities have been set! SCEN_NUM: {},{},{}, {}", this.scenNum,scenarioCurveHis.get("M0001").getIrCurveId(), this.scenarioCurveHis.size());
	}
	
	
	private void setIrCurve() throws Exception {
		
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;				
		
		if(scenarioCurveHis != null && !scenarioCurveHis.isEmpty()) this.setScenarioCurveHis();
		else this.setIrCurveHis();
		
		dcntMatTerm = matTerm;		 
		dcntMatTermIntRate = matTermIntRate;
	}
	
	/**TODO
	 * this.currency.equals(DEFAULT_CURRENCY) --> ������ ��ȭĿ�긦 ���캸�� ���� ���̾�����.. �Ļ��� Ķ���극�̼��� �ϳ��� �ؾ��ϹǷ� �׷��� �ߴ�...(�ϳ���? ��ȭ��?)
	 * isIrCalibration�� FALSE(0)���·� ���ʿ� �ʱ�ȭ�� �ȴ�. 
	 * setScenarioCurveHis�� ȣ��Ǵ� ��츸 implied spread�� �����ϰ� �׿ܿ��� ������� �ʴ´�. �ᱹ standard�϶��� ���Ѵ�?
	 * �����丵�� �ʿ��� ������ �ٸ� Ŭ�������� setter�� ó��??? �������� ����...
	 * setIrCurveHis�� matTermIntRate�� �������带 ó���� �ʿ䰡 �ִ�? ����? (�ʱⰪ�� 0.0�̹Ƿ� ������ ������ ���� ����)
	 */	
	private void setScenarioCurveHis() throws Exception {		
		
		if(this.irCurveId != null) {
			this.discountSpread  = this.impSpread;
			this.isIrCalibration = INTEGER_TRUE;			
			
			matTerm = scenarioCurveHis.keySet().stream().toArray(String[]::new);			
			matTermIntRate = addSpread(scenarioCurveHis.values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.discountSpread);
		}
		else setIrCurveHis();
	}	

	
	private void setIrCurveHis() throws Exception {
		
		//this.toRealScale = TO_REAL_INT_RATE;
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;
		
		if(this.irCurveHis.get(this.irCurveId) != null && !this.irCurveHis.get(this.irCurveId).isEmpty()) {			

			matTerm = this.irCurveHis.get(this.irCurveId).keySet().stream().toArray(String[]::new);
			matTermIntRate = addSpread(this.irCurveHis.get(this.irCurveId).values().stream().map(s -> toRealScale * s.getIntRate()).mapToDouble(Double::doubleValue).toArray(), this.discountSpread);
		}		
		else if(this.irCurveHis.get(this.irCurveId) == null) log.error("IR CURVE ID is NULL in [{}]", this.expoId);
		else if(this.irCurveHis.get(this.irCurveId).isEmpty()) log.error("IR CURVE DATA is Empty in [{}]", this.expoId);
		else log.error("Check the IR CURVE DATA in [{}]", this.expoId);	    
	}	
	
	
	protected void evaluateCashflow() throws Exception {
		
//		log.info(" in evaluation BondAbs: {},{}", embeddedOption);
		setAttributes();
		setPayoffDate();
		setPayoffAmount();			
//		log.info(" in evaluation BondAbs end1: {},{}", embeddedOption, extDura );
		
//		if(this.embeddedOption == EMBEDDED_OPTION) setAdjustMaturity();
//		log.info(" in evaluation BondAbs end2: {},{}", maturityDate, adjMaturityDate);
	}	
	
	
	protected abstract void setAttributes() throws Exception;	
	
	protected abstract void setPayoffDate() throws Exception;	
	
	protected abstract void setPayoffAmount() throws Exception;
	

	/**
	 * MAIN���� �����Ǵ� �Լ��μ� �� Ŭ���� �ȿ����� ���� ȣ���� ����
	 * adjMaturityDate���� ��������� �ʱ�ȸ ������, �� �Լ��� ������ �����μ�, calibration �ܰ��� impliedMaturity�� Ŭ������ ������
	 * else �������Ͽ��� �� �̷��� �ߴ��� ������ �����ؾ��Ѵ�.
	 * �����ȯ���� �����ϴ� �༮�鿡 ���ؼ��� �� �޼ҵ� ������ ������ؾ��ҵ���
	 */
	@Override
	public void setImpliedMaturity(Double impliedMaturityDays) throws Exception {
		
		if(impliedMaturityDays != null) {		
			this.adjMaturityDate = TimeUtil.addDays(this.baseDate, impliedMaturityDays.intValue());		
			this.toBeAdjustedMaturity = false;			
			//log.info("Implied Maturity Date have been set to {} in EXPO_ID: {}", this.adjMaturityDate, this.expoId);		
		}
		else {			
			this.adjMaturityDate = this.maturityDate;			
			this.toBeAdjustedMaturity = true;
		}
	}


	protected void setAdjustMaturity() throws Exception {		
		
		//Keep Original Valuation Result
		//Effective Maturity and Accrued Interest before Adjusting Maturity Date!!!			
		//Effective Maturity is not affected by Adjusted Maturity!!!
		this.effeMatValue     = getEffectiveMaturity();
		this.accruedIntValue  = getAccruedInterest();
		this.isFirstValuation = false;		
			
		if(toBeAdjustedMaturity) {			
			
			//if(this.extDura != NULL_INT) {	
			if(this.extDura > NULL_DOUBLE) {
				this.maturityDate = TimeUtil.addYearFrac(this.baseDate,  this.extDura);
				
				//log.info("1st ITR = Days: {}, matDate: {}, extDura: {}, extUprc: {}, extUprcUnit: {}", this.extDura*365, this.maturityDate, this.extDura, this.extUprc, this.extUprcUnit);			
				for(int i=0; i<IMPLIED_MAT_ITR_MAX; i++) {
					
					setPayoffDate();
					setPayoffAmount();								
					setImpliedMaturity();							
					//log.info("Following ITR = impDays: {}, impDura: {}, matDate: {}", this.impDura*365, this.impDura, this.maturityDate);
					if(Math.abs(this.extDura - this.impDura) < IMPLIED_MAT_TOL) break; 
					if(i==IMPLIED_MAT_ITR_MAX-1) this.maturityDate = this.orgMaturityDate;
				}
			}
			else if(this.optStrDate != null) this.maturityDate = this.optStrDate;			
			else this.maturityDate = this.orgMaturityDate;
		}	
		else this.maturityDate = this.adjMaturityDate;
		
		//TODO: Check below sentence when CF results have some error (Especially Principal CF)
		this.maturityPremium = Math.max(1.0, this.maturityPremiumOrg 
		                                     * TimeUtil.getTimeFactor(this.issueDate, this.maturityDate,    this.dayCountBasis)
			                                 / TimeUtil.getTimeFactor(this.issueDate, this.orgMaturityDate, this.dayCountBasis));
		setPayoffDate();
		setPayoffAmount();
	}
	
	
	protected double getImpliedMaturity() throws Exception {				
    	//log.info("AFTER ADJ MAT: {}, EXT_DUR: {}, EFF_DUR: {}, DCM_CD: {}", this.maturityDate, this.extDura, getEffectiveDuration(), this.dayCountBasis);
		return  TimeUtil.getActualDayDiff(this.baseDate, this.maturityDate);
    }
	
	
	private void setImpliedMaturity() throws Exception {	
		 
		this.impDura  = getEffectiveDuration();
		//this.impDura = getModifiedDuration();
		//this.impDura = getMacaulayDuration();				

		if(Math.abs(this.extDura - this.impDura) > IMPLIED_MAT_TOL) {		
			this.maturityDate = TimeUtil.addYearFrac(this.maturityDate, this.extDura - this.impDura);
			if(this.maturityDate.isAfter(this.orgMaturityDate)) this.maturityDate = this.orgMaturityDate;
		}
		//log.info("Ext Duration: {}, New Duration: {}, {}, {}", this.extDura, this.impDura, this.extDura - this.impDura, this.maturityDate);
	}	
	
	
	protected double getPresentValue() throws Exception {
		return getPresentValue(this.matTermIntRate);
	}


	protected double getPresentValue(double[] dcntRate) throws Exception {    	
    	return getPresentValue(this.matTerm, dcntRate, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);				
	}
    

	protected double getPresentValueBsCrny(double[] dcntRate) throws Exception {    	
		return getPresentValue(dcntRate) * this.fxRate;				
	}    

	
	protected double getPresentValue(double ytm) throws Exception {
    	return getPresentValue(ytm, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);    	
    }
	
    
    protected double getPresentValueBsCrny(double ytm) throws Exception {
    	return getPresentValue(ytm) * this.fxRate;    	
    }
    

    protected double getPresentValueClean() throws Exception {
    	return getPresentValue() - getAccruedInterest();    	
    }
    
	
	protected double getEffectiveDuration() throws Exception {		
		return getEffectiveDuration(this.matTerm, this.matTermIntRate, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);
	}
	

	protected double getEffectiveDuration(double[] dcntRate) throws Exception {		
		return getEffectiveDuration(this.matTerm, dcntRate, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);
	}	
	
	/**
	 * Macaulay Duration���� �����෹�̼����� �Ѿ�� �������� paymentTerm�� ���°� �ƴѰ�...compoundPeriod�̾���ϳ�?...
	 */
	protected double getModifiedDuration() throws Exception {
		//return getMacaulayDuration() / (1.0 + getYieldToMaturity());
		return getMacaulayDuration() / ( 1.0 + getYieldToMaturity() / TimeUtil.getCompoundFrequency(this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType) );
	}

	
	protected double getMacaulayDuration() throws Exception {
		return getMacaulayDuration(this.matTermIntRate);
	}
	

	protected double getMacaulayDuration(double[] dcntRate) throws Exception {
		return getMacaulayDuration(this.matTerm, dcntRate, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);
	}
	
	
	protected double getTimeWeightedPresentValue() throws Exception {		
		return getTimeWeightedPresentValue(this.matTermIntRate); 
	}
	

	protected double getTimeWeightedPresentValue(double[] dcntRate) throws Exception {    	
    	return getTimeWeightedPresentValue(this.matTerm, dcntRate, this.baseDate, this.payoffDate, this.payoffAmount, this.dcntCmpdMtd, this.dcntCmpdPeriod, this.dcntCmpdPeriodType, this.dayCountBasis);				
	}	
    
    
    protected double getYieldToMaturity() throws Exception {    	
    	return getYieldToMaturity(this.fairBsAmt + getAccruedInterest() * this.fxRate);
    }
	
    
    protected double getYieldToMaturity(double targetBsAmt) throws Exception {
    	return getYieldToMaturity(targetBsAmt, INITIAL_GUESS);
    }

    /**
     * TODO: price�� currency�� �޾Ƽ�...getPresentValue�� ���߾� ����ȭ�� �ϴ� ����� �ʿ��ϴ�.
     * Ȥ�� pvPositive�� ���� �ٷ� ȣ���ϴ� �޼��带 �����ϴ� �͵� ����̴�. �ᱹ �Լ��� ȣ���ϴ� ����� ã�ƾ� �� ���̴�. 
     */
    private double getYieldToMaturity(double targetBsAmt, double initGuess) throws Exception {
    	
    	double ratePos   = 0.0;
    	double rateNeg   = 0.0;
    	double rateNew   = 0.0;
    	double fnRatePos = 0.0;
    	double fnRateNeg = 0.0;
    	double fnRateNew = 0.0;    	
    	
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	ratePos = Math.min( initGuess,  1.0);
    	rateNeg = Math.max(-initGuess, -0.9);    	

    	fnRatePos = getPresentValueBsCrny(ratePos) - targetBsAmt;		
    	fnRateNeg = getPresentValueBsCrny(rateNeg) - targetBsAmt;    	
    	if(Double.isNaN(fnRatePos) || Double.isNaN(fnRateNeg)) return 0.0;
    	
    	if(fnRatePos * fnRateNeg > ZERO_DOUBLE) {
    		
    		for(int i=0; i<MAX_INITIAL_GUESS; i++) {    			    			
    			
    			ratePos  *=  2.0;
    			rateNeg   = -0.9;    			
    	    	fnRatePos = getPresentValueBsCrny(ratePos) - targetBsAmt;		
    	    	fnRateNeg = getPresentValueBsCrny(rateNeg) - targetBsAmt;	        	

	        	if(fnRatePos * fnRateNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnRatePos) || Double.isNaN(fnRateNeg)) return 0.0;
	        	
	        	if(i==0) log.warn("Changing Initial Guess in Calculating YTM: EXPO_ID = [{}]", this.getExpoId());
    		}    		    		
        	if(fnRatePos * fnRateNeg > ZERO_DOUBLE) return 0.0;
    	}    	

    	rateNew = 0.5 * (ratePos + rateNeg);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		

    		fnRateNew = getPresentValueBsCrny(rateNew) - targetBsAmt;    		

    		if(fnRateNeg * fnRateNew < 0) {
    			
    			fnRatePos = fnRateNew;
    			ratePos   = rateNew;
    			rateNew   = 0.5 * (ratePos + rateNeg);    			
    			if(Math.abs(rateNew - ratePos) < ZERO_DOUBLE) return rateNew;
    		}
        	else if(fnRatePos * fnRateNew < 0) {
        		
        		fnRateNeg = fnRateNew;
        		rateNeg   = rateNew;
        		rateNew   = 0.5 * (ratePos + rateNeg);        		
        		if(Math.abs(rateNew - rateNeg) < ZERO_DOUBLE) return rateNew;
        	}
        	else return rateNew;    		
    	}    	
    	log.warn("Calculating Yield to Maturity is failed: [{}]", this.getExpoId());
    	return 0.0;    	
    }    
    
    
    protected double getEffectiveMaturity() throws Exception {
		
		if(isFirstValuation) {
			this.effeMatValue = getEffectiveMaturity(this.baseDate, this.payoffDate, this.payoffAmount, this.dayCountBasis);			
		}						
		return this.effeMatValue;
    }

	/**
	 * for Securing BS compatibility, using curAccruedInterest(IN KRW for FOREIGN ASSET); //for original source(not calculation) Caution: FX_RATE 
	 */
    protected double getAccruedInterest() throws Exception {    	
    	
		if(isFirstValuation) {
			this.accruedIntValue = getAccruedInterest(this.baseDate, this.payoffDate, this.interestPayoffAmount, this.dayCountBasis);			
		}		
		return this.accruedIntValue;		 
    }

    
    protected double getAccruedInterestBsCrny() throws Exception {
    	return getAccruedInterest() * this.fxRate;
    }
    
    
    protected double getImpliedSpread() throws Exception {    	
    	return getImpliedSpread(this.fairBsAmt + getAccruedInterestBsCrny(), INITIAL_GUESS);	
    }    
    
    
    protected double getImpliedSpread(double price) throws Exception {    	
    	return getImpliedSpread(price, INITIAL_GUESS);    	
    }    
    
    /**
     * TODO: this.isIrCalibration�� calibration���� �� IR shock �������� ��� INTEGER_TRUE�� �Ӽ��� ���Ѵ�.
     * �ٸ�, �� �޼��� �� ��ü�� Ķ���극�̼� ���μ��������� total MAP�� ����Ǿ� ���ǰ� ������ IR Shock���μ��������� Ư���� �ǹ̸� ���� �ʴ´�.
     * ���� FX�ó����� �� �ֽ���ݽó����������� calibration�������� ����� ���罺�����带 �״�� ����ϹǷ�, ����� ���� ���ٸ� �ǹ̰� ����.
     * �ƿ﷯, IrCalibration���μ����� �ʿ����(StanDard �ó�����) ��Ȳ������ �� �� ��ü�� ������� �ʴ´�.
     */
    private double getImpliedSpread(double targetBsAmt, double initGuess) throws Exception {
    	
    	if(this.isIrCalibration != INTEGER_TRUE) return 0.0;
    	
    	double spreadPos   = 0.0;
    	double spreadNeg   = 0.0;
    	double spreadNew   = 0.0;
    	double fnSpreadPos = 0.0;
    	double fnSpreadNeg = 0.0;
    	double fnSpreadNew = 0.0;    	
    	
    	if(Math.abs(initGuess) <= 0) initGuess = INITIAL_GUESS;    	
    	spreadPos = Math.min( initGuess,  1.0);
    	spreadNeg = Math.max(-initGuess, -0.9); 

    	fnSpreadPos = getPresentValueBsCrny(addSpread(this.matTermIntRate, spreadPos * this.isIrCalibration)) - targetBsAmt;		
    	fnSpreadNeg = getPresentValueBsCrny(addSpread(this.matTermIntRate, spreadNeg * this.isIrCalibration)) - targetBsAmt;    	
    	if(Double.isNaN(fnSpreadPos) || Double.isNaN(fnSpreadNeg)) return 0.0;
    	
    	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) {
    		
    		for(int i=0; i<MAX_INITIAL_GUESS; i++) {    			
    			
	        	spreadPos  *=  2.0;
	        	spreadNeg   = -0.9;
	        	fnSpreadPos = getPresentValueBsCrny(addSpread(this.matTermIntRate, spreadPos * this.isIrCalibration)) - targetBsAmt;	    			
	        	fnSpreadNeg = getPresentValueBsCrny(addSpread(this.matTermIntRate, spreadNeg * this.isIrCalibration)) - targetBsAmt;	        	

	        	if(fnSpreadPos * fnSpreadNeg < ZERO_DOUBLE) break;
	        	if(Double.isNaN(fnSpreadPos) || Double.isNaN(fnSpreadNeg)) return 0.0;
	        	
	        	if(i==0) log.warn("Changing Initial Guess in Calculating Implied Spread: EXPO_ID = [{}]", this.getExpoId());	        	
    		}    		    		
        	if(fnSpreadPos * fnSpreadNeg > ZERO_DOUBLE) return 0.0;
    	}    	

    	spreadNew = 0.5 * (spreadPos + spreadNeg);
    	
    	for(int i=0; i<MAX_ITERATION; i++) {    		

    		fnSpreadNew = getPresentValueBsCrny(addSpread(this.matTermIntRate, spreadNew * this.isIrCalibration)) - targetBsAmt;    		

    		if(fnSpreadNeg * fnSpreadNew < 0) {
    			
    			fnSpreadPos = fnSpreadNew;
    			spreadPos   = spreadNew;
    			spreadNew   = 0.5 * (spreadPos + spreadNeg);    			
    			if(Math.abs(spreadNew - spreadPos) < ZERO_DOUBLE) return spreadNew;
    		}
        	else if(fnSpreadPos * fnSpreadNew < 0) {
        		
        		fnSpreadNeg = fnSpreadNew;
        		spreadNeg   = spreadNew;
        		spreadNew   = 0.5 * (spreadPos + spreadNeg);        		
        		if(Math.abs(spreadNew - spreadNeg) < ZERO_DOUBLE) return spreadNew;        		
        	}
        	else return spreadNew;
    		log.info("i:{}, calc implied spread :{}",i, spreadNew) ;
    	}   
    	log.warn("Calculating Implied Spread is failed: [{}]", this.getExpoId());
    	return 0.0;
    }		
		
    /**
     * this.result.setValue(Math.floor(this.interestPayoffAmount[i] * fxRatio)) -> floor ���� ���� �ʿ� (1���� ���������� ��� ����)
     */	
	public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {		
		
//		log.info("At getValuation : {}", currencyType);
		evaluateCashflow();		
	    
		List<KicsAssetResult> cflist = new ArrayList<KicsAssetResult>();
		double fxRatio = (currencyType ? DEF_FX_RATE : this.fxRate);			
		
	    this.result.setBaseDate(TimeUtil.dateToString(this.baseDate));		
		this.result.setExpoId(this.expoId);
	    this.result.setFundCd(this.fundCd);
		this.result.setProdTpcd(this.prodTpcd);
		this.result.setAccoCd(this.accoCd);		
		this.result.setDeptCd(this.deptCd);
			
		this.result.setLegType(DEF_LEG_NAME);		    	
		this.result.setCurrency(currencyType ? this.currency : DEF_CURRENCY);
		
		
			    	
    	for(Integer fe : financialElements) {			    		
//    		log.info("in financialElements In Bond Abs : {},{}", fe, financialElements.size());
    		this.result.setResultType(String.format("%02d",fe));
    		this.result.setResultName(setResultDefineMap(fe));
    		
    		switch(fe.intValue()) {
			    case FE_PRINCIPAL_CF: {
			    	for(int i=0; i<payoffDate.length; i++) {			    		
			    		this.result.setResultDate(TimeUtil.dateToString(payoffDate[i]));
			    		this.result.setValue(Math.floor(this.principalPayoffAmount[i] * fxRatio));
			    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));			    			
		    		}
			    	break;
			    }
			    case FE_INTEREST_CF: {						    							    	
			    	for(int i=0; i<payoffDate.length; i++) {			    		
			    		this.result.setResultDate(TimeUtil.dateToString(payoffDate[i]));
			    		this.result.setValue(Math.floor(this.interestPayoffAmount[i] * fxRatio)); 
			    		//this.result.setValue(this.interestPayoffAmount[i] * fxRatio);
			    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
			    	}
			    	break;
			    }
			    case FE_YIELD_TO_MATURITY: {			    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(getYieldToMaturity());
			    	cflist.add(cloneEntity(this.result));			    	
			    	break;			    	
			    }
			    case FE_PV_DIRTY: {			    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(Math.floor(getPresentValue() * fxRatio));
			    	cflist.add(cloneEntity(this.result));			    	
			    	break;
			    }			    
			    case FE_ACCRUED_INTEREST: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
			    	this.result.setValue(Math.floor(getAccruedInterest() * fxRatio));
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_PV_CLEAN: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(Math.floor(getPresentValueClean() * fxRatio));						    	
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_EFFECTIVE_MATURITY: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
			    	this.result.setValue(getEffectiveMaturity());			    	
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_EFFECTIVE_DURATION: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
			    	this.result.setValue(getEffectiveDuration());
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_MODIFIED_DURATION: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
			    	this.result.setValue(getModifiedDuration());
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_BS_AMOUNT: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
			    	this.result.setValue(getFairBsAmt() / (currencyType && !this.currency.equals(DEF_CURRENCY) ? this.fxRateBase : DEF_FX_RATE));
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    case FE_ACCRUED_INT_BS: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
			    	this.result.setValue((getAccrAmt() - getUernAmt()) / (currencyType && !this.currency.equals(DEF_CURRENCY) ? this.fxRateBase : DEF_FX_RATE));
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }			   
			    case FE_IRATE_EXPOSURE: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(Math.floor(getPresentValueClean() * fxRatio));						    	
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }			    
			    case FE_FXRT_EXPOSURE: {		
			    	if(this.currency != null && !this.currency.equals(DEF_CURRENCY)) {
				    	this.result.setCurrency(this.currency);
				    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
				    	this.result.setValue(Math.floor(getPresentValueClean() * 1.0));		
				    	cflist.add(cloneEntity(this.result));
			    	}
			    	break;
			    }
			    case FE_STOCK_EXPOSURE: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(Math.floor(0 * fxRatio));
			    	if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }			    
			    case FE_IMPLIED_SPREAD: {
			    	log.info("fe:{} FE_IMPLIED_SPREAD",fe);
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(getImpliedSpread());						    	
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }				
			    case FE_IMPLIED_MATURITY: {		
			    	if(this.embeddedOption == EMBEDDED_OPTION) {
			    		this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    		this.result.setValue(getImpliedMaturity());						    	
			    		cflist.add(cloneEntity(this.result));
			    	}
			    	break;
			    }			    
			    default: break;
    		}
    	}			    
		return cflist;
	}
	
}