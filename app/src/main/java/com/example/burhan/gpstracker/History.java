package com.example.burhan.gpstracker;


import android.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import com.example.burhan.gpstracker.database.FeedReaderDbHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class History extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);

        String URL = getResources().getString(R.string.URL);
        uri = Uri.parse(URL);

        getSupportLoaderManager().initLoader(0, null, History.this);

        locationList = new ArrayList<>();

        adapter = new LocationAdapter(this, locationList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uri, null, null, null, "id");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        if (c.moveToFirst()) {
            do {

                Location l = new Location(c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_LOCATION))
                        , c.getString(c.getColumnIndex(FeedReaderDbHelper.KEY_DATE)));
                locationList.add(l);
                adapter.notifyDataSetChanged();
            } while (c.moveToNext());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
