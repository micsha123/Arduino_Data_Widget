package ru.mail.micsha.arduinodatawidget.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import ru.mail.micsha.arduinodatawidget.R;
import ru.mail.micsha.arduinodatawidget.activity.WidgetConfigActivity;

public class BroadcastService extends Service {
    private static final String ACTION = "org.kangaroo.rim.action.ACTION_DATA_RECEIVE";
    private static final String EXTRA_ARGS = "org.kangaroo.rim.device.EXTRA_ARGS";
    private static final String EXTRA_COMMAND = "org.kangaroo.rim.device.EXTRA_COMMAND";
    private static final String PREFS_NAME = "ru.mail.micsha.arduinodatawidget.activity.WidgetConfigActivity";
    final IntentFilter intentFilter = new IntentFilter(ACTION);
    WidgetReceiver widgetReceiver = new WidgetReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.intentFilter.addAction(ACTION);
        registerReceiver(widgetReceiver, intentFilter);
    }

    public class WidgetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences prefs = context.getSharedPreferences(BroadcastService.PREFS_NAME, 0);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            String command = intent.getStringExtra(EXTRA_COMMAND);
            String args = intent.getStringExtra(EXTRA_ARGS);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            int widgetID = prefs.getInt(command, 0);
            if(widgetID > 0){
                views.setViewVisibility(R.id.parameter_value_text, View.GONE);
                views.setViewVisibility(R.id.parameter_value_img, View.GONE);
                if(WidgetConfigActivity.getFontPath(context) != null){
                    views.setViewVisibility(R.id.parameter_value_img, View.VISIBLE);
                    views.setImageViewBitmap(R.id.parameter_value_img, WidgetConfigActivity
                            .buildUpdate(context, args, WidgetConfigActivity.getFontPath(context)));
                } else{
                    views.setViewVisibility(R.id.parameter_value_text, View.VISIBLE);
                    views.setTextViewText(R.id.parameter_value_text, args);
                    views.setTextColor(R.id.parameter_value_text, WidgetConfigActivity.getTextColor(context));
                    views.setFloat(R.id.parameter_value_text, "setTextSize", WidgetConfigActivity.getTextSize(context));
                }
                appWidgetManager.updateAppWidget(widgetID, views);
            }
        }
    }
}
