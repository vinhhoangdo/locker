package com.example.locker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locker.adapter.AllApplicationAdapter;
import com.example.locker.database.DatabaseHandler;
import com.example.locker.database.LockPackageDatabase;
import com.example.locker.databinding.FragmentAllApplicationBinding;
import com.example.locker.model.AllApps;
import com.example.locker.util.PermissionDialog;
import com.example.locker.util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AllApplicationFragment extends Fragment {
    FragmentAllApplicationBinding binding;
    private PackageManager packageManager = null;
    private List<ResolveInfo> appListInfo = null;
    ArrayList<AllApps> allAppList = new ArrayList<>();
    ArrayList<String> listApps = new ArrayList<>();
    AllApplicationAdapter applicationAdapter;
    DatabaseHandler databaseHandler;
    LockPackageDatabase lockPackageDatabase;
    RecyclerView.LayoutManager manager;
    private String mPackageName = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllApplicationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        databaseHandler = new DatabaseHandler(requireContext());
        lockPackageDatabase = new LockPackageDatabase(requireContext());
        packageManager = requireActivity().getPackageManager();
        mPackageName = requireActivity().getPackageName();
        binding.progress.setVisibility(View.VISIBLE);
        new Handler().post(() -> {
            LoadApplications();
            AllAppDatabase();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (appListInfo != null) {
            AllAppDatabase();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void LoadApplications() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        appListInfo = checkForLaunchIntent(packageManager.queryIntentActivities(intent, 0));
    }

    @NonNull
    private List<ResolveInfo> checkForLaunchIntent(@NonNull List<ResolveInfo> list) {
        ArrayList<ResolveInfo> appList = new ArrayList<>();
        for (ResolveInfo info : list) {
            try {
                if (packageManager.getLaunchIntentForPackage(info.activityInfo.packageName) != null) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return appList;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AllAppDatabase() {
        PackageManager mPackageManager = requireActivity().getPackageManager();
        allAppList.clear();
        for (int i = 0; i < appListInfo.size(); i++) {
            ResolveInfo info = appListInfo.get(i);
            String pkgName = info.activityInfo.packageName;
            boolean isLocked = lockPackageDatabase.getPackageName(pkgName);
            if (!isLocked && !pkgName.contains(mPackageName)) {
                String appName = (String) info.loadLabel(mPackageManager);
                Drawable icon = info.loadIcon(mPackageManager);
                allAppList.add(new AllApps(appName, icon, pkgName));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allAppList.sort(Comparator.comparing(app -> app.appName));
        }
        binding.recycler.setLayoutManager(manager);
        applicationAdapter = new AllApplicationAdapter(requireContext(), allAppList);
        binding.recycler.setAdapter(applicationAdapter);
        binding.progress.setVisibility(View.GONE);
        applicationAdapter.SetOnItemClickListener((view, app) -> {
            if(!Utils.isOvlUsagePermissionChecked(requireContext())) {
                showPermissionDialog();
            } else {
                boolean isLockedPackage = lockPackageDatabase.getPackageName(app.getPackageName());
                if (!isLockedPackage) {
                    lockPackageDatabase.addPackage(app.getPackageName());
                }
                allAppList.remove(app);
                applicationAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showPermissionDialog() {
        new PermissionDialog().show(requireActivity().getSupportFragmentManager(), PermissionDialog.PERMISSION_TAG);
    }
}
