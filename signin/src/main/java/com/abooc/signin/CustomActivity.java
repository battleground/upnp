package com.abooc.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

public class CustomActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, CustomActivity.class);
        context.startActivity(intent);
    }

    private FrameLayout sign_layout;
    private KeyboardWatcher mKeyboardWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom);
        sign_layout = (FrameLayout) findViewById(R.id.sign_layout);
        mKeyboardWatcher = new KeyboardWatcher(getWindow().getDecorView());

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
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mKeyboardWatcher.destroy();
        super.onDestroy();
    }

}
