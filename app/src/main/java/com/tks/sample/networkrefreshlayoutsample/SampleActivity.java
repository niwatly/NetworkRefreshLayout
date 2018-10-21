package com.tks.sample.networkrefreshlayoutsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.tks.networkrefreshlayout.NetworkRefreshLayout;

public class SampleActivity extends AppCompatActivity {
	private NetworkRefreshLayout mNetworkRefreshLayout;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sample);
		
		mNetworkRefreshLayout = findViewById(R.id.content);
		
		mNetworkRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mNetworkRefreshLayout.startLoading(false);
				loadContent();
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mNetworkRefreshLayout.startLoading(false);
		loadContent();
	}
	
	private void loadContent() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				
				}
				final String content = "some success result";
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boolean empty = content.length() == 0;
						mNetworkRefreshLayout.finishLoading(empty);
					}
				});
			}
		}).start();
	}
}
