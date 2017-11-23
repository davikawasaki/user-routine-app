package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kawasaki on 06/11/17.
 */

/**
 * PlaceType Class Model Domain.
 * Integrated with ORMLite Annotations to create Database Table.
 */
@DatabaseTable
public class PlaceType {

    // PlaceType table column names
    public static final String ID   = "id";
    public static final String NAME = "name";

    // Setting table columns
    @DatabaseField(generatedId = true, columnName = ID)
    private int    id;
    @DatabaseField(canBeNull = false, unique = true, columnName = NAME)
    private String name;

    /**
     * Main PlaceType Constructor.
     * @param name
     */
    public PlaceType(String name) {
        setName(name);
    }

    /**
     * Empty PlaceType Constructor.
     * ORMLite needs this constructor for table creation.
     */
    public PlaceType() {}

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
     * Whenever PlaceType is called, return its name.
     * @return name
     */
    @Override
    public String toString() {
        return getName();
    }

}
