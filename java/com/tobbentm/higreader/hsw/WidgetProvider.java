package com.tobbentm.higreader.hsw;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.tobbentm.higreader.MainActivity;
import com.tobbentm.higreader.R;

/**
 * Created by Tobias on 03.02.14.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views;
            views = updateWidgetListView(context, appWidgetId);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent penIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.widget_list, penIntent);

        remoteViews.setRemoteAdapter(R.id.widget_list, svcIntent);
        remoteViews.setEmptyView(R.id.widget_list, R.id.widget_error_tv);
        return remoteViews;
    }
}
