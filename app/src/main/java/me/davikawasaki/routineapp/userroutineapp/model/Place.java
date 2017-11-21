package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kawasaki on 06/11/17.
 */

@DatabaseTable
public class Place {

    public static final String ID      = "id";
    public static final String NAME    = "name";
    public static final String TYPE    = "type";
    public static final String ADDRESS = "address";
    public static final String CITY    = "city";
    public static final String STATE   = "state";
    public static final String COUNTRY = "country";

    @DatabaseField(generatedId = true, columnName = ID)
    private int id;
    @DatabaseField(canBeNull = false, unique = true, columnName = NAME)
    private String name;
    @DatabaseField(canBeNull = false, foreign = true)
    private PlaceType placeType;
    @DatabaseField(columnName = ADDRESS)
    private String address;
    @DatabaseField(columnName = CITY)
    private String city;
    @DatabaseField(columnName = STATE)
    private String state;
    @DatabaseField(columnName = COUNTRY)
    private String country;

    public Place(String name, PlaceType placeType, String address, String city, String state, String country) {
        this.name = name;
        this.placeType = placeType;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Place() {}

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
    public PlaceType getPlaceType() { return this.placeType; }
    public void setType(PlaceType placeType) {
        this.placeType = placeType;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return getName();
    }
}
