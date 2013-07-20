package org.springbatch.testcase;

import java.io.Serializable;

public class InputData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String letter;
	
	public InputData() {
		super();
	}
	
	public InputData(String letter) {
		this.letter = letter;
	}

	@Override
	public String toString() {
		return "InputData [letter=" + letter + "]";
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}
}
