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

/**
 * Database Helper Class with ORMLite inheritance.
 * Manages and manipulates DAOs from ORM, which have own methods for SQLite Database manipulations.
 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // DB variables
    private static final String DB_NAME    = "db.routine.app";
    private static final int    DB_VERSION = 1;

    // Activity Context and databaseHelper instance
    private Context context;
    private static DatabaseHelper instance;

    // PlaceType, Place and Routine DAOs
    private Dao<PlaceType, Integer> placeTypeDAO;
    private Dao<Place, Integer> placeDAO;
    private Dao<Routine, Integer> routineDAO;

    /**
     * Return unique DatabaseHelper instance.
     * @param cont
     * @return instance (DatabaseHelper)
     */
    public static DatabaseHelper getInstance(Context cont) {
        if(instance == null) instance = new DatabaseHelper(cont);
        return instance;
    }

    /**
     * Instatiate Database with context, databaseName and databaseVersion.
     * @param cont
     */
    private DatabaseHelper(Context cont) {
        super(cont, DB_NAME, null, DB_VERSION);
        this.context = cont;
    }

    /**
     * On DatabaseHelper instantiation implements sequence of scripts.
     * Create PlaceType Table with starting rows, and then create Place and Routine Tables.
     * @param database
     * @param connectionSource
     * @exception java.sql.SQLException
     */
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

    /**
     * On DatabaseHelper Version Upgrade implements sequence of scripts.
     * Drop all tables and run onCreate method again, instantiating tables again.
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     * @exception java.sql.SQLException
     */
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

    /**
     * Instantiate or return a PlaceTypeDAO.
     * Enable a CRUD manager for Place Types Model.
     * @return placeTypeDAO (Dao<PlaceType, Integer>)
     * @throws java.sql.SQLException
     */
    public Dao<PlaceType, Integer> getPlaceTypeDAO() throws java.sql.SQLException {
        if(placeTypeDAO == null) placeTypeDAO = getDao(PlaceType.class);
        return placeTypeDAO;
    }

    /**
     * Instantiate or return a PlaceDAO.
     * Enable a CRUD manager for Places Model.
     * @return placeDAO (Dao<Place, Integer>)
     * @throws java.sql.SQLException
     */
    public Dao<Place, Integer> getPlaceDAO() throws java.sql.SQLException {
        if(placeDAO == null) placeDAO = getDao(Place.class);
        return placeDAO;
    }

    /**
     * Instantiate or return a RoutineDAO.
     * Enable a CRUD manager for Routines Model.
     * @return routineDAO (DAO<Routine, Integer>)
     * @throws java.sql.SQLException
     */
    public Dao<Routine, Integer> getRoutineDAO() throws java.sql.SQLException {
        if(routineDAO == null) routineDAO = getDao(Routine.class);
        return routineDAO;
    }

}
