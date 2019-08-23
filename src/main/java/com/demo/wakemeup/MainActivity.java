package com.demo.wakemeup;

import com.nexacro.NexacroUpdatorActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends NexacroUpdatorActivity {

    public MainActivity() {
        super();
        setBootstrapURL("https://www.tobesoft.com/demo/wakemeup/_android_/start_android.json");
        setProjectURL("https://www.tobesoft.com/demo/wakemeup/_android_/");
    }

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
    	
        Intent intent = getIntent();
        if(intent != null) {
            String bootstrapURL = intent.getStringExtra("bootstrapURL");
            String projectUrl = intent.getStringExtra("projectUrl");
            if(bootstrapURL != null) {
                setBootstrapURL(bootstrapURL);
                setProjectURL(projectUrl);
            }
        }

        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }  
    }
    
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    protected void onDestroy() {
        new UserNotify(this);

        super.onDestroy();
    }
}