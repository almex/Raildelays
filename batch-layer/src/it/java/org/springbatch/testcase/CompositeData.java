package org.springbatch.testcase;

public class CompositeData {

	private DataFromSource1 data1;

	private DataFromSource2 data2;

	public CompositeData(DataFromSource1 data1, DataFromSource2 data2) {
		this.data1 = data1;
		this.data2 = data2;
	}

	@Override
	public String toString() {
		return "CompositeData [data1=" + data1 + ", data2=" + data2 + "]";
	}

	public DataFromSource1 getData1() {
		return data1;
	}

	public void setData1(DataFromSource1 data1) {
		this.data1 = data1;
	}

	public DataFromSource2 getData2() {
		return data2;
	}

	public void setData2(DataFromSource2 data2) {
		this.data2 = data2;
	}

	public int compare() {
		int result = 0;
		
		if (data1 == null) {
			result = data2 != null ? 1 : 0;
		} else if (data2 == null) {
			result = data1 != null ? -1 : 0;
		} else {			
			result = (int) Math.signum(data1.getLetter().compareTo(data2.getLetter()));
		}		
		
		return result;
	}

}
