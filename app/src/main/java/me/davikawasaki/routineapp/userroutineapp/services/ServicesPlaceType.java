package me.davikawasaki.routineapp.userroutineapp.services;

import android.content.Context;
import android.widget.Adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;

/**
 * Created by kawasaki on 21/11/17.
 */

/**
 * Place Type Service Layer.
 * Implements Database and List Processes.
 * @see PlaceType
 */
public class ServicesPlaceType {

    /**
     * Get place type rows list from SQLite.
     * List is ordered by place type Name.
     * @param context
     * @return placeTypeList/null
     * @exception SQLException
     */
    public static List<PlaceType> getPlaceTypeList(Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            return connection.getPlaceTypeDAO()
                    .queryBuilder()
                    .orderBy(PlaceType.NAME, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get place type from ID.
     * @param id
     * @param context
     * @return placeType/null
     * @exception SQLException
     */
    public static PlaceType getPlaceTypeFromId(int id, Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);
            return connection.getPlaceTypeDAO().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new place type with name as required field.
     * Check if place name already exists, since it's an unique field.
     * @param name
     * @param context
     * @return createdStatus
     * @exception SQLException
     */
    public static Boolean registerPlaceType(String name, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            List<PlaceType> list = connection.getPlaceTypeDAO()
                    .queryBuilder()
                    .where().eq(PlaceType.NAME, name)
                    .query();

            if (list.size() > 0) return false;

            PlaceType placeType = new PlaceType();

            placeType.setName(name);
            connection.getPlaceTypeDAO().create(placeType);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update place type with name as required field.
     * Check if place type is not in use from registered places.
     * @param placeType
     * @param context
     * @return updatedStatus
     * @exception SQLException
     */
    public static Boolean updatePlaceType(PlaceType placeType, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            if(ServicesPlaceType.checkPlaceTypeInsidePlaces(placeType.getId(), context)) return false;

            int result = connection.getPlaceTypeDAO().update(placeType);
            if(result == 1) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a place type list.
     * Check if each place type is not in use from registered places.
     * @param placeTypeList
     * @param context
     * @return removedStatus
     * @exception SQLException
     */
    public static Boolean removePlaceTypes(List<PlaceType> placeTypeList, Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            for( PlaceType placeType : placeTypeList ) {
                if(ServicesPlaceType.checkPlaceTypeInsidePlaces(placeType.getId(),
                        context)) return false;
            }

            connection.getPlaceTypeDAO().delete(placeTypeList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if placeType is in use in any of registered places.
     * If quantity rows returned from database query are bigger than zero
     * place type updates and removals aren't authorized to proceed.
     * @param placeTypeId
     * @param context
     * @return isInsidePlacesStatus
     * @exception SQLException
     */
    public static Boolean checkPlaceTypeInsidePlaces(int placeTypeId, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            List<Place> placeList = connection.getPlaceDAO()
                    .queryBuilder()
                    .where()
                    .eq(Place.TYPE, placeTypeId)
                    .query();

            return (placeList.size() > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compare two lists to remove items equal to both lists.
     * @param fullPlaceTypeList
     * @param toRemovePlaceTypeList
     * @return updatedPlaceTypeList
     */
    public static List<PlaceType> iterateAndRemoveFromList(List<PlaceType> fullPlaceTypeList,
                                                       List<PlaceType> toRemovePlaceTypeList) {

        List<PlaceType> updatedPlaceTypeList = new ArrayList<>(fullPlaceTypeList);

        for ( PlaceType rptl : toRemovePlaceTypeList ) {
            for ( PlaceType pt : fullPlaceTypeList ) {
                if(rptl.getId() == pt.getId()) updatedPlaceTypeList.remove(rptl);
            }
        }

        return updatedPlaceTypeList;
    }

    /**
     * Get adapter and return its own place type list.
     * @param adapter
     * @return placeTypeList
     */
    public static List<PlaceType> getListFromArrayAdapter(Adapter adapter) {
        int n = adapter.getCount();
        List<PlaceType> placeTypeList = new ArrayList<>(n);

        for(int i = 0; i < n; i++) {
            PlaceType placeType = (PlaceType) adapter.getItem(i);
            placeTypeList.add(placeType);
        }

        return placeTypeList;
    }

    /**
     * Iterate adapter list and get position from name argument.
     * @param adapter
     * @param name
     * @return position
     */
    public static int getPositionFromArrayAdapter(Adapter adapter, String name) {
        List<PlaceType> list = getListFromArrayAdapter(adapter);
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).toString().matches(name)) return i;
        }
        return -1;
    }

}
