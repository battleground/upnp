package com.abooc.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.abooc.util.Debug;

public class SignActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, SignActivity.class);
        context.startActivity(intent);
    }

    private FrameLayout sign_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign);

        sign_layout = (FrameLayout) findViewById(R.id.sign_layout);

        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.VISIBLE);
        signIn.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        attach((ViewGroup) signIn);

//        final int originHeight = sign_layout.getHeight();
//        getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int height = sign_layout.getHeight();
//                Debug.anchor("height:" + height);
//
//                if ((height - originHeight) > 448) {
//
//                }
//
//            }
//        });
    }

    void attach(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof EditText) {
                childAt.setOnTouchListener(onTouchListener);
            }
        }

    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && !isShown) {
                isShown = true;
                upSignLayout();
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    boolean isUp = false;
    boolean isDown = true;

    boolean isUp() {
        return isUp;
    }

    boolean isDown() {
        return isDown;
    }

    private void upSignLayout() {
        isUp = true;
        isDown = false;

        ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
        layoutParams.height = sign_layout.getHeight() + 470;
        sign_layout.setLayoutParams(layoutParams);
        sign_layout.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }


    private void downSignLayout() {
        isDown = true;
        isUp = false;

        ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
        layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        sign_layout.setLayoutParams(layoutParams);
        sign_layout.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
    }


    public void onToSignIn(View view) {
        if (isShown) {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = sign_layout.getHeight() + 115;
            sign_layout.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            sign_layout.setLayoutParams(layoutParams);
        }

        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.VISIBLE);
        attach((ViewGroup) signIn);

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.GONE);

    }

    boolean isShown;

    public void onToSignUp(View view) {
        if (isShown) {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = sign_layout.getHeight() - 115;
            sign_layout.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            sign_layout.setLayoutParams(layoutParams);
        }

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.VISIBLE);
        attach((ViewGroup) signUp);

        View signIn = sign_layout.findViewById(R.id.sign_in);
        signIn.setVisibility(View.GONE);
    }

    public void onBackSignUp(View view) {
        if (isShown) {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = sign_layout.getHeight() - 152;
            sign_layout.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            sign_layout.setLayoutParams(layoutParams);
        }

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.VISIBLE);
        attach((ViewGroup) signUp);

        View signUpInfo = sign_layout.findViewById(R.id.sign_up_info);
        signUpInfo.setVisibility(View.GONE);
    }

    public void onToSignUpInfo(View view) {
        if (isShown) {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = sign_layout.getHeight() + 152;
            sign_layout.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = sign_layout.getLayoutParams();
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            sign_layout.setLayoutParams(layoutParams);
        }

        View signUpInfo = sign_layout.findViewById(R.id.sign_up_info);
        signUpInfo.setVisibility(View.VISIBLE);
        attach((ViewGroup) signUpInfo);

        View signUp = sign_layout.findViewById(R.id.sign_up);
        signUp.setVisibility(View.GONE);
    }

    public void onLoginEvent(View view) {
    }

    public void onHideKeyboard(View view) {
        if (isShown) {
            isShown = false;

            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                Debug.anchor(currentFocus.getClass().getSimpleName());
                currentFocus.clearFocus();
            }
            Keyboard.hideKeyboard(this, view);

            downSignLayout();
        }
    }

    public void onSelectedEdit(View view) {
    }

    public void onClose(View view) {
        super.onBackPressed();
    }
}
