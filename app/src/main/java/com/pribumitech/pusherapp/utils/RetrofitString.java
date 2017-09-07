package com.pribumitech.pusherapp.utils;
import com.pribumitech.pusherapp.BuildConfig;
import com.pribumitech.pusherapp.utils.ApiServices;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 2.x Base Class
 */
public class RetrofitString {

    private static RetrofitString ourInstance;
    private ApiServices mWordpressApi;
    private Retrofit mRetrofit;

    private RetrofitString() {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(30, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.writeTimeout(30, TimeUnit.SECONDS);
        client.addInterceptor(this.getInterceptor());
        OkHttpClient clientBuild = client.build();

        Retrofit.Builder retrofit = new Retrofit.Builder();
        retrofit.baseUrl(BuildConfig.PUSHER_END_POINT);
        retrofit.client(clientBuild);
        retrofit.addConverterFactory(new ToStringConverterFactory());
        retrofit.addConverterFactory(GsonConverterFactory.create());
        //retrofit.addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        this.mRetrofit = retrofit.build();
    }

    /**
     * Singleton Instance
     *
     * @return RetrofitString
     */
    public static RetrofitString getInstance() {
        if (ourInstance == null) {
            ourInstance = new RetrofitString();
        }
        return ourInstance;
    }

    /**
     * @return Interceptor
     */
    private Interceptor getInterceptor() {

        return new Interceptor() {
            public Response intercept(Interceptor.Chain chain) throws IOException {
                return chain.proceed(
                        chain.request()
                                .newBuilder()
                                .header("Cache-Control", "no-cache")
                                //.header("User-Agent", "X-" + Konstanta.API_KEY)
                                .addHeader("Accept", "application/json")
                                .build()
                );
            }
        };
    }

    /**
     * Get Wordpress API
     *
     * @return ApiServices
     */
    public ApiServices getApisServices() {
        if (this.mWordpressApi == null) {
            this.mWordpressApi = this.mRetrofit.create(ApiServices.class);
        }
        return this.mWordpressApi;
    }

}
