package com.vedhafishfarm.fishtasql.feature.homepage.newsFeed;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.feature.homepage.MainActivity;
import com.vedhafishfarm.fishtasql.feature.homepage.MainViewModel;
import com.vedhafishfarm.fishtasql.model.post.PostResponse;
import com.vedhafishfarm.fishtasql.model.post.PostsItem;
import com.vedhafishfarm.fishtasql.utils.ViewModelFactory;
import com.vedhafishfarm.fishtasql.utils.adapter.PostAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter postAdapter;
    private List<PostsItem> postsItems;
    private Context context;

    private int limit = 1;
    private int offset = 0;
    private Boolean isFirstLoading = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyv_newsfeed);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        postsItems = new ArrayList<>();

        viewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory()).get(MainViewModel.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemReached()){
                    offset += limit;
                    fetchNews();
                }
            }
        });
    }

    private boolean isLastItemReached() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastCompletelyVisibleItemPosition();
        int numberOfItems = postAdapter.getItemCount();
        return (position >= numberOfItems -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }

    private void fetchNews() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getUid());
        params.put("limit", limit+"");
        params.put("offset", offset+"");
        ((MainActivity) getActivity()).showProgressBar();

        viewModel.getNewsFeed(params).observe(getViewLifecycleOwner(), new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {
                ((MainActivity) getActivity()).hideProgressBar();

                if (postResponse.getStatus() == 200) {
                    postsItems.addAll(postResponse.getPosts());

                    if (isFirstLoading){
                        postAdapter = new PostAdapter(context, postsItems);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        postAdapter.notifyItemRangeInserted(postsItems.size(), postResponse.getPosts().size());
                    }
                    isFirstLoading = false;
                } else {
                    Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        postsItems.clear();
        isFirstLoading = true;
    }
}