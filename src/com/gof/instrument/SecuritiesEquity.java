package com.gof.instrument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.KicsAssetResult;
import com.gof.entity.KicsAssetSecr;
import com.gof.enums.EFinElements;
import com.gof.util.GeneralUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SecuritiesEquity extends SecuritiesAbstract {
	
	protected LocalDate[] payoffDate;	
	protected double[]    principalPayoffAmount;
	protected double[]    interestPayoffAmount;
	
	public SecuritiesEquity(LocalDate baseDate) {		
		super();
		this.baseDate = baseDate;		
	}	
	
	public SecuritiesEquity() {
		super();
		this.scenNum              = 0;
		this.scenarioCurveHis     = null;
		this.fxTypCd              = null;
		this.fxVal                = null;
		this.financialElements    = EFinElements.EQUITY.getEFinElementList();		
	}	
		
	@Override
	public void setInstrumentEntities(KicsAssetSecr entity) throws Exception {
		this.baseDate        = setDate(entity.getBaseDate());
		this.expoId          = entity.getExpoId();
		this.fundCd          = entity.getFundCd();		
		this.prodTpcd        = entity.getProdTpcd();		
		this.accoCd          = entity.getAccoCd();				
		this.isinCd          = entity.getIsinCd();		
		this.deptCd          = entity.getDeptCd();
		this.currency        = GeneralUtil.objectToPrimitive(entity.getCrnyCd(),   DEF_CURRENCY);
		this.issueDate       = setDate(entity.getIssuDate(), this.baseDate);		
		this.maturityDate    = setDate(entity.getMatrDate(), this.baseDate.plusDays(1));				
		this.bsAmt           = GeneralUtil.objectToPrimitive(entity.getBsAmt());
		this.fairBsAmt       = GeneralUtil.objectToPrimitive(entity.getFairBsAmt());
		this.dayCountBasis   = 1;
		if(this.baseDate == null) log.error("Equity Instrument Error: {}", entity);
		
		//log.info("entity: {}", entity);
	}	
	
	
	protected void evaluateCashflow() throws Exception {
		
		setAttributes();
		setPayoffDate();
		setPayoffAmount();
	}
	
	
	protected void setAttributes() throws Exception {
		
	}	
	

	protected void setPayoffDate() throws Exception {		
		
		payoffDate    = new LocalDate[1];
		payoffDate[0] = this.maturityDate;		
	}
	

    protected void setPayoffAmount() throws Exception {	    
	 
    	this.principalPayoffAmount  = new double[payoffDate.length];
	    this.principalPayoffAmount[0] = this.fairBsAmt;
	    //this.netPrincipalPayoffAmount[0] = this.deriPdcAseAmt - this.deriPdcDbtAmt;
    }
    
	
	protected double getPresentValue() throws Exception {		
		return this.principalPayoffAmount[0];
	}
	
	
	protected double getEffectiveMaturity() throws Exception {    	
		return TimeUtil.getTimeFactor(baseDate, maturityDate, this.dayCountBasis);		
    }
    
    
	protected double getAccruedInterest() throws Exception {
    	return 0.0;
    }

	
	/**
     * TODO: EQUITY, NON EQUITY, EQUITY OPTION, EQUITY FUTURES,  �� �����Ѵ�? �׸��� ������ ��ì�̵� �����ϴ� �ֵ�....�ֽ���� �ȸ��� �ֵ�...��ȯ����?
     */	
	@Override
	public List<KicsAssetResult> getValuation(boolean currencyType) throws Exception {
		
		evaluateCashflow();				
		
		List<KicsAssetResult> cflist = new ArrayList<KicsAssetResult>();
		//double fxRatio = (currencyType ? DEF_FX_RATE : this.fxRate);	
		
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
//			    case FE_PRINCIPAL_CF: {
//			    	for(int i=0; i<payoffDate.length; i++) {			    		
//			    		this.result.setResultDate(TimeUtil.dateToString(payoffDate[i]));
//			    		this.result.setValue(Math.floor(this.principalPayoffAmount[i] * fxRatio));
//			    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));			    			
//		    		}
//			    	break;
//			    }
//			    case FE_INTEREST_CF: {						    							    	
//			    	for(int i=0; i<payoffDate.length; i++) {			    		
//			    		this.result.setResultDate(TimeUtil.dateToString(payoffDate[i]));
//			    		this.result.setValue(Math.floor(this.interestPayoffAmount[i] * fxRatio)); 
//			    		if(Math.abs(this.result.getValue()) > ZERO_DOUBLE) cflist.add(cloneEntity(this.result));
//			    	}
//			    	break;
//			    }
//			    case FE_PV_DIRTY: {			    	
//			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
//			    	this.result.setValue(Math.floor(getPresentValue() * fxRatio));
//			    	cflist.add(cloneEntity(this.result));			    	
//			    	break;
//			    }			    
//			    case FE_EFFECTIVE_MATURITY: {						    							    	
//			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));    	
//			    	this.result.setValue(getEffectiveMaturity());
//			    	cflist.add(cloneEntity(this.result));
//			    	break;
//			    }
			    case FE_PV_CLEAN: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
			    	this.result.setValue(Math.floor(getFairBsAmt()));						    	
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }						    
//			    case FE_FXRT_EXPOSURE: {		
//			    	if(this.currency != null && !this.currency.equals(DEF_CURRENCY)) {
//				    	this.result.setCurrency(this.currency);
//				    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));
//				    	this.result.setValue(Math.floor((getPresentValue() - getAccruedInterest()) * 1.0));		
//				    	cflist.add(cloneEntity(this.result));
//			    	}
//			    	break;
//			    }					    	    
			    case FE_BS_AMOUNT: {						    							    	
			    	this.result.setResultDate(TimeUtil.dateToString(this.baseDate));			    	
			    	this.result.setValue(getFairBsAmt() / (currencyType && !this.currency.equals(DEF_CURRENCY) ? this.fxRateBase : DEF_FX_RATE));
			    	cflist.add(cloneEntity(this.result));
			    	break;
			    }
			    default: break;
    		}
    	}			    
		return cflist;
	}			    
    
}
