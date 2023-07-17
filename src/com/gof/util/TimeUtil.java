package com.gof.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.TreeMap;

import com.gof.interfaces.Constant;
import com.gof.interfaces.Instrument;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TimeUtil implements Constant {	

	public static Map<LocalDate, Double> mergeCashflows(LocalDate[] cfdate1, double[] cashflow1, LocalDate[] cfdate2, double[] cashflow2) {		
		return mergeCashflows(true, cfdate1, cashflow1, Instrument.DEF_FX_RATE, cfdate2, cashflow2, Instrument.DEF_FX_RATE);
	}	

	
	public static Map<LocalDate, Double> mergeCashflows(boolean addSubYn, LocalDate[] cfdate1, double[] cashflow1, double cf1FxRate, LocalDate[] cfdate2, double[] cashflow2, double cf2FxRate) {
		
		int sign;
		sign = addSubYn ? 1 : -1;		
		
		Map<LocalDate, Double> mergeCfs = new TreeMap<LocalDate, Double>();
		
		for(int i=0; i<cfdate1.length; i++) mergeCfs.put(cfdate1[i], cashflow1[i] * cf1FxRate);		
		
		for(int j=0; j<cfdate2.length; j++) {			
			if(mergeCfs.containsKey(cfdate2[j])) mergeCfs.replace(cfdate2[j], mergeCfs.get(cfdate2[j]) + sign * cf2FxRate * cashflow2[j]);			
			else mergeCfs.put(cfdate2[j],  cf2FxRate * cashflow2[j]);
		}		
		return mergeCfs;
	}	
	

	/**
	 * TODO: #1 date1.isAfter case, #2 default case  
	 */
	public static double getTimeFactor(LocalDate date1, LocalDate date2, int dayCountBasis) {		
			      
	    if(date1.isAfter(date2)) {
            //log.warn("date1 cannot be larger than date2 !!!: {}, {}", date1, date2);
            return 0.0;	    	
	    }
	    
	    switch(dayCountBasis) {
	    
	        case DCB_ACT_365: return ChronoUnit.DAYS.between(date1, date2) / 365.0;
	        case DCB_A30_360: return daysBetweenA30360(date1, date2) / 360.0;
	        case DCB_E30_360: return daysBetweenE30360(date1, date2) / 360.0;
	        case DCB_ACT_ACT: return getTimeFactorActAct(date1, date2);
            case DCB_ACT_360: return ChronoUnit.DAYS.between(date1, date2) / 360.0;
            case DCB_MON_DIF: return ChronoUnit.MONTHS.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)) * 1.0 / MONTH_IN_YEAR;
            
            default: {
               log.error("Unidentified Day Count Basis code: {}", dayCountBasis);
               return 0.0;
	        }
	    }	    
	}	
	
	
    public static double getTimeFactorActAct(LocalDate date1, LocalDate date2) {    	

    	double timeFactor;    	
    	
    	timeFactor =  (double) (date1.lengthOfYear() - date1.getDayOfYear()) / date1.lengthOfYear();    	
    	timeFactor += (double) date2.getDayOfYear() / date2.lengthOfYear();    	
    	timeFactor += (double) date2.getYear() - date1.getYear() - 1.0;
    	
    	//System.out.println("TF(act/act) = " + timeFactor + " : " + date1 + " | " + date2);
    	return timeFactor;    	
    }   
    
    
    public static int daysBetweenA30360(LocalDate date1, LocalDate date2) {    	
    	
    	int day1 = date1.getDayOfMonth();
    	int day2 = date2.getDayOfMonth();
    	int daysDiff;
    	
    	daysDiff = (date2.getYear() - date1.getYear()) * MONTH_IN_YEAR;
    	daysDiff += date2.getMonthValue() - date1.getMonthValue();
    	daysDiff *= 30;
    	
    	if (date1.getMonth().equals(Month.FEBRUARY) && day1 == date1.lengthOfMonth()) {
    		if(date2.getMonth().equals(Month.FEBRUARY) && day2 == date2.lengthOfMonth()) {
    			day2 = 30;    			
    		}
    		day1 = 30;
    	}
    	
    	if (day2 == 31 && day1 >= 30) day2 = 30;
    	if (day1 == 31) day1 = 30;
    	
    	daysDiff += day2 - day1;
    	
    	return daysDiff;
    }
    
    
    public static int daysBetweenE30360(LocalDate date1, LocalDate date2) {    	
    	
    	int day1 = date1.getDayOfMonth();
    	int day2 = date2.getDayOfMonth();
    	int daysDiff;
    	
    	daysDiff = (date2.getYear() - date1.getYear()) * MONTH_IN_YEAR;
    	daysDiff += date2.getMonthValue() - date1.getMonthValue();
    	daysDiff *= 30;
    	
    	if (day1 > 30) day1 = 30;
    	if (day2 > 30) day2 = 30;
    	
    	daysDiff += day2 - day1;
    	
    	return daysDiff;
    }    
	
	
	public static double[] getTimeFactor(LocalDate date1, LocalDate[] dates, int dayCountBasis) {
		
		double[] timeFactor = new double[dates.length];
		
		for(int i=0; i<dates.length; i++) {	
			
		    if(date1.isAfter(dates[i])) timeFactor[i] = 0.0;		    
		    else timeFactor[i] = getTimeFactor(date1, dates[i], dayCountBasis);		    
		}		
		return timeFactor;	    
	}	
	

	public static double[] getDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {
		
		double[] timeFactor = new double[dates.length];		
		double[] discountRate = new double[dates.length];
		double[] discountFactor = new double[dates.length];		
		
//		double[] timeFactor2 = new double[dates.length];
//		LocalDate nextDate = baseDate;
//		for(int i=0; i<dates.length; i++) {
//			if(baseDate.isBefore(dates[i])) {
//				nextDate = dates[i];
//				break;
//			}			
//		}		
		
		for(int i=0; i<dates.length; i++) {
			
		    if(baseDate.isAfter(dates[i])) {
		    	discountRate  [i] = 0.0;
		    }
		    else {
		    	discountRate  [i] = getDiscountRate(matTerm, dcntRate, baseDate, dates[i]);
		    }		    
		    timeFactor        [i] = getTimeFactor(baseDate, dates[i], dayCountBasis);
		    discountFactor    [i] = getDiscountFactor(discountRate[i], dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, timeFactor[i]);
		    
//		    log.info("disctount Factor in TimeUtil: {},{},{},{},{},{}", i, dcntCmpdPeriod,  dates[i],  discountRate[i], timeFactor[i], discountFactor[i]);
		}
		return discountFactor;		
	}

	
	public static double[] getDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {		
		return getDiscountFactor(matTerm, dcntRate, baseDate, dates, CMPD_MTD_DISC, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
	}		


	public static double[] getDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, int dayCountBasis) {		
		return getDiscountFactor(matTerm, dcntRate, baseDate, dates, CMPD_MTD_DISC, Instrument.DEF_CMPD_PERIOD_YEAR, TIME_UNIT_YEAR, dayCountBasis);
	}
	
	
	public static double getDiscountRate(String[] matTerm, double[] dcntRate, LocalDate date1, LocalDate date2) {		
		return interpolate(matTerm, dcntRate, monthBetweenMatCd(date1, date2));
	}
	
	/**
	 * YTM(유통수익률)을 활용하여 현재가치를 구하는 경우 YTM자체가 연금리를 가정하고 있기 때문에, 단순히 TimeFactor가 아니라...복리로 할인한다. (1 + r/m)^(mt) 방식으로..
	 * 결국 FRM교과서 등의 예제는 이자지급주기별로 재투자됨을 고려한다는 의미이다??
	 * 스팟금리의 경우 지급주기가 할인율에 영향을 미치는 것은 안될 것이며, 상식적으로 3개월간격의 이표채의 쿠폰만 고려하면 6개월째 쿠폰에 대해서 모순이 생긴다.
	 * 결론적으로 1년금리인 YTM을 반영한 PV와 기간구조의 스팟금리를 고려한 PV의 할인구조는 다르다고 보는것이 합당하다.
	 * 무엇보다 여기서의 cmpd Mtd가 할인에 관한 부분이지, 복리채와는 별개로 생각해야 할 것이다. 단리채나 할인채를 생각하면 부리와 할인은 별개의 문제이다.
	 */
	public static double getDiscountFactor(double yield, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, double timeFactor) {
		
		
		double dcntCmpdFreq = getCompoundFrequency(dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType);
		
//		log.info("TimeUtil 1 : {}, {}, {},{},{},{} ", yield, dcntCmpdPeriod);
//		log.info("TimeUtil  : {}, {}, {},{},{},{} ", yield, dcntCmpdFreq, timeFactor,dcntCmpdFreq * timeFactor,  yield / dcntCmpdFreq, 1.0 / (Math.pow(1.0 + yield / dcntCmpdFreq, dcntCmpdFreq * timeFactor)));

		switch(dcntCmpdMtd) {
    	
	    	case CMPD_MTD_SIMP: return 1.0 / (1.0 + yield * timeFactor);	    	
	    	case CMPD_MTD_DISC: return 1.0 / (Math.pow(1.0 + yield / dcntCmpdFreq, dcntCmpdFreq * timeFactor));	    		    	
	    	case CMPD_MTD_CONT: return 1.0 / (Math.exp(yield * timeFactor));	    	
	    	default: 
	    		log.warn("compoundMethod[{}] is  wrong in getDiscountFactor Method", dcntCmpdMtd);
	    		return 1.0;
    	}
		//int intTerm = (int) (Math.floor(timeFactor * pmtFreq));
		//double decimalTerm = timeFactor * pmtFreq - intTerm;		
    	//case CMPD_MTD_DISC:	return 1.0 / ( Math.pow(1.0 + yield / pmtFreq, intTerm) * (1.0 + yield / pmtFreq * decimalTerm) );
    	//case CMPD_MTD_DISC:	return 1.0 / ( Math.pow(1.0 + yield, (int) Math.floor(timeFactor)) * (1.0 + yield * (timeFactor - Math.floor(timeFactor))) );	   
	}    

	
	public static double getDiscountFactor(double yield, double timeFactor) {
		return getDiscountFactor(yield, CMPD_MTD_SIMP, timeFactor);
	}
	
	
	public static double getDiscountFactor(double yield, char dcntCmpdMtd, double timeFactor) {
		return getDiscountFactor(yield, dcntCmpdMtd, Instrument.DEF_CMPD_PERIOD_YEAR, TIME_UNIT_YEAR, timeFactor);				
	}	
	
	

	public static double[] getDiscountFactor(double ytm, LocalDate baseDate, LocalDate[] dates, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {		
		
		double[] discountFactor = new double[dates.length];		
		
		for(int i=0; i<dates.length; i++) {			
			discountFactor[i] = getDiscountFactor(ytm, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, getTimeFactor(baseDate, dates[i], dayCountBasis));
		}
		return discountFactor;		
	}

	
	public static double[] getDiscountFactor(double ytm, LocalDate baseDate, LocalDate[] dates, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {		
		return getDiscountFactor(ytm, baseDate, dates, CMPD_MTD_DISC, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
	}

	
	public static double[] getDiscountFactor(double ytm, LocalDate baseDate, LocalDate[] dates, int dayCountBasis) {		
		return getDiscountFactor(ytm, baseDate, dates, CMPD_MTD_DISC, Instrument.DEF_CMPD_PERIOD_YEAR, TIME_UNIT_YEAR, dayCountBasis);
	}
	

	public static double[] getTimeWeightedDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, char dcntCmpdMtd, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {
		
		double[] discountFactor = getDiscountFactor(matTerm, dcntRate, baseDate, dates, dcntCmpdMtd, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);				
		double[] weightedDiscountFactor = new double[discountFactor.length];
		
		for(int i=0; i<dates.length; i++) {		    	    
		    weightedDiscountFactor[i]= getTimeFactor(baseDate, dates[i], dayCountBasis) * discountFactor[i];
		}	
		return weightedDiscountFactor;		
	}	
		
	
	public static double[] getTimeWeightedDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, int dcntCmpdPeriod, char dcntCmpdPeriodType, int dayCountBasis) {		
		return getTimeWeightedDiscountFactor(matTerm, dcntRate, baseDate, dates, CMPD_MTD_DISC, dcntCmpdPeriod, dcntCmpdPeriodType, dayCountBasis);
	}
	
	
	public static double[] getTimeWeightedDiscountFactor(String[] matTerm, double[] dcntRate, LocalDate baseDate, LocalDate[] dates, int dayCountBasis) {		
		return getTimeWeightedDiscountFactor(matTerm, dcntRate, baseDate, dates, CMPD_MTD_DISC, Instrument.DEF_CMPD_PERIOD_YEAR, TIME_UNIT_YEAR, dayCountBasis);
	}

	
    /** 
     * Having no preprocessed interest attributes(compound/payment Frequency) for general interest factor
     */    
    public static double getIntFactor(boolean isProrated, double yield, char cmpdMtd, int cmpdPeriod, char cmpdPeriodType, int pmtTerm, char pmtTermType, double intTimeFactor) {
    	
    	double pmtFreq = getPaymentFrequency(pmtTerm, pmtTermType);
        double cmpdFreq = getCompoundFrequency(cmpdMtd, cmpdPeriod, cmpdPeriodType);
        
        return getIntFactor(isProrated, yield, cmpdMtd, cmpdFreq, pmtFreq, intTimeFactor);        
    }
	
    /** 
     * 
     * Assuming that when having compoundFrequency and paymentFrequency
     * @return either regular interest factor(false, intTimeFactor has no effect(not necessary)) OR pro-rated interest factor(true, paymentFrequency has no effect) 
     */    
    public static double getIntFactor(boolean isProrated, double yield, char cmpdMtd, double cmpdFreq, double pmtFreq, double intTimeFactor) {
    	
        if(isProrated) return getIntFactorPrr(yield, cmpdMtd, cmpdFreq, intTimeFactor);
        else return getIntFactorReg(yield, cmpdMtd, cmpdFreq, pmtFreq);        
    }      
    
    /** 
     * Detailed calculation for regular interest factor 
     */
    public static double getIntFactorReg(double yield, char cmpdMtd, double cmpdFreq, double pmtFreq) {
    	
    	switch(cmpdMtd) {    	
    	
	    	case CMPD_MTD_SIMP: return yield / pmtFreq;	    	
	    	case CMPD_MTD_DISC: return Math.pow(1.0 + yield / cmpdFreq, cmpdFreq / pmtFreq) - 1.0;	    	
	    	case CMPD_MTD_CONT: return Math.exp(yield / pmtFreq) - 1.0;	    	
	    	default: 
	    		log.warn("compoundMethod[{}] is wrong in getIntFactor Method", cmpdMtd);
	    		return 0.0;	    	
    	}    	
    }

    /** 
     * For annual compounding for regular interest factor 
     */
    public static double getIntFactorReg(double yield, char cmpdMtd, double pmtFreq) {
    	return getIntFactorReg(yield, cmpdMtd, Instrument.DEF_CMPD_FREQ_YEAR, pmtFreq);    	
    }    
    
    /** 
     * Having no preprocessed interest attributes(compound/payment Frequency) for regular interest factor
     */    
    public static double getIntFactorReg(double yield, char cmpdMtd, int cmpdPeriod, char cmpdPeriodType, int pmtTerm, char pmtTermType) {
    	
        double pmtFreq = getPaymentFrequency(pmtTerm, pmtTermType);
        double cmpdFreq = getCompoundFrequency(cmpdMtd, cmpdPeriod, cmpdPeriodType);
        return getIntFactorReg(yield, cmpdMtd, cmpdFreq, pmtFreq);       
     }    
    
    /** 
     * Detailed calculation for prorated interest factor 
     */    
    public static double getIntFactorPrr(double yield, char cmpdMtd, double cmpdFreq, double intTimeFactor) {
    	
    	//log.info("IntFactorPrr: S:{},D:{}", yield * intTimeFactor, Math.pow(1.0 + yield / cmpdFreq, cmpdFreq * intTimeFactor) - 1.0);
    	switch(cmpdMtd) {   
    	
	    	case CMPD_MTD_SIMP: return yield * intTimeFactor;	    	
	    	case CMPD_MTD_DISC: return Math.pow(1.0 + yield / cmpdFreq, cmpdFreq * intTimeFactor) - 1.0;	    	
	    	case CMPD_MTD_CONT: return Math.exp(yield * intTimeFactor) - 1.0;	    	
	    	default: 
	    		log.warn("compoundMethod[{}] is wrong in getIntFactor Method", cmpdMtd);
	    		return 0.0;
    	}    	
    }
    
    /** 
     * For annual compounding for prorated interest factor 
     */    
    public static double getIntFactorPrr(double yield, char cmpdMtd, double intTimeFactor) {
    	return getIntFactorPrr(yield, cmpdMtd, Instrument.DEF_CMPD_FREQ_YEAR, intTimeFactor);    	
    }
    
    /** 
     * Having no preprocessed interest attributes(compound Frequency) for prorated interest factor
     */    
    public static double getIntFactorPrr(double yield, char cmpdMtd, int cmpdPeriod, char cmpdPeriodType, double intTimeFactor) {
 	   
        double cmpdFreq = getCompoundFrequency(cmpdMtd, cmpdPeriod, cmpdPeriodType);
        return getIntFactorPrr(yield, cmpdMtd, cmpdFreq, intTimeFactor);        
    }    

    
    public static double getIntFactor(double discountFactor) {    	
       return 1.0 / discountFactor - 1.0;
    }
    
    
	public static boolean isOddCouponDate(LocalDate date1, LocalDate date2, int pmtTerm, char pmtTermType) {

	    switch(pmtTermType) {
	    
    	    case TIME_UNIT_MONTH:    	    	 
    	    	if(date1.plusMonths(pmtTerm).isEqual(date2)) return false;
    	    	else return true;    	    	
    	    	
    	    case TIME_UNIT_YEAR:    	    	 
    	    	if(date1.plusYears(pmtTerm).isEqual(date2)) return false;
    	    	else return true;
    	    	
    	    default: return false;    	    
	    }    	    
	}	
	
    /**
     * TODO: paymentFreq는 1년에 몇번을 전제로 하고 있음 따라서 2년에 1회 라던지 이런상황은 Integer형태에서 소화가 안됨
     * 이표채의 경우보다는 할인채나 복리채의 경우에 이슈가 될 수 있는 상황임
     */
    public static double getPaymentFrequency(int pmtTerm, char pmtTermType) {    	
    	
    	if(pmtTerm < ZERO_DOUBLE) {
    		log.warn("paymentTerm[{}] is wrong in getPaymentFrequency Method", pmtTerm);
    		return 1.0;
    	}
    	
    	switch(pmtTermType) {
    	    	
	    	case TIME_UNIT_END:   return 1.0;	    	
	    	case TIME_UNIT_YEAR:  return 1.0 * YEAR_IN_YEAR  / pmtTerm;	    	
	    	case TIME_UNIT_MONTH: return 1.0 * MONTH_IN_YEAR / pmtTerm;	    	
	    	case TIME_UNIT_WEEK:  return 1.0 * WEEK_IN_YEAR  / pmtTerm;	    	
	    	case TIME_UNIT_DAY:   return 1.0 * DAY_IN_YEAR   / pmtTerm;	    	
		    default: 
		    	log.warn("paymentTermType[{}] is wrong in getPaymentFrequency Method", pmtTermType);
		    	return 1.0;		    			  
    	}    	    	
    }    
    
    
    public static double getCompoundFrequency(char cmpdMtd, int cmpdPeriod, char cmpdPeriodType) {    	
    	
    	switch(cmpdMtd) {    	
	    	
    	    case CMPD_MTD_SIMP: return 1.0;	    	
	    	case CMPD_MTD_CONT: return Double.MAX_VALUE;	    	
	    	case CMPD_MTD_DISC: {
	    		
	    	    if     (cmpdPeriodType == TIME_UNIT_YEAR)  return 1.0 * YEAR_IN_YEAR   / cmpdPeriod;	    	    
	    	    else if(cmpdPeriodType == TIME_UNIT_MONTH) return 1.0 * MONTH_IN_YEAR  / cmpdPeriod;
	    	    else if(cmpdPeriodType == TIME_UNIT_DAY)   return 1.0 * DAY_IN_YEAR    / cmpdPeriod;
	    	    else log.warn("compoundPeriodType[{}] is wrong in getCompoundFrequency Method", cmpdPeriodType);	    	    
	    	}	    		
	       default: 
	    	   log.warn("compoundMethod[{}] is wrong in getCompoundFrequency Method", cmpdMtd);	  
	    	   return 1.0;	       
    	}    	
    }    
    
    
    public static LocalDate stringToDate(String dateString) {
    	
		if(dateString != null && dateString.length() == 8) {
			
			int year  = Integer.parseInt(dateString.substring(0, 4));
			int month = Integer.parseInt(dateString.substring(4, 6));
			int day   = Integer.parseInt(dateString.substring(6, 8));			

			return LocalDate.of(year, month, day);    		
		}
		return null;    	
    	
//    	try {    		
//    		if(dateString != null && dateString.length() == 8) {
//        		
//        		int year  = Integer.parseInt(dateString.substring(0, 4));
//        		int month = Integer.parseInt(dateString.substring(4, 6));
//        		int day   = Integer.parseInt(dateString.substring(6, 8));    		
//        		
//        		return LocalDate.of(year, month, day);    		
//        	}    		
//    	}
//    	catch(Exception e) {    		
//    		return null;
//    	}    	
//    	return null;    	
    }
    
    
    public static String dateToString(LocalDate date) {    	
    	
    	if(date != null) {    		    		
    		return    String.format("%04d", date.getYear())
    				+ String.format("%02d", date.getMonthValue())
    				+ String.format("%02d", date.getDayOfMonth());
    	}    	
    	return null;    	
    }

    
    public static boolean isSettableDay(String dateString) throws Exception {    	
    	
    	if(dateString == null) return false;
    	
    	try {
    		stringToDate(dateString);
    	}
    	catch (Exception e) {
    		return false;
    	}    	
    	return true;
    }    
    
    
	public static boolean isSettableDay(LocalDate date, int day) throws Exception {		 
		
		try {
			date.withDayOfMonth(day);
		}
		catch (Exception e)	{
			return false;						
		}		
		return true;
	}
	
	
	public static boolean isYmString(String ymString) {
		
		if(ymString.length() != 6) return false;
		else return true;		
	}
	
	
	public static boolean isSettableDay(LocalDate date1, LocalDate date2) throws Exception {		
		return isSettableDay(date1, date2.getDayOfMonth());		
	}
	
    
	public static LocalDate setDays(LocalDate date1, LocalDate date2) throws Exception {
		
		if(isSettableDay(date1, date2)) {
			return date1.withDayOfMonth(date2.getDayOfMonth());			
		}
		return date1;
	}
	
	
	public static LocalDate setDays(LocalDate date, int day) throws Exception {
		
		if(isSettableDay(date, day)) {
			return date.withDayOfMonth(day);
		}
		return null;
	}    
    
    
    public static String toYearMonth(String dateString) {    	
    	return dateString.substring(0,6);
    }
    
    
    public static String toYearMonth(LocalDate date) {    	
    	return String.format("%04d", date.getYear()) + String.format("%02d", date.getMonthValue());
    }
    
    
	public static String toEndOfMonth(String baseDate) {
		
		if(baseDate.length() == 4) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), 12, 1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else if(baseDate.length( )== 6) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), 1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else if(baseDate.length() == 8) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4))
										, Integer.valueOf(baseDate.substring(4,6))
											, Integer.valueOf(baseDate.substring(6,8))).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else {
			log.error("Convert Date Error : {} is not date format", baseDate);
		}
		return null;
	}
	

	public static String addMonth(String baseDate, int monNum) {
		
		if(baseDate.length()==4) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), 1, 1).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyy"));
		}
		else if(baseDate.length()==6) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4)), Integer.valueOf(baseDate.substring(4,6)), 1).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyyMM"));
		}
		else if(baseDate.length()==8) {
			return LocalDate.of(Integer.valueOf(baseDate.substring(0,4))
										, Integer.valueOf(baseDate.substring(4,6))
											, Integer.valueOf(baseDate.substring(6,8))).plusMonths(monNum).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		else {
			log.error("Convert Date Error : {} is not date format", baseDate);
		}
		return null;
	}
	
	
	public static LocalDate addYearFrac(LocalDate baseDate, double yearFrac) {		
		return baseDate.plusDays(Math.round(yearFrac*365));
	}

	
	public static double getActualDayDiff(LocalDate date1, LocalDate date2) {
		return 1.0 * ChronoUnit.DAYS.between(date1, date2);
	}

	
	public static LocalDate addDays(LocalDate date1, int days) {
		return date1.plusDays(days);
	}
	
		
	public static int monthBetween(String bssd, String compareDate) {
		
		String baseBssd = bssd.substring(0,6) + "01";
		String otherBssd = compareDate.substring(0, 6) +"01";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		
		return (int) ChronoUnit.MONTHS.between(LocalDate.parse(baseBssd, formatter), LocalDate.parse(otherBssd, formatter));
		
	}   
	
    
    public static String shiftMatCd(boolean direction, String matCd) {
    	
    	int matTerm = Integer.parseInt(matCd.substring(1,5));
    	System.out.println("matTerm = " + matTerm);
    	
    	matTerm = direction ? matTerm + 1 : matTerm - 1;
    	System.out.println("matTerm = " + matTerm);
    	return TIME_UNIT_MONTH + String.format("%04d", matTerm);    	
    }   
    
    
    //TODO to be developed compatible to ORACLE Time Function
    public static double monthBetweenDouble(LocalDate date1, LocalDate date2) {

    	if(ChronoUnit.DAYS.between(date1, date2) < 0) return -1 * monthBetweenDouble(date1, date2);    	

    	int i = 1;
    	double tmp = 0.0;
    	
    	while(true) {
    		
    		//System.out.println(i + "th searching " + date1.toString() + " : " + date1.plusMonths(i).toString());
    		
    		tmp = 1.0 * ChronoUnit.DAYS.between(date1.plusMonths(i-1), date2) / ChronoUnit.DAYS.between(date1.plusMonths(i-1), date1.plusMonths(i));
    		System.out.println("Ratio :" + tmp + " : " + ChronoUnit.DAYS.between(date1.plusMonths(i-1), date2) + " / " + ChronoUnit.DAYS.between(date1.plusMonths(i-1), date1.plusMonths(i)));    		 
    		
    		if(date1.plusMonths(i).compareTo(date2) >= 0) {
    			return i;
    		}    		
    		i++;    		
    	}   	
    }    
    
    
    public static int monthBetween(LocalDate date1, LocalDate date2) {
    	
		long month = ChronoUnit.MONTHS.between(date1, date2);		
		return (int) monthBetween(date1, date2, month);		
    }   
    
    
    public static long monthBetween(LocalDate date1, LocalDate date2, long month) {
    	
    	switch(date1.plusMonths(month).compareTo(date2)) {
    	
	    	case 0: return month ;
	    	case -1: return month + 1;
	    	default: return month;    	
    	}    	    	    	
    }    
    
    
    public static String monthBetweenMatCd(LocalDate date1, LocalDate date2) {    	
		return TIME_UNIT_MONTH + String.format("%04d", monthBetween(date1, date2));		
	} 
    

    public static double interpolate(String[] strs, double[] values, String str, int extrapType, int interpType) {
    	
    	int[] idxs = new int[strs.length];
    	int idx = Integer.parseInt(str.substring(1, 5));
    	
    	for(int i=0; i<strs.length; i++) idxs[i] = Integer.parseInt(strs[i].substring(1, 5)); 	
    	
    	return interpolate(idxs, values, idx, extrapType, interpType);    	
    }
    
    /**
     * based on str = "M0003" format
     */
    public static double interpolate(String[] strs, double[] values, String str) {
    	
    	int[] idxs = new int[strs.length];
    	int idx = Integer.parseInt(str.substring(1, 5));    	
    	for(int i=0; i<strs.length; i++) idxs[i] = Integer.parseInt(strs[i].substring(1, 5)); 	
    	
    	return interpolate(idxs, values, idx);    	
    }
    
  
    public static double interpolate(int[] idxs, double[] values, int idx) {
    	//return interpolate(idxs, values, idx, EXTRAP_TYPE_CONSTANT, INTERP_TYPE_CONSTANT);    	
    	return interpolate(idxs, values, idx, EXTRAP_TYPE_CONSTANT, INTERP_TYPE_LINEAR);
    }
    
    
    public static double interpolate(int[] idxs, double[] values, int idx, int extrapType, int interpType) {    	
       
        if(idxs.length != values.length) {
        	log.info("The length of indexes and values must be equal!");
        	return 0.0;
        }
        if(idxs.length < 2) {
        	log.info( "The length of indexes must be 2 at least!");
        	return 0.0;
        }
        
        int idxFirst = idxs[0];
        int idxLast = idxs[idxs.length-1];
        int idxNearby = 0;
        double value = 0.0;
        
        if(idx < idxFirst) {
        	
        	switch(extrapType) {
        	
        	    case EXTRAP_TYPE_CONSTANT: 
        		    value = values[0];
        		    break;
        	    case EXTRAP_TYPE_LINEAR:
        	    	idxNearby = idxs[1];
        	    	value = (values[1] - values[0]) / (idxNearby - idxFirst) * (idx - idxFirst) + values[0];
        	    	break;
        	    default: 
        	    	log.info("Undefined Extraplotation Type!");
        	    	return 0.0;        	    
        	}        	
        }
        else if(idx > idxLast) {
        	
        	switch(extrapType) {
        	
	    	    case EXTRAP_TYPE_CONSTANT: 
	    		    value = values[values.length-1];	    		    
	    		    break;
	    	    case EXTRAP_TYPE_LINEAR:
	    	    	idxNearby = idxs[idxs.length-2];
        	    	value = (values[values.length-1] - values[values.length-2]) / (idxLast - idxNearby) * (idx - idxNearby) / values[values.length-2];
	    	    	break;
	    	    default: 
	    	    	log.info("Undefined Extraplotation Type!");
	    	    	return 0.0;	    	    		    	    	
        	}        	
    	}
        else {        	
        	
        	switch(interpType) {        	
        	
        	    case INTERP_TYPE_CONSTANT:        	    	
        	    	for(int i=1; i<idxs.length; i++) {
		                if(idx == idxs[i]) {            	   
		                    value = values[i];
		                    break;
		                }
		                else if(idx < idxs[i]) {
		                    value = values[i-1];
		                    break;
		                }        	    		
        	    	}        	
        	    	break;        	    	
        	    case INTERP_TYPE_LINEAR:        	    	
        	    	for(int i=1; i<idxs.length; i++) {         	   
		                if(idx == idxs[i]) {            	   
		                    value = values[i];
		                    break;
		                }
		                else if(idx < idxs[i]) {
		                    value = (values[i]-values[i-1]) / (idxs[i]-idxs[i-1]) * (idx-idxs[i-1]) + values[i-1];
		                    break;
		                }
        	    	}
        	    	break;        	    	
        	    default: {
        	    	log.info("Undefined Interpolation Type!");
        	    	return 0.0;
        	    }
        	}        	
        }        
        return value;            
    }
    
    
    public static double interpolate(double[] idxs, double[] values, int idx) {
    	
        double value = 0;
       
        if(idx <= idxs[0]) value = values[0];       
        else {    	   
            for(int i=1; i<idxs.length; i++) {
        	   
                if(Math.abs(idx-idxs[i]) < ZERO_DOUBLE) {            	   
                    value = values[i];
                    break;
                }
                else if(idx < idxs[i]) {
                    value = (values[i]-values[i-1]) / (idxs[i]-idxs[i-1]) * (idx-idxs[i-1]) + values[i-1];
                    break;
                }
            }
            if(idx > idxs[idxs.length-1]) value = values[values.length-1];
        }        
        return value;        
    }
    
    
    public static double toContSpotRate(double discSpotRate) {    	
    	return Math.log(1 + discSpotRate);
    }    
    
    
    public static double toDiscSpotRate(double contSpotRate) {    	
    	return Math.exp(contSpotRate) - 1.0;
    }   
    
}
