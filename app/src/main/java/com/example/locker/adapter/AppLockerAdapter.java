package com.example.locker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locker.R;
import com.example.locker.database.DatabaseHandler;
import com.example.locker.database.LockPackageDatabase;
import com.example.locker.model.AppDetails;

import java.util.ArrayList;

public class AppLockerAdapter extends RecyclerView.Adapter<AppLockerAdapter.LockViewHolder>{
    OnItemClickListener mItemClickListener;
    Context context;
    ArrayList<AppDetails> listDetails;
    DatabaseHandler databaseHandler;
    LockPackageDatabase packageDatabase;
    public AppLockerAdapter(Context context, ArrayList<AppDetails> listDetails) {
        this.context = context;
        this.listDetails = listDetails;
        databaseHandler = new DatabaseHandler(context);
        packageDatabase = new LockPackageDatabase(context);
    }

    @NonNull
    @Override
    public AppLockerAdapter.LockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_locked_app, parent, false);
        return new LockViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull AppLockerAdapter.LockViewHolder holder, int position) {
        final AppDetails appDetails = listDetails.get(position);
        holder.imageView.setImageDrawable(appDetails.getAppIcon());
        holder.name.setText(appDetails.getAppName());
    }

    @Override
    public int getItemCount() {
        return listDetails.size();
    }

    public class LockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView name;
        ImageView btnDelete;

        public LockViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.appIcon);
            name = itemView.findViewById(R.id.appName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
