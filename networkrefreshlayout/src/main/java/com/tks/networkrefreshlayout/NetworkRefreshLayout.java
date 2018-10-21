package com.tks.networkrefreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class NetworkRefreshLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {
	
	//コンテンツがない時のビュー
	private View mEmptyView;
	private int mEmptyViewResId;
	
	//コンテンツがある時のビュー
	private View mContentView;
	private int mContentViewResId;
	
	//コンテンツをローディングしているときのビュー
	private View mLoadingView;
	private int mLoadingViewResId;
	
	private boolean mDisableRefresh;
	
	private OnRefreshListener mWrappedListener;
	
	public NetworkRefreshLayout(Context context) {
		this(context, null);
	}
	
	public NetworkRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NetworkRefreshLayout);
		if (array != null) {
			//IDを覚えておいて、追加される子供の中から
			//コンテンツビューとエンプティービューを見つける
			mEmptyViewResId = array.getResourceId(R.styleable.NetworkRefreshLayout_emptyId, -1);
			mContentViewResId = array.getResourceId(R.styleable.NetworkRefreshLayout_contentId, -1);
			mLoadingViewResId = array.getResourceId(R.styleable.NetworkRefreshLayout_loadingId, -1);
			
			boolean disable = array.getBoolean(R.styleable.NetworkRefreshLayout_disableRefresh, false);
			setDisableRefresh(disable);
			
			int colorsResId = array.getResourceId(R.styleable.NetworkRefreshLayout_color_scheme, 0);
			if (colorsResId != 0) {
				TypedArray colorsTypedArray = getResources().obtainTypedArray(colorsResId);
				int[] colors = new int[colorsTypedArray.length()];
				
				for (int i = 0; i < colorsTypedArray.length(); i++) {
					colors[i] = colorsTypedArray.getColor(i, Color.BLACK);
				}
				
				setColorSchemeColors(colors);
				colorsTypedArray.recycle();
			}
			
			array.recycle();
		}
	}
	
	@Override
	public void setOnRefreshListener(OnRefreshListener listener) {
		//リフレッシュ開始時に行いたい処理があるのでラップする
		mWrappedListener = listener;
		super.setOnRefreshListener(this);
	}
	
	/**
	 * プルリフレッシュによるロードが始まったときや
	 * 初期値のロードが始まった時に呼ぶ
	 */
	public void startLoading() {
		startLoading(false);
	}
	
	public void startLoading(boolean hideContent) {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
		}
		if (mEmptyView != null) {
			mEmptyView.setVisibility(View.GONE);
		}
		//ローディング中もコンテンツを隠さない
		if (hideContent) {
			if (mContentView != null) {
				mContentView.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * プルリフレッシュによるロードが終わった時や
	 * 初期値のロードが終わった時に呼ぶ
	 */
	public void finishLoading(boolean empty) {
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.GONE);
		}
		if (empty) {
			if (mEmptyView != null) {
				mEmptyView.setVisibility(View.VISIBLE);
			}
			if (mContentView != null) {
				mContentView.setVisibility(View.GONE);
			}
		} else {
			if (mEmptyView != null) {
				mEmptyView.setVisibility(View.GONE);
			}
			if (mContentView != null) {
				mContentView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void setDisableRefresh(boolean disable) {
		mDisableRefresh = disable;
		setEnabled(!mDisableRefresh);
	}
	
	@Override
	public void onRefresh() {
		if (mDisableRefresh) {
			return;
		}
		startLoading();
		if (mWrappedListener != null) {
			mWrappedListener.onRefresh();
		}
	}
	
	@Override
	public void onViewAdded(View child) {
		super.onViewAdded(child);
		
		findTargetView(child);
	}
	
	private void findTargetView(View child) {
		int resId = child.getId();
		if (resId == mEmptyViewResId) {
			mEmptyView = child;
		} else if (resId == mContentViewResId) {
			mContentView = child;
		} else if (resId == mLoadingViewResId) {
			mLoadingView = child;
		} else if (child instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup)child;
			for (int i = 0;i < parent.getChildCount(); i++) {
				findTargetView(parent.getChildAt(i));
			}
		}
	}
}
