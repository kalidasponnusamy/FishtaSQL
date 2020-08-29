package com.vedhafishfarm.fishtasql.utils.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vedhafishfarm.fishtasql.R;
import com.vedhafishfarm.fishtasql.data.remote.ApiClient;
import com.vedhafishfarm.fishtasql.model.post.PostsItem;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<PostsItem> postsItems;

    public PostAdapter(Context context, List<PostsItem> postsItems) {
        this.context = context;
        this.postsItems = postsItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostsItem postsItem = postsItems.get(position);
        holder.peopleName.setText(postsItem.getName());
        holder.date.setText(postsItem.getStatusTime());

        if (postsItem.getPrivacy().equals("0")){
            holder.privacyIcon.setImageResource(R.drawable.ic_privacy_friends);
        } else if (postsItem.getPrivacy().equals("1")){
            holder.privacyIcon.setImageResource(R.drawable.ic_only_me);
        } else {
            holder.privacyIcon.setImageResource(R.drawable.ic_public);
        }

        String profileImage = postsItem.getProfileUrl();

        if (!postsItem.getProfileUrl().isEmpty()){
            if (Uri.parse(postsItem.getProfileUrl()).getAuthority()==null){
                profileImage = ApiClient.BASE_URL + postsItem.getProfileUrl();
            }

            Glide.with(context).load(profileImage).placeholder(R.drawable.default_profile_placeholder).into(holder.peopleImage);


        }

        if (!postsItem.getStatusImage().isEmpty()){
            holder.statusImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(ApiClient.BASE_URL + postsItem.getStatusImage()).placeholder(R.drawable.default_profile_placeholder).into(holder.statusImage);
        } else {
            holder.statusImage.setVisibility(View.GONE);
        }

        if (postsItem.getPost().isEmpty()){
            holder.post.setVisibility(View.GONE);
        } else {
            holder.post.setVisibility(View.VISIBLE);
            holder.post.setText(postsItem.getPost());
        }

    }

    @Override
    public int getItemCount() {
        return postsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView peopleImage;
        TextView peopleName;
        TextView date;
        TextView post;
        ImageView privacyIcon;
        ImageView statusImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            peopleImage = itemView.findViewById(R.id.people_image);
            peopleName = itemView.findViewById(R.id.people_name);
            date = itemView.findViewById(R.id.date);
            post = itemView.findViewById(R.id.post);
            statusImage = itemView.findViewById(R.id.status_image);
            privacyIcon = itemView.findViewById(R.id.privacy_icon);
        }
    }
}
