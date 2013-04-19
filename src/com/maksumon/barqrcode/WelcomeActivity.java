package com.maksumon.barqrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;


public class WelcomeActivity extends Activity {

	private ProgressBar progressBar;
	protected static final int TIMER_RUNTIME = 5000; // in ms --> 5s

	protected boolean mbActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		final Thread timerThread = new Thread() {
			@Override
			public void run() {
				mbActive = true;
				try {
					int waited = 0;
					while(mbActive && (waited < TIMER_RUNTIME)) {
						sleep(200);
						if(mbActive) {
							waited += 200;
							updateProgress(waited);
						}
					}
					startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				} catch(InterruptedException e) {
					// do nothing
				} finally {
					finish();
				}
			}
		};
		timerThread.start();
	}

	public void updateProgress(final int timePassed) {
		if(null != progressBar) {
			// Ignore rounding error here
			final int progress = progressBar.getMax() * timePassed / TIMER_RUNTIME;
			progressBar.setProgress(progress);
		}
	}
}
