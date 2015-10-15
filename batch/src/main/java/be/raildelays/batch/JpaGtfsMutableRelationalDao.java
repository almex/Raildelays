package be.raildelays.batch;

import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Almex
 * @since 2.0
 */
public class JpaGtfsMutableRelationalDao implements GtfsMutableRelationalDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public void open() {

    }

    @Override
    public void saveEntity(Object entity) {

    }

    @Override
    public void updateEntity(Object entity) {

    }

    @Override
    public void saveOrUpdateEntity(Object entity) {

    }

    @Override
    public <K extends Serializable, T extends IdentityBean<K>> void removeEntity(T entity) {

    }

    @Override
    public <T> void clearAllEntitiesForType(Class<T> type) {

    }

    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public void close() {
        em.close();
    }

    @Override
    public List<String> getTripAgencyIdsReferencingServiceId(AgencyAndId serviceId) {
        return null;
    }

    @Override
    public List<Route> getRoutesForAgency(Agency agency) {
        return null;
    }

    @Override
    public List<Stop> getStopsForStation(Stop station) {
        return null;
    }

    @Override
    public List<Trip> getTripsForRoute(Route route) {
        return null;
    }

    @Override
    public List<Trip> getTripsForShapeId(AgencyAndId shapeId) {
        return null;
    }

    @Override
    public List<Trip> getTripsForServiceId(AgencyAndId serviceId) {
        return null;
    }

    @Override
    public List<Trip> getTripsForBlockId(AgencyAndId blockId) {
        return null;
    }

    @Override
    public List<StopTime> getStopTimesForTrip(Trip trip) {
        return null;
    }

    @Override
    public List<StopTime> getStopTimesForStop(Stop stop) {
        return null;
    }

    @Override
    public List<AgencyAndId> getAllShapeIds() {
        return null;
    }

    @Override
    public List<ShapePoint> getShapePointsForShapeId(AgencyAndId shapeId) {
        return null;
    }

    @Override
    public List<Frequency> getFrequenciesForTrip(Trip trip) {
        return null;
    }

    @Override
    public List<AgencyAndId> getAllServiceIds() {
        return null;
    }

    @Override
    public ServiceCalendar getCalendarForServiceId(AgencyAndId serviceId) {
        return null;
    }

    @Override
    public List<ServiceCalendarDate> getCalendarDatesForServiceId(AgencyAndId serviceId) {
        return null;
    }

    @Override
    public List<FareRule> getFareRulesForFareAttribute(FareAttribute fareAttribute) {
        return null;
    }

    @Override
    public Collection<Agency> getAllAgencies() {
        return null;
    }

    @Override
    public Agency getAgencyForId(String id) {
        return null;
    }

    @Override
    public Collection<ServiceCalendar> getAllCalendars() {
        return null;
    }

    @Override
    public ServiceCalendar getCalendarForId(int id) {
        return null;
    }

    @Override
    public Collection<ServiceCalendarDate> getAllCalendarDates() {
        return null;
    }

    @Override
    public ServiceCalendarDate getCalendarDateForId(int id) {
        return null;
    }

    @Override
    public Collection<FareAttribute> getAllFareAttributes() {
        return null;
    }

    @Override
    public FareAttribute getFareAttributeForId(AgencyAndId id) {
        return null;
    }

    @Override
    public Collection<FareRule> getAllFareRules() {
        return null;
    }

    @Override
    public FareRule getFareRuleForId(int id) {
        return null;
    }

    @Override
    public Collection<FeedInfo> getAllFeedInfos() {
        return null;
    }

    @Override
    public FeedInfo getFeedInfoForId(int id) {
        return null;
    }

    @Override
    public Collection<Frequency> getAllFrequencies() {
        return null;
    }

    @Override
    public Frequency getFrequencyForId(int id) {
        return null;
    }

    @Override
    public Collection<Pathway> getAllPathways() {
        return null;
    }

    @Override
    public Pathway getPathwayForId(AgencyAndId id) {
        return null;
    }

    @Override
    public Collection<Route> getAllRoutes() {
        return null;
    }

    @Override
    public Route getRouteForId(AgencyAndId id) {
        return null;
    }

    @Override
    public Collection<ShapePoint> getAllShapePoints() {
        return null;
    }

    @Override
    public ShapePoint getShapePointForId(int id) {
        return null;
    }

    @Override
    public Collection<Stop> getAllStops() {
        return null;
    }

    @Override
    public Stop getStopForId(AgencyAndId id) {
        return null;
    }

    @Override
    public Collection<StopTime> getAllStopTimes() {
        return null;
    }

    @Override
    public StopTime getStopTimeForId(int id) {
        return null;
    }

    @Override
    public Collection<Transfer> getAllTransfers() {
        return null;
    }

    @Override
    public Transfer getTransferForId(int id) {
        return null;
    }

    @Override
    public Collection<Trip> getAllTrips() {
        return null;
    }

    @Override
    public Trip getTripForId(AgencyAndId id) {
        return null;
    }

    @Override
    public <T> Collection<T> getAllEntitiesForType(Class<T> type) {
        return null;
    }

    @Override
    public <T> T getEntityForId(Class<T> type, Serializable id) {
        return null;
    }
}
