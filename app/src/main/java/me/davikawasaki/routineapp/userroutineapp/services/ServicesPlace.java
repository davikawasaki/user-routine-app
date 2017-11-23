package me.davikawasaki.routineapp.userroutineapp.services;

import android.content.Context;
import android.widget.Adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;
import me.davikawasaki.routineapp.userroutineapp.utils.UtilsString;

/**
 * Created by kawasaki on 19/11/17.
 */

/**
 * Place Service Layer.
 * Implements Database and List Processes.
 * @see Place
 */
public class ServicesPlace {

    /**
     * Get place rows list from SQLite.
     * List is ordered by place Name.
     * @param context
     * @return placeList/null
     * @exception SQLException
     */
    public static List<Place> getPlaceList(Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            return connection.getPlaceDAO()
                    .queryBuilder()
                    .orderBy(Place.NAME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get place from ID.
     * Refresh placeType foreign key after request if not null.
     * @param id
     * @param context
     * @return place/null
     * @exception SQLException
     */
    public static Place getPlaceFromId(int id, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            Place place = connection.getPlaceDAO().queryForId(id);

            // Refresh foreign key place type
            if(place != null) connection.getPlaceTypeDAO().refresh(place.getPlaceType());

            return place;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new place with name and placeType as required fields.
     * Check if place name already exists, since it's an unique field;
     * Checks for empty fields that aren't required.
     * @param name
     * @param placeType
     * @param address
     * @param city
     * @param state
     * @param country
     * @param context
     * @return createdStatus
     * @exception SQLException
     */
    public static Boolean registerPlace(String name, PlaceType placeType,
                                        String address, String city, String state,
                                        String country, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            List<Place> list = connection.getPlaceDAO()
                    .queryBuilder()
                    .where().eq(Place.NAME, name)
                    .query();

            if (list.size() > 0) return false;

            Place place = new Place();

            place.setName(name);
            if(placeType != null) place.setType(placeType);
            if(!UtilsString.stringEmpty(address)) place.setAddress(address);
            if(!UtilsString.stringEmpty(city)) place.setCity(city);
            if(!UtilsString.stringEmpty(state)) place.setState(state);
            if(!UtilsString.stringEmpty(country)) place.setCountry(country);
            connection.getPlaceDAO().create(place);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update place with name and placeType as required fields.
     * Check if place is not in use from registered routines.
     * @param place
     * @param context
     * @return updatedStatus
     * @exception SQLException
     */
    public static Boolean updatePlace(Place place, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            Boolean status = ServicesPlace.checkPlaceInsideRoutines(place.getId(), context);
            if(status == null || !status) return false;

            int result = connection.getPlaceDAO().update(place);
            if(result == 1) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a place list.
     * Check if each place is not in use from registered routines.
     * @param placeList
     * @param context
     * @return removedStatus
     * @exception SQLException
     */
    public static Boolean removePlaces(List<Place> placeList, Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            for( Place place : placeList ) {
                Boolean status = ServicesPlace.checkPlaceInsideRoutines(place.getId(), context);
                if(status == null || !status) return false;
            }

            connection.getPlaceDAO().delete(placeList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if place is in use in any of registered routines.
     * If quantity rows returned from database query are bigger than zero
     * place updates and removals aren't authorized to proceed.
     * @param placeId
     * @param context
     * @return isInsideRoutinesStatus
     * @exception SQLException
     */
    public static Boolean checkPlaceInsideRoutines(int placeId, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            List<Routine> routineList = connection.getRoutineDAO()
                    .queryBuilder()
                    .where()
                    .eq(Routine.ORIGIN_PLACE, placeId)
                    .or()
                    .eq(Routine.DESTINATION_PLACE, placeId)
                    .query();

            return (routineList.size() > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compare two lists to remove items equal to both lists.
     * @param fullPlaceList
     * @param toRemovePlaceList
     * @return updatedPlaceList
     */
    public static List<Place> iterateAndRemoveFromList(List<Place> fullPlaceList,
                                                         List<Place> toRemovePlaceList) {

        List<Place> updatedPlaceList = new ArrayList<>(fullPlaceList);

        for ( Place rpl : toRemovePlaceList ) {
            for ( Place p : fullPlaceList ) {
                if(rpl.getId() == p.getId()) updatedPlaceList.remove(rpl);
            }
        }

        return updatedPlaceList;
    }

    /**
     * Get adapter and return its own place list.
     * @param adapter
     * @return placeList
     */
    public static List<Place> getListFromArrayAdapter(Adapter adapter) {
        int n = adapter.getCount();
        List<Place> placeList = new ArrayList<>(n);

        for(int i = 0; i < n; i++) {
            Place place = (Place) adapter.getItem(i);
            placeList.add(place);
        }

        return placeList;
    }

    /**
     * Iterate adapter list and get position from name argument.
     * @param adapter
     * @param name
     * @return position
     */
    public static int getPositionFromArrayAdapter(Adapter adapter, String name) {
        List<Place> list = getListFromArrayAdapter(adapter);
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).toString().matches(name)) return i;
        }
        return -1;
    }

}
