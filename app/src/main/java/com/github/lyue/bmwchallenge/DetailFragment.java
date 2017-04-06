package com.github.lyue.bmwchallenge;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lyue.mylibrary.NextFragment;

/**
 * Created by yue_liang on 2017/4/5.
 */

public class DetailFragment extends NextFragment {
    TextView mArrivalTime;
    TextView mLocation;
    TextView mAddress;
    TextView mLatitude;
    TextView mLongitude;
    public void update(String key) {
        if (mArrivalTime == null) {
            mArrivalTime = (TextView) getView().findViewById(R.id.arrival_time);
        }
        mArrivalTime.setText(String.format("Arrival time : %s",
                LocationLoader.get().getJavaBean(key).getArrivalTime()));

        if (mLocation == null) {
            mLocation = (TextView) getView().findViewById(R.id.location);
        }
        mLocation.setText(String.format("Location : %s",
                LocationLoader.get().getJavaBean(key).getName()));

        if (mAddress == null) {
            mAddress = (TextView) getView().findViewById(R.id.address);
        }
        mAddress.setText(String.format("Address : %s",
                LocationLoader.get().getJavaBean(key).getAddress()));


        if (mLatitude == null) {
            mLatitude = (TextView) getView().findViewById(R.id.latitude);
        }
        mLatitude.setText(String.format("Latitude : %f",
                LocationLoader.get().getJavaBean(key).getLatitude()));

        if (mLongitude == null) {
            mLongitude = (TextView) getView().findViewById(R.id.longitude);
        }
        mLongitude.setText(String.format("Longitude : %f",
                LocationLoader.get().getJavaBean(key).getLongitude()));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        return view;
    }
}
