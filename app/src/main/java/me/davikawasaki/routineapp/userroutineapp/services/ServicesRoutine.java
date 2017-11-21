package me.davikawasaki.routineapp.userroutineapp.services;

import android.content.Context;

import me.davikawasaki.routineapp.userroutineapp.config.DatabaseHelper;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;

/**
 * Created by kawasaki on 19/11/17.
 */

public class ServicesRoutine {

    public static Routine getRoutineFromId(int id, Context context) {
        try {

            DatabaseHelper connection = DatabaseHelper.getInstance(context);

            Routine routine = connection.getRoutineDAO().queryForId(id);

            // Refresh foreign key places
            if(routine != null) {
                connection.getPlaceDAO().refresh(routine.getOriginPlace());
                connection.getPlaceDAO().refresh(routine.getDestinationPlace());
            }

            return routine;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
