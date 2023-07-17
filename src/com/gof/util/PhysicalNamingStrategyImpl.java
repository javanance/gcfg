package com.gof.util;

import java.io.Serializable;
import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class PhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl implements Serializable {

	private static final long serialVersionUID = -3189474054653840418L;
	public static final PhysicalNamingStrategyImpl INSTANCE = new PhysicalNamingStrategyImpl();
	 
    @Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
		// TODO Auto-generated method stub
    	if(super.toPhysicalSchemaName(name, context)==null) {
    		return new Identifier("IQCMOWN", false);
//    		return new Identifier("IESGOWN", false);
    	}
		return super.toPhysicalSchemaName(name, context);
	}

	@Override
	 public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
	     return new Identifier(addUnderscores(name.getText()), name.isQuoted());
	 }

	 @Override
	 public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
	     return new Identifier(addUnderscores(name.getText()), name.isQuoted());
	 }


	 protected static String addUnderscores(String name) {
	     final StringBuilder buf = new StringBuilder( name.replace('.', '_') );
	     for (int i=1; i<buf.length()-1; i++) {
	        if (
	             Character.isLowerCase( buf.charAt(i-1) ) &&
	             Character.isUpperCase( buf.charAt(i) ) &&
	             Character.isLowerCase( buf.charAt(i+1) )
	         ) {
	             buf.insert(i++, '_');
	         }
	     }
	     return buf.toString().toUpperCase(Locale.ROOT);
	 }
	}