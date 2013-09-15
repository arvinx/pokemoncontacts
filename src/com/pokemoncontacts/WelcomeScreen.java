package com.pokemoncontacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class WelcomeScreen extends Activity {
	AlertDialog.Builder alertCompletion;
	ProgressDialog mProgressDialog;
	Context mContext = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome_screen);
	}

	public void actionContinue(View view) {
		//showDialog("Whooops", "Make sure you have at least one pokemon generation selected!", "OK");
		new BackupContacts().execute();
	}

	private void showDialog(String title, String message, String buttonText, OnClickListener onClickListener) {
		alertCompletion = new AlertDialog.Builder(
				WelcomeScreen.this);
		alertCompletion.setTitle(title);
		alertCompletion.setMessage(message);
		alertCompletion.setPositiveButton(buttonText, onClickListener);
		alertCompletion.create();
		alertCompletion.show();
	}

	private class BackupContacts extends AsyncTask<Void, Integer, Void> implements ContactPhotoChangedNotification {

		private Integer numberOfContacts = ContactManager.getNumberOfContacts() - 1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ContactManager.setObserver(this);
			setUpProgressDialog();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void ... params) {
			ContactManager.backupContactPhotos();
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		public void contactUpdated(Integer progress) {
			if (progress.intValue() == numberOfContacts.intValue()) {
				mProgressDialog.dismiss();
				displayCompletionAlert();
			} else {
				publishProgress(progress);
			}
		}

		private void setUpProgressDialog() {
			mProgressDialog = new ProgressDialog(WelcomeScreen.this);
			mProgressDialog.setTitle("Backing up contacts");
			//mProgressDialog.setMessage("This might take a while!");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(numberOfContacts);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}

		public void displayCompletionAlert() {
			WelcomeScreen.this.runOnUiThread(new Runnable() {
				public void run() {
					OnClickListener onClickListener = new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							    Intent intent = new Intent(mContext, MainActivity.class);
								startActivity(intent);
							}
					};
					showDialog("Backed Up Contact Images Successfully", null, "Cool", onClickListener);
				}
			});
		}
	}
}
