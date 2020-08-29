package com.vedhafishfarm.fishtasql.feature.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.feature.homepage.friends.FriendRequestAdapter;
import com.vedhafishfarm.fishtasql.feature.homepage.friends.FriendsFragment;
import com.vedhafishfarm.fishtasql.feature.homepage.newsFeed.NewsFeedFragment;
import com.vedhafishfarm.fishtasql.feature.postupload.PostUploadActivity;
import com.vedhafishfarm.fishtasql.feature.profile.ProfileActivity;
import com.vedhafishfarm.fishtasql.feature.search.SearchActivity;
import com.vedhafishfarm.fishtasql.model.GeneralResponse;
import com.vedhafishfarm.fishtasql.model.friend.FriendResponse;
import com.vedhafishfarm.fishtasql.utils.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements FriendRequestAdapter.IPerformAction {

    private BottomNavigationView bottomNavigationView;
    private FriendsFragment friendsFragment;
    private NewsFeedFragment newsFeedFragment;
    private FloatingActionButton fab;
    private ImageView searchIcon;
    private ProgressBar progressBar;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(MainViewModel.class);

        progressBar = findViewById(R.id.progressbar);

        bottomNavigationView = findViewById(R.id.navigation);
        searchIcon = findViewById(R.id.toolbar_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostUploadActivity.class));
            }
        });

        friendsFragment = new FriendsFragment();
        newsFeedFragment = new NewsFeedFragment();
        setFragment(newsFeedFragment);

        setBottomNavigationView();
    }

    private void setBottomNavigationView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.newsFeedFragment:
                        setFragment(newsFeedFragment);
                        return true;
                    case R.id.FriendFragment:
                        setFragment(friendsFragment);
                        return true;
                    case R.id.profileActivity:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getUid()));
                        return false;
                }
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment).commit();
    }

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void performAction(int position, String profileId, int operationType) {
        showProgressBar();
            viewModel.performAction(new ProfileActivity.PerformAction(
                    operationType+"",FirebaseAuth.getInstance().getUid(), profileId)).observe(this, new Observer<GeneralResponse>() {
                @Override
                public void onChanged(GeneralResponse generalResponse) {
                    hideProgressBar();
                    Toast.makeText(MainActivity.this, generalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (generalResponse.getStatus() == 200){
                        FriendResponse response = viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).getValue();
                        response.getResult().getRequests().remove(position);
                        viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).setValue(response);
                    }
                }
            });
    }
}