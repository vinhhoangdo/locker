package com.example.locker.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locker.adapter.AppLockerAdapter;
import com.example.locker.database.DatabaseHandler;
import com.example.locker.database.LockPackageDatabase;
import com.example.locker.databinding.FragmentLockedApplicationBinding;
import com.example.locker.model.AppDetails;

import java.util.ArrayList;
import java.util.Comparator;

public class LockedApplicationFragment extends Fragment {
    FragmentLockedApplicationBinding binding;
    ArrayList<String> listLocked = new ArrayList<>();
    ArrayList<AppDetails> listLockedApp = new ArrayList<>();
    LockPackageDatabase lockPackageDatabase;
    AppLockerAdapter appLockerAdapter;
    RecyclerView.LayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLockedApplicationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        lockPackageDatabase = new LockPackageDatabase(requireContext());
        LoadLockedApplication();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadLockedApplication() {
        listLockedApp.clear();
        if (listLocked.size() > 0) {
            listLocked.clear();
            Cursor cursor = lockPackageDatabase.getAllData();

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    listLocked.add(cursor.getString(1));
                    cursor.moveToNext();
                }
            }
        } else {
            Cursor cursor = lockPackageDatabase.getAllData();

            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    listLocked.add(cursor.getString(1));
                    cursor.moveToNext();
                }
            }
        }

        for (int i = 0; i < listLocked.size(); i++) {
            PackageManager mPackageManager = requireActivity().getPackageManager();
            Drawable icon;
            try {
                icon = mPackageManager.getApplicationIcon(listLocked.get(i));
                String appName = (String) mPackageManager.getApplicationLabel(mPackageManager.getApplicationInfo(listLocked.get(i), PackageManager.GET_META_DATA));
                listLockedApp.add(new AppDetails(appName, icon, listLocked.get(i)));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listLockedApp.sort(Comparator.comparing(app -> app.appName));
        }

        binding.lockedRecycler.setLayoutManager(manager);
        appLockerAdapter = new AppLockerAdapter(requireContext(), listLockedApp);
        binding.lockedRecycler.setAdapter(appLockerAdapter);
        appLockerAdapter.SetOnItemClickListener((view, position) -> {
            AppDetails appDetails = listLockedApp.get(position);
            boolean isLockedPackage = lockPackageDatabase.getPackageName(appDetails.getPackageName());
            if (isLockedPackage) {
                lockPackageDatabase.delete(appDetails.getPackageName());
            }
            Toast.makeText(requireActivity(), appDetails.appName + " removed", Toast.LENGTH_SHORT).show();
            listLockedApp.remove(listLockedApp.get(position));
            appLockerAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadLockedApplication();
    }
}
