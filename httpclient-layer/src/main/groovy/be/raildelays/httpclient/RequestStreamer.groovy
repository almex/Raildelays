package be.raildelays.httpclient

interface RequestStreamer {
	
	/**
	 * Request a train. 
	 * As it's coming from a liveboard we can also retrieve 
	 * scheduling for this train.
	 * 
	 * @param idTrain train's id using Railtime format
	 * @return an HTML content as a stream to parse
	 */
	public OutputStream getTrain(String idTrain)
	
	/**
	 * Request train list.
	 * 
	 * @return an HTML content as a stream to parse
	 */
	public OutputStream getTrainList()
	
	
	/**
	 * Request station list.
	 * 
	 * @return an HTML content as a stream to parse
	 */
	public OutputStream getStationList()
	
	
	/**
	 * Request a direction for a train to retrieve delays.
	 * 
	 * @param idTrain train's id using Railtime format
	 * @param day day for which you want to retrive
	 * @return an HTML content as a stream to parse
	 */
	public OutputStream getDelays(String idTrain,  Date day)
	
	
	/**
	 * Probably duplicated to getTrain()
	 * 
	 * @param idStationFrom station's id using Railtime format
	 * @param idStationTo station's id using Railtime format
	 * @param day
	 * @return an HTML content as a stream to parse
	 */
	//public OutputStream getSchedule(String idStationFrom, String idStationTo,  Date day)
}
