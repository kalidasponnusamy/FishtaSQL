package com.vedhafishfarm.fishtasql.feature.homepage.friends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.data.remote.ApiClient;
import com.vedhafishfarm.fishtasql.feature.profile.ProfileActivity;
import com.vedhafishfarm.fishtasql.model.friend.Friend;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    Context context;
    List<Friend> friends;

    public FriendAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend item = friends.get(position);
        holder.profileName.setText(item.getName());

        String image = "";

        if (Uri.parse(item.getProfileUrl()).getAuthority() == null){
            image = ApiClient.BASE_URL+item.getProfileUrl();
        } else {
            image = item.getProfileUrl();
        }

        Glide.with(context).load(image).placeholder(R.drawable.default_profile_placeholder).into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid", item.getUid()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView profileName;
        Button btnAcceptRequest;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            btnAcceptRequest = itemView.findViewById(R.id.btn_accept);
            btnAcceptRequest.setVisibility(View.GONE);
        }
    }
}
