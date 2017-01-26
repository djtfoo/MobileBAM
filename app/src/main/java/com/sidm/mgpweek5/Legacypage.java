package com.sidm.mgpweek5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
// Facebook imports
//import com.facebook.AccessToken;
//import com.facebook.AccessTokenTracker;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.Profile;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.facebook.login.widget.ProfilePictureView;
//import com.facebook.share.ShareApi;
//import com.facebook.share.model.SharePhoto;
//import com.facebook.share.model.SharePhotoContent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Foo on 19/1/2017.
 */

public class Legacypage extends Activity implements View.OnClickListener {

    private Button btn_back;

    SharedPreferences SharedPref_Name;
    String PlayerName;

    // Week 14 Facebook
    //private Button btn_fbLogin;
    //private Button btn_sharescore;
//
    //boolean loggedin = false;
    //private CallbackManager callbackManager;
    //private LoginManager loginManager;

    // ShareDialog shareDialog;
    //ProfilePictureView profile_pic;
    //List<String> PERMISSIONS = Arrays.asList("publish_actions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hide top bar

        //FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.legacypage);

        // Facebook
        //profile_pic = (ProfilePictureView)findViewById(R.id.picture);
        //callbackManager = CallbackManager.Factory.create();

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        //btn_fbLogin = (LoginButton)findViewById(R.id.fb_login_button);
        //btn_fbLogin.setOnClickListener(this);
        //btn_sharescore = (Button)findViewById(R.id.btn_sharescore);
        //btn_sharescore.setOnClickListener(this);

        // Score
        TextView nameText;
        nameText = (TextView)findViewById(R.id.PlayerName);

        SharedPref_Name = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        PlayerName = SharedPref_Name.getString("PlayerUSERID", "DEFAULT");

        nameText.setText(String.format(PlayerName));

        //AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        //    @Override
        //    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        //        //AccessToken at = AccessToken.getCurrentAccessToken();
//
        //        if (currentAccessToken == null) {
        //            // user logged out
        //            profile_pic.setProfileId("");
        //        }
        //        else {
        //            profile_pic.setProfileId(Profile.getCurrentProfile().getId());
        //        }
        //    }
        //};
//
        //accessTokenTracker.startTracking();
//
        //loginManager = LoginManager.getInstance();
        //loginManager.logInWithPublishPermissions(this, PERMISSIONS);
//
        //loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        //    @Override
        //    public void onSuccess(LoginResult loginResult) {
        //        profile_pic.setProfileId(Profile.getCurrentProfile().getId());
        //        sharePost();
        //    }
        //    @Override
        //    public void onCancel() { System.out.println("Login attempt canceled."); }
        //    @Override
        //    public void onError(FacebookException e) {
        //        System.out.println("Login attempt failed.");
        //    }
        //});

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v == btn_back) {
            intent.setClass(this, Mainmenu.class);
            startActivity(intent);
        }
//
        //if (v == btn_sharescore) {
        //    sharePost();
        //}
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    // Week 14 Facebook - share info
    //public void sharePost() {
    //    Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//
    //    SharePhoto photo = new SharePhoto.Builder()
    //            .setBitmap(image)
    //            .setCaption("Thank you for playing BAM! - Big Apocalyptic Monsters. You have cleared the game as " + PlayerName)
    //            .build();
//
    //    SharePhotoContent content = new SharePhotoContent.Builder()
    //            .addPhoto(photo)
    //            .build();
//
    //    ShareApi.share(content, null);
    //}
//
    //// Week 14 Facebook - to use the callbackManager to manage login
    //@Override
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //    callbackManager.onActivityResult(requestCode, resultCode, data);
    //}

}
