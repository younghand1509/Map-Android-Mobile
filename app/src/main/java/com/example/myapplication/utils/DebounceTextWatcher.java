package com.example.myapplication.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

public class DebounceTextWatcher implements TextWatcher {
    private final Handler handler = new Handler();
    private final long delay;
    private final RunnableCallback callback;

    private Runnable workRunnable;

    public interface RunnableCallback {
        void run(String text);
    }

    public DebounceTextWatcher(long delay, RunnableCallback callback) {
        this.delay = delay;
        this.callback = callback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (workRunnable != null) handler.removeCallbacks(workRunnable);
    }

    @Override
    public void afterTextChanged(Editable s) {
        workRunnable = () -> callback.run(s.toString());
        handler.postDelayed(workRunnable, delay);
    }
}
