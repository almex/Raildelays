package be.raildelays.domain.railtime;

import java.io.Serializable;

public class Station implements Serializable {

	private static final long serialVersionUID = -187269999487626223L;

	private String name;
	
	public Station (String name) {
		this.setName(name);	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
