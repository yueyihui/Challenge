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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yue_liang on 2016/12/4.
 */

public class MainViewHolder extends RecyclerView.ViewHolder {
    private TextView mLocationContent;
    private TextView mAddressContent;
    private View mReferenceView;
    public MainViewHolder(View itemView) {
        super(itemView);
        mLocationContent = (TextView) itemView.findViewById(getLocationResourceId());
        mAddressContent = (TextView) itemView.findViewById(getAddressResourceId());
        mReferenceView = itemView.findViewById(getReferenceViewResourceId());
    }

    public int getLocationResourceId() {
        return R.id.location_content;
    }

    public int getAddressResourceId() {
        return R.id.address_content;
    }

    public int getReferenceViewResourceId() {
        return R.id.reference_view;
    }

    public View getReferenceView() {
        return mReferenceView;
    }

    public TextView getLocationView() {
        return mLocationContent;
    }

    public TextView getAddressView() {
        return mAddressContent;
    }
}