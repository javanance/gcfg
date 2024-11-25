package com.gof.interfaces;

import java.util.List;
import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.entity.KicsAssetResult;


public interface Instrument extends Constant {
	
	public String getExpoId() throws Exception;	
	
	public void setInstrumentEntities(KicsAsset entity) throws Exception;	
	
//	public void setIrScenarioCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;
//	public void setIrScenarioFxCurveEntities(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;

	// 24.11.22 sy add 시나리오, 통화커브별 금리시나리오 
	public void setIrScenarioEntities(Integer scenNum,String crnyCd, Map<String, IrCurveHis> scenarioCurveHis, Double spread) throws Exception;
	
	public void setFxScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception;
	
	public void setStockScenarioEntities(Integer scenNum, String typCd, Double val) throws Exception;
	
	public void setImpliedMaturity(Double impliedMaturityDays) throws Exception;
	
	public List<KicsAssetResult> getValuation() throws Exception;
	
	public List<KicsAssetResult> getValuation(boolean toOriginalCurrency) throws Exception;	
	
	
	public static final boolean TO_ORIGINAL_CURRENCY      = true;
	public static final boolean TO_DEFAULT_CURRENCY       = false;	
	public static final String  DEF_CURRENCY              = "KRW";		
	
	
	public static final int     INST_BOND_ZCB             = 101;	
	public static final int     INST_BOND_SCB             = 102;	
	public static final int     INST_BOND_DCB             = 103;
	public static final int     INST_BOND_FRB             = 104;
	public static final int     INST_BOND_FRN             = 105;
	public static final int     INST_BOND_FAB             = 106;
	public static final int     INST_BOND_FAN             = 107;
	public static final int     INST_BOND_C5S2            = 110;
	public static final int     INST_BOND_NON             = 199;	
	
	
	public static final int     INST_SECR_EQUITY          = 201;
	public static final int     INST_SECR_EQFUT           = 202;
	public static final int     INST_SECR_EQOPT           = 203;
	public static final int     INST_SECR_EQWRT           = 211;
	public static final int     INST_SECR_STRUC           = 221;	
	public static final int     INST_SECR_NON             = 299;
	
	
	public static final int     INST_LOAN_ZCB             = 301;	
	public static final int     INST_LOAN_SCB             = 302;	
	public static final int     INST_LOAN_DCB             = 303;
	public static final int     INST_LOAN_FRB             = 304;
	public static final int     INST_LOAN_FRN             = 305;
	public static final int     INST_LOAN_FAB             = 306;
	public static final int     INST_LOAN_FAN             = 307;	
	public static final int     INST_LOAN_NON             = 399;	
	
	
	public static final int     INST_FIDE_FXSWAP          = 451;	
	public static final int     INST_FIDE_CCSWAP          = 452;	
	public static final int     INST_FIDE_IDXFUT          = 461;
	public static final int     INST_FIDE_IDXOPTC         = 462;
	public static final int     INST_FIDE_IDXOPTP         = 463;
	public static final int     INST_FIDE_KTBFUT          = 471;	
	public static final int     INST_FIDE_KTBFWD          = 472;
	public static final int     INST_FIDE_NON             = 499;
	
	
	public static final int     INST_ACCO                 = 888;
	public static final int     INST_NON_IDENTIFIED       = 999;	
	
	
	public static final String  ABSOLUTE_MAX_DATE         = "21991231";
	public static final String  ABSOLUTE_MIN_DATE         = "19700101";	
	public static final String  DEFAULT_KRW_RFCURVE_ID    = "1111111";
	public static final double  TO_REAL_INT_RATE          = 0.01;
	
	
	public static final double  DEF_TIME_FACTOR           = 1.0;
	public static final double  DEF_UNIT_PRICE            = 1.0;
	public static final double  DEF_STRIKE_PRICE          = 0.0;
	public static final double  DEF_FX_RATE               = 1.0;
	
	
	public static final int     DEF_CMPD_PERIOD_YEAR      =   1;
	public static final double  DEF_CMPD_FREQ_YEAR        = 1.0;
	public static final int     DEF_PMT_TERM_MONTH        =  12;	

	
	public static final boolean ODD_COUPON                = true;
	public static final boolean REGULAR_COUPON            = false;
	public static final int     NON_COUPON_PMT_TERM       = 0;	
	
	
	public static final int     EMBEDDED_OPTION           = 1;
	public static final double  IMPLIED_MAT_TOL           = 0.0025;
	public static final int     IMPLIED_MAT_ITR_MAX       = 10;
	public static final double  EFFECTIVE_DURATION_BP     = 0.01;
	
	
	public static final int     FE_PRINCIPAL_CF           =  1;
	public static final int     FE_INTEREST_CF            =  2;
	public static final int     FE_YIELD_TO_MATURITY      =  3;		
	public static final int     FE_PV_DIRTY               = 12;
	public static final int     FE_ACCRUED_INTEREST       = 13;	
	public static final int     FE_PV_CLEAN               = 14;
	public static final int     FE_EFFECTIVE_MATURITY     = 16;
	public static final int     FE_EFFECTIVE_DURATION     = 17;
	public static final int     FE_MODIFIED_DURATION      = 18;
	public static final int     FE_BS_AMOUNT              = 21;
	public static final int     FE_ACCRUED_INT_BS         = 22;
	public static final int     FE_IRATE_EXPOSURE         = 31;
	public static final int     FE_FXRT_EXPOSURE          = 32;
	public static final int     FE_STOCK_EXPOSURE         = 33;	
	public static final int     FE_IMPLIED_SPREAD         = 61;
	public static final int     FE_IMPLIED_MATURITY       = 62;
	public static final int     FE_IMPLIED_SIGMA          = 63;
	
	public static final int     FE_FWDFUT_PRICE           = 71;
	public static final int     FE_KTB_FORWARD_RATE       = 72;
	public static final int     FE_DPDR                   = 73;
	

	public static final int     REC_LEG_KEY               = 1;
	public static final int     PAY_LEG_KEY               = 2;
	public static final int     NET_LEG_KEY               = 3;	
	public static final String  DEF_LEG_NAME              = "STD";
	public static final String  REC_LEG_NAME              = "REC";
	public static final String  PAY_LEG_NAME              = "PAY";	
	public static final String  NET_LEG_NAME              = "NET";
	
	
	public static final boolean REC_LEG                   = false;
	public static final boolean PAY_LEG                   = true;
	public static final String  PRINCIPAL_SWAP            = "Y";
	
	
	public static final String  POSITION_LONG             = "L";
	public static final String  POSITION_SHORT            = "S";		
	public static final String  CALL_OPTION               = "C";
	public static final String  PUT_OPTION                = "P";
	
	                            
	public static final String  OPTION_PRICING_BSE        = "BSE";
	public static final String  OPTION_PRICING_KRX        = "KRX";
	public static final String  OPTION_PRICING_MCS        = "MCS";
	
}