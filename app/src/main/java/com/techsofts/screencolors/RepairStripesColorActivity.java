package com.techsofts.screencolors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

public class RepairStripesColorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    Spinner spin,spinnerspeed;
    TextView txt1,txt2,txt3,txtInstr;
    EditText edtText;
    Button btnBegin;
    CheckBox chkAssistant;


    String kindTime,stxtInstr;
    RelativeLayout lytMain;

    String [] colors;

    int totTime, INTERVAL = 1000;

    private FrameLayout adContainerView;
    private AdManagerAdView mAdView;


    private InterstitialAd mInterstitialAd = null;

    Window window;
    ActionBar actionBar;

    BackgroundDrawableColor bg;
    private ScheduledExecutorService timer;

    private int speedStripes;

    public void ComponentsInvisibility(){

        spin.setVisibility(View.INVISIBLE);
        //spinnerspeed.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        //txt3.setVisibility(View.INVISIBLE);
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
        setContentView(R.layout.activity_repair_stripes_color);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });


        // Prepare the Interstitial Ad
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
        });

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

        setArrayColors();

        spin = (Spinner)findViewById(R.id.spinner);
        //spinnerspeed = (Spinner)findViewById(R.id.spinnerspeed);
        edtText = (EditText)findViewById(R.id.edtText);
        txt1 = (TextView)findViewById(R.id.txtText1);
        txt2 = (TextView)findViewById(R.id.txtText2);
        //txt3 = (TextView)findViewById(R.id.txtText3);
        btnBegin = (Button)findViewById(R.id.btnBegin);
        lytMain = (RelativeLayout) findViewById(R.id.lytMain);
        txtInstr = (TextView)findViewById(R.id.txtInstr);
        chkAssistant = (CheckBox)findViewById(R.id.chkAssistant);

        stxtInstr = "1.- Before start repairing please ensure to put screen brightness at full.\n" +
                "2.- To finish repairing mode just bring back Navigation Bar and press back button.\n" +
                "3.- ScreenColors assistant will notify you when the tool finishes(if enabled).\n" +
                "4.- It's needed to leave the screen turned on.\n" +
                "5.- Select a desired speed for stripes(it's up to you).\n" +
                "6.- The screen will show color stripes, the device must not be used in this process.\n\n" +
                "Important Message: " + "This process does not ensure your screen will be repaired it depends how much damage has the screen.\n" +
                "The use of this application is under risk of the own user, the developer doesn't make responsible "+
                "if the device gets more damage.\n";

        txtInstr.setText(stxtInstr);

        gradientBackground(lytMain);


        btnBegin.setOnClickListener(this);
        lytMain.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minuteshours, R.layout.spinner_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.speedstripes, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.spinner_item);


        spin.setAdapter(adapter);
        //spinnerspeed.setAdapter(adapter2);

        spin.setOnItemSelectedListener(this);
        //spinnerspeed.setOnItemSelectedListener(this);

        setFullScreen();

        bg = new BackgroundDrawableColor();

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

    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
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

            /*case R.id.spinnerspeed:

                speedStripes = Integer.parseInt((String) parent.getItemAtPosition(position));

                break;*/
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
                        bg.setSpeedStripe(100);
                        bg.start();
                    }

                    if (kindTime.equals("Minutes")) {
                        totTime = ((Integer.parseInt(edtText.getText().toString())) * 60) * 1000;
                        scheduleFinish(totTime);
                        lytMain.setBackground(bg);
                        bg.setSpeedStripe(100);
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
                Intent intent2 = new Intent(RepairStripesColorActivity.this, BeginActivity.class);
                intent2.putExtra("FromRepair", "1");
                intent2.putExtra("Assistant", chkAssistant.isChecked());
                RepairStripesColorActivity.this.startActivity(intent2);
                RepairStripesColorActivity.this.finish();
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

    public void setArrayColors(){
        colors = new String[] {"#000000",
                "#1B1811",
                "#16161D",
                "#013220",
                "#1B1B1B",
                "#1A2421",
                "#3D0C02",
                "#004225",
                "#014421",
                "#232B2B",
                "#301934",
                "#004B49",
                "#3C1414",
                "#064E40",
                "#00563F",
                "#3D2B1F",
                "#1B4D3E",
                "#1B4D3E",
                "#006400",
                "#660000",
                "#3B2F2F",
                "#4B3621",
                "#58111A",
                "#006A4E",
                "#006B3C",
                "#3B3C36",
                "#00703C",
                "#4B5320",
                "#483C32",
                "#592720",
                "#7B3F00",
                "#2F4F4F",
                "#4A5D23",
                "#7C0A02",
                "#008000",
                "#800020",
                "#36454F",
                "#654321",
                "#665D1E",
                "#568203",
                "#543D37",
                "#6C541E",
                "#177245",
                "#8B0000",
                "#008B8B",
                "#8B008B",
                "#5D3954",
                "#7F1734",
                "#126180",
                "#88540B",
                "#960018",
                "#563C5C",
                "#556B2F",
                "#702963",
                "#4D1A7F",
                "#801818",
                "#534B4F",
                "#841B2D",
                "#00009C",
                "#614051",
                "#007AA5",
                "#A2006D",
                "#555555",
                "#4D5D53",
                "#6F4E37",
                "#703642",
                "#87421F",
                "#007BA7",
                "#007BA7",
                "#0018A8",
                "#555D50",
                "#8A3324",
                "#228B22",
                "#967117",
                "#967117",
                "#0047AB",
                "#0093AF",
                "#79443B",
                "#6C3082",
                "#2F847C",
                "#26428B",
                "#4A646C",
                "#3B7A57",
                "#2E2D88",
                "#9E1B32",
                "#1034A6",
                "#8DB600",
                "#4F7942",
                "#58427C",
                "#81613C",
                "#0048BA",
                "#0072BB",
                "#0087BD",
                "#54626F",
                "#2E5894",
                "#9C2542",
                "#B8860B",
                "#03C03C",
                "#536872",
                "#87413F",
                "#893F45",
                "#483D8B",
                "#9FA91F",
                "#AA381E",
                "#0D98BA",
                "#536878",
                "#954535",
                "#333399",
                "#9F2B68",
                "#B31B1B",
                "#B31B1B",
                "#00CC99",
                "#CC5500",
                "#696969",
                "#856D4D",
                "#A52A2A",
                "#AB274F",
                "#1560BD",
                "#00CED1",
                "#9400D3",
                "#B22222",
                "#2243B6",
                "#C46210",
                "#8806CE",
                "#D70040",
                "#7E5E60",
                "#B0BF1A",
                "#C41E3A",
                "#B53389",
                "#2A52BE",
                "#C32148",
                "#E30022",
                "#E48400",
                "#856088",
                "#915C83",
                "#B87333",
                "#1974D2",
                "#CAE00D",
                "#00B7EB",
                "#86608E",
                "#BD33A4",
                "#CE2029",
                "#D2691E",
                "#D2691E",
                "#D71868",
                "#DC143C",
                "#E4D00A",
                "#5072A7",
                "#AB4B52",
                "#C72C48",
                "#246BCE",
                "#D3212D",
                "#1DACD6",
                "#DA1884",
                "#F400A1",
                "#A8516E",
                "#AF6E4D",
                "#666699",
                "#996666",
                "#5F9EA0",
                "#A67B5B",
                "#A67B5B",
                "#B94E48",
                "#A4C639",
                "#9932CC",
                "#CD7F32",
                "#0247FE",
                "#00BFFF",
                "#00BFFF",
                "#0000FF"};
    }
}

class BackgroundDrawableColor extends Drawable implements Runnable, Animatable {
    private static final long FRAME_DELAY = 1000 / 60;
    private boolean mRunning = false;
    private long mStartTime;

    //private int mDuration = 500;
    private int speedStripe;

    private Paint mPaint;
    private int mStripes = 8;

    public int getSpeedStripe() {
        return speedStripe;
    }

    public void setSpeedStripe(int speedStripe) {
        this.speedStripe = speedStripe;
    }

    private int [] colorStripes = new int[]{ Color.BLACK, Color.WHITE,Color.BLUE,Color.CYAN,Color.GRAY,Color.RED,Color.YELLOW,Color.MAGENTA, Color.DKGRAY, Color.GREEN, Color.LTGRAY};

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

            Random randomColor = new Random();

            for (int i = 0; i < mStripes * 2 + 2; i++) {
                int color1 = randomColor.nextInt(colorStripes.length);

                mPaint.setColor(colorStripes[color1]);

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