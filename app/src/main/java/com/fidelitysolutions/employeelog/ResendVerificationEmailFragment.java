package com.fidelitysolutions.employeelog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResendVerificationEmailFragment#} factory method to
 * create an instance of this fragment.
 */
public class ResendVerificationEmailFragment extends DialogFragment {

    Context mContext;
    Button mSend, mCancel;
    EditText mVermail, mVerPassword;

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            if (getArguments().getBoolean("notAlertDialog")) {
//                return super.onCreateDialog(savedInstanceState);
//            }
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Alert Dialog");
//        builder.setMessage("Alert Dialog inside DialogFragment");
//
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//            }
//        });
//
//        return builder.create();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resend_verification_email,container,false);




        mContext = getActivity();
        mVermail = view.findViewById(R.id.editTextVerEmailAddress);
        mVerPassword = view.findViewById(R.id.editTextVerPassword);
        mSend = view.findViewById(R.id.buResendVerEmail);
        mCancel = view.findViewById(R.id.bucancelVerification);

        //cancel
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        //Send Verification email
        mSend.setOnClickListener((view1) -> {

            Log.d(TAG, "onCreateView: attempting to resend verification email");
            if(!isEmpty(mVermail.getText().toString())
                    && !isEmpty(mVerPassword.getText().toString())){
                authenticateAndResendEmail(mVermail.getText().toString(),
                        mVerPassword.getText().toString());
            } else {
                Toast.makeText(mContext, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            }

            getDialog().dismiss();
        });

        return view;
    }

    //Method to resend verification mail
    /**
     * returns sends verication
     * @param
     * @return
     * */
    private void authenticateAndResendEmail(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(mContext, "Verification email sent successfully",
                            Toast.LENGTH_SHORT).show();
                    //getDialog().dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Invalid credentials \n Reset your password",
                        Toast.LENGTH_SHORT).show();
                //getDialog().dismiss();
            }
        });

    }

    /**
     * returns true if param is null
     * @paramSends verification email
     * @return
     * */
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener((task -> {
                if(task.isSuccessful()){
                    Toast.makeText(mContext, "Sent verification email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "send verification failed, server error", Toast.LENGTH_SHORT).show();
                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Network failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * returns true if param is null
     *
     * */
    private boolean isEmpty(String string){
        return string.equals("");
    }

}