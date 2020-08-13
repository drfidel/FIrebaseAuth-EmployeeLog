package com.fidelitysolutions.employeelog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingsFragment#} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends DialogFragment {

    ImageView mProfilePic;
    TextView mTvChangePP, mTvChangeUserName, mTvChangeUserEmail;
    EditText  mEdtSettingsUserName, mEdtSettingsUserEmail;
    Button mBuOkay, mBuCancel;

    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        //
        mContext=getActivity();
        mProfilePic= view.findViewById(R.id.imvSettingsAvatar);
        mTvChangePP = view.findViewById(R.id.tvSettingsEditAvatar);
        mTvChangeUserName = view.findViewById(R.id.tvSettingsEditDisplayName);
        mTvChangeUserEmail = view.findViewById(R.id.tvSettingsEditEmail);
        mEdtSettingsUserName = view.findViewById(R.id.edtSettingsDisplayName);
        mEdtSettingsUserEmail = view.findViewById(R.id.edtSettingUserEmail);
        mBuOkay = view.findViewById(R.id.buOkay);
        mBuCancel = view.findViewById(R.id.buCancel);

        //When new text is typed in fields below; activate the buttons
        //When the change dp button is clicked; open gallery to get a photo to upload

        //fire events if user pressed for change
        //change avatar onclick event
        mTvChangePP.setOnClickListener(view1 -> {
            Toast.makeText(mContext, "Clicked to edit PP", Toast.LENGTH_SHORT).show();
        });

        //Change Display name
        mTvChangeUserName.setOnClickListener(view1 -> {
            Toast.makeText(mContext, "Clicked to edit DName", Toast.LENGTH_SHORT).show();
        });

        //change Email address via link or manual
        mTvChangeUserEmail.setOnClickListener(view1 -> {
            Toast.makeText(mContext, "Clicked to edit Email", Toast.LENGTH_SHORT).show();
        });

        //cancel button
        mBuCancel.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        mBuOkay.setOnClickListener(view1 -> {
            getDialog().dismiss();
        });

        return view;
    }
}