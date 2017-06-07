package com.abooc.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Space;

import com.abooc.util.Debug;

public class FullScreenSignActivity extends Activity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, FullScreenSignActivity.class);
        context.startActivity(intent);
    }

    private FrameLayout sign_layout;
    private Space bottomFillSpace;

    private KeyboardWatcher mKeyboardWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_sign);

        final View rootView = findViewById(R.id.rootView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int height = r.bottom - r.top;
                Debug.anchor("r.top:" + r.top + ", r.bottom:" + r.bottom + ", height:" + height);
            }
        });

        sign_layout = (FrameLayout) findViewById(R.id.sign_layout);
        bottomFillSpace = (Space) findViewById(R.id.bottomFillSpace);

        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.VISIBLE);
        signIn.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));

        mKeyboardWatcher = new KeyboardWatcher(getWindow().getDecorView());
        mKeyboardWatcher.setWatcherListener(new OnSoftKeyboardStateChangedListener() {
            @Override
            public void onSoftKeyboardStateChanged(boolean isShowing, int keyboardHeight) {
                if (isShowing) {
                    ViewGroup.LayoutParams layoutParams = bottomFillSpace.getLayoutParams();
                    layoutParams.height = keyboardHeight - mKeyboardWatcher.getStatusBarHeight() * 4;
//                    layoutParams.height = keyboardHeight;
                    bottomFillSpace.setLayoutParams(layoutParams);
                    bottomFillSpace.setVisibility(View.VISIBLE);
                } else {
                    bottomFillSpace.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Debug.anchor("hardKeyboardHidden:" + newConfig.hardKeyboardHidden + ", screenLayout:" + newConfig.screenLayout +"" + newConfig.toString());
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isKeyboardShowing() {
        return mKeyboardWatcher.isKeyboardShowing();
    }

    public void onToSignIn(View view) {
        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.VISIBLE);

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.GONE);
    }

    public void onToSignUp(View view) {
        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.VISIBLE);

        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.GONE);
    }

    public void onBackSignUp(View view) {
        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.VISIBLE);

        View signUpInfo = sign_layout.findViewById(R.id.sign_up_info);
        signUpInfo.setVisibility(View.GONE);
    }

    public void onToSignUpInfo(View view) {
        View signUpInfo = sign_layout.findViewById(R.id.sign_up_info);
        signUpInfo.setVisibility(View.VISIBLE);

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.GONE);
    }

    public void onLoginEvent(View view) {
    }

    public void onHideKeyboard(View view) {
        if (isKeyboardShowing()) {
            Keyboard.hideKeyboard(this, view);

            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }
        }
    }

    public void onClose(View view) {
//        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Debug.anchor();
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mKeyboardWatcher.destroy();
        super.onDestroy();
    }

}
