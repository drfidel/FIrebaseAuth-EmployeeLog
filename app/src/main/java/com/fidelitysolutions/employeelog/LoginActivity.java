package com.fidelitysolutions.employeelog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //
    private static final String TAG = "SignUpActivity";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String DOMAIN_NAME = "gmail.com";

    //widgets
    private EditText mEmail, mPassword;
    private Button mSignIn;
    private ProgressBar mProgressBar;
    private TextView mNotRegistered, mResendVerificationMail;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get all widget resources
        mEmail = findViewById(R.id.edtUserEmailAddress);
        mPassword = findViewById(R.id.edtUserPassword);
        mSignIn = findViewById(R.id.buSignIn);
        mProgressBar = findViewById(R.id.progressBar2);
        mNotRegistered = findViewById(R.id.tvIfnotRegistered);
        mResendVerificationMail = findViewById(R.id.tvResendVerificationMail);

        hideDialogue();

        mNotRegistered.setOnClickListener(view -> {
            redirectSignUpScreen();
        });

        //get status of auth
        setupFirebaseAuth();

        //Redirect to Verification screen
        mResendVerificationMail.setOnClickListener((view) -> {
            redirectSendVerification();
        });

        mSignIn.setOnClickListener((view) -> {
            //Check if fields are filled out
            Log.d(TAG, "onClick: attempting to register");

            //check for null value edt text fields
            if(!isEmpty(mEmail.getText().toString())
                    && !isEmpty(mPassword.getText().toString())){
                //check if user has a company email address
                if (isValidDomain(mEmail.getText().toString())){
                        //execute signIn to firebase database
                        showDialogue();

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),mPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hideDialogue();
                                        //redirect user to user dashboard
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if(user != null) {
                                            redirectUserDashBoardScreen();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                hideDialogue();
                            }
                        });

                } else {
                    Toast.makeText(LoginActivity.this, "domain Invalid", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(LoginActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            }
        });


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
     * redirects to user DashBoard screen
     * */
    private void redirectUserDashBoardScreen(){
        Log.d(TAG, "redirectDashboardScreen: redirecting to UserDashboard screen");
        Intent intent = new Intent(LoginActivity.this, UserDashBoardActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * redirects to user SignUp screen
     * */
    private void redirectSignUpScreen(){
        Log.d(TAG, "redirectSignUpScreen: redirecting to UserSignUP screen");
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Resend verification email
     * */
    private void redirectSendVerification() {
        Log.d(TAG, "redirectSendVerification: redirect to verification email");

        ResendVerificationEmailFragment dialogFragment = new ResendVerificationEmailFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);

        dialogFragment.setArguments(bundle);

        ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        dialogFragment.show(ft, "dialog");
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

    /*
    * ------------------setup firebase authlistener------------------------
    * */
    private void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    if(user.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with:"+ user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Check your Email Inbox for a verification link",
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                } else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }
}
