package com.example.luke;

import android.app.Application;

public class LukeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppRepository.init(this);
    }
}
