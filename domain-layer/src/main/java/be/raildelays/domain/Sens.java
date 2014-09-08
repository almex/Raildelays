package be.raildelays.domain;

public enum Sens {
    DEPARTURE("D", "D", "D"), ARRIVAL("A", "A", "A");

    private String railtimeParameter;
    private String mobileParameter;
    private String bRailParameter;

    Sens(String railtimeParameter, String mobileParameter, String bRailParameter) {
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
