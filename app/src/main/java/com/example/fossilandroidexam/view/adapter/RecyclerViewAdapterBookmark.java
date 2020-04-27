package com.example.fossilandroidexam.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fossilandroidexam.R;
import com.example.fossilandroidexam.model.StackoverflowService.User;
import com.example.fossilandroidexam.modelview.StackoverflowViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RecyclerViewAdapterBookmark extends RecyclerView.Adapter<RecyclerViewAdapterBookmark.ViewHolder>{
    private List<User> listUser;
    private Context context;
    private List<String> listBookmark;
    private Map<String, Bitmap> mapImage;
    private StackoverflowViewModel viewModel;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textDisplay;
        public ImageView imageProfile;
        public ImageView imageBookmark;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDisplay = itemView.findViewById(R.id.displayInfo);
            imageProfile = itemView.findViewById(R.id.profile_image);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            imageBookmark = itemView.findViewById(R.id.bookmark);
            imageBookmark.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //filtBoorkmark();
                    Log.d("mapBookmark", String.valueOf(listBookmark.size()));
                    String key = (String) v.getTag();
                    if (listBookmark.contains(key)) {
                        listBookmark.remove(key);
                        viewModel.updateAUserOfBookmarkData(key, false);

                        for (User user: listUser
                             ) {
                            if(user.strUserId.equals(v.getTag()))
                            {
                                listUser.remove(user);
                                return;
                            }
                        }
                    }
                    notifyDataSetChanged();


                }
            });
        }
    }


    public RecyclerViewAdapterBookmark(Context context, StackoverflowViewModel viewModel) {
        this.listUser = new ArrayList<>();
        this.context = context;
        listBookmark = new ArrayList<>();
        mapImage = new HashMap<>();
        this.viewModel = viewModel;
        this.viewModel.getListBookmarkUsers().observe((LifecycleOwner) context, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d("getListBookmarkUsers", String.valueOf(users));
                try {
                    clearListUser();
                    addListUser(users);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.viewModel.getListbookmark().observe((LifecycleOwner) context, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                listBookmark.addAll(strings);
            }
        });
        this.viewModel.getEntryImage().observe((LifecycleOwner) context, new Observer<Map.Entry<String, Bitmap>>() {
            @Override
            public void onChanged(Map.Entry<String, Bitmap> stringBitmapEntry) {
                mapImage.put(stringBitmapEntry.getKey(), stringBitmapEntry.getValue());
                notifyDataSetChanged();
            }
        });

    }

    @NonNull
    @Override
    public RecyclerViewAdapterBookmark.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View view =
                inflater.inflate(R.layout.item_recylerview, parent, false);
        // Get the app's shared preferences


        return new RecyclerViewAdapterBookmark.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterBookmark.ViewHolder holder, int position) {
        User user = listUser.get(position);
        holder.textDisplay.setText(user.toString());
        holder.itemView.setTag(user.strUserId);
        holder.imageProfile.setImageBitmap(mapImage.get(user.srtProfileImageUrl));
        holder.imageBookmark.setTag(user.strUserId);
        holder.linearLayout.setTag(user.strUserId);

        // load bookmark
        holder.imageBookmark.setImageResource(R.drawable.bookmark_black);

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }


    public void clearListUser() {
        listUser.clear();
    }

    public void notifyAdapterChange() {
        notifyDataSetChanged();
    }

    public List<String> getListBookmark() {
        return this.listBookmark;
    }

    @SuppressLint("CheckResult")
    public void addListUser(List<User> list) throws ExecutionException, InterruptedException {
        listUser.addAll(list);
        Log.d("List user in adapter", String.valueOf(list));
        String url;
        for (User user : list
        ) {
            if(mapImage.containsKey(user.srtProfileImageUrl))
                return;
            viewModel.loadImage(user.srtProfileImageUrl);
        }
    }
}
