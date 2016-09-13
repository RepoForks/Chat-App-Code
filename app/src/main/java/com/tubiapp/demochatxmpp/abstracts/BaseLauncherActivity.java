package com.tubiapp.demochatxmpp.abstracts;

import android.view.MotionEvent;

public abstract class BaseLauncherActivity extends BaseActivity {
    private Thread mSplashThread;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSplashThread != null
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized (mSplashThread) {
                mSplashThread.notifyAll();
            }
        }
        return true;
    }

    protected abstract void initPreTasks();

    protected abstract void showNextActivity();

    /**
     * Show the splash in minimum duration
     *
     * @param duration
     */
    public void showSplash(final int duration) {
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        initPreTasks();
                        wait(duration);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stopSplash();
                }
            }
        };
        mSplashThread.start();
    }

    public void stopSplash() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showNextActivity();
                finish();
            }
        });
    }
}