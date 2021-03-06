package com.fakkudroid.adapter;

import java.io.File;
import java.util.LinkedList;

import com.fakkudroid.bean.DoujinBean;
import com.fakkudroid.util.Util;
import com.fakkudroid.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DoujinListAdapter extends ArrayAdapter<DoujinBean> {

	LayoutInflater inf;
	LinkedList<DoujinBean> objects;
	boolean cacheMode;

	public DoujinListAdapter(Context context, int resource,
			int textViewResourceId, LinkedList<DoujinBean> objects,
			boolean cacheMode) {
		super(context, resource, textViewResourceId, objects);
		this.inf = LayoutInflater.from(context);
		this.objects = objects;
		this.cacheMode = cacheMode;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		DoujinBean s = objects.get(position);
		if (convertView == null) {
			convertView = inf.inflate(R.layout.row_doujin, null);
			holder = new ViewHolder();
			holder.tvDoujin = (TextView) convertView
					.findViewById(R.id.tvDoujin);
			holder.tvArtist = (TextView) convertView
					.findViewById(R.id.tvArtist);
			holder.tvSerie = (TextView) convertView.findViewById(R.id.tvSerie);
			holder.tvDescription = (TextView) convertView
					.findViewById(R.id.tvDescription);
			holder.tvTags = (TextView) convertView.findViewById(R.id.tvTags);
			holder.wvTitle = (WebView) convertView.findViewById(R.id.wvTitle);
			holder.wvPage = (WebView) convertView.findViewById(R.id.wvPage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvDoujin.setText(Util.limitString(s.getTitle(), 36, "..."));
		holder.tvArtist.setText(Util.limitString(
				s.getArtist().getDescription(), 36, "..."));
		holder.tvSerie.setText(s.getSerie().getDescription());
		holder.tvDescription.setText(Html.fromHtml(s.getDescription().replace(
				"<br>", "<br/>")));
		;
		holder.tvTags.setText(s.getTags());

		holder.wvTitle.setFocusable(false);
		holder.wvTitle.setLongClickable(false);
		holder.wvTitle.setClickable(false);
		holder.wvTitle.setFocusableInTouchMode(false);

		holder.wvPage.setFocusable(false);
		holder.wvPage.setLongClickable(false);
		holder.wvPage.setClickable(false);
		holder.wvPage.setFocusableInTouchMode(false);

		if (cacheMode) {

			File titleFile = new File(getContext().getCacheDir(), s.getFileImageTitle());
			File pageFile = new File(getContext().getCacheDir(), s.getFileImagePage());
			
			holder.wvTitle.loadDataWithBaseURL(null, Util
					.createHTMLImagePercentage("file://" + titleFile.getAbsolutePath(), 100,
							parent.getResources()), "text/html", "utf-8", null);
			holder.wvPage.loadDataWithBaseURL(null, Util
					.createHTMLImagePercentage("file://" + pageFile.getAbsolutePath(), 100,
							parent.getResources()), "text/html", "utf-8", null);
		} else {
			holder.wvTitle.loadDataWithBaseURL(null, Util
					.createHTMLImagePercentage(s.getUrlImageTitle(), 100,
							parent.getResources()), "text/html", "utf-8", null);
			holder.wvPage.loadDataWithBaseURL(null, Util
					.createHTMLImagePercentage(s.getUrlImagePage(), 100,
							parent.getResources()), "text/html", "utf-8", null);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView tvDoujin;
		TextView tvSerie;
		TextView tvArtist;
		TextView tvTags;
		TextView tvDescription;

		WebView wvTitle;
		WebView wvPage;
	}
}
