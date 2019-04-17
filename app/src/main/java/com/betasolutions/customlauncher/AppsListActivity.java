package com.betasolutions.customlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppsListActivity extends Activity {
    private PackageManager manager;
    private List<AppDetail> apps;
    private GridView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        loadApps();
        loadListView();
        addClickListener();
    }

    private boolean isGoogleApp (ResolveInfo ri) {
        return ri.activityInfo.packageName.contains("chrome")
                || ri.activityInfo.packageName.contains("google")
                || ri.activityInfo.packageName.contains("com.android.vending");
    }

    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            if (!isGoogleApp(ri)) {
                AppDetail app = new AppDetail();
                app.label = ri.loadLabel(manager);
                app.name = ri.activityInfo.packageName;
                app.icon = ri.activityInfo.loadIcon(manager);
                apps.add(app);
            }
        }
    }

    private void loadListView() {
        list = findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.list_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appLabel = convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    private void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                AppsListActivity.this.startActivity(i);
            }
        });
    }

    private void restartActivity() {
        Intent intent = new Intent(this, AppsListActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onStop() {
        this.restartActivity();
        super.onStop();
    }
}
