package me.davikawasaki.routineapp.userroutineapp.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.R;
import me.davikawasaki.routineapp.userroutineapp.model.Place;
import me.davikawasaki.routineapp.userroutineapp.model.PlaceType;
import me.davikawasaki.routineapp.userroutineapp.model.Routine;

/**
 * Created by kawasaki on 06/11/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME    = "db.routine.app";
    private static final int    DB_VERSION = 1;

    private Context context;
    private static DatabaseHelper instance;

    private Dao<PlaceType, Integer> placeTypeDAO;
    private Dao<Place, Integer> placeDAO;
    private Dao<Routine, Integer> routineDAO;

    public static DatabaseHelper getInstance(Context cont) {
        if(instance == null) instance = new DatabaseHelper(cont);
        return instance;
    }

    private DatabaseHelper(Context cont) {
        super(cont, DB_NAME, null, DB_VERSION);
        this.context = cont;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PlaceType.class);

            String[] initialPlaceTypes = context.getResources().getStringArray(R.array.starting_types);
            List<PlaceType> list = new ArrayList<PlaceType>();

            for (int cont=0; cont < initialPlaceTypes.length; cont++) {
                PlaceType type = new PlaceType(initialPlaceTypes[cont]);
                list.add(type);
            }

            getPlaceTypeDAO().create(list);

            TableUtils.createTable(connectionSource, Place.class);
            TableUtils.createTable(connectionSource, Routine.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, PlaceType.class, true);
            TableUtils.dropTable(connectionSource, Place.class, true);
            TableUtils.dropTable(connectionSource, Routine.class, true);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e); // Force exception to quit and stop application
        }
        onCreate(database, connectionSource);
    }

    public Dao<PlaceType, Integer> getPlaceTypeDAO() throws java.sql.SQLException {
        if(placeTypeDAO == null) placeTypeDAO = getDao(PlaceType.class);
        return placeTypeDAO;
    }

    public Dao<Place, Integer> getPlaceDAO() throws java.sql.SQLException {
        if(placeDAO == null) placeDAO = getDao(Place.class);
        return placeDAO;
    }

    public Dao<Routine, Integer> getRoutineDAO() throws java.sql.SQLException {
        if(routineDAO == null) routineDAO = getDao(Routine.class);
        return routineDAO;
    }
}
