package com.abooc.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DevListActivity extends AppCompatActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, DevListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onOpenFullScreenActivity(View view) {
        FullScreenSignActivity.launch(this);
    }

    public void onOpenCustomActivity(View view) {
        CustomActivity.launch(this);
    }

}
