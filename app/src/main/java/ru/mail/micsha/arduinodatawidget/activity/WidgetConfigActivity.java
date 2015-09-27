package ru.mail.micsha.arduinodatawidget.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Map;

import ru.mail.micsha.arduinodatawidget.R;
import ru.mail.micsha.arduinodatawidget.widget.ArduinoAppWidget;

public class WidgetConfigActivity extends AppCompatActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "ru.mail.micsha.arduinodatawidget.activity.WidgetConfigActivity";
    private static final String PREFS_CHECKBOX ="check";
    private EditText paramNameEditText;
    private CheckBox checkParamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        setContentView(R.layout.activity_widget_config);

        paramNameEditText = (EditText) findViewById(R.id.parameter_name_edittext);
        checkParamName = (CheckBox) findViewById(R.id.parameter_name_checked);
        String paramName = loadPrefName(this, appWidgetId);
        if (paramName != null) {
            paramNameEditText.setText(paramName);
            checkParamName.setChecked(isCheckedPref(this, paramName));
        }

        Button generalOptionButton = (Button) findViewById(R.id.general_options_button);
        generalOptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WidgetConfigActivity.this, GeneralOptionsActivity.class);
                startActivity(intent);
            }
        });

        Button updateWidgetButton = (Button) findViewById(R.id.add_widget_button);
        updateWidgetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefName(WidgetConfigActivity.this, appWidgetId, paramNameEditText.getText()
                        .toString());
                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(WidgetConfigActivity.this);
                ComponentName thisAppWidget = new ComponentName(WidgetConfigActivity.this
                        .getPackageName(), ArduinoAppWidget.class.getName());

                Intent updateIntent = new Intent(WidgetConfigActivity.this, ArduinoAppWidget.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                WidgetConfigActivity.this.sendBroadcast(updateIntent);
                finish();
            }
        });
    }

    private void savePrefName(Context context, int appWidgetId, String prefName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREFS_CHECKBOX + prefName, checkParamName.isChecked());
        prefs.remove(loadPrefName(context, appWidgetId));
        prefs.putInt(prefName, appWidgetId);
        prefs.commit();
    }

    public static String loadPrefName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Map<String, ?> mapAll = prefs.getAll();
        for(Map.Entry<String,?> entry : mapAll.entrySet()){
            if(entry.getValue().toString().equalsIgnoreCase(Integer.toString(appWidgetId))){
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isCheckedPref(Context context, String prefName){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREFS_CHECKBOX + prefName, false);
    }

    public static String getFontPath(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String path = prefs.getString(GeneralOptionsActivity.PREFS_FONTPATH, null);
        return path;
    }

    public static int getTextSize(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int size = prefs.getInt(GeneralOptionsActivity.PREFS_TEXTSIZE, 12);
        return size;
    }

    public static int getTextColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int color = prefs.getInt(GeneralOptionsActivity.PREFS_TEXTCOLOR, 0);
        return color;
    }

    public static int getBackgroundColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int color = prefs.getInt(GeneralOptionsActivity.PREFS_BACKCOLOR, 0);
        return color;
    }

    public static Bitmap buildUpdate(Context context, String text, String path) {
        int fontSizePX = convertDiptoPix(context, getTextSize(context));
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface fontStyle = Typeface.createFromFile(path);
        paint.setAntiAlias(true);
        paint.setTypeface(fontStyle);
        paint.setColor(getTextColor(context));
        paint.setTextSize(fontSizePX);
        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);
        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
}
