package com.gennext.hiren.newrepository;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    ArrayMap<String, Object> postParams;
    String URL="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postParams = new ArrayMap<>();
        postParams.put("id","252");
        RestClientHelper.getInstance().post(URL, postParams, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                Log.e("response",response);
            }

            @Override
            public void onError(String error) {
                Log.e("response error",error);
            }
        });


    }
}
