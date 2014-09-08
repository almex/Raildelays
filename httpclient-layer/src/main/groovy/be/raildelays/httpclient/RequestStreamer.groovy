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
    //public Reader getTrain(String idTrain)

    /**
     * Request train list.
     *
     * @param stationNameFrom station's name using Railtime format
     * @param stationNameTo station's name using Railtime format
     * @param day date for which you do the search
     * @param hour between 00 and 24
     * @param language specify 'en', 'fr' or 'nl'
     * @return an HTML content as a stream to parse
     */
    public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour, String language)

    /**
     * Request line list between two stations/stops.
     *
     * @param language specify 'en', 'fr' or 'nl'
     * @return an HTML content as a stream to parse
     */
    //public Reader getLineList(String idStationFrom, String idStationTo, Date timeFrom, Date timeTo, String language)

    /**
     * Request station list.
     *
     * @param language specify 'en', 'fr' or 'nl'
     * @return an HTML content as a stream to parse
     */
    public Reader getStationList(String language)

    /**
     * Request a direction for a train to retrieve delays.
     *
     * @param idTrain train's id using Railtime format
     * @param day day for which you want to retrive
     * @param language specify 'en', 'fr' or 'nl'
     * @param sens 'D' for departure or 'A' for arrival
     * @return an HTML content as a stream to parse
     */
    public Reader getDelays(String idTrain, Date day, String language, String sens)

    /**
     * Probably duplicated to getTrain()
     *
     * @param idStationFrom station's id using Railtime format
     * @param idStationTo station's id using Railtime format
     * @param day
     * @return an HTML content as a stream to parse
     */
    //public Reader getSchedule(String idStationFrom, String idStationTo,  Date day)
}
