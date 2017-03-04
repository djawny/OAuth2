package com.sdaacademy.jawny.daniel.oauth2;

import android.app.Application;

import org.fuckboilerplate.rx_social_connect.RxSocialConnect;

import io.victoralbertos.jolyglot.GsonSpeaker;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxSocialConnect.register(this, "encryption key")
                .using(new GsonSpeaker());
    }
}
