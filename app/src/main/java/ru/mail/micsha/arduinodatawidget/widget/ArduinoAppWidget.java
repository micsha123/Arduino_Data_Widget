package ru.mail.micsha.arduinodatawidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import ru.mail.micsha.arduinodatawidget.R;
import ru.mail.micsha.arduinodatawidget.activity.WidgetConfigActivity;
import ru.mail.micsha.arduinodatawidget.service.BroadcastService;


public class ArduinoAppWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            Intent intent = new Intent(context, WidgetConfigActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.settings_button, pendingIntent);
            views.setInt(R.id.settings_button, "setBackgroundColor",
                    Color.TRANSPARENT);

            String prefName = WidgetConfigActivity.loadPrefName(context, appWidgetId);

            if(WidgetConfigActivity.isCheckedPref(context, prefName)){
                views.setViewVisibility(R.id.parameter_name_text, View.GONE);
                views.setViewVisibility(R.id.parameter_name_img, View.GONE);
            } else{
                views.setViewVisibility(R.id.parameter_name_text, View.VISIBLE);
                views.setViewVisibility(R.id.parameter_name_img, View.VISIBLE);
            }

            if (prefName != null) {
                if(WidgetConfigActivity.getFontPath(context) != null){
                    views.setViewVisibility(R.id.parameter_name_text, View.GONE);
                    views.setImageViewBitmap(R.id.parameter_name_img, WidgetConfigActivity
                            .buildUpdate(context, prefName, WidgetConfigActivity.getFontPath(context)));
                } else{
                    views.setViewVisibility(R.id.parameter_name_img, View.GONE);
                    views.setTextViewText(R.id.parameter_name_text, prefName);
                    views.setTextColor(R.id.parameter_name_text, WidgetConfigActivity.getTextColor(context));
                    views.setFloat(R.id.parameter_name_text, "setTextSize", WidgetConfigActivity.getTextSize(context));
                }
                views.setInt(R.id.widget_background, "setBackgroundColor",
                        WidgetConfigActivity.getBackgroundColor(context));
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        context.startService(new Intent(context, BroadcastService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

