package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

// Facebook imports
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Foo on 28/1/2017.
 */

public class LegacyPanelView extends View {

    public LegacyPanelView(Context context, Activity activity) {
        super(context);
    }

    /*SharedPreferences SharedPref_Name;
    String PlayerName;

    // Week 13 Toast
    CharSequence text;
    int toastTime;
    Toast toast;

    // Week 14 Facebook
    private GUIbutton FBLoginButton;
    private GUIbutton shareScoreButton;

    boolean loggedin = false;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

    // ShareDialog shareDialog;
    ProfilePictureView profile_pic;
    List<String> PERMISSIONS = Arrays.asList("publish_actions");

    public LegacyPanelView(Context context, Activity activity) {
        super(context);
        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // 1d) Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;
        FacebookSdk.sdkInitialize(context);

        // Week 13 Toast
        //Toastmessage(context);

        // Facebook
        profile_pic = (ProfilePictureView)findViewById(R.id.picture);
        callbackManager = CallbackManager.Factory.create();

        // Score
        SharedPref_Name = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        PlayerName = SharedPref_Name.getString("PlayerUSERID", "DEFAULT");

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                AccessToken at = AccessToken.getCurrentAccessToken();

                if (currentAccessToken == null) {
                    // user logged out
                    profile_pic.setProfileId("");
                }
                else {
                    profile_pic.setProfileId(Profile.getCurrentProfile().getId());
                }
            }
        };

        accessTokenTracker.startTracking();

        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, PERMISSIONS);

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                profile_pic.setProfileId(Profile.getCurrentProfile().getId());
            }
            @Override
            public void onCancel() { System.out.println("Login attempt canceled."); }
            @Override
            public void onError(FacebookException e) {
                System.out.println("Login attempt failed.");
            }
        });
    }

    //@Override
    //public void onDraw(Canvas canvas)
    //{
//
    //}
    */

}
