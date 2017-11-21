package me.davikawasaki.routineapp.userroutineapp.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kawasaki on 06/11/17.
 */

@DatabaseTable
public class PlaceType {

    public static final String ID   = "id";
    public static final String NAME = "name";

    @DatabaseField(generatedId = true, columnName = ID)
    private int    id;
    @DatabaseField(canBeNull = false, unique = true, columnName = NAME)
    private String name;

    public PlaceType(String name) {
        setName(name);
    }

    public PlaceType() {}

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

    @Override
    public String toString() {
        return getName();
    }
}
