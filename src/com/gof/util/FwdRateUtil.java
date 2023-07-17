
package com.gof.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.GregorianCalendar;
import java.util.Map;

import com.gof.entity.IrCurveHis;

import lombok.extern.slf4j.Slf4j;


/**
 * @author takion77@gmail.com
 *
 */

@Slf4j
@SuppressWarnings("serial")
public class FwdRateUtil extends GregorianCalendar {
	
//	public static  double getForwardRate(String baseDate, int intTerm, LocalDate cfDate,  Map<String, IrCurveHis> ts) {
	public static  double getForwardRate(String baseDate, int intTerm, LocalDate cfDate,  Map<Long, Double> ts) {
		LocalDate baseDt = LocalDate.parse(baseDate, DateTimeFormatter.BASIC_ISO_DATE);
		
		if(baseDt.isAfter(cfDate)) {
			return 0.0;
		}
		
//		double shortTs 		= Period.between(baseDt, cf.getAdjStLocalDate()).getDays() /365.0	;
//		double longTs 		= Period.between(baseDt, cf.getAdjEndLocalDate()).getDays() /365.0	;
//		long shortMon = Period.between(baseDt, cf.getAdjStLocalDate()).toTotalMonths();
//		long longMon = Period.between(baseDt, cf.getAdjEndLocalDate()).toTotalMonths();
		
		double shortTs 		= baseDt.until(cfDate , ChronoUnit.DAYS) /365.0	;
		double longTs 		= baseDt.until(cfDate, ChronoUnit.DAYS) /365.0	;
		double betweenTs 	= longTs - shortTs ;
		
//		long shortMon 		= baseDt.until(cf.getAdjStLocalDate() , ChronoUnit.MONTHS) +1;
//		long longMon 		= baseDt.until(cf.getAdjEndLocalDate(), ChronoUnit.MONTHS) +1;
//		long shortMon 		= baseDt.until(cf.getIntStDate() , ChronoUnit.MONTHS) +1;
//		long longMon 		= baseDt.until(cf.getIntEndDate(), ChronoUnit.MONTHS) +1;
		
		
		
		
		long longMon  = ChronoUnit.MONTHS.between(baseDt.withDayOfMonth(1), cfDate.withDayOfMonth(1)) ;
//		long shortMon = ChronoUnit.MONTHS.between(baseDt.withDayOfMonth(1), cf.getIntStDate().withDayOfMonth(1)) ;
//		Sync To Excel!!!!!
		long shortMon  = longMon - intTerm;
		
//		long shortMon 		= Period.between(baseDt.withDayOfMonth(1), cf.getIntStDate().withDayOfMonth(1)).getYears()*12 
//								+ Period.between(baseDt.withDayOfMonth(1), cf.getIntStDate().withDayOfMonth(1)).getMonths();
//		long longMon 		= Period.between(baseDt.withDayOfMonth(1), cf.getIntEndDate().withDayOfMonth(1)).getYears() *12
//								+ Period.between(baseDt.withDayOfMonth(1), cf.getIntEndDate().withDayOfMonth(1)).getMonths();
		
		
//		log.info("fwd rate : {},{},{},{},{},{},{},{},{},{}",  cfDate,shortMon, longMon, intTerm);
		double shortRate 	= ts.get(shortMon);
		double longRate 	= ts.get(longMon);
		
		
//		double fwdIntFactor = Math.pow(1+longRate, longTs)  / Math.pow(1+shortRate, shortTs); 
//		double fwdrate 		= Math.pow( fwdIntFactor, 1/betweenTs) -1 ;
		
//		for excel sync!!!!
		double fwdIntFactor = Math.pow(1+longRate, longMon/12.0)  / Math.pow(1+shortRate, shortMon/12.0); 
		double fwdrate 		= Math.pow( fwdIntFactor, 12.0/(longMon -shortMon)) -1 ;
		
//		log.info("fwd Rate In FwdRateUtil : {},{},{},{},{},{},{},{},{},{},{}", shortMon, longMon, shortRate, longRate, shortTs, longTs, betweenTs, fwdrate);
		return fwdrate;
	}
}
