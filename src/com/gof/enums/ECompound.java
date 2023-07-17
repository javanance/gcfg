package com.gof.enums;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;


public enum ECompound {
		Simply(1)
	,	Monthly(12){
			public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate) {
				if(baseDate ==null || cfDate ==null) {
					return 1.0;
				}
				else {
					LocalDate tempBaseDate = baseDate.plusDays(-1); 			//월초데이터는 전월말로 처리
					LocalDate tempCfDate   = cfDate.plusDays(-1);				//월초데이터는 전월말로 처리
					long monNum = ChronoUnit.MONTHS.between(tempBaseDate.with(TemporalAdjusters.firstDayOfMonth()) , tempCfDate.with(TemporalAdjusters.firstDayOfMonth()));
					double tf = Math.max((double)monNum  , 0.0);
//					logger.info("In ECompound : {}, {}", tf, irRate);
					return Math.pow((1+irRate/12), -1*tf);
				}
			}
			
			public double getDfDerivative(double irRate, LocalDate baseDate, LocalDate cfDate) {
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				else{
					LocalDate tempBaseDate = baseDate.plusDays(-1); 			//월초데이터는 전월말로 처리
					LocalDate tempCfDate   = cfDate.plusDays(-1);				//월초데이터는 전월말로 처리
					long monNum = ChronoUnit.MONTHS.between(tempBaseDate.with(TemporalAdjusters.firstDayOfMonth()) , tempCfDate.with(TemporalAdjusters.firstDayOfMonth()));
					double tf =Math.max((double)monNum  , 0.0);
					return Math.pow((1+irRate/12), -1 * tf - 1 ) * -1 * tf / 12 ;
				}
			}
			
			public double getIntRateFromDf(LocalDate baseDate, LocalDate cfDate, double df) {
				
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				else {
					LocalDate tempBaseDate = baseDate.plusDays(-1); 			//월초데이터는 전월말로 처리
					LocalDate tempCfDate   = cfDate.plusDays(-1);				//월초데이터는 전월말로 처리
					long monNum = ChronoUnit.MONTHS.between(tempBaseDate.with(TemporalAdjusters.firstDayOfMonth()) , tempCfDate.with(TemporalAdjusters.firstDayOfMonth()));
					double tf = Math.max((double)monNum  , 1.0);
					
					return 12 *( Math.pow(df , -1/tf) -1) ;
				}
				
			}
			public double getIntRateFromDf(int unitNum, double df) {
					double tf = Math.max((double)unitNum  , 1.0);
					return 12 *( Math.pow(df , -1/tf) -1) ;
			}
		} 
	
	,	Quarterly(4)
	, 	SemiAnnu(2)
	, 	Annualy(1){
			public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate) {
				if(baseDate ==null || cfDate ==null) {
					return 1.0;
				}
				else {
					double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
					return Math.pow((1+irRate), -1*tf);
				}	
			}
			
			public double getDfDerivative(double irRate, LocalDate baseDate, LocalDate cfDate) {
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				else{
					double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
					return Math.pow((1+irRate), -1 * tf - 1 ) * -1 * tf;
				}
			}
			public double getIntRateFromDf(LocalDate baseDate, LocalDate cfDate, double df) {
				
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				
				else {
					double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 1.0) / 365;
					return 1* ( Math.pow(df , -1/ tf) -1) ;
					
				}
				
			}
			public double getIntRateFromDf(int unitNum, double df) {
				double tf = Math.max((double)unitNum  , 1.0) / 365;
				return 1* ( Math.pow(df , -1/ tf) -1) ;
			}
		}
	,	Continously(10000){
			public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate) {
				double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
				return Math.exp(-1*irRate * tf);
			}
			
			public double getDfDerivative(double irRate, LocalDate baseDate, LocalDate cfDate) {
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				else{
					double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
					return Math.exp(-1* irRate * tf - 1) * -1 * tf;
				}
			}
			
			public double getIntRateFromDf(LocalDate baseDate, LocalDate cfDate, double df) {
				if(baseDate ==null || cfDate ==null) {
					return 0.0;
				}
				else {
					double tf =(double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
					return Math.log(df) / tf ;
				}
				
			}
		}
	
	;
	
	//private static Logger logger = LoggerFactory.getLogger(ECompound.class);
	private int frequency;
	
	private ECompound(int freq) {
		this.frequency = freq;
	}
	
	public int getFrequency() {
		return frequency;
	}

	public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate) {
		return 0.0;
	}
	public double getDfDerivative(double irRate, LocalDate baseDate, LocalDate cfDate) {
		return 0.0;
	}
	
	public double getIntRateFromDf(LocalDate baseDate, LocalDate cfDate, double df) {
		return 0.0;
	}
	
	public double getIntRateFromDf(int unitNum, double df) {
		return 0.0;
	}
}
