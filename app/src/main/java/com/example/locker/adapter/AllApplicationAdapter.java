package com.example.locker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
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
import com.example.locker.model.AllApps;
import com.example.locker.service.AppLaunchDetectionService;
import com.example.locker.util.Constant;

import java.util.ArrayList;

public class AllApplicationAdapter extends RecyclerView.Adapter<AllApplicationAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;

    private final Context context;
    private final PackageManager packageManager;
    ArrayList<AllApps> list;
    DatabaseHandler databaseHandler;
    LockPackageDatabase packageDatabase;

    public AllApplicationAdapter(@NonNull Context context, ArrayList<AllApps> appsList) {
        this.context = context;
        this.list = appsList;
        packageManager = context.getPackageManager();
        databaseHandler = new DatabaseHandler(context);
        packageDatabase = new LockPackageDatabase(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAllAppList(ArrayList<AllApps> updateAllApps) {
        this.list.clear();
        this.list = updateAllApps;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_all_application, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllApps model = list.get(position);
        holder.imageView.setImageDrawable(model.getAppIcon());
        holder.textView.setText(model.getAppName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;
        ImageView btnLock;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.appName);
            imageView = itemView.findViewById(R.id.appIcon);
            btnLock = itemView.findViewById(R.id.btnLock);
            btnLock.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, list.get(getAdapterPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, AllApps app);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
