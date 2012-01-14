/**
 * 
 */
package be.raildelays.domain;

/**
 * Enumerate all languages handled by the different SNCB/NMBS websites
 * This list also different parameters value that should be used for 
 * b-rail.be, railtime.be or railtime.be/mobile.
 * 
 * 
 * @author Almex
 */
public enum Language {
	
	ENGLISH("en","en","en"),
	FRENCH("fr","fr","fr"),
	DUTCH("nl","nl","nl");

	private String railtimeParameter;
	private String mobileParameter;
	private String bRailParameter;
	
	Language(String railtimeParameter, String mobileParameter, String bRailParameter) {
		this.bRailParameter = bRailParameter;
		this.mobileParameter = mobileParameter;
		this.railtimeParameter = railtimeParameter;
	}
	
	public String getRailtimeParameter() {
		return railtimeParameter;
	}
	public void setRailtimeParameter(String railtimeParameter) {
		this.railtimeParameter = railtimeParameter;
	}
	public String getMobileParameter() {
		return mobileParameter;
	}
	public void setMobileParameter(String mobileParameter) {
		this.mobileParameter = mobileParameter;
	}
	public String getbRailParameter() {
		return bRailParameter;
	}
	public void setbRailParameter(String bRailParameter) {
		this.bRailParameter = bRailParameter;
	}
	
}
