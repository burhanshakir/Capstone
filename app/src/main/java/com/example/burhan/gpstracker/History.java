package com.example.burhan.gpstracker;


import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.example.burhan.gpstracker.database.FeedReaderDbHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class History extends AppCompatActivity {

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        locationList = new ArrayList<>();

        adapter = new LocationAdapter(this,locationList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        prepareHistory();

    }

    public void prepareHistory()
    {

        String URL = "content://com.example.burhan.gpstracker.database/history";

        Uri uri = Uri.parse(URL);
        CursorLoader cl = new CursorLoader(this,uri, null, null, null, "id");
        Cursor c = cl.loadInBackground();

        if (c.moveToFirst()) {
            do{

                Location l = new Location(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_LOCATION))
                        ,c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_DATE)));
                locationList.add(l);
                adapter.notifyDataSetChanged();
            } while (c.moveToNext());
        }
    }
}
