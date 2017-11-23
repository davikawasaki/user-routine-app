package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by kawasaki on 06/11/17.
 */

/**
 * Routine Class Model Domain.
 * Integrated with ORMLite Annotations to create Database Table.
 */
@DatabaseTable
public class Routine {

    // Routine table column names
    public static final String ID                = "id";
    public static final String NAME              = "name";
    public static final String START_TIME        = "start_time";
    public static final String END_TIME          = "end_time";
    public static final String ORIGIN_PLACE      = "origin_place";
    public static final String DESTINATION_PLACE = "destination_place";

    // Setting table columns, with originPlace and destinationPlace as 1-1 foreign relationship
    @DatabaseField(generatedId = true, columnName = ID)
    private int    id;
    @DatabaseField(canBeNull = false, columnName = NAME)
    private String name;
    @DatabaseField(canBeNull = false, columnName = START_TIME)
    private Date   startDateTime;
    @DatabaseField(columnName = END_TIME)
    private Date   endDateTime;
    @DatabaseField(columnName = ORIGIN_PLACE, canBeNull = false, foreign = true)
    private Place  originPlace;
    @DatabaseField(columnName = DESTINATION_PLACE, canBeNull = false, foreign = true)
    private Place  destinationPlace;

    /**
     * Main Routine Constructor.
     * @param startDateTime
     * @param originPlace
     * @param destinationPlace
     */
    public Routine(Date startDateTime, Place originPlace, Place destinationPlace) {
        this.startDateTime = startDateTime;
        this.originPlace = originPlace;
        this.destinationPlace = destinationPlace;
    }

    /**
     * Empty Routine Constructor.
     * ORMLite needs this constructor for table creation.
     */
    public Routine() {}

    /**
     * ID getter.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * ID setter.
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Name getter.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Name setter.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * StartDateTime getter.
     * @return startDateTime
     */
    public Date getStartDateTime() {
        return startDateTime;
    }

    /**
     * StartDateTime setter.
     * @param startDateTime
     */
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     * EndDateTime getter.
     * @return endDateTime
     */
    public Date getEndDateTime() {
        return endDateTime;
    }

    /**
     * EndDateTime setter.
     * @param endDateTime
     */
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     * OriginPlace getter.
     * @return originPlace
     */
    public Place getOriginPlace() {
        return originPlace;
    }

    /**
     * OriginPlace setter.
     * @param originPlace
     */
    public void setOriginPlace(Place originPlace) {
        this.originPlace = originPlace;
    }

    /**
     * DestinationPlace getter.
     * @return destinationPlace
     */
    public Place getDestinationPlace() {
        return destinationPlace;
    }

    /**
     * DestinationPlace setter.
     * @param destinationPlace
     */
    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    /**
     * Whenever Routine is called, return its name.
     * @return
     */
    @Override
    public String toString() {
        return getName();
    }

}
