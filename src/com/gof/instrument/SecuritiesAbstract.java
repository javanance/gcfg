package com.gof.instrument;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetResult;
import com.gof.enums.EInstrument;
import com.gof.util.GeneralUtil;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class SecuritiesAbstract extends InstrumentAbstract {		
	
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
	protected double      maturityPremium;	
	protected int         amortTerm;
	protected double      amortAmt;

	protected boolean     isRealNumber;
	protected double      toRealScale;	
	protected double      spotPrice;
	protected double      spotPriceBase;
	protected double      underlyingExercisePrice;
	protected double      underlyingSpotPrice;
	protected double      underlyingSpotPriceBase;	
	protected int         embeddedOption;
	protected LocalDate   optStrDate;
	protected LocalDate   optEndDate;	
	
	protected double      notionalCurrent;	
	protected double      notionalStart;		
	protected double      bsAmt;
	protected double      fairBsAmt;		
	protected double      accrAmt;
	protected double      uernAmt;	
	
	protected double      extUprc;
	protected double      extUprcUnit;
	protected double      extDura;    
	protected double      extUprc1;
	protected double      extUprc2;
	protected double      extDura1;
	protected double      extDura2;			
			
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

	protected LocalDate   orgMaturityDate;		
	protected LocalDate   adjMaturityDate;	
	protected double      impDura;	
	protected double      maturityPremiumOrg;
	protected boolean     isFirstValuation;	
	protected boolean     toBeAdjustedMaturity;
	
	protected double      effeMatValue;	
	protected double      accruedIntValue;	

	
	public SecuritiesAbstract() {		
		super();		
	}	
	
	// 24.11.22 sy add 시나리오, 통화커브별 금리시나리오 
	@Override
	public void setIrScenarioEntities(Integer scenNum, String crnyCd, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception{
		this.scenNum          = scenNum;
		this.scenarioCurveHis = scenarioCurveHis;	
	}
	
//	@Override
//	public void setIrScenarioFxCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception{
//		
//	}
//	
//	@Override
//	public void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception {
//	
//		this.scenNum          = scenNum;
//		this.scenarioCurveHis = scenarioCurveHis;		
//	}

	
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
	
}