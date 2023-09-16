package com.techsofts.screencolors;

import androidx.appcompat.app.ActionBar;import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.ads.AdListener;
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
import com.techsofts.screencolors.R;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RepairActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    Spinner spin;
    TextView txt1,txt2,txtInstr;
    EditText edtText;
    Button btnBegin;
    CheckBox chkAssistant;


    String kindTime,stxtInstr;
    RelativeLayout lytMain;

    int totTime, INTERVAL = 1000;

    private FrameLayout adContainerView;
    private AdManagerAdView mAdView;

    Thread thread = new Thread();

    String colors [] = {"BLUE","RED","YELLOW","#FFA500","#800080","GREEN"};

    String color;
    int i = 0;

    private InterstitialAd mInterstitialAd = null;

    Window window;
    ActionBar actionBar;

    public void ComponentsInvisibility(){

        spin.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
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

    private void updateColor() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    lytMain.setBackgroundColor(Color.parseColor(color));
            }
        });

    }

    private void makeColors(final int Time) {

            thread = new Thread(new Runnable() {
                public void run() {
                    for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(Time); stop > System.nanoTime(); ) {
                        try {
                            Thread.sleep(INTERVAL);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        color = colors[i];
                        updateColor();


                        i = i + 1;
                        if(i == 6){
                            i = 0;
                        }
                    }
                    Intent intent2 = new Intent(RepairActivity.this,BeginActivity.class);
                    intent2.putExtra("FromRepair","1");
                    intent2.putExtra("Assistant",chkAssistant.isChecked());
                    RepairActivity.this.startActivity(intent2);
                    finish();
                }
            });
            thread.start();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

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

        spin = (Spinner)findViewById(R.id.spinner);
        edtText = (EditText)findViewById(R.id.edtText);
        txt1 = (TextView)findViewById(R.id.txtText1);
        txt2 = (TextView)findViewById(R.id.txtText2);
        btnBegin = (Button)findViewById(R.id.btnBegin);
        lytMain = (RelativeLayout) findViewById(R.id.lytMain);
        txtInstr = (TextView)findViewById(R.id.txtInstr);
        chkAssistant = (CheckBox)findViewById(R.id.chkAssistant);

        stxtInstr = "1.- Before start repairing please ensure to put screen brightness at full.\n" +
                "2.- To finish repairing mode just bring back Navigation Bar and press back button.\n" +
                "3.- ScreenColors assistant will notify you when the tool finishes(if enabled).\n" +
                "4.- It's needed to leave the screen turned on.\n" +
                "5.- The screen will change between several colors, the device must not be used in this process.\n\n" +
                "Important Message: " + "This process does not ensure your screen will be repaired it depends how much damage has the screen.\n" +
                "The use of this application is under risk of the own user, the developer doesn't make responsible "+
                "if the device gets more damage.\n";

        txtInstr.setText(stxtInstr);

        lytMain.setBackgroundColor(Color.WHITE);

        btnBegin.setOnClickListener(this);
        lytMain.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minuteshours, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(this);

        setFullScreen();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String finalText;
        String text = getResources().getString(R.string.how_many);
        kindTime = (String) parent.getItemAtPosition(position);
        finalText = text + " " + kindTime;
        txt2.setText(finalText);
        finalText = "";
        setFullScreen();
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
                    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    setFullScreen();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtText.getWindowToken(), 0);


                    if (kindTime.equals("Hours")) {
                        totTime = (Integer.parseInt(edtText.getText().toString())) * 3600;
                        makeColors(totTime);
                    }

                    if (kindTime.equals("Minutes")) {
                        totTime = (Integer.parseInt(edtText.getText().toString())) * 60;
                        makeColors(totTime);
                    }
                }
                 break;
        }
    }

    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intentBack = new Intent(this,BeginActivity.class);
        startActivity(intentBack);
        finish();
    }
}
