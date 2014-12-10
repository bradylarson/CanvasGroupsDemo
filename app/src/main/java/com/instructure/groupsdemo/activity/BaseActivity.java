package com.instructure.groupsdemo.activity;

import android.app.Activity;
import android.content.Context;

import com.instructure.canvasapi.model.CanvasError;
import com.instructure.canvasapi.utilities.APIStatusDelegate;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.ErrorDelegate;

import retrofit.RetrofitError;

/**
 * Created by brady on 12/8/14.
 */
public class BaseActivity extends Activity implements APIStatusDelegate, ErrorDelegate {
    @Override
    public void onCallbackStarted() {

    }

    @Override
    public void onCallbackFinished(CanvasCallback.SOURCE source) {

    }

    @Override
    public void onNoNetwork() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void noNetworkError(RetrofitError error, Context context) {

    }

    @Override
    public void notAuthorizedError(RetrofitError error, CanvasError canvasError, Context context) {

    }

    @Override
    public void invalidUrlError(RetrofitError error, Context context) {

    }

    @Override
    public void serverError(RetrofitError error, Context context) {

    }

    @Override
    public void generalError(RetrofitError error, CanvasError canvasError, Context context) {

    }
}
