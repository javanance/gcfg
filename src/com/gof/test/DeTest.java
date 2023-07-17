package com.gof.test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.DaoUtil;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.KicsAssetFideDao;
import com.gof.dao.KicsAssetSecrDao;
import com.gof.dao.KicsFssScenDao;
import com.gof.entity.*;
import com.gof.enums.EInstrument;
import com.gof.interfaces.Instrument;
import com.gof.interfaces.KicsAsset;
import com.gof.util.GeneralUtil;
import com.gof.util.HibernateUtil;
import com.gof.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeTest {
	
	public DeTest() {}	

	private final static Logger logger = LoggerFactory.getLogger(DeTest.class.getSimpleName());	
	
	public static void main(String[] args) throws Exception {		
	    Session session = HibernateUtil.getSessionFactory().openSession();
	    session.beginTransaction();	    
	    
	    String bseDt = "20230331";	    
	    String sqlHisDate = "select max(a.baseDate) from IrCurveHis a where a.baseDate <= :param";	    
	    List<String> irHisDateList = session.createQuery(sqlHisDate)
	    			.setParameter("param", bseDt)
	    			.getResultList();
	    String irHisDate = irHisDateList.get(0);	    
	    log.info("aaa : {}", irHisDateList);
	    
	    List<KicsAsset> instruments = new ArrayList<KicsAsset>();
	    instruments = KicsAssetSecrDao.getEntities(bseDt);
	    instruments.forEach(s->log.info("aaaa : {},{}", s.toString()));
	    KicsAssetAcco temp ;
	    
//	    for(KicsAsset aa : instruments) {
//	    	temp = (KicsAssetAcco)aa;
//	    	log.info("zzzz : {},{}", temp.toString());
//	    }
	    
	    
	    String baseYymm = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE).substring(0,6);
	    
		 KicsFssScenDao.getEntitiesMap(bseDt, 2, "KUSD1000").entrySet().forEach(s-> log.info("zzz: {},{}", s.getKey(), s.getValue()));
	    
		 log.info("base : {}", baseYymm);
	    
	}
	
}