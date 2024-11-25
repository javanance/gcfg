package com.gof.instrument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetLoan;
import com.gof.entity.KicsAssetResult;
import com.gof.enums.EDayCountBasis;
import com.gof.enums.EFinElements;
import com.gof.enums.EInstrument;
import com.gof.util.GeneralUtil;
import com.gof.util.TimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
public abstract class LoanAbstract extends InstrumentAbstract {		
	
	protected LocalDate   baseDate;	
	protected String      expoId;
	protected String      fundCd;
	protected String      prodTpcd;
	protected String      accoCd;
	protected String      accoNm;
	protected String      isinCd;	
	protected String      isinNm;	
	protected String      contId;
	protected String      deptCd;
	protected String      instTpcd;
	protected int         instDtlsTpcd;	

	protected LocalDate   issueDate;
	protected LocalDate   maturityDate;
	protected LocalDate   firstCouponDate;
	
	protected String      currency;	
	protected double      fxRate;
	protected double      fxRateBase;
	protected int         dayCountBasis;		
	protected double      iRate;
	protected String      irCurveId;
	protected double      spreadOverCurve;
	protected int         paymentTerm;	
	protected int         cmpdPeriod;	
	protected char        cmpdPeriodType;
	protected char        cmpdMethod;	
	protected char        paymentTermType;
	
	protected int         amortTerm;
	protected double      amortAmt;

	protected boolean     isRealNumber;
	protected double      toRealScale;	
	protected double      spotPrice;
	protected double      spotPriceBase;
	
	protected double      notionalCurrent;	
	protected double      notionalStart;		
	protected double      bsAmt;
	protected double      fairBsAmt;		
	protected double      accrAmt;
	protected double      uernAmt;	
			
	protected List<IrCurve>                            irCurve;
	protected Map<String, Map<String, IrCurveHis>>     irCurveHis;	
	protected Map<String, IrCurveHis>                  scenarioCurveHis;	
	protected Integer                                  scenNum;	
	
	protected int         isIrCalibration;
	protected int         isStockShock;
	protected String      fxTypCd;
	protected Double      fxVal;
			
	protected List<Integer>   financialElements;
	protected KicsAssetResult result = new KicsAssetResult();	
	
	protected LocalDate[] payoffDate;	
	protected double[]    principalPayoffAmount;
	protected double[]    interestPayoffAmount;
	protected double[]    payoffAmount;
	
	protected String[]    matTerm;		 
	protected double[]    matTermIntRate;	
	protected boolean     cfDirection;	
	
	protected double      impSpread;
	protected double      discountSpread;
	protected char        dcntCmpdMtd;
	protected int         dcntCmpdPeriod;
	protected char        dcntCmpdPeriodType;
	protected String[]    dcntMatTerm;		 
	protected double[]    dcntMatTermIntRate;


	public LoanAbstract(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) {		
		super();
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;		
	}		
	
	public LoanAbstract() {				
		super();
		this.scenNum              = 0;
		this.scenarioCurveHis     = null;
		this.fxTypCd              = null;
		this.fxVal                = null;
	}
	
	@Override
	protected void setInstrumentEntities(KicsAssetLoan entity) throws Exception {

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
			log.warn("MaturityDate must be after IssueDate in [{}]", this.expoId);			
		}		
		if(this.maturityDate.isBefore(this.baseDate.plusDays(1))) { 
			this.maturityDate = this.baseDate.plusDays(1);
			log.warn("MaturityDate must be after BaseDate in [{}]", this.expoId);			
		}		
		this.firstCouponDate  = setDateOrNull(entity.getFrstIntDate());		
		if(this.firstCouponDate != null && this.firstCouponDate.isBefore(this.issueDate)) this.firstCouponDate = null;				
				
		this.cfDirection             = false;		
		this.isStockShock            = INTEGER_FALSE;
		
		this.currency                = GeneralUtil.objectToPrimitive(entity.getCrnyCd(),   DEF_CURRENCY);
		this.fxRate                  = GeneralUtil.objectToPrimitive(entity.getCrnyFxrt(), DEF_FX_RATE);		
		this.spotPrice               = GeneralUtil.objectToPrimitive(entity.getCrnyFxrt(), DEF_UNIT_PRICE);
		this.fxRateBase              = this.fxRate;
		this.spotPriceBase           = this.spotPrice;
		this.dayCountBasis           = EDayCountBasis.getEDayCountBasis(entity.getDcbCd()).getDcbCode();
		                             
		this.iRate                   = GeneralUtil.objectToPrimitive(entity.getIrate());		
		this.paymentTerm             = GeneralUtil.objectToPrimitive(entity.getIntPayCyc(), DEF_PMT_TERM_MONTH);
		                             
		this.amortTerm               = GeneralUtil.objectToPrimitive(entity.getAmortTerm(), DEF_PMT_TERM_MONTH);
		this.amortAmt                = GeneralUtil.objectToPrimitive(entity.getAmortAmt());
				                     
		this.spreadOverCurve         = GeneralUtil.objectToPrimitive(entity.getAddSprd());
		this.discountSpread          = 0.0;
		this.isIrCalibration         = INTEGER_FALSE;		
		this.notionalStart           = GeneralUtil.objectToPrimitive(entity.getNotlAmtOrg());
        this.notionalCurrent         = GeneralUtil.objectToPrimitive(entity.getNotlAmt());
        this.bsAmt                   = GeneralUtil.objectToPrimitive(entity.getBsAmt());
        this.fairBsAmt               = GeneralUtil.objectToPrimitive(entity.getBsAmt());	
        if(Math.abs(this.fairBsAmt) > ZERO_DOUBLE) {
        	this.notionalCurrent = GeneralUtil.objectToPrimitive(entity.getNotlAmt(), this.bsAmt);
        }                
        
		this.accrAmt                 = GeneralUtil.objectToPrimitive(entity.getAccrAmt());
		this.uernAmt                 = GeneralUtil.objectToPrimitive(entity.getUernAmt());
		
		this.isRealNumber            = GeneralUtil.objectToPrimitive(entity.getIsRealNumber());
		this.isRealNumber            = true;
		//log.info("real number:{}", this.isRealNumber);
		this.irCurveHis              = new HashMap<String, Map<String, IrCurveHis>>();		
		this.irCurveId               = DEFAULT_KRW_RFCURVE_ID;
		this.irCurve                 = null;

//		if(entity.getIrCurve() == null || entity.getIrCurve().isEmpty()) {			
//			log.warn("IrCurve Data is not found at {}!!!", baseDate);		
//		}
//		else if(!entity.getIrCurve().isEmpty() && !entity.getIrCurve().get(0).getIrCurveHis().isEmpty()) {			
//			
//				this.irCurveId               = entity.getIrCurve().get(0).getIrCurveId();
//				this.irCurve                 = entity.getIrCurve();				
//				for(IrCurve irc : irCurve) irCurveHis.put(irc.getIrCurveId(), irc.getIrCurveHis());
//				//log.info("{}, {}, {}", entity.getIrCurve().get(0).getIrCurveId(), entity.getIrCurve(), entity.getIrCurve().get(0));				
//				
//				setIrCurve();
//		}
//		else {
//			log.warn("IrCurveHis Data is not found at {}!!!", baseDate);
//		}		
	}	

    @Override
	public void setIrScenarioEntities(Integer scenNum,String crnyCd, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception {
		
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;
		this.impSpread        = GeneralUtil.objectToPrimitive(spread);		
		this.setIrCurve();				
		//log.info("IR Scenario Curve Entities have been set! SCEN_NUM: {}, {}", this.scenNum, this.scenarioCurveHis.size());
	}
	
//    @Override
//	public void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception {
//		
//		this.scenNum          = scenNum;
//		this.scenarioCurveHis = scenarioCurveHis;
//		this.impSpread        = GeneralUtil.objectToPrimitive(spread);		
//		this.setIrCurve();				
//		//log.info("IR Scenario Curve Entities have been set! SCEN_NUM: {}, {}", this.scenNum, this.scenarioCurveHis.size());
//	}
	
	
	private void setIrCurve() throws Exception {
		
		this.toRealScale = this.isRealNumber ? 1.0 : TO_REAL_INT_RATE;				
		
		if(scenarioCurveHis != null && !scenarioCurveHis.isEmpty()) this.setScenarioCurveHis();
		else this.setIrCurveHis();
		
		dcntMatTerm = matTerm;		 
		dcntMatTermIntRate = matTermIntRate;
	}
	
	
	public void setFxScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception {
		
		this.scenNum = scenNum;
		this.fxTypCd = typCd;
		this.fxVal   = val;
		if(this.currency != null && !this.currency.equals(DEF_CURRENCY)) {
			this.fxRate = GeneralUtil.setScenarioValue(this.fxRateBase, this.fxTypCd, this.fxVal);				
		}
	}

	
	public void setStockScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception {		
		
		this.scenNum      = scenNum;
		this.isStockShock = INTEGER_TRUE;
		
		if(this.instTpcd.equals(EInstrument.EQTY_ORDINARY.getLegacyCode())) {
			this.spotPrice = GeneralUtil.setScenarioValue(this.spotPriceBase, typCd, val);			
		}
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
		
		setAttributes();
		setPayoffDate();
		setPayoffAmount();		
	}	
	
	
	protected abstract void setAttributes() throws Exception;	
	
	protected abstract void setPayoffDate() throws Exception;	
	
	protected abstract void setPayoffAmount() throws Exception;	

	
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
    	return getEffectiveMaturity(this.baseDate, this.payoffDate, this.payoffAmount, this.dayCountBasis);
    }

	/**
	 * for Securing BS compatibility, using curAccruedInterest(IN KRW for FOREIGN ASSET); //for original source(not calculation) Caution: FX_RATE 
	 */
    protected double getAccruedInterest() throws Exception {    	
		return getAccruedInterest(this.baseDate, this.payoffDate, this.interestPayoffAmount, this.dayCountBasis);			
    }

    
    protected double getAccruedInterestBsCrny() throws Exception {
    	return getAccruedInterest() * this.fxRate;
    }   
		
    /**
     * this.result.setValue(Math.floor(this.interestPayoffAmount[i] * fxRatio)) -> floor ���� ���� �ʿ� (1���� ���������� ��� ����)
     */	
	public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {
		
		evaluateCashflow();		
		
		List<KicsAssetResult> cflist = new ArrayList<KicsAssetResult>();		
		List<Integer> financialElements = EFinElements.LOAN.getEFinElementList();
		double fxRatio = (currencyType ? DEF_FX_RATE : this.fxRate);
	    
	    /////////////////////////////////////////////////////////////		
	    this.result.setBaseDate(TimeUtil.dateToString(this.baseDate));		
		this.result.setExpoId(this.expoId);
	    this.result.setFundCd(this.fundCd);
		this.result.setProdTpcd(this.prodTpcd);
		this.result.setAccoCd(this.accoCd);		
		this.result.setDeptCd(this.deptCd);
			
		this.result.setLegType(DEF_LEG_NAME);		    	
		this.result.setCurrency(currencyType ? this.currency : DEF_CURRENCY);
			    	
    	for(Integer fe : financialElements) {			    		
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
			    default: break;
    		}
    	}			    
		return cflist;
	}
	
//	@Override
//	public void setIrScenarioFxCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception{
//		
//	}
	
}
	