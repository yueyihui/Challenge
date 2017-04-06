package com.github.lyue.bmwchallenge;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yue_liang on 2017/4/5.
 */

public class LocationLoader {
    private static final String TAG = LocationLoader.class.getSimpleName();
    public interface OnLoaderListener {
        public void onLoadEnd(boolean isSuccessful);
    }
    private OnLoaderListener mOnLoaderListener;
    private volatile HashMap<String, JavaBean> mJavaBeanMap;
    private boolean mIsLoading = false;
    public void setOnLoaderListener(OnLoaderListener mOnLoaderListener) {
        this.mOnLoaderListener = mOnLoaderListener;
    }

    private static LocationLoader mLocationLoader = null;
    private LocationLoader() {

    }
    public static LocationLoader get(){
        if (mLocationLoader == null) {
            mLocationLoader = new LocationLoader();
        }
        return mLocationLoader;
    }

    public boolean isEmpty() {
        if (mJavaBeanMap == null) {
            return true;
        } else {
            return mJavaBeanMap.isEmpty();
        }
    }

    public void load(String url) {
        new AsyncTask<String, Void, Boolean>(){
            @Override
            protected void onPreExecute() {
                mIsLoading = true;
            }

            @Override
            protected Boolean doInBackground(String[] params) {
                Response response = null;
                try {
                    response = mLocationLoader.loadURL(params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                if(response != null) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<JavaBean>>() {

                    }.getType();
                    try {
                        ArrayList<JavaBean> jsonBeanList = gson.
                                fromJson(response.body().string(), type);
                        if(mJavaBeanMap == null) {
                            mJavaBeanMap = new HashMap<String, JavaBean>();
                        } else {
                            mJavaBeanMap.clear();
                        }
                        for (JavaBean javaBean : jsonBeanList) {
                            Log.d(TAG,"java Bean :\n" + javaBean);
                            mJavaBeanMap.put(javaBean.getName(), javaBean);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                mIsLoading = false;
                if (mOnLoaderListener != null) {
                    mOnLoaderListener.onLoadEnd(isSuccessful);
                }
            }
        }.execute(url);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    private Response loadURL(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    private String[] getLocation() {
        if (mJavaBeanMap == null)
            return new String[0];
        ArrayList<String> location = new ArrayList<>(mJavaBeanMap.size());
        Iterator<Map.Entry<String, JavaBean>> key = mJavaBeanMap.entrySet().iterator();
        while (key.hasNext()) {
            String name = key.next().getKey();
            location.add(name);
        }
        return location.toArray(new String[location.size()]);
    }

    public JavaBean getJavaBean(String key) throws NullPointerException {
        if (mJavaBeanMap != null) {
            return mJavaBeanMap.get(key);
        } else {
            throw new NullPointerException();
        }
    }

    public String[] getLocationByNameSort() {
        String[] names = getLocation();
        Arrays.sort(names);
        return names;
    }

    public String[] getAddressByNameSort(String[] keys) {
        ArrayList<String> address = new ArrayList<>();
        for (String name : keys) {
            address.add(mJavaBeanMap.get(name).getAddress());
        }
        return address.toArray(new String[address.size()]);
    }

    public String[] getLocationByArrivalTimeSort() {
        ArrayList<String> arrivalTimeList = new ArrayList<>();
        ArrayList<String> locationList = new ArrayList<>();
        HashMap<String, String> temporary = new HashMap<>();
        String[] names = getLocation();
        for (String name : names) {
            String arrivalTime = mJavaBeanMap.get(name).getArrivalTime();
            arrivalTimeList.add(arrivalTime);
            temporary.put(arrivalTime, name);
        }
        String[] times = arrivalTimeList.toArray(new String[arrivalTimeList.size()]);
        Arrays.sort(times);
        for (String time : times) {
            locationList.add(temporary.get(time));
        }
        return locationList.toArray(new String[locationList.size()]);
    }

    public String[] getAddressByArrivalTimeSort(String[] keys) {
        ArrayList<String> address = new ArrayList<>();
        for (String name : keys) {
            address.add(mJavaBeanMap.get(name).getAddress());
        }
        return address.toArray(new String[address.size()]);
    }
}
