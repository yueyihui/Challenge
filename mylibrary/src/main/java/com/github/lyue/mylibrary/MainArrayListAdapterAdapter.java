/**
 * Copyright (C) 2016 yueyihui

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lyue.mylibrary;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class MainArrayListAdapterAdapter extends RecyclerView.Adapter<MainViewHolder>
        implements ArrayListAdapter {
    private Activity mActivity;
    private String[] mLocationStrings;
    private String[] mAddressStrings;
    private String TAG = MainArrayListAdapterAdapter.class.getName();
    private Transformer mTransformer;

    public MainArrayListAdapterAdapter(Activity activity) {
        mActivity = activity;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    public MainArrayListAdapterAdapter(Activity activity, String[] data1, String[] data2) {
        mActivity = activity;
        mLocationStrings = data1;
        mAddressStrings = data2;
        mTransformer = ((TransformTool) activity).getTransformer();
    }

    @Override
    public void changeData(String[] data) {
        mAddressStrings = data;
        this.notifyDataSetChanged();
    }

    @Override
    public void changeData(String[] location, String[] address) {
        mLocationStrings = location;
        mAddressStrings = address;
        this.notifyDataSetChanged();
    }

    @Override
    public void changeData(List<String> dataList) {
        this.notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(mActivity).
                inflate(R.layout.main_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        String str = mLocationStrings[position];
        TextView textView = holder.getLocationView();
        textView.setText(String.format("Location : %s", str));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performClick();
            }
        });

        str = mAddressStrings[position];
        textView = holder.getAddressView();
        textView.setText(String.format("Address : %s", str));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performClick();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTransformer.isAnimating() && !mTransformer.isExtended()) {
                    mTransformer.activateAwareMotion(v, holder, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddressStrings.length;
    }

}
