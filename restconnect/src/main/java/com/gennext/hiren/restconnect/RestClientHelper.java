package com.gennext.hiren.restconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hiren on 23/10/17.
 */

public class RestClientHelper {

    private static final String LOG_TAG = "REST_CONNECT>";
    private static String defaultBaseUrl = "";
    private static final Object lockObject = new Object();
    private static RestClientHelper restClientHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private RestClientHelper() {
    }

    public interface RestClientListener {
        void onSuccess(String response);
        void onError(String error);
    }

    public static RestClientHelper getInstance() {
        if (restClientHelper == null)
            synchronized (lockObject) {
                if (restClientHelper == null)
                    restClientHelper = new RestClientHelper();
            }
        return restClientHelper;
    }

    private final Executor executor;

    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
                5L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull final Runnable r) {
                        return new Thread(r, LOG_TAG + "Thread");
                    }
                });
        executor.allowCoreThreadTimeOut(true);
        this.executor = executor;
    }

    private void addHeaders(Request.Builder builder, @NonNull ArrayMap<String, String> headers) {
        for (String key : headers.keySet()) {
            builder.addHeader(key, headers.get(key));
        }
    }

    public void get(Context context,@NonNull String serviceUrl, @NonNull RestClientListener restClientListener) {
        this.context = context;
        get(context,serviceUrl, null, null, restClientListener);
    }

    public void get(Context context,@NonNull String serviceUrl, ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        this.context = context;
        get(context,serviceUrl, null, params, restClientListener);
    }

    public void get(Context context,@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, RestClientListener restClientListener) {
        try {
            final Request.Builder builder = new Request.Builder();
            if (headers != null){
                addHeaders(builder, headers);}
            builder.url(generateUrlParams(serviceUrl, params));
            execute(context, builder, restClientListener);
        }catch (Exception e){
            Log.e("error in get",e+"");
        }
    }

    public void post(Context context,@NonNull String serviceUrl, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        this.context = context;
        post(context,serviceUrl, null, params, restClientListener);
    }

    public void post(Context context,@NonNull String serviceUrl, ArrayMap<String, String> headers, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        try {
            final Request.Builder builder = new Request.Builder();
            if (headers != null){
                addHeaders(builder, headers);}
            StringBuffer urls = new StringBuffer();
            if (defaultBaseUrl.length() > 0){
                urls.append(defaultBaseUrl);}
            urls.append(serviceUrl);
            builder.url(urls.toString());
            builder.post(generateRequestBody(params));
            execute(context, builder, restClientListener);
        }catch (Exception e){
            Log.e("error in post",e+"");
        }
    }

    public void put(Context context,@NonNull String serviceUrl, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        this.context = context;
        put(context,serviceUrl, null, params, restClientListener);
    }

    public void put(Context context,@NonNull String serviceUrl, ArrayMap<String, String> headers, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        try {
            final Request.Builder builder = new Request.Builder();
            if (headers != null){
                addHeaders(builder, headers);}
            StringBuffer urls = new StringBuffer();
            if (defaultBaseUrl.length() > 0){
                urls.append(defaultBaseUrl);}
            urls.append(serviceUrl);
            builder.url(urls.toString());
            builder.put(generateRequestBody(params));
            execute(context, builder, restClientListener);
        }catch (Exception e){
            Log.e("error in put",e+"");
        }
    }

    public void delete(Context context,@NonNull String serviceUrl, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        this.context = context;
        delete(context,serviceUrl, null, params, restClientListener);
    }

    public void delete(Context context,@NonNull String serviceUrl, ArrayMap<String, String> headers, @NonNull ArrayMap<String, Object> params, @NonNull RestClientListener restClientListener) {
        try {
            final Request.Builder builder = new Request.Builder();
            if (headers != null){
                addHeaders(builder, headers);}
            StringBuffer urls = new StringBuffer();
            if (defaultBaseUrl.length() > 0){
                urls.append(defaultBaseUrl);}
            urls.append(serviceUrl);
            builder.url(urls.toString());
            builder.delete(generateRequestBody(params));
            execute(context, builder, restClientListener);
        }catch (Exception e){
            Log.e("error in delete ",e+"");
        }
    }

    public void postMultipart(Context context,@NonNull String serviceUrl, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        this.context = context;
        postMultipart(context,serviceUrl, null, null, files, restClientListener);
    }

    public void postMultipart(Context context,@NonNull String serviceUrl, ArrayMap<String, Object> params, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        this.context = context;
        postMultipart(context,serviceUrl, null, params, files, restClientListener);
    }

    public void postMultipart(Context context,@NonNull String serviceUrl, ArrayMap<String, String> headers, ArrayMap<String, Object> params, @NonNull ArrayMap<String, File> files, @NonNull RestClientListener restClientListener) {
        final Request.Builder builder = new Request.Builder();
        if (headers != null){
            addHeaders(builder, headers);}
        StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0){
            urls.append(defaultBaseUrl);}
        urls.append(serviceUrl);
        builder.url(urls.toString());
        builder.post(generateMultipartBody(params, files));
        execute(context,builder, restClientListener);
    }

    private void execute(final Context context,final Request.Builder builder, final RestClientListener restClientListener) {
        this.context = context;
        getLoader(context);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES).build();
                try {
                    System.setProperty("http.keepAlive", "false");
                    final Response response = client.newCall(builder.build()).execute();
                    final String responseData = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.code() == 200) {
                                progressDialog.dismiss();
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                restClientListener.onSuccess(responseData);
                            } else {
                                progressDialog.dismiss();
                                alertDialogBuilder = new AlertDialog.Builder(context);
                                restClientListener.onError(responseData);
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                alertDialogBuilder.setMessage(response.message());
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Retry",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        execute(context,builder, restClientListener);
                                                    }
                                                })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            alertDialogBuilder = new AlertDialog.Builder(context);
                            restClientListener.onError("Getting Bad Response...");
                            alertDialogBuilder.setMessage("Getting Bad Response...");
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("Retry",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    execute(context,builder, restClientListener);
                                                }
                                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });
                }
            }
        });
    }

    private void getLoader(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private String generateUrlParams(String serviceUrl, ArrayMap<String, Object> params) {
        final StringBuffer urls = new StringBuffer();
        if (defaultBaseUrl.length() > 0)
            urls.append(defaultBaseUrl);
        urls.append(serviceUrl);
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    urls.append("?" + key + "=" + params.get(key));
                } else {
                    urls.append("&" + key + "=" + params.get(key));
                }
                i++;
            }
        }
        return urls.toString();
    }

    private RequestBody generateRequestBody(ArrayMap<String, Object> params) {
        final JSONObject jsonObj = new JSONObject();
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    jsonObj.put(key, params.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObj));
        return requestBody;
    }

    private RequestBody generateMultipartBody(ArrayMap<String, Object> params, ArrayMap<String, File> files) {
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, String.valueOf(params.get(key)));
            }
        }
        if (files != null) {
            for (String key : files.keySet()) {
                builder.addFormDataPart(key, key, RequestBody.create(MediaType.parse("image/png"), files.get(key)));
            }
        }
        return builder.build();
    }

}
