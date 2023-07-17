package com.gof.test;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.enums.EBoolean;
import com.gof.enums.EFinElements;


public class TestGeneral {
	
	private final static Logger logger = LoggerFactory.getLogger(TestGeneral.class.getSimpleName());

	enum EBoolean2 {		
		Y(true)
	,	YES(true)
	,   N(false)	
	,   NO(false)
	;
		private boolean trueFalse;
		
		private EBoolean2(boolean trueFalse) {
		    this.trueFalse = trueFalse;
	    }		
		
		public boolean isTrueFalse() {
			return trueFalse;
		}		
	}

	enum EInstrument1 {		
		
		   FX_FORWARD          ("60A", 51)	
		 , CCS_FORWARD         ("62B", 52)
		 , KTB_FUTURES         ("700", 53)
		 , EMBEDDED_SWAP       ("62H", 54)
		 , EMBEDDED_OPTION     ("78H", 55)
		 , OTC_CALLPUT_OPTION  ("80H", 56)
		 
		 , FUND_FX_FORWARD     ("41A", 51)
		 , FUND_FX_FORWARD2    ("51A", 51)
		 , FUND_KTB_FUTURES    ("52A", 53)
		 , FUND_CCS_61A        ("61A", 52)
		 , FUND_CCS_62A        ("62A", 52)
		 , FUND_CCS_66A        ("66A", 52)		 
		 
		 , NON_IDENTIFIED      ("NA" , 99)
		 
	    ;
		
		private String invmPdcTpcd;
		
		private Integer instType;		
				
		private EInstrument1 (String invmPdcTpcd, Integer instType) {
			this.invmPdcTpcd = invmPdcTpcd;
			this.instType = instType;
		}		
		
		public String getInvmPdcTpcd() {
			return this.invmPdcTpcd;
		}		
		
		public Integer getInstType() {
			return this.instType;
		}
	}

	
	enum EInstrument2 {		
		
		   I60A        ("FX_FORWARD"          , 51)	
		 , I62B        ("CCS_FORWARD"         , 52)
		 , I700        ("KTB_FUTURES"         , 53)
		 , I62H        ("EMBEDDED_SWAP"       , 54)
		 , I78H        ("EMBEDDED_OPTION"     , 55)
		 , I80H        ("OTC_CALLPUT_OPTION"  , 56)
		 , I41A        ("FX_FORWARD"          , 51)
		 , I51A        ("FX_FORWARD"          , 51)
		 , I52A        ("KTB_FUTURES"         , 53)
		 , I61A        ("CCS_FORWARD"         , 52)
		 , I62A        ("CCS_FORWARD"         , 52)
		 , I66A        ("CCS_FORWARD"         , 52)		 
		 , INON        ("NON_IDENTIFIED"      , 99)
		 
	    ;
		
		private String instName;
		
		private Integer instType;		
				
		private EInstrument2 (String instName, Integer instType) {
			this.instName = instName;
			this.instType = instType;
		}		
		
		public String getInstName() {
			return this.instName;
		}		
		
		public Integer getInstType() {
			return this.instType;
		}
	}
	
	
	public static void main(String[] args) {		
		
		logger.info("EnumTest : {},{},{},{},{},{}", EBoolean.Y.isTrueFalse());	
		logger.info("EnumTest : {},{},{},{},{},{}", EBoolean2.YES.isTrueFalse());
		logger.info("EnumTest : {},{},{},{},{},{}", EInstrument1.CCS_FORWARD.getInvmPdcTpcd(), EInstrument1.CCS_FORWARD.getInstType());
		logger.info("EnumTest : {},{},{},{},{},{}", EInstrument2.I60A.getInstName(), EInstrument2.I60A.getInstType());		
		
//		for(EInstrument aa : EInstrument.values()) {
//			//System.out.println(aa.getInvmPdcTpcd());
//			//if(aa instanceof String) System.out.println("String");
//			if(aa instanceof EInstrument) System.out.println(aa.getClass().getSimpleName());
//
//			System.out.println(aa.getInvmPdcTpcd());
//		}
		
		for (Integer aa : EFinElements.SWAP.getEFinElements()) {
			logger.info("FX_FORWARD : {},{},{},{},{},{}", aa.intValue());	
		}
	}


}
