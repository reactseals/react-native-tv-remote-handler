package com.reactnativetvremotehandler;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableMap;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Map;
import java.util.Objects;

@ReactModule(name = TvRemoteHandlerModule.NAME)
public class TvRemoteHandlerModule extends ReactContextBaseJavaModule {
    public static final String NAME = "TVRemoteHandler";
    private ReactContext mReactContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter mJSModule = null;
    private static TvRemoteHandlerModule instance = null;

    private static final Map<Integer, String> KEY_EVENTS_ACTIONS =
        MapBuilder.<Integer, String>builder()
            .put(KeyEvent.KEYCODE_DPAD_CENTER, "select")
            .put(KeyEvent.KEYCODE_ENTER, "select")
            .put(KeyEvent.KEYCODE_SPACE, "select")
            .put(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, "playPause")
            .put(KeyEvent.KEYCODE_MEDIA_REWIND, "rewind")
            .put(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, "fastForward")
            .put(KeyEvent.KEYCODE_DPAD_UP, "up")
            .put(KeyEvent.KEYCODE_DPAD_RIGHT, "right")
            .put(KeyEvent.KEYCODE_DPAD_DOWN, "down")
            .put(KeyEvent.KEYCODE_DPAD_LEFT, "left")
            .build();

    public TvRemoteHandlerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        instance = this;
        mReactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    public static TvRemoteHandlerModule getInstance() {
        return instance;
    }

    public void onKeyDownEvent(final KeyEvent keyEvent) {
        if (!mReactContext.hasActiveCatalystInstance()) {
          return;
        }

        if (mJSModule == null) {
          mJSModule = mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }


        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            mJSModule.emit("onKeyDown", getJsEventParams(keyEvent));
          }
        });
    };

    private WritableMap getJsEventParams(KeyEvent keyEvent) {
        WritableMap params = new WritableNativeMap();
        int action = keyEvent.getAction();
        int eventKeyCode = keyEvent.getKeyCode();
        String eventType = KEY_EVENTS_ACTIONS.containsKey(eventKeyCode) ? KEY_EVENTS_ACTIONS.get(eventKeyCode) : "";
        String focusedViewId = "";

        if (this.getCurrentActivity() != null && this.getCurrentActivity().getCurrentFocus() != null) {
            focusedViewId = String.valueOf(this.getCurrentActivity().getCurrentFocus().getId());
        }

        params.putString("eventType", eventType);
        params.putInt("action", action);
        params.putString("focusedViewId", focusedViewId);

        return params;
    }
}
