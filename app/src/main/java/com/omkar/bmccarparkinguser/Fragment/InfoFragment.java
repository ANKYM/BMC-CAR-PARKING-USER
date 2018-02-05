package com.omkar.bmccarparkinguser.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.omkar.bmccarparkinguser.R;

/**
 * Created by Yogesh on 05-02-2018.
 */

public class InfoFragment extends BottomSheetFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
}
