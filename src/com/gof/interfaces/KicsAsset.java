package com.gof.interfaces;

import java.util.Set;

public interface KicsAsset {	
	
	public String getBaseDate();
	
	public String getExpoId();
	
	public String getProdTpcd();
	
	public String getInstTpcd();
	
	public String getInstDtlsTpcd();
	
	public String getIsinCd();
	
	public String getCrnyCd();
	
	public Set<String> getCrnySet();
	
	public void setIsRealNumber(Boolean bool);
	
}
