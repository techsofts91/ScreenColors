package com.techsofts.screencolors;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

public class RepairStripesBWActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    Spinner spin,spinnerspeed;
    TextView txt1,txt2,txt3,txtInstr;
    EditText edtText;
    Button btnBegin;
    CheckBox chkAssistant;


    String kindTime,stxtInstr;
    RelativeLayout lytMain;

    int totTime, INTERVAL = 1000;

    private FrameLayout adContainerView;
    private AdManagerAdView mAdView;

    private InterstitialAd mInterstitialAd = null;


    Window window;
    ActionBar actionBar;

    BackgroundDrawable bg;
    private ScheduledExecutorService timer;

    private int speedStripes;

    public void ComponentsInvisibility(){

        spin.setVisibility(View.INVISIBLE);
        spinnerspeed.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        txt3.setVisibility(View.INVISIBLE);
        edtText.setVisibility(View.INVISIBLE);
        btnBegin.setVisibility(View.INVISIBLE);
        mAdView.setVisibility(View.INVISIBLE);
        txtInstr.setVisibility(View.INVISIBLE);
        chkAssistant.setVisibility(View.INVISIBLE);
    }

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
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setFullScreen() {

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        actionBar = getSupportActionBar();
        actionBar.hide();
        // or add <item name="android:windowTranslucentStatus">true</item> in the theme
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setFullScreenNotch(window, actionBar);
    }

    void setFullScreenNotch(Window window, ActionBar actionBar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            actionBar.hide();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_stripes_b_w);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });


       /* // Prepare the Interstitial Ad
        mInterstitialAd.load(this, getResources().getString(R.string.interstitial_full_screen), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                // Call displayInterstitial() function
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                        setFullScreen();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });

                displayInterstitial();
                setFullScreen();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                mInterstitialAd = null;
            }
        });*/

        //Block for Banner Ad
        adContainerView = findViewById(R.id.ad_view_container);
        mAdView = new AdManagerAdView(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(getString(R.string.home_banner));
        adContainerView.addView(mAdView);
        mAdView.loadAd(adRequest);

        window = this.getWindow();

        spin = (Spinner)findViewById(R.id.spinner);
        spinnerspeed = (Spinner)findViewById(R.id.spinnerspeed);
        edtText = (EditText)findViewById(R.id.edtText);
        txt1 = (TextView)findViewById(R.id.txtText1);
        txt2 = (TextView)findViewById(R.id.txtText2);
        txt3 = (TextView)findViewById(R.id.txtText3);
        btnBegin = (Button)findViewById(R.id.btnBegin);
        lytMain = (RelativeLayout) findViewById(R.id.lytMain);
        txtInstr = (TextView)findViewById(R.id.txtInstr);
        chkAssistant = (CheckBox)findViewById(R.id.chkAssistant);

        stxtInstr = "1.- Before start repairing please ensure to put screen brightness at full.\n" +
                "2.- To finish repairing mode just bring back Navigation Bar and press back button.\n" +
                "3.- ScreenColors assistant will notify you when the tool finishes(if enabled).\n" +
                "4.- It's needed to leave the screen turned on.\n" +
                "5.- Select a desired speed for stripes(it's up to you).\n" +
                "6.- The screen will show black and white stripes, the device must not be used in this process.\n\n" +
                "Important Message: " + "This process does not ensure your screen will be repaired it depends how much damage has the screen.\n" +
                "The use of this application is under risk of the own user, the developer doesn't make responsible "+
                "if the device gets more damage.\n";

        txtInstr.setText(stxtInstr);

        lytMain.setBackgroundColor(Color.BLACK);

        btnBegin.setOnClickListener(this);
        lytMain.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minuteshours, R.layout.spinner_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.speedstripes, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.spinner_item);


        spin.setAdapter(adapter);
        spinnerspeed.setAdapter(adapter2);

        spin.setOnItemSelectedListener(this);
        spinnerspeed.setOnItemSelectedListener(this);

        setFullScreen();

        bg = new BackgroundDrawable();

    }

  /*  public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }*/

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {

            case R.id.spinner:

            String finalText;
            String text = getResources().getString(R.string.how_many);
            kindTime = (String) parent.getItemAtPosition(position);
            finalText = text + " " + kindTime;
            txt2.setText(finalText);
            setFullScreen();

            break;

            case R.id.spinnerspeed:

            speedStripes = Integer.parseInt((String) parent.getItemAtPosition(position));

            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnBegin:

                String textTime = edtText.getText().toString();
                if((textTime).equals("")) {

                    Toast.makeText(this, "Please insert the time",
                            Toast.LENGTH_LONG).show();

                }else{

                    ComponentsInvisibility();
                    setFullScreen();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtText.getWindowToken(), 0);

                    if (kindTime.equals("Hours")) {
                        totTime = ((Integer.parseInt(edtText.getText().toString())) * 3600) * 1000;
                        scheduleFinish(totTime);
                        lytMain.setBackground(bg);
                        bg.setSpeedStripe(speedStripes);
                        bg.start();
                    }

                    if (kindTime.equals("Minutes")) {
                        totTime = ((Integer.parseInt(edtText.getText().toString())) * 60) * 1000;
                        scheduleFinish(totTime);
                        lytMain.setBackground(bg);
                        bg.setSpeedStripe(speedStripes);
                        bg.start();
                    }
                }
                break;
        }
    }

    private void scheduleFinish(int totTime){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent2 = new Intent(RepairStripesBWActivity.this, BeginActivity.class);
                intent2.putExtra("FromRepair", "1");
                intent2.putExtra("Assistant", chkAssistant.isChecked());
                RepairStripesBWActivity.this.startActivity(intent2);
                RepairStripesBWActivity.this.finish();
            }
        }, totTime);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intentBack = new Intent(this,BeginActivity.class);
        startActivity(intentBack);
        finish();
    }
}

class BackgroundDrawable extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private boolean mRunning = false;
    private long mStartTime;

    //private int mDuration = 500;
    private int speedStripe;

    private Paint mPaint;
    private int mStripes = 7;

    public int getSpeedStripe() {
        return speedStripe;
    }

    public void setSpeedStripe(int speedStripe) {
        this.speedStripe = speedStripe;
    }

    private void init() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (isRunning()) {
            // animation in progress
            final int save = canvas.save();

            long timeDiff = SystemClock.uptimeMillis() - mStartTime;
            canvas.clipRect(bounds);

            float progress = ((float) timeDiff) / ((float) speedStripe); // 0..1

            float width = bounds.width() / (mStripes * 2);

            for (int i = 0; i < mStripes * 2 + 2; i++) {
                mPaint.setColor(i % 2 == 0 ? Color.BLACK : Color.WHITE);
                canvas.drawRect(bounds.left + width * (i - 1) + progress * 2 * width, bounds.top, bounds.left + width * i + progress * 2* width, bounds.bottom, mPaint);
            }

            canvas.restoreToCount(save);
        } else {
            // todo draw normal
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        init();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void start() {
        if (mRunning) stop();
        mRunning = true;
        mStartTime = SystemClock.uptimeMillis();
        invalidateSelf();
        scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DELAY);
    }

    @Override
    public void stop() {
        unscheduleSelf(this);
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void run() {
        invalidateSelf();
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis + FRAME_DELAY < mStartTime + speedStripe) {
            scheduleSelf(this, uptimeMillis + FRAME_DELAY);
        } else {
            mRunning = false;
            start();
        }
    }
}