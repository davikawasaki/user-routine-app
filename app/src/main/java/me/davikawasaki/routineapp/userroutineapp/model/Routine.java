package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by kawasaki on 06/11/17.
 */

@DatabaseTable
public class Routine {

    public static final String ID                = "id";
    public static final String NAME              = "name";
    public static final String START_TIME        = "start_time";
    public static final String END_TIME          = "end_time";
    public static final String ORIGIN_PLACE      = "origin_place";
    public static final String DESTINATION_PLACE = "destination_place";
    public static final String DATE              = "date";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;
    @DatabaseField(canBeNull = false, columnName = NAME)
    private String name;
    @DatabaseField(canBeNull = false, columnName = START_TIME)
    private Date startDateTime;
    @DatabaseField(columnName = END_TIME)
    private Date endDateTime;
    @DatabaseField(canBeNull = false, foreign = true)
    private Place originPlace;
    @DatabaseField(canBeNull = false, foreign = true)
    private Place destinationPlace;

    public Routine(Date startDateTime, Place originPlace, Place destinationPlace) {
        this.startDateTime = startDateTime;
        this.originPlace = originPlace;
        this.destinationPlace = destinationPlace;
    }

    public Routine() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }
    public Date getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
    public Place getOriginPlace() {
        return originPlace;
    }
    public void setOriginPlace(Place originPlace) {
        this.originPlace = originPlace;
    }
    public Place getDestinationPlace() {
        return destinationPlace;
    }
    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    @Override
    public String toString() {
        return getName();
    }
}
