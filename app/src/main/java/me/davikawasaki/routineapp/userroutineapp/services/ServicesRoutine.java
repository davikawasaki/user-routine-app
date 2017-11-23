package me.davikawasaki.routineapp.userroutineapp.services;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;

/**
 * Created by kawasaki on 19/11/17.
 */

/**
 * Routine Service Layer.
 * Implements Database and List Processes.
 * @see Routine
 */
public class ServicesRoutine {

    /**
     * Get routine rows list from SQLite.
     * List is ordered by routine ID.
     * @param context
     * @return routineList/null
     * @exception SQLException
     */
    public static List<Routine> getRoutineList(Context context) {
        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            return connection.getRoutineDAO()
                    .queryBuilder()
                    .orderBy(Routine.ID, true)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get routine from ID.
     * Refresh places foreign keys after request if not null.
     * @param id
     * @param context
     * @return routine/null
     * @exception SQLException
     */
    public static Routine getRoutineFromId(int id, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            Routine routine = connection.getRoutineDAO().queryForId(id);

            if(routine != null) {
                connection.getPlaceDAO().refresh(routine.getOriginPlace());
                connection.getPlaceDAO().refresh(routine.getDestinationPlace());
            }

            return routine;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new routine with name, originPlace/destinationPlace/startDateTime as required fields.
     * @param routine
     * @param context
     * @return createdStatus
     * @exception SQLException
     */
    public static Boolean createRoutine(Routine routine, Context context) {

        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);
            int result = connection.getRoutineDAO().create(routine);

            // Check if routine was created
            if(result == 1) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Update routine with name, originPlace/destinationPlace/startDateTime as required fields.
     * @param routine
     * @param context
     * @return updatedStatus
     * @exception SQLException
     */
    public static Boolean updateRoutine(Routine routine, Context context) {

        try {

            DatabaseHelper connection = DatabaseHelper
                    .getInstance(context);

            int result = connection.getRoutineDAO().update(routine);

            // Check if routine was updated
            if(result == 1) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Remove a routine list.
     * @param routineList
     * @param context
     * @exception SQLException
     */
    public static void removeRoutines(List<Routine> routineList, Context context) {

        try {
            DatabaseHelper connection = DatabaseHelper.getInstance(context);
            connection.getRoutineDAO().delete(routineList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancel routine in course.
     * If a routine is canceled, it's removed from database.
     * @param routineId
     * @param context
     * @return cancelRoutineStatus
     * @exception SQLException
     */
    public static Boolean cancelRoutine(int routineId, Context context) {

        try {
            DatabaseHelper connection = DatabaseHelper
                    .getInstance(context);
            int result = connection.getRoutineDAO()
                    .deleteById(routineId);

            // Check if routine was removed
            if(result == 1) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Compare two lists to remove items equal to both lists.
     * @param fullRoutineList
     * @param toRemoveRoutineList
     * @return updatedRoutineList
     */
    public static List<Routine> iterateAndRemoveFromList(List<Routine> fullRoutineList,
                                                         List<Routine> toRemoveRoutineList) {

        List<Routine> updatedRoutineList = new ArrayList<>(fullRoutineList);

        for ( Routine rrl : toRemoveRoutineList ) {
            for ( Routine r : fullRoutineList ) {
                if(rrl.getId() == r.getId()) updatedRoutineList.remove(rrl);
            }
        }

        return updatedRoutineList;
    }

}
