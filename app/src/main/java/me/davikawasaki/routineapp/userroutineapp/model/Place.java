package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kawasaki on 06/11/17.
 */

/**
 * Place Class Model Domain.
 * Integrated with ORMLite Annotations to create Database Table.
 */
@DatabaseTable
public class Place {

    // Place table column names
    public static final String ID      = "id";
    public static final String NAME    = "name";
    public static final String TYPE    = "type";
    public static final String ADDRESS = "address";
    public static final String CITY    = "city";
    public static final String STATE   = "state";
    public static final String COUNTRY = "country";

    // Setting table columns, with placeType as 1-1 foreign relationship
    @DatabaseField(generatedId = true, columnName = ID)
    private int       id;
    @DatabaseField(canBeNull = false, unique = true, columnName = NAME)
    private String    name;
    @DatabaseField(columnName = TYPE, canBeNull = false, foreign = true)
    private PlaceType placeType;
    @DatabaseField(columnName = ADDRESS)
    private String    address;
    @DatabaseField(columnName = CITY)
    private String    city;
    @DatabaseField(columnName = STATE)
    private String    state;
    @DatabaseField(columnName = COUNTRY)
    private String    country;

    /**
     * Main Place Constructor.
     * @param name
     * @param placeType
     * @param address
     * @param city
     * @param state
     * @param country
     */
    public Place(String name, PlaceType placeType, String address, String city, String state, String country) {
        this.name = name;
        this.placeType = placeType;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    /**
     * Empty Place Constructor.
     * ORMLite needs this constructor for table creation.
     */
    public Place() {}

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
     * Name setter.
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
     * PlaceType getter.
     * @return placeType
     */
    public PlaceType getPlaceType() { return this.placeType; }

    /**
     * PlaceType setter.
     * @param placeType
     */
    public void setType(PlaceType placeType) {
        this.placeType = placeType;
    }

    /**
     * Address getter.
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Address setter.
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * City getter.
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * City setter.
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * State getter.
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * State setter.
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Country getter.
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Country setter.
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Whenever Place is called, return its name.
     * @return name
     */
    @Override
    public String toString() {
        return getName();
    }

}
