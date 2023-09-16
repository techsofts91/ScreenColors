package com.techsofts.screencolors;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BeginActivity extends Activity implements View.OnClickListener {

    ImageButton turnOffApp,btnInfo;
    Button btnFollowTwitter;
    CardView cardTest,cardRepair,cardRepairBW,cardRepairStripesBW,cardRepairStripesColor,cardRepairGradient;
    Button btnPremium,btnMoreApps;

    private FrameLayout adContainerView;
    private AdManagerAdView mAdView;
    ConstraintLayout lytMain;
    String [] colors;
    int result;
    private TextToSpeech txtSpeech;
    String vfromRepair,vToSpeech;
    AudioManager audioManager;
    int vMaxVolume = 0;
    private MediaPlayer reproductor;
    Boolean vfromRepair2;

    Window window;
    ActionBar actionBar;
    private PopupWindow popupWindow;

    SharedPreferences seenRewardedAd;
    private String PREFS_SEEN_REWAD = "SeenAD";

    private RewardedAd rewardedAd;


    @Override
    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

        setFullScreen();

        //gradientBackground(lytMain);
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public boolean changeScreenBrightness(Context context, int screenBrightnessValue)
    {
        try {
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);


            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    screenBrightnessValue);
            return true;
        }
        catch (Exception e) {
            //Log.e("Screen Brightness", "error changing screen brightness");
            return false;
        }
    }

    public void textToSpeech(String text) {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                txtSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                txtSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
    }

    public void gradientBackground(View view){

        GradientDrawable gd = new GradientDrawable();
        Random randomColor = new Random();
        int color1 = randomColor.nextInt(155);
        String arrayColor1 = colors[color1];
        int color2 = randomColor.nextInt(155);
        String arrayColor2 = colors[color2];

        // Set the color array to draw gradient
        gd.setColors(new int[]{
                Color.parseColor(arrayColor1),
                Color.parseColor(arrayColor2)
        });
        // Set GradientDrawable shape is a rectangle
        gd.setShape(GradientDrawable.RECTANGLE);

        view.setBackground(gd);
    }

    private void setFullScreen() {

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        actionBar = getActionBar();
       // actionBar.hide();
        // or add <item name="android:windowTranslucentStatus">true</item> in the theme
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setFullScreenNotch(window, actionBar);
    }

    void setFullScreenNotch(Window window, ActionBar actionBar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            //actionBar.hide();
        }
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    private Date formatStringToDate(String stringDate){
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return date;
    }

    void registerRewardedAd(){
        SharedPreferences settings = this.getSharedPreferences(PREFS_SEEN_REWAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("seen_rewardedad",true);
        editor.putString("click_time", getCurrentTimeStamp());
        editor.commit();
    }

    private int getElapsedHours(){
        String previous = seenRewardedAd.getString("click_time", "");
        String current = getCurrentTimeStamp();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //long diff = current.getTime() - previous.getTime();
        //long minutes = diff / (60 * 1000);
        return getHoursDifference(formatStringToDate(previous),formatStringToDate(current));
    }

    private int getElapsedSeconds(){
        String previous = seenRewardedAd.getString("click_time", "");
        String current = getCurrentTimeStamp();
        //long diff = current.getTime() - previous.getTime();
        //long minutes = diff / (60 * 1000);
        return getSecondsDifference(formatStringToDate(previous),formatStringToDate(current));
    }

    public static int getHoursDifference(Date fromDate,Date toDate)
    {
        int secondsInMilli = 1000;
        int minutesInMilli = secondsInMilli * 60;
        int hoursInMilli = minutesInMilli * 60;

        if(fromDate==null||toDate==null)
            return 0;

        int difference = (int) ((toDate.getTime() - fromDate.getTime()));

        int elapsedHours = difference / hoursInMilli;

        return elapsedHours;
    }

    public static int getSecondsDifference(Date fromDate,Date toDate)
    {
        int secondsInMilli = 1000;
        int minutesInMilli = secondsInMilli * 60;
        int hoursInMilli = minutesInMilli * 60;

        if(fromDate==null||toDate==null)
            return 0;

        int difference = (int) ((toDate.getTime() - fromDate.getTime()));

        int elapsedSeconds = difference / secondsInMilli;

        return elapsedSeconds;
    }

    private void alertDialogRewardeAd(){
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup, null);


        popupWindow = new PopupWindow(popupView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT,
                true);

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        ((ImageButton) popupView.findViewById(R.id.adView))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            showRewardedAd();
                    }
                });

        ((ImageButton) popupView.findViewById(R.id.closepopupbutton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                    }
                });
    }

    private void alertDialogTurnOffApp(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Turn off Screen Colors?");
        //alert.setMessage("For security purposes after repairing tool finishes is necessary to give system permissions to ScreenColors.");

        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                System.exit(0);
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void showRewardedAd() {
        if(rewardedAd != null){
            rewardedAd.show(this,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            registerRewardedAd();
                        }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        seenRewardedAd = this.getSharedPreferences(PREFS_SEEN_REWAD,Context.MODE_PRIVATE);

        //Block for Banner Ad
        adContainerView = findViewById(R.id.ad_view_container);
        mAdView = new AdManagerAdView(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(getString(R.string.home_banner));
        adContainerView.addView(mAdView);
        mAdView.loadAd(adRequest);

        //Validate if a rewardedad has been seen previously
        if(!seenRewardedAd.getBoolean("seen_rewardedad",false) || getElapsedHours() >= 1) {
            loadRewardedAd();
            if(getElapsedHours() >= 1){
                SharedPreferences settings = this.getSharedPreferences(PREFS_SEEN_REWAD, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("seen_rewardedad",false);
                editor.putString("click_time", null);
                editor.commit();
            }
        }

        window = this.getWindow();
        setFullScreen();

        setArrayColors();
        lytMain = findViewById(R.id.lytMain);
        turnOffApp = findViewById(R.id.turnOffApp);
        cardTest = findViewById(R.id.cardTScreen);
        cardRepair = findViewById(R.id.cardRepairMultiColor);
        cardRepairBW = findViewById(R.id.cardRepairBW);
        cardRepairStripesBW = findViewById(R.id.cardRepairStripesBW);
        cardRepairStripesColor = findViewById(R.id.cardRepairStripesColor);
        cardRepairGradient = findViewById(R.id.cardRepairGradientColor);
        btnPremium = findViewById(R.id.btnPremium);
        btnMoreApps = findViewById(R.id.btnMoreApss);
        btnInfo = findViewById(R.id.btnInfo);
        btnFollowTwitter = findViewById(R.id.btnFollowTwitter);

        turnOffApp.setOnClickListener(this);
        cardTest.setOnClickListener(this);
        cardRepair.setOnClickListener(this);
        cardRepairBW.setOnClickListener(this);
        cardRepairStripesBW.setOnClickListener(this);
        cardRepairStripesColor.setOnClickListener(this);
        cardRepairGradient.setOnClickListener(this);
        btnPremium.setOnClickListener(this);
        btnMoreApps.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnFollowTwitter.setOnClickListener(this);

        gradientBackground(lytMain);


        Intent intentfromRepair = getIntent();
        vfromRepair = intentfromRepair.getStringExtra("FromRepair");
        vfromRepair2 = intentfromRepair.getBooleanExtra("Assistant",false);
        if(vfromRepair == null){
            vfromRepair = "0";
        }

        if (vfromRepair.equals("1") && vfromRepair2){
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            vMaxVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vMaxVolume, 0);
            //playFinishSound();
            vToSpeech = getResources().getString(R.string.speech_finished);
            changeScreenBrightness(this,1);

                txtSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            result = txtSpeech.setLanguage(Locale.US);
                            if (result == TextToSpeech.LANG_MISSING_DATA | result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                result = txtSpeech.setLanguage(Locale.ENGLISH);
                            }

                            if (result == TextToSpeech.LANG_MISSING_DATA | result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                result = txtSpeech.setLanguage(Locale.UK);
                            }
                            textToSpeech(vToSpeech);
                        }
                    }
                });
         }

      /*  Context context = getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Check whether has the write settings permission or not.
            boolean settingsCanWrite = Settings.System.canWrite(context);
            if (!settingsCanWrite) {

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Advice");
                alert.setMessage("For security purposes after repairing tool finishes is necessary to give system permissions to ScreenColors.");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // If do not have write settings permission then open the Can modify system settings panel.
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        startActivity(intent);

                    }
                });

               /* alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });*/

               // alert.show();

           // }
        //}
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void loadRewardedAd() {
        final FullScreenContentCallback fullScreenContentCallback =
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Code to be invoked when the ad showed full screen content.
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd = null;
                        popupWindow.dismiss();
                        // Code to be invoked when the ad dismissed full screen content.
                    }
                };

        RewardedAd.load(
                this,
                getResources().getString(R.string.rewarded_ad),
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback(){
                    public void onAdLoaded(RewardedAd ad){
                        rewardedAd = ad;
                        rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.turnOffApp:

                alertDialogTurnOffApp();
                break;
            case R.id.cardTScreen:
                Intent intent = new Intent(BeginActivity.this,MainActivity.class);
                this.startActivity(intent);
                break;
            case R.id.cardRepairMultiColor:
                Intent intent2 = new Intent(BeginActivity.this,RepairActivity.class);
                this.startActivity(intent2);
                finish();
                break;
            case R.id.cardRepairBW:
                Intent intent3 = new Intent(BeginActivity.this,RepairBWActivity.class);
                this.startActivity(intent3);
                finish();
                break;
            case R.id.cardRepairStripesBW:

                if (!seenRewardedAd.getBoolean("seen_rewardedad", false)){
                    alertDialogRewardeAd();
                }else {
                    Intent intent4 = new Intent(BeginActivity.this, RepairStripesBWActivity.class);
                    this.startActivity(intent4);
                    finish();
                }
                break;
            case R.id.cardRepairStripesColor:
                Intent intent5 = new Intent(BeginActivity.this,RepairStripesColorActivity.class);
                this.startActivity(intent5);
                finish();
                break;
                case R.id.cardRepairGradientColor:
                Intent intent6 = new Intent(BeginActivity.this,RepairGradient.class);
                this.startActivity(intent6);
                finish();
                break;
           case R.id.btnPremium:
                Intent intent7 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techsofts.screencolorspremium"));
                startActivity(intent7);
                break;
            case R.id.btnMoreApss:
                Intent intent8 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=TechSofts"));
                startActivity(intent8);
                break;
            case R.id.btnInfo:
                Intent intent9 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.screencolorsapp.com/"));
                startActivity(intent9);
                break;
            case R.id.btnFollowTwitter:
                Intent intent10 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ScreenColorsApp/"));
                startActivity(intent10);
                break;
        }
    }

    public void setArrayColors(){
        colors = new String[] {"#000027",
                "#00004b",
                "#00006f",
                "#000093",
                "#0000b7",
                "#0000db",
                "#001427",
                "#00264b",
                "#002700",
                "#002714",
                "#002727",
                "#00386f",
                "#004a93",
                "#004b00",
                "#004b26",
                "#004b4b",
                "#005cb7",
                "#006edb",
                "#006f00",
                "#006f38",
                "#006f6f",
                "#009300",
                "#00934a",
                "#009393",
                "#00b700",
                "#00b75c",
                "#00b7b7",
                "#00db00",
                "#00db6e",
                "#00dbdb",
                "#140027",
                "#141414",
                "#142700",
                "#2424ff",
                "#2492ff",
                "#24ff24",
                "#24ff92",
                "#24ffff",
                "#26004b",
                "#262626",
                "#264b00",
                "#270000",
                "#270014",
                "#270027",
                "#271400",
                "#272700",
                "#38006f",
                "#383838",
                "#386f00",
                "#4848ff",
                "#48a4ff",
                "#48ff48",
                "#48ffa4",
                "#48ffff",
                "#4a0093",
                "#4a4a4a",
                "#4a9300",
                "#4b0000",
                "#4b0026",
                "#4b004b",
                "#4b2600",
                "#4b4b00",
                "#5c00b7",
                "#5c5c5c",
                "#5cb700",
                "#6c6cff",
                "#6cb6ff",
                "#6cff6c",
                "#6cffb6",
                "#6cffff",
                "#6e00db",
                "#6e6e6e",
                "#6edb00",
                "#6f0000",
                "#6f0038",
                "#6f006f",
                "#6f3800",
                "#6f6f00",
                "#9090ff",
                "#90c8ff",
                "#90ff90",
                "#90ffc8",
                "#90ffff",
                "#919191",
                "#9224ff",
                "#92ff24",
                "#930000",
                "#93004a",
                "#930093",
                "#934a00",
                "#939300",
                "#a2a2a2",
                "#a448ff",
                "#a4ff48",
                "#b3b3b3",
                "#b4b4ff",
                "#b4daff",
                "#b4ffb4",
                "#b4ffda",
                "#b4ffff",
                "#b66cff",
                "#b6ff6c",
                "#b70000",
                "#b7005c",
                "#b700b7",
                "#b75c00",
                "#b7b700",
                "#c4c4c4",
                "#c890ff",
                "#c8ff90",
                "#d5d5d5",
                "#d8d8ff",
                "#d8ecff",
                "#d8ffd8",
                "#d8ffec",
                "#d8ffff",
                "#dab4ff",
                "#daffb4",
                "#db0000",
                "#db006e",
                "#db00db",
                "#db6e00",
                "#dbdb00",
                "#e6e6e6",
                "#ecd8ff",
                "#ecffd8",
                "#ff2424",
                "#ff2492",
                "#ff24ff",
                "#ff4848",
                "#ff48a4",
                "#ff48ff",
                "#ff6c6c",
                "#ff6cb6",
                "#ff6cff",
                "#ff9090",
                "#ff90c8",
                "#ff90ff",
                "#ff9224",
                "#ffa448",
                "#ffb4b4",
                "#ffb4da",
                "#ffb4ff",
                "#ffb66c",
                "#ffc890",
                "#ffd8d8",
                "#ffd8ec",
                "#ffd8ff",
                "#ffdab4",
                "#ffecd8",
                "#ffff24",
                "#ffff48",
                "#ffff6c",
                "#ffff90",
                "#ffffb4",
                "#ffffd8"};
    }
}
