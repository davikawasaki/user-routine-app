package me.davikawasaki.routineapp.userroutineapp.services;

import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

import me.davikawasaki.routineapp.userroutineapp.model.Place;

/**
 * Created by kawasaki on 19/11/17.
 */

public class ServicesPlace {

    public static List<Place> getListFromArrayAdapter(Adapter adapter) {
        int n = adapter.getCount();
        List<Place> placeList = new ArrayList<>(n);

        for(int i = 0; i < n; i++) {
            Place place = (Place) adapter.getItem(i);
            placeList.add(place);
        }

        return placeList;
    }

    public static int getPositionFromArrayAdapter(Adapter adapter, String name) {
        List<Place> list = getListFromArrayAdapter(adapter);
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).toString().matches(name)) return i;
        }
        return -1;
    }

}
