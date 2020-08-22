package com.fidelitysolutions.employeelog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserDashBoardActivity extends AppCompatActivity {
    private static final String TAG = "UserDashboardActivity";
    
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //navbar views
    ImageView mProfilePic;
    TextView mTvProfileName, mTvEmail;
    View navHeader;
    NavigationView navigationView;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);
        Log.d(TAG, "onCreate: OnCreateStart.");

        //link nav views with layout
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        mProfilePic = navHeader.findViewById(R.id.imvNavAvatar);
        mTvProfileName= navHeader.findViewById(R.id.tvNavUserDisplayName);
        mTvEmail= navHeader.findViewById(R.id.tvNavUserEmail);
        
        setupFirebaseAuth();
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getUserDetails();
        //setUserDetails();
    }



    //setUser Details
    private void setUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                    setDisplayName("Akiyo Fidel").
                    setPhotoUri(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/5/5d/Crateva_religiosa.jpg")).build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: User Profile updated");

                                getUserDetails();
                            }
                        }
                    });
        }
    }

    private void getUserDetails(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            String properties = "uid: " + uid + "\n" +
                    "name: " + name + "\n" +
                    "email: " + email + "\n" +
                    "photoUrl: " + photoUrl;
            Log.d(TAG, "getUserDetails: properties: \n" + properties);

            //render in views
            mTvEmail.setText(email);
            mTvProfileName.setText(name);
            //mProfilePic.setImageURI(photoUrl);

            Picasso.get().load(photoUrl).into(mProfilePic);

        }
    }

    private void setupFirebaseAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    if (user.isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                        Toast.makeText(UserDashBoardActivity.this, "Authenticated with:" + user.getEmail(),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UserDashBoardActivity.this, SignUpActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(UserDashBoardActivity.this, "Check your Email Inbox for a verification link",
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
    protected void onResume() {
        super.onResume();
        checkAuthenticationAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //signout user
        FirebaseAuth.getInstance().signOut();
        //FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
    }

    private void checkAuthenticationAuth() {
        Log.d(TAG, "checkAuthenticationAuth: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationAuth: user is null, navigating back to login screen");

            Intent intent = new Intent(UserDashBoardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {
            Log.d(TAG, "checkAuthenticationAuth: user is authenticated");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_dash_board, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:{
                //code to open settings fragment
                Log.d(TAG, "onOptionsItemSelected: opened user accounts settings");

                UserSettingsFragment dialogFragment = new UserSettingsFragment();
                FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);

                dialogFragment.setArguments(bundle);

                ftr = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ftr.remove(prev);
                }
                ftr.addToBackStack(null);


                dialogFragment.show(ftr, "dialog");}

//                Intent settingsIntent = new Intent(UserDashBoardActivity.this, UserSettingsFragment.class);
//                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(settingsIntent);
//                finish();
                return true;

            case R.id.action_signout:
                //code to signout
                Log.d(TAG, "onOptionsItemSelected: If sign out selected, signout autustatus");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserDashBoardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //getUserDetails();

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    
    
}