package com.sdaacademy.jawny.daniel.oauth2;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.squareup.picasso.Picasso;

import org.fuckboilerplate.rx_social_connect.Response;
import org.fuckboilerplate.rx_social_connect.RxSocialConnect;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.user_id)
    TextView mUserId;

    @BindView(R.id.avatar)
    ImageView mAvatar;

    private GetGitHubInfoTask getGitHubInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.logon)
    public void logon() {
        final String apiKey = "e355a6fc0e29b9ec63cb";
        final String apiSecret = "1aa8c5dccef1b1940ae1fc96d22fb374eb6a95f1";
        final String secretState = "secret" + new Random().nextInt(999999);

        OAuth20Service service = new ServiceBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .state(secretState)
                .callback("http://localhost:8080")
                .build(GitHubApi.instance());

        RxSocialConnect.with(this, service)
                .subscribe(new Observer<Response<MainActivity, OAuth2AccessToken>>() {
                    @Override
                    public void onNext(Response<MainActivity, OAuth2AccessToken> tokenResponse) {
                        String token = tokenResponse.token().getAccessToken();
                        Log.i("TEST", "token: " + token);
                        getGitHubInfoTask = new GetGitHubInfoTask();
                        getGitHubInfoTask.setMainActivity(MainActivity.this);
                        getGitHubInfoTask.execute(token);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("TEST", e);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void showError(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }

    public void displayResponse(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            String id = jsonObject.optString("id");
            if (!id.isEmpty()) {
                mUserId.setText("ID: " + id);
                String url = jsonObject.optString("avatar_url");
                if (!url.isEmpty()) {
                    Picasso.with(this).load(url).into(mAvatar);
                }
            } else {
                showError("Blad logowania");
            }
        } catch (JSONException e) {
            showError("Blad json");
            e.printStackTrace();
        }
    }
}
