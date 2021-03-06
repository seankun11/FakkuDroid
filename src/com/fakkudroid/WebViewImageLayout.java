package com.fakkudroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fakkudroid.util.Util;
import com.fakkudroid.util.NoZoomControlWebView;

public class WebViewImageLayout extends RelativeLayout{

	String imageFile;
	ProgressBar bar;
	NoZoomControlWebView wb;
	
	private WebViewImageLayout(Context context){
		super(context);
	}
	
	public WebViewImageLayout(String imageFile, Context context){
		super(context);
		this.imageFile = imageFile;
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		setBackgroundColor(Color.GREEN);
		bar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);;
		bar.setId(R.id.view_status);
		bar.setMax(100);
		
		wb = new NoZoomControlWebView(getContext());
		wb.getSettings().setJavaScriptEnabled(true);
		
		this.bar.setProgress(0);

		wb.setWebChromeClient(new WebChromeClient() {
		   public void onProgressChanged(WebView view, int progress) {
		     // Activities and WebViews measure progress with different scales.
		     // The progress meter will automatically disappear when we reach 100%
		     WebViewImageLayout.this.bar.setProgress(progress);
		   }
		 });
		wb.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(getContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		 });
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, getBarHeight());
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(bar, params);
		
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, bar.getId());
		addView(wb, params);
	}
	
	public int getBarHeight(){
		final float scale = getContext().getResources().getDisplayMetrics().density;
		int pixels = (int) (5 * scale + 0.5f);
		return pixels;
	}
	
	@SuppressWarnings("deprecation")
	public void startLoader(int width, int height, boolean japaneseMode){
		height-= getBarHeight();
		wb.loadDataWithBaseURL(null,
				Util.createHTMLImage(imageFile, width/wb.getScale(), height/wb.getScale(), japaneseMode, this.getResources()),
				"text/html", "utf-8", null);
	}
	
	@SuppressWarnings("deprecation")
	public void resizeImage(int width, int height){
		height-= getBarHeight();
		wb.loadUrl("javascript:resizeAll(" + width/wb.getScale() + "," + height/wb.getScale() + ");");
	}
	
	public void changeJapaneseMode(boolean japaneseMode){
		wb.loadUrl("javascript:japaneseMode=" + japaneseMode);
	}
	
	public void zoomIn(){
		wb.zoomIn();
	}
	
	public void zoomOut(){
		wb.zoomOut();
	}
}
