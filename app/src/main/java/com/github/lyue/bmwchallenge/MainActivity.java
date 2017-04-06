package com.github.lyue.bmwchallenge;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lyue.mylibrary.GettingToolbar;
import com.github.lyue.mylibrary.MainArrayListAdapterAdapter;
import com.github.lyue.mylibrary.MainViewHolder;
import com.github.lyue.mylibrary.SpaceItemDecoration;
import com.github.lyue.mylibrary.TransformTool;
import com.github.lyue.mylibrary.Transformer;


public class MainActivity extends AppCompatActivity implements TransformTool, GettingToolbar {
    private RecyclerView mMainRecyclerView;
    private Transformer mTransformer;
    private LocationLoader mLocationLoader;
    private int mSelectedDataPosition;
    private static final String TAG = MainActivity.class.getName();
    private static final String SELECTED_DATA_POSITION = "mSelectedDataPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationLoader.isLoading()) {
                    mLocationLoader.
                            load("http://localsearch.azurewebsites.net/api/Locations");
                    Toast.makeText(MainActivity.this,
                            "Loading location information", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Loading, please wait", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initLocationLoader();
        initRecyclerView();
        restoreState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpstreamNetworkMonitor.registerNetworkMonitor(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpstreamNetworkMonitor.unregisterNetworkMonitor(this);
    }

    private void initLocationLoader() {
        mLocationLoader = LocationLoader.get();
        mLocationLoader.setOnLoaderListener(new LocationLoader.OnLoaderListener() {
            @Override
            public void onLoadEnd(boolean isSuccessful) {
                if (isSuccessful) {
                    if (mMainRecyclerView.getAdapter() == null) {
                        setMainRecyclerViewAdapter();
                    } else {
                        updateDataSortByName();
                    }
                } else {
                    if (isConnected()) {
                        Toast.makeText(MainActivity.this, "auto try again",
                                Toast.LENGTH_SHORT).show();
                        LocationLoader.get().
                                load("http://localsearch.azurewebsites.net/api/Locations");
                    } else {
                        Toast.makeText(MainActivity.this, "Network disconnected",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    private void restoreState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mSelectedDataPosition = savedInstanceState.getInt(SELECTED_DATA_POSITION,-1);
            if (mSelectedDataPosition > -1) {
                mMainRecyclerView.scrollToPosition(mSelectedDataPosition);
                mMainRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        RecyclerView.ViewHolder holder = mMainRecyclerView.findViewHolderForAdapterPosition
                                (mSelectedDataPosition);
                        holder.itemView.performClick();
                        mSelectedDataPosition = -1;
                    }
                });
            }
        }
    }

    private void initRecyclerView() {
        mTransformer = new Transformer(this);
        int spacingInPixels = getResources().
                getDimensionPixelSize(R.dimen.recycler_view_item_view_space);
        mMainRecyclerView = (RecyclerView) mTransformer.getMainView();
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        if (!mLocationLoader.isEmpty()) {
            setMainRecyclerViewAdapter();
        }
        mTransformer.setAnimationListener(new Transformer.AnimationListener() {
            @Override
            public void onRiseUpStart(View mTargetView, int selectedDataPosition) {
                mSelectedDataPosition = selectedDataPosition;
            }

            @Override
            public void onRiseUpEnd(View mTargetView, int selectedDataPosition) {
                    TextView textView = ((MainViewHolder) mMainRecyclerView.
                            getChildViewHolder(mTargetView)).
                            getLocationView();
                    String title = textView.getText().toString().split(" : ")[1];
                    setTitle(title);

                    ((DetailFragment) getTransformer().getNextFragment()).update(title);
            }

            @Override
            public void onResetStart(View mTargetView, int selectedDataPosition) {
                setTitle("");
            }

            @Override
            public void onResetEnd(View mTargetView, int selectedDataPosition) {
                setTitle(getResources().getString(R.string.app_name));
                mSelectedDataPosition = -1;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTransformer.isExtended() && mSelectedDataPosition != -1) {
            outState.putInt(SELECTED_DATA_POSITION, mSelectedDataPosition);
        }
    }

    @Override
    public Transformer getTransformer() {
        return mTransformer;
    }

    @Override
    public void onBackPressed() {
        if (!mTransformer.isAnimating() &&
                mTransformer.isExtended()) {
            mTransformer.reset();
        } else if (mTransformer.isAnimating()) {
            //do nothing, if remove else if, will encounter exit activity when animating
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Toolbar getV7Toolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.sort_by_name:
                updateDataSortByName();
                return true;
            case R.id.sort_by_arrival_time:
                updateDataSortByArrivalTime();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMainRecyclerViewAdapter() {
        String[] locations = mLocationLoader.getLocationByNameSort();
        mMainRecyclerView.setAdapter(new MainArrayListAdapterAdapter(
                MainActivity.this,
                mLocationLoader.getLocationByNameSort(),
                mLocationLoader.getAddressByNameSort(locations)));
    }

    private void updateDataSortByName() {
        String[] locations = mLocationLoader.getLocationByNameSort();
        ((MainArrayListAdapterAdapter) mMainRecyclerView.getAdapter()).
                changeData(locations,
                        mLocationLoader.getAddressByNameSort(locations));
    }

    private void updateDataSortByArrivalTime() {
        String[] locations = mLocationLoader.getLocationByArrivalTimeSort();
        ((MainArrayListAdapterAdapter) mMainRecyclerView.getAdapter()).
                changeData(locations,
                        mLocationLoader.getAddressByArrivalTimeSort(locations));
    }
}
