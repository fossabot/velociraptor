package com.pluscubed.velociraptor.api;

import com.pluscubed.velociraptor.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LimitInterceptor implements Interceptor {

    private Callback callback;

    public LimitInterceptor(Callback endpoint) {
        this.callback = endpoint;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("User-Agent", "Velociraptor/" + BuildConfig.VERSION_NAME)
                .build();
        try {
            Response proceed = chain.proceed(request);
            if (!proceed.isSuccessful()) {
                callback.updateTimeTaken(Integer.MAX_VALUE);
                throw new IOException(proceed.code() + ": " + proceed.toString());
            } else {
                callback.updateTimeTaken((int) (proceed.receivedResponseAtMillis() - proceed.sentRequestAtMillis()));
            }
            return proceed;
        } finally {
            callback.updateTimestamp(System.currentTimeMillis());
        }
    }


    public static class Callback {
        public void updateTimeTaken(int timeTaken) {
        }

        public void updateTimestamp(long timeTakenTimestamp) {
        }
    }
}
