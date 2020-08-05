package com.fidelitysolutions.employeelog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import static android.text.TextUtils.isEmpty;

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SignUpActivity";

    private static final String DOMAIN_NAME = "gmail.com";

    //widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private TextView mAlreadySigned;
    private Button mSignUp;
    private ProgressBar mProgressBar;
    private CheckBox mAcceptTCs;

    private static void onClick(View view) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableSignUpButtonConditionally();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get all widget resources
        mEmail = findViewById(R.id.edtRegisterUserEmail);
        mPassword = findViewById(R.id.edtSignUPUserPassword);
        mConfirmPassword = findViewById(R.id.edtSignUPUserPasswordConfirm);
        mSignUp = findViewById(R.id.buSignUp);
        mProgressBar = findViewById(R.id.progressBar);
        mAlreadySigned = findViewById(R.id.tvHaveAccount);
        mAcceptTCs = findViewById(R.id.cbxAgreeTerms);


        hideDialogue();

        mAcceptTCs.setOnCheckedChangeListener(this);
        enableSignUpButtonConditionally();

        //Redirect to login screen
        mAlreadySigned.setOnClickListener((view) -> {
            redirectLoginScreen();
        });


        //sign-Up
        mSignUp.setOnClickListener(view -> {
            Log.d(TAG, "onClick: attempting to register");

            //check for null value edt text fields
            if(!isEmpty(mEmail.getText().toString())
                    && !isEmpty(mPassword.getText().toString())
                    && !isEmpty(mConfirmPassword.getText().toString())){

                //check if user has a company email address
                if (isValidDomain(mEmail.getText().toString())){

                    //check if passwords match
                    if(doStringsMatch(mPassword.getText().toString(),mConfirmPassword.getText().toString())){
                        registerNewEmail(mEmail.getText().toString(), mConfirmPassword.getText().toString());
                        Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords do not mathc", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Register with company Email", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "you must fill all the fields", Toast.LENGTH_SHORT).show();
            }
    });

        hideSoftKeyboard();

    }



    /**
    * Send verification email
    * */
    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Send Verification Email", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(SignUpActivity.this, "Couldn't send verification Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**
     * Signup new user
     * @param
     * @return
     * */
    private void registerNewEmail(String email, String Password){
        showDialogue();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, Password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: oncomplete:" + task.isSuccessful());

                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Authstate:"+
                                    FirebaseAuth.getInstance().getCurrentUser().getUid());

                            sendVerificationEmail();

                            FirebaseAuth.getInstance().signOut();

                            //redirect user to login screen
                            redirectLoginScreen();

                        } else {
                            Toast.makeText(SignUpActivity.this, "Unable to SignUP", Toast.LENGTH_SHORT).show();
                        }
                        hideDialogue();
                    }
                }
        );
    }


    /**
     * returns true if @param1 matchers @param2
     * @param1 s1
     * @param2 s2
     * @return
     * */
    private boolean doStringsMatch(String s1, String s2) {
            return s1.equals(s2);
    }

    /**
     * returns true if param is null
     * @param string
     * @return
     * */
    private boolean isEmpty(String string){
        return string.equals("");
    }

    /**
     * returns true if the user's email contains '@tabian.ca'
     * @param email
     * @return
     * */
    private boolean isValidDomain(String email) {
        Log.d(TAG, "isValidDomain: veryfing email has correct domain"+ email);
        String domain = email.substring(email.indexOf("@")+1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domain: "+ domain);
        return  domain.equals(DOMAIN_NAME);
    }

    /**
     * redirects to login screen
     * */
    private void redirectLoginScreen(){
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen");
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void showDialogue(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialogue(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void enableSignUpButtonConditionally(){
        if(mAcceptTCs.isChecked()){
            mSignUp.setEnabled(true);
        } else {
            mSignUp.setEnabled(false);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            enableSignUpButtonConditionally();
    }
}