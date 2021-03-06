package com.fakkudroid;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;

import com.fakkudroid.adapter.DoujinListAdapter;
import com.fakkudroid.bean.DoujinBean;
import com.fakkudroid.core.FakkuConnection;
import com.fakkudroid.core.FakkuDroidApplication;
import com.fakkudroid.util.Util;
import com.fakkudroid.R;

import android.util.Log;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RelatedContentListActivity extends ListActivity {

	/**
	 * constante para identificar la llave con la que env�o datos a trav�s de
	 * intents para comunicar entre las dos actividades: Main y ShowElement
	 */

	FakkuDroidApplication app;
	LinkedList<DoujinBean> llDoujin;
	DoujinListAdapter da;
	int nroPage = 1;
	private View mFormView;
	private View mStatusView;
	boolean cacheMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doujin_list);

		mFormView = findViewById(R.id.view_form);
		mStatusView = findViewById(R.id.view_status);

		app = (FakkuDroidApplication) getApplication();

		loadPage();
		configSettings();
	}

	private void configSettings() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cacheMode = prefs.getBoolean("cache_mode_checkbox", false);
	}
	
	public void nextPage(View view) {
		nroPage++;
		loadPage();
		Context context = getApplicationContext();
		CharSequence text = "Page " + nroPage;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public void previousPage(View view) {
		if (nroPage - 1 == 0) {

			Context context = getApplicationContext();
			CharSequence text = "There aren't more pages.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			nroPage--;
			loadPage();
			Context context = getApplicationContext();
			CharSequence text = "Page " + nroPage;
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	public void viewInBrowser(View view) {
		Intent viewBrowser = new Intent(Intent.ACTION_VIEW);
		viewBrowser.setData(Uri.parse(app.getCurrent().getUrl()));
		RelatedContentListActivity.this.startActivity(viewBrowser);
	}

	public void refresh(View view) {
		loadPage();
	}

	private void loadPage() {
		TextView tvPage = (TextView) findViewById(R.id.tvPage);
		tvPage.setText("Page " + nroPage);
		new DownloadCatalog().execute(app.getCurrent().urlRelated(nroPage));
	}

	/**
	 * Funci�n auxiliar que recibe una lista de mapas, y utilizando esta data
	 * crea un adaptador para poblar al ListView del dise�o
	 * */
	private void setData() {
		da = new DoujinListAdapter(this, R.layout.row_doujin, 0, llDoujin, cacheMode);
		this.setListAdapter(da);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DoujinBean data = llDoujin.get(position);
		app.setCurrent(data);
		Intent it = new Intent(this, DoujinActivity.class);
		this.startActivity(it);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mFormView.setVisibility(View.VISIBLE);
			mFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	class DownloadCatalog extends AsyncTask<String, Float, Integer> {

		protected void onPreExecute() {
			showProgress(true);
		}

		protected Integer doInBackground(String... urls) {

			try {
				Log.i(DownloadCatalog.class.toString(), "URL Catalog: "
						+ urls[0]);
				llDoujin = FakkuConnection.parseHTMLCatalog(urls[0]);
			} catch (ClientProtocolException e1) {
				Log.e(DownloadCatalog.class.toString(), "Exception", e1);
			} catch (IOException e1) {
				Log.e(DownloadCatalog.class.toString(), "Exception", e1);
			} catch (URISyntaxException e1) {
				Log.e(DownloadCatalog.class.toString(), "Exception", e1);
			}
			if(llDoujin==null)
				llDoujin = new LinkedList<DoujinBean>();
			if (cacheMode)
				for (DoujinBean bean : llDoujin) {
					try {
						File dir = getCacheDir();
						
						File myFile = new File(dir, bean.getFileImageTitle());
						Util.saveInStorage(myFile, bean.getUrlImageTitle());

						myFile = new File(dir, bean.getFileImagePage());
						Util.saveInStorage(myFile, bean.getUrlImagePage());
					} catch (Exception e) {
						Log.e(DownloadCatalog.class.toString(), "Exception", e);
					}
				}
			return llDoujin.size();
		}

		protected void onPostExecute(Integer size) {
			setData();
			showProgress(false);
		}
	}

}
