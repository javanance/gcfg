package com.gof.enums;

import java.util.ArrayList;
import java.util.List;

import com.gof.interfaces.Instrument;

		
public enum EFinElements {	

	   BOND        (
			          Instrument.FE_PRINCIPAL_CF         	    
			   	    , Instrument.FE_INTEREST_CF
			   	    , Instrument.FE_YIELD_TO_MATURITY	 
  			   	    , Instrument.FE_PV_DIRTY
  			   	    , Instrument.FE_ACCRUED_INTEREST
  			   	    , Instrument.FE_PV_CLEAN  			   	    
  			   	    , Instrument.FE_EFFECTIVE_MATURITY
  			   	    , Instrument.FE_EFFECTIVE_DURATION
  			   	    , Instrument.FE_MODIFIED_DURATION				   	
				   	, Instrument.FE_BS_AMOUNT
				   	, Instrument.FE_ACCRUED_INT_BS
//				   	, Instrument.FE_IRATE_EXPOSURE
//				   	, Instrument.FE_FXRT_EXPOSURE
//				   	, Instrument.FE_STOCK_EXPOSURE
				   	, Instrument.FE_IMPLIED_SPREAD
				   	, Instrument.FE_IMPLIED_MATURITY
			       )

	 , LOAN        (
		              Instrument.FE_PRINCIPAL_CF         	    
		   	        , Instrument.FE_INTEREST_CF
		   	        , Instrument.FE_YIELD_TO_MATURITY	 
		   	        , Instrument.FE_PV_DIRTY
		   	        , Instrument.FE_ACCRUED_INTEREST
		   	        , Instrument.FE_PV_CLEAN  			   	    
		   	        , Instrument.FE_EFFECTIVE_MATURITY
		   	        , Instrument.FE_EFFECTIVE_DURATION
		   	        , Instrument.FE_MODIFIED_DURATION				   	
			   	    , Instrument.FE_BS_AMOUNT
			   	    , Instrument.FE_ACCRUED_INT_BS
//			   	    , Instrument.FE_IRATE_EXPOSURE
//			   	    , Instrument.FE_FXRT_EXPOSURE
//			   	    , Instrument.FE_STOCK_EXPOSURE
			   	    , Instrument.FE_IMPLIED_SPREAD
			   	    , Instrument.FE_IMPLIED_MATURITY
		           )	   
	 
	 , SWAP        (
	                  Instrument.FE_PRINCIPAL_CF	         	    
                    , Instrument.FE_INTEREST_CF
                    , Instrument.FE_YIELD_TO_MATURITY
                    //, Instrument.FE_FWDFUT_PRICE                    
                    , Instrument.FE_PV_DIRTY
                    , Instrument.FE_ACCRUED_INTEREST
                    , Instrument.FE_PV_CLEAN                    
                    , Instrument.FE_EFFECTIVE_MATURITY
                    , Instrument.FE_EFFECTIVE_DURATION
                    , Instrument.FE_MODIFIED_DURATION
                    , Instrument.FE_BS_AMOUNT
                    , Instrument.FE_ACCRUED_INT_BS
//                    , Instrument.FE_IRATE_EXPOSURE
//                    , Instrument.FE_FXRT_EXPOSURE
//                    , Instrument.FE_STOCK_EXPOSURE
                    , Instrument.FE_IMPLIED_SPREAD                    
	               )	   
	 
     , KTB_FUTURES (
        	          Instrument.FE_PRINCIPAL_CF	         	    
        	        , Instrument.FE_PV_DIRTY	    
        	        //, Instrument.FE_ACCRUED_INTEREST
        	        , Instrument.FE_PV_CLEAN
        	        , Instrument.FE_EFFECTIVE_MATURITY	         	    
        	        , Instrument.FE_BS_AMOUNT
        	        //, Instrument.FE_ACCRUED_INT_BS
//        	        , Instrument.FE_IRATE_EXPOSURE
//                    , Instrument.FE_FXRT_EXPOSURE
//                    , Instrument.FE_STOCK_EXPOSURE
        	        , Instrument.FE_KTB_FORWARD_RATE
        	        , Instrument.FE_IMPLIED_SPREAD
                   )	 
     , KTB_FWD (
	          Instrument.FE_PRINCIPAL_CF	         	    
	        , Instrument.FE_PV_DIRTY	    
	        //, Instrument.FE_ACCRUED_INTEREST
	        , Instrument.FE_PV_CLEAN
	        , Instrument.FE_INTEREST_CF
	        , Instrument.FE_EFFECTIVE_MATURITY	         	    
	        , Instrument.FE_BS_AMOUNT
	        //, Instrument.FE_ACCRUED_INT_BS
//	        , Instrument.FE_IRATE_EXPOSURE
//           , Instrument.FE_FXRT_EXPOSURE
//           , Instrument.FE_STOCK_EXPOSURE
	        , Instrument.FE_KTB_FORWARD_RATE
	        , Instrument.FE_IMPLIED_SPREAD
          )	
	 
	 , FIDE_EXCG   (
		    	      Instrument.FE_PRINCIPAL_CF		    	    
		    	    , Instrument.FE_PV_CLEAN
		    	    , Instrument.FE_EFFECTIVE_MATURITY		    	    
		    	    , Instrument.FE_BS_AMOUNT
//		    	    , Instrument.FE_IRATE_EXPOSURE
//                    , Instrument.FE_FXRT_EXPOSURE
//                    , Instrument.FE_STOCK_EXPOSURE
//                    , Instrument.FE_IMPLIED_SIGMA                    
                   )

	 , EQUITY      (
	                  Instrument.FE_PRINCIPAL_CF
  	                , Instrument.FE_PV_CLEAN
  	                , Instrument.FE_EFFECTIVE_MATURITY
  	                , Instrument.FE_BS_AMOUNT
//		   	        , Instrument.FE_IRATE_EXPOSURE
//		   	        , Instrument.FE_FXRT_EXPOSURE
//		   	        , Instrument.FE_STOCK_EXPOSURE		   	        
                   )

     , ACCOUNT     (
     	              Instrument.FE_PRINCIPAL_CF         
     	            , Instrument.FE_PV_CLEAN			  
     	            , Instrument.FE_EFFECTIVE_MATURITY
     	            , Instrument.FE_BS_AMOUNT
//              	    , Instrument.FE_IRATE_EXPOSURE
//              	    , Instrument.FE_FXRT_EXPOSURE
//              	    , Instrument.FE_STOCK_EXPOSURE
                   )

	 , DEFAULT     (  		                            		            
	   	              Instrument.FE_PRINCIPAL_CF
			        , Instrument.FE_PV_DIRTY
			        , Instrument.FE_ACCRUED_INTEREST
			        , Instrument.FE_PV_CLEAN
  		            , Instrument.FE_EFFECTIVE_MATURITY  		            
  		            , Instrument.FE_BS_AMOUNT
//  		            , Instrument.FE_IRATE_EXPOSURE
//                    , Instrument.FE_FXRT_EXPOSURE
//                    , Instrument.FE_STOCK_EXPOSURE
                   )
	;

	
	private Integer[] finElements;
			
	private EFinElements(Integer... elements) {		
		this.finElements = elements;		
	}
	
	public Integer[] getEFinElements() {
		return finElements;
	}
	
	public List<Integer> getEFinElementList() {
		
		List<Integer> feList = new ArrayList<Integer>();		
		for(Integer fe : finElements) feList.add(fe);
		
		return feList;
	}		

	
	public static EFinElements getEFinElements(String instName) {		 
		
		for(EFinElements fe : EFinElements.values()) {
			if(fe.name().equals(instName)) return fe;			
		}
		return DEFAULT;		
	}
	    	
}
	