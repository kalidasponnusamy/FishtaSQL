package com.vedhafishfarm.fishtasql.feature.homepage.friends;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.feature.homepage.MainActivity;
import com.vedhafishfarm.fishtasql.feature.homepage.MainViewModel;
import com.vedhafishfarm.fishtasql.model.friend.Friend;
import com.vedhafishfarm.fishtasql.model.friend.FriendResponse;
import com.vedhafishfarm.fishtasql.model.friend.Request;
import com.vedhafishfarm.fishtasql.utils.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private MainViewModel viewModel;

    private Context context;
    private RecyclerView friendRequestRecyv, friendsRecyv;
    private TextView friendTitle, requestTitle, defaultTitle;

    private FriendAdapter friendAdapter;
    private FriendRequestAdapter friendRequestAdapter;
    private List<Friend> friends = new ArrayList<>();
    private List<Request> requests = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider((FragmentActivity)context,new ViewModelFactory()).get(MainViewModel.class);

        friendRequestRecyv = view.findViewById(R.id.friend_request_recyv);
        friendsRecyv = view.findViewById(R.id.friends_recyv);
        friendTitle = view.findViewById(R.id.friend_title);
        requestTitle = view.findViewById(R.id.request_title);
        defaultTitle = view.findViewById(R.id.default_textview);

        friendRequestAdapter = new FriendRequestAdapter(context, requests);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        friendRequestRecyv.setAdapter(friendRequestAdapter);
        friendRequestRecyv.setLayoutManager(linearLayoutManager2);


        friendAdapter = new FriendAdapter(context, friends);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(context);
        friendsRecyv.setAdapter(friendAdapter);
        friendsRecyv.setLayoutManager(linearLayoutManager);
        loadFriends();
    }

    private void loadFriends() {
        ((MainActivity) getActivity()).showProgressBar();
        viewModel.loadFriends(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<FriendResponse>() {
            @Override
            public void onChanged(FriendResponse friendResponse) {
                ((MainActivity) getActivity()).hideProgressBar();
                loadData(friendResponse);
            }
        });
    }

    private void loadData(FriendResponse friendResponse) {
        if (friendResponse.getStatus() == 200) {
            friends.clear();
            friends.addAll(friendResponse.getResult().getFriends());
            friendAdapter.notifyDataSetChanged();

            requests.clear();
            requests.addAll(friendResponse.getResult().getRequests());
            friendRequestAdapter.notifyDataSetChanged();

            if (friendResponse.getResult().getFriends().size() >0) {
                friendTitle.setVisibility(View.VISIBLE);
            }else {
                friendTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getRequests().size() >0) {
                requestTitle.setVisibility(View.VISIBLE);
            }else {
                requestTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getFriends().size() == 0 && friendResponse.getResult().getRequests().size() == 0) {
                defaultTitle.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(context, friendResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}