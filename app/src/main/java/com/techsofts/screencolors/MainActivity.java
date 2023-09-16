package com.techsofts.screencolors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.BaseAdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btnRed,btnBlue,btnYellow,btnOrange,btnPurple,btnGreen,btnBlack,btnWhite;

    private CheckBox chkbSound;

    private RelativeLayout lytMain;

    private TextToSpeech txtSpeech;

    private boolean isTTready;

    private FrameLayout adContainerView;
    private AdManagerAdView mAdView;

    int result;

    private InterstitialAd mInterstitialAd = null;

    Window window;
    ActionBar actionBar;

    public void mainBgd(){

        lytMain.setBackgroundColor(Color.parseColor("#808080"));
        btnBlue.setTextColor(Color.BLUE);
        btnRed.setTextColor(Color.RED);
        btnYellow.setTextColor(Color.YELLOW);
        btnOrange.setTextColor(Color.parseColor("#FFA500"));
        btnPurple.setTextColor(Color.parseColor("#800080"));
        btnGreen.setTextColor(Color.GREEN);
        btnBlack.setTextColor(Color.BLACK);
        btnWhite.setTextColor(Color.WHITE);

        //changing statusbar color
       /* if (android.os.Build.VERSION.SDK_INT >= 21) {
           // Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#808080"));

            View mview = findViewById(R.id.lytMain);
            mview.setSystemUiVisibility(0);
        }*/

        btnBlue.setVisibility(View.VISIBLE);
        btnRed.setVisibility(View.VISIBLE);
        btnYellow.setVisibility(View.VISIBLE);
        btnOrange.setVisibility(View.VISIBLE);
        btnPurple.setVisibility(View.VISIBLE);
        btnGreen.setVisibility(View.VISIBLE);
        btnBlack.setVisibility(View.VISIBLE);
        btnWhite.setVisibility(View.VISIBLE);
    }

    public void DarkNotifBar(){
        View mview = findViewById(R.id.lytMain);
        if (mview != null) {
            mview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void adViewInvisibility(){

        mAdView.setVisibility(View.INVISIBLE);
    }

    public void textToSpeech(String text){
        if (isTTready && chkbSound.isChecked()) {
            if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                txtSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                txtSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }else{

            btnBlue.setVisibility(View.INVISIBLE);
            btnRed.setVisibility(View.INVISIBLE);
            btnYellow.setVisibility(View.INVISIBLE);
            btnOrange.setVisibility(View.INVISIBLE);
            btnPurple.setVisibility(View.INVISIBLE);
            btnGreen.setVisibility(View.INVISIBLE);
            btnBlack.setVisibility(View.INVISIBLE);
            btnWhite.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onPause() {
        if(txtSpeech !=null){
            txtSpeech.stop();
            txtSpeech.shutdown();
        }
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

    void setFullScreenNotch(Window window, ActionBar actionBar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            actionBar.hide();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        setFullScreen();

        btnRed = (Button) findViewById(R.id.btnRed);
        btnBlue= (Button) findViewById(R.id.btnBlue);
        btnYellow = (Button) findViewById(R.id.btnYellow);
        btnOrange = (Button)findViewById(R.id.btnOrange);
        btnPurple = (Button)findViewById(R.id.btnPurple);
        btnGreen = (Button) findViewById(R.id.btnGreen);
        btnBlack = (Button) findViewById(R.id.btnBlack);
        btnWhite = (Button) findViewById(R.id.btnWhite);
        lytMain = (RelativeLayout) findViewById(R.id.lytMain);
        chkbSound = (CheckBox)findViewById(R.id.chkbSound);


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_buttons);
        chkbSound.startAnimation(animation);
        btnBlack.startAnimation(animation);
        btnBlue.startAnimation(animation);
        btnRed.startAnimation(animation);
        btnYellow.startAnimation(animation);
        btnOrange.startAnimation(animation);
        btnPurple.startAnimation(animation);
        btnGreen.startAnimation(animation);
        btnWhite.startAnimation(animation);


        btnRed.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btnYellow.setOnClickListener(this);
        btnOrange.setOnClickListener(this);
        btnPurple.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        btnBlack.setOnClickListener(this);
        btnWhite.setOnClickListener(this);
        lytMain.setOnClickListener(this);

        setFullScreen();

        txtSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if(status == TextToSpeech.SUCCESS){
                    isTTready = true;
                    result = txtSpeech.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA | result == TextToSpeech.LANG_NOT_SUPPORTED){
                        result = txtSpeech.setLanguage(Locale.ENGLISH);
                        /*Toast.makeText(getApplicationContext(), "Language is missing",
                                Toast.LENGTH_LONG).show();*/
                    }

                    if(result == TextToSpeech.LANG_MISSING_DATA | result == TextToSpeech.LANG_NOT_SUPPORTED){
                        result = txtSpeech.setLanguage(Locale.UK);
                        /*Toast.makeText(getApplicationContext(), "Language is missing",
                                Toast.LENGTH_LONG).show();*/
                    }
                }
            }
        });
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

    private void setFullScreen() {

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        actionBar = getSupportActionBar();
        actionBar.hide();
        // or add <item name="android:windowTranslucentStatus">true</item> in the theme
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setFullScreenNotch(window, actionBar);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.btnBlue:

			    lytMain.setBackgroundColor(Color.BLUE);
                btnBlue.setTextColor(Color.WHITE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                //changing statusbar color
                setFullScreen();

                adViewInvisibility();
                chkbSound.setVisibility(View.INVISIBLE);

                textToSpeech(btnBlue.getText().toString());
                break;

            case R.id.btnRed:
                lytMain.setBackgroundColor(Color.RED);
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setTextColor(Color.WHITE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnRed.getText().toString());

                break;
            case R.id.btnYellow:
                lytMain.setBackgroundColor(Color.YELLOW);
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setTextColor(Color.BLACK);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnYellow.getText().toString());

                break;
            case R.id.btnOrange:
                lytMain.setBackgroundColor(Color.parseColor("#FFA500"));
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setTextColor(Color.WHITE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnOrange.getText().toString());

               break;
            case R.id.btnPurple:
                lytMain.setBackgroundColor(Color.parseColor("#800080"));
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setTextColor(Color.WHITE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnPurple.getText().toString());

                break;
            case R.id.btnGreen:
                lytMain.setBackgroundColor(Color.GREEN);
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setTextColor(Color.BLACK);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnGreen.getText().toString());

                break;
            case R.id.btnBlack:
                lytMain.setBackgroundColor(Color.BLACK);
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setTextColor(Color.WHITE);
                btnWhite.setVisibility(View.INVISIBLE);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnBlack.getText().toString());

                break;
            case R.id.btnWhite:
                lytMain.setBackgroundColor(Color.WHITE);
                btnBlue.setVisibility(View.INVISIBLE);
                btnRed.setVisibility(View.INVISIBLE);
                btnYellow.setVisibility(View.INVISIBLE);
                btnOrange.setVisibility(View.INVISIBLE);
                btnPurple.setVisibility(View.INVISIBLE);
                btnGreen.setVisibility(View.INVISIBLE);
                btnBlack.setVisibility(View.INVISIBLE);
                btnWhite.setTextColor(Color.BLACK);

                setFullScreen();

                adViewInvisibility();

                chkbSound.setVisibility(View.INVISIBLE);
                textToSpeech(btnWhite.getText().toString());

                break;
            case R.id.lytMain:

                setFullScreen();

                mainBgd();

                chkbSound.setVisibility(View.VISIBLE);

                mAdView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void displayInterstitial() {
        // If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }
}

