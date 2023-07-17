package com.gof.interfaces;


public interface Constant {
	                            
	public static final char    TIME_UNIT_END         = 'E';
	public static final char    TIME_UNIT_YEAR        = 'Y';
	public static final char    TIME_UNIT_QUAT        = 'Q';
	public static final char    TIME_UNIT_MONTH       = 'M';
	public static final char    TIME_UNIT_WEEK        = 'W';
	public static final char    TIME_UNIT_DAY         = 'D';
	
	
	public static final int     YEAR_IN_YEAR          =   1;
	public static final int     QUAT_IN_YEAR          =   4;
	public static final int     MONTH_IN_YEAR         =  12;
	public static final int     WEEK_IN_YEAR          =  53;
	public static final int     DAY_IN_YEAR           = 365;
	                                                 
	                                                 
	public static final char    CMPD_MTD_SIMP         = 'S';
	public static final char    CMPD_MTD_DISC         = 'D';
	public static final char    CMPD_MTD_CONT         = 'C';
	public static final char    CMPD_MTD_EXOTIC       = 'E';			
	
	
	public static final int     DCB_ACT_365           = 1;
	public static final int     DCB_A30_360           = 2;
	public static final int     DCB_E30_360           = 3;
	public static final int     DCB_ACT_ACT           = 4;
	public static final int     DCB_ACT_360           = 5;
	public static final int     DCB_MON_DIF           = 9;
	
	
	public static final char    ADD                   = 'A';
	public static final char    SUBTRACT              = 'S';
	public static final char    MULTIPLY              = 'M';
	public static final char    DIVIDE                = 'D';	

	
	public static final double  ZERO_DOUBLE           = 0.00000000001;
	public static final int     NULL_INT              = 0;
	public static final double  NULL_DOUBLE           = 0.0;	
	
		
	public static final int     MAX_ITERATION         = 100;
	public static final int     MAX_INITIAL_GUESS     = 3;
	public static final double  INITIAL_GUESS         = 0.5;
	
	
	public static final int     INTEGER_TRUE          = 1;
	public static final int     INTEGER_FALSE         = 0;
	public static final int     INTEGER_NULL          = -1;
	
	
	public static final int     EXTRAP_TYPE_CONSTANT  = 1;
	public static final int     EXTRAP_TYPE_LINEAR    = 2;	
	public static final int     INTERP_TYPE_CONSTANT  = 3;
	public static final int     INTERP_TYPE_LINEAR    = 4;	
	
	
	public static final int     LENGTH_ISIN_CD        = 12;	
	public static final double  ONE_BP                = 0.0001;
	
}
