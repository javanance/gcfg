package com.gof.instrument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.KicsAssetResult;
import com.gof.enums.EFinElements;
import com.gof.util.TimeUtil;

public class DerivativesBalance extends DerivativesAbstract {
	
	//private final static Logger logger = LoggerFactory.getLogger(DerivativesBalance.class.getSimpleName());
	
	public DerivativesBalance(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;		
	}	
	
	public DerivativesBalance() {}	
	
	public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {
		
		evaluateCashflow();				
		
		List<KicsAssetResult> cflist = new ArrayList<KicsAssetResult>();
		
		List<Integer> legType = new ArrayList<Integer>();
		legType.add(REC_LEG_KEY);
		legType.add(PAY_LEG_KEY);
		legType.add(NET_LEG_KEY);
		
//		List<Integer> financialElements = new ArrayList<Integer>();
//		financialElements.add(FE_PRESENT_VALUE);
//		financialElements.add(FE_EFFECTIVE_MATURITY);		

		List<Integer> financialElements = EFinElements.DEFAULT.getEFinElementList();
		
//		double recTimeFactor = TimeUtil.getTimeFactor(baseDate, maturityDate, recDayCountBasis);
//		double payTimeFactor = TimeUtil.getTimeFactor(baseDate, maturityDate, payDayCountBasis);	    
	    double recFxRatio = (currencyType ? DEF_UNIT_PRICE : this.recFxRateBase);
	    double payFxRatio = (currencyType ? DEF_UNIT_PRICE : this.payFxRateBase);
		
		/////////////////////////////////////////////////////////////		
	    this.result.setBaseDate(TimeUtil.dateToString(this.baseDate));		
		this.result.setExpoId(this.expoId);
	    this.result.setFundCd(this.fundCd);
		this.result.setProdTpcd(this.prodTpcd);
		this.result.setAccoCd(this.accoCd);
		this.result.setDeptCd(this.deptCd);		

		for(Integer leg : legType) {
			
			this.result.setLegType(setLegDefineMap(leg));		
			
			switch(leg.intValue()) {			

			    case REC_LEG_KEY: {
			    	this.result.setCurrency(currencyType ? this.recCurrency : DEF_CURRENCY);
	
			    	for(Integer fe : financialElements) {
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {						    
			    		    case FE_PV_DIRTY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(this.recAmt * recFxRatio);
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }				    		
				    		case FE_EFFECTIVE_MATURITY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getEffectiveMaturity());
						    	cflist.add(cloneEntity(this.result));
						    	break;						    
						    }				    		 
						    case FE_ACCRUED_INTEREST: {
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));						    	
						    	this.result.setValue(this.recAccruedInterest / this.recFxRateBase * recFxRatio);
						    	//Originally KRW Converted Value from Source
						    	cflist.add(cloneEntity(this.result));						    	
						    	break;
						    }
						    case FE_PV_CLEAN: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue((this.recAmt - this.recAccruedInterest / this.recFxRateBase) * recFxRatio);
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_FXRT_EXPOSURE: {		
						    	if(this.recCurrency != null && !this.recCurrency.equals(DEF_CURRENCY)) {
							    	this.result.setCurrency(this.recCurrency);
							    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
							    	this.result.setValue((this.recAmt - this.recAccruedInterest / this.recFxRateBase) * recFxRatio);
							    	cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }						    
						    default: break;						
			    		}
			    	}			    	
			    	break;  //switch -> RECEIVE_LEG_KEY
			    }
			    case PAY_LEG_KEY: {			    	
			    	this.result.setCurrency(currencyType ? this.recCurrency : DEF_CURRENCY);
	
			    	for(Integer fe : financialElements) {
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {
			    		    case FE_PV_DIRTY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(this.payAmt * payFxRatio);
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_MATURITY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getEffectiveMaturity());
						    	cflist.add(cloneEntity(this.result));
						    	break;						    
						    }						  
						    case FE_ACCRUED_INTEREST: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(this.payAccruedInterest / this.payFxRateBase * payFxRatio);
						    	//Originally KRW Converted Value from Source								
						    	cflist.add(cloneEntity(this.result));						    	
						    	break;
						    }
						    case FE_PV_CLEAN: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue((this.payAmt - this.payAccruedInterest / this.payFxRateBase) * payFxRatio);
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }						    
						    case FE_FXRT_EXPOSURE: {		
						    	if(this.payCurrency != null && !this.payCurrency.equals(DEF_CURRENCY)) {
							    	this.result.setCurrency(this.payCurrency);
							    	this.result.setValue((this.payAmt - this.payAccruedInterest / this.payFxRateBase) * payFxRatio);
							    	this.result.setValue(this.payAmt);
							    	cflist.add(cloneEntity(this.result));
						    	}
						    	break;
						    }
						    default: break;						
			    		}
			    	}			    	
			    	break;  //switch -> PAY_LEG_KEY
			    }		    	
			    case NET_LEG_KEY: {			    	
			    	this.result.setCurrency(DEF_CURRENCY);

			    	for(Integer fe : financialElements) {
			    		this.result.setResultType(String.format("%02d",fe));
			    		this.result.setResultName(setResultDefineMap(fe));
			    		
			    		switch(fe.intValue()) {
						    case FE_PV_DIRTY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getPresentValue() + this.recAccruedInterest - this.payAccruedInterest);						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_EFFECTIVE_MATURITY: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getEffectiveMaturity()-getEffectiveMaturity());
						    	cflist.add(cloneEntity(this.result));
						    	break;						    
						    }		
						    case FE_ACCRUED_INTEREST: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(this.recAccruedInterest - this.payAccruedInterest);						    	
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_PV_CLEAN: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getPresentValue());
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    case FE_BS_AMOUNT: {						    							    	
						    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
						    	this.result.setValue(getBsAmt());
						    	cflist.add(cloneEntity(this.result));
						    	break;
						    }
						    default: break;						
			    		}
			    	}			    	
			    	break;  //switch -> NET_PAYOFF_KEY
			    }
			    default: break;
			}
		}		
		return cflist;
	}		
	
	protected void setAttributes() {}	

	protected void setPayoffDate() {		
		
		netPayoffDate    = new LocalDate[1];
		netPayoffDate[0] = this.maturityDate;		
	}
	

    protected void setPayoffAmount() {	    
	 
    	this.netPrincipalPayoffAmount  = new double[netPayoffDate.length];
	    this.netPrincipalPayoffAmount[0] = this.bsAmt;
	    //this.netPrincipalPayoffAmount[0] = this.deriPdcAseAmt - this.deriPdcDbtAmt;
    }
    
	 
	protected double getPresentValue() {
		
		return this.netPrincipalPayoffAmount[0];
	}
	
	
    private double getEffectiveMaturity() {
    	
		return TimeUtil.getTimeFactor(baseDate, maturityDate, this.recDayCountBasis);		
    }
    
    
    protected double getImpliedSpread() throws Exception {
    	return 0.0;
    }
    
    protected double getImpliedSpread(double price) throws Exception {
    	return 0.0;
    }
    
    protected double getImpliedSpread(double price, double initGuess) throws Exception {
    	return 0.0;
    }

    
}
