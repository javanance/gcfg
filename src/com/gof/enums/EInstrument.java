package com.gof.enums;

import java.util.Map;

import com.gof.entity.IrCurveHis;
import com.gof.instrument.DerivativesBalance;
import com.gof.instrument.DerivativesCcsForward;
import com.gof.instrument.DerivativesFxForward;
import com.gof.instrument.DerivativesIndexFutures;
import com.gof.instrument.DerivativesIndexOption;
import com.gof.instrument.DerivativesKtbForward;
import com.gof.instrument.DerivativesKtbFutures;
import com.gof.instrument.LoanBulletRepay;
import com.gof.instrument.SecuritiesAccount;
import com.gof.instrument.SecuritiesBondAccount;
//import com.gof.instrument.SecuritiesBondAccount;
import com.gof.instrument.SecuritiesBondAmort;
import com.gof.instrument.SecuritiesBondCompound;
import com.gof.instrument.SecuritiesBondCoupon;
import com.gof.instrument.SecuritiesBondFrnCoupon;
import com.gof.instrument.SecuritiesBondZeroCoupon;
import com.gof.instrument.SecuritiesEquity;
import com.gof.interfaces.Instrument;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Slf4j
public enum EInstrument {		
	
	  BOND_ZERO_CPN       ("B1" , Instrument.INST_BOND_ZCB)
	, BOND_SIMP_CMPD      ("B2" , Instrument.INST_BOND_SCB)
	, BOND_DISC_CMPD      ("B3" , Instrument.INST_BOND_DCB)  
	, BOND_FIX_RATE       ("B4" , Instrument.INST_BOND_FRB)	  
	, BOND_FLT_RATE       ("B5" , Instrument.INST_BOND_FRN)
	, BOND_FIX_AMORT      ("B6" , Instrument.INST_BOND_FAB)
	, BOND_FLT_AMORT      ("B7" , Instrument.INST_BOND_FAN)
	, BOND_EXOTIC_CMPD    ("B8" , Instrument.INST_BOND_C5S2)
	, BOND_NON            ("BZ" , Instrument.INST_BOND_NON)
	
	  
	, EQTY_ORDINARY       ("E1" , Instrument.INST_SECR_EQUITY)
	, EQTY_FUTURES        ("E2" , Instrument.INST_SECR_EQFUT)
	, EQTY_OPTION         ("E3" , Instrument.INST_SECR_EQOPT)
	, EQTY_WARRANT        ("E4" , Instrument.INST_SECR_EQWRT)
	, EQTY_STRUC          ("E5" , Instrument.INST_SECR_STRUC)	
	, EQTY_NON            ("EZ" , Instrument.INST_SECR_NON)
	
	
	, FIDE_FXSWAP         ("D1" , Instrument.INST_FIDE_FXSWAP)	
	, FIDE_CCSWAP         ("D2" , Instrument.INST_FIDE_CCSWAP)
	, FIDE_KTBFUT         ("D3" , Instrument.INST_FIDE_KTBFUT)
	, FIDE_IDXFUT         ("D4" , Instrument.INST_FIDE_IDXFUT)
	, FIDE_IDXOPTC        ("D5" , Instrument.INST_FIDE_IDXOPTC)	
	, FIDE_IDXOPTP        ("D6" , Instrument.INST_FIDE_IDXOPTP)
	
	, FIDE_KTBFWD         ("D7" , Instrument.INST_FIDE_KTBFWD)
	, FIDE_NON            ("DZ" , Instrument.INST_FIDE_NON)
	

	, LOAN_ZERO_CPN       ("L1" , Instrument.INST_LOAN_ZCB)
	, LOAN_SIMP_CMPD      ("L2" , Instrument.INST_LOAN_SCB)
	, LOAN_DISC_CMPD      ("L3" , Instrument.INST_LOAN_DCB)  
	, LOAN_FIX_RATE       ("L4" , Instrument.INST_LOAN_FRB)	  
	, LOAN_FLT_RATE       ("L5" , Instrument.INST_LOAN_FRN)
	, LOAN_FIX_AMORT      ("L6" , Instrument.INST_LOAN_FAB)
	, LOAN_FLT_AMORT      ("L7" , Instrument.INST_LOAN_FAN)
	, LOAN_NON            ("LZ" , Instrument.INST_LOAN_NON)
	, LOAN_TEST1          ("14" , Instrument.INST_LOAN_FRB)
	, LOAN_TEST2          ("24" , Instrument.INST_LOAN_FRB)
	
	
	, INST_ACCO           ("AZ" , Instrument.INST_ACCO)
	, INST_NON            ("NZ" , Instrument.INST_NON_IDENTIFIED)		
    ;
	
	
	private String legacyCode;
	
	private Integer instCode;		
			
	private EInstrument(String legacyCode, Integer instCode) {
		this.legacyCode = legacyCode;
		this.instCode = instCode;
	}	

    
	public static EInstrument getEInstrument(String legacyCode) {		 
		
		for(EInstrument inst : EInstrument.values()) {
			if(inst.getLegacyCode().equals(legacyCode)) return inst;			
		}
		return INST_NON;		
	}


    public Instrument getInstrumentModel() throws Exception {    	
//    	log.info("In Einstrument :{},{}", instCode);

	    switch(this.instCode) {    	
	
    	    case Instrument.INST_BOND_ZCB:             return new SecuritiesBondZeroCoupon();	        
    	    case Instrument.INST_BOND_SCB:             return new SecuritiesBondCompound();
    	    case Instrument.INST_BOND_DCB:             return new SecuritiesBondCompound();
    	    case Instrument.INST_BOND_C5S2:            return new SecuritiesBondCompound();
    	    case Instrument.INST_BOND_FRB:             return new SecuritiesBondCoupon();
//    	    case Instrument.INST_BOND_FRN:             return new SecuritiesBondCoupon();
    	    case Instrument.INST_BOND_FRN:             return new SecuritiesBondFrnCoupon();
    	    case Instrument.INST_BOND_FAB:             return new SecuritiesBondAmort();
    	    case Instrument.INST_BOND_FAN:             return new SecuritiesBondAmort();
    	    case Instrument.INST_BOND_NON:             return new SecuritiesBondAccount();    	    
    	    //case Instrument.INST_BOND_NON:             return new SecuritiesAccount();
    	    
    	    case Instrument.INST_SECR_EQUITY:          return new SecuritiesEquity();    	    
    	    case Instrument.INST_SECR_EQFUT:           return new SecuritiesEquity();
    	    case Instrument.INST_SECR_EQOPT:           return new SecuritiesEquity();
    	    case Instrument.INST_SECR_EQWRT:           return new SecuritiesEquity();
    	    case Instrument.INST_SECR_STRUC:           return new SecuritiesEquity();    	                
    	    case Instrument.INST_SECR_NON:  	       return new SecuritiesEquity();
    	    
    	    
    	    case Instrument.INST_FIDE_FXSWAP:          return new DerivativesFxForward();
    	    case Instrument.INST_FIDE_CCSWAP:          return new DerivativesCcsForward();
    	    case Instrument.INST_FIDE_KTBFUT:          return new DerivativesKtbFutures();    	    
    	    case Instrument.INST_FIDE_KTBFWD:          return new DerivativesKtbForward();
    	    case Instrument.INST_FIDE_IDXFUT:          return new DerivativesIndexFutures();
    	    case Instrument.INST_FIDE_IDXOPTC:         return new DerivativesIndexOption();    	    
    	    case Instrument.INST_FIDE_IDXOPTP:         return new DerivativesIndexOption();
    	    case Instrument.INST_FIDE_NON:             return new DerivativesBalance();
    	    
    	    
    	    case Instrument.INST_LOAN_FRB:             return new LoanBulletRepay();    	    
    	    
    	    
    	    case Instrument.INST_ACCO:                 return new SecuritiesAccount();
    	    case Instrument.INST_NON_IDENTIFIED:       return new SecuritiesAccount();
    	    default: {
    	    	log.info("Unidentified Instrument Model: [{}]", this.instCode);
    	    	return new SecuritiesAccount();
    	    }
	    }	    
    }        
    
    
    public Instrument getInstrumentModel(Integer scenNum, Map<String, IrCurveHis> scenarioCurveHis) throws Exception {    	

	    switch(instCode) {}	   
	    return null;
    }

}
