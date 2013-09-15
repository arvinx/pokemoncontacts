package com.pokemoncontacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;



public class MainActivity extends Activity {
	ProgressDialog mProgressDialog;
	AlertDialog.Builder alertCompletion;
	Context mContext = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ContactManager.context = this;
		checkFirstTimeRunning();
	}

	public void actionRandomGenerate(View view) 
	{
		boolean valid = setSelectedGenerations(view);
		if (valid) {
			new ContactUpdater().execute();
		} else {
			showDialog("Whooops", "Make sure you have at least one pokemon generation selected!", "OK");
		}
	}

	public void actionCustom(View view) {
		boolean valid = setSelectedGenerations(view);
		if (valid) {
			Intent intent = new Intent(this, ContactsList.class);
			startActivity(intent);
		} else {
			showDialog("Whooops", "Make sure you have at least one pokemon generation selected!", "OK");
		}
	}
	
	public void actionAlterativeOption(View view) {
		CheckBox checkBox = (CheckBox)view;
		View parentView = (View) view.getParent();
		if (checkBox.isChecked()) {
			((CheckBox)parentView.findViewById(R.id.checkBoxG1)).setChecked(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG2)).setChecked(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG3)).setChecked(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG4)).setChecked(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG5)).setChecked(false);
			
			((CheckBox)parentView.findViewById(R.id.checkBoxG1)).setEnabled(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG2)).setEnabled(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG3)).setEnabled(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG4)).setEnabled(false);
			((CheckBox)parentView.findViewById(R.id.checkBoxG5)).setEnabled(false);
		} else {
			((CheckBox)parentView.findViewById(R.id.checkBoxG1)).setEnabled(true);
			((CheckBox)parentView.findViewById(R.id.checkBoxG2)).setEnabled(true);
			((CheckBox)parentView.findViewById(R.id.checkBoxG3)).setEnabled(true);
			((CheckBox)parentView.findViewById(R.id.checkBoxG4)).setEnabled(true);
			((CheckBox)parentView.findViewById(R.id.checkBoxG5)).setEnabled(true);
		}
	}

	public void actionReset(View view) {
		alertCompletion = new AlertDialog.Builder(
				MainActivity.this);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new ContactRestorer().execute();
			}
		};
		alertCompletion.setTitle("Reset Contact Images?");
		alertCompletion.setMessage("Are you sure you want to restore your contact images to the ones before installing this app?");
		alertCompletion.setPositiveButton("Yes", onClickListener);
		alertCompletion.setNegativeButton("No", null);
		alertCompletion.create();
		alertCompletion.show();
	}

	private void checkFirstTimeRunning() {
		String PREFS_NAME = "MyPrefsFile";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getBoolean(Constants.FIRST_RUN_KEY, true)) {
			settings.edit().putBoolean(Constants.FIRST_RUN_KEY, false).commit(); 
			Intent intent = new Intent(this, WelcomeScreen.class);
			startActivity(intent);
		}
	}

	private boolean setSelectedGenerations(View view) {
		view = (View) view.getParent().getParent();
		LinearLayout options = (LinearLayout) view.findViewById(R.id.selectGenerationView);
		PokemonCollection.generationsSelected[0] = ((CheckBox)options.findViewById(R.id.checkBoxG1)).isChecked() ? POKEMON_GENERATION.GENERATION_1 : null;
		PokemonCollection.generationsSelected[1] = ((CheckBox)options.findViewById(R.id.checkBoxG2)).isChecked() ? POKEMON_GENERATION.GENERATION_2 : null;
		PokemonCollection.generationsSelected[2] = ((CheckBox)options.findViewById(R.id.checkBoxG3)).isChecked() ? POKEMON_GENERATION.GENERATION_3 : null;
		PokemonCollection.generationsSelected[3] = ((CheckBox)options.findViewById(R.id.checkBoxG4)).isChecked() ? POKEMON_GENERATION.GENERATION_4 : null;
		PokemonCollection.generationsSelected[4] = ((CheckBox)options.findViewById(R.id.checkBoxG5)).isChecked() ? POKEMON_GENERATION.GENERATION_5 : null;
		for (POKEMON_GENERATION generation : PokemonCollection.generationsSelected) {
			if (generation != null) return true;
		}
		return false;
	}

	private void showDialog(String title, String message, String buttonText) {
		alertCompletion = new AlertDialog.Builder(
				MainActivity.this);
		alertCompletion.setTitle(title);
		alertCompletion.setMessage(message);
		alertCompletion.setPositiveButton(buttonText, null);
		alertCompletion.create();
		alertCompletion.show();
	}

	private class ContactUpdater extends AsyncTask<Void, Integer, Void> implements ContactPhotoChangedNotification {

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
			ContactManager.readContactsAndSetPhotos();
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
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setTitle("Updating Contacts");
			//mProgressDialog.setMessage("This might take a while!");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(numberOfContacts);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}

		public void displayCompletionAlert() {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					showDialog("Updated Contacts Successfully", "", "Cool");
				}
			});
		}
	}


	private class ContactRestorer extends AsyncTask<Void, Integer, Void> implements ContactPhotoChangedNotification {

		private Integer numberOfContacts = ContactManager.numContactsToRestore() - 1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ContactManager.setObserver(this);
			setUpProgressDialog();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void ... params) {
			ContactManager.restoreContacts();
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
			} else if (progress.intValue() == -1) {
				mProgressDialog.dismiss();
				dispalyFailureAlert();
			} else {
				publishProgress(progress);
			}
		}

		private void setUpProgressDialog() {
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setTitle("Restoring Contacts");
			//mProgressDialog.setMessage("This might take a while!");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(numberOfContacts);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}

		public void displayCompletionAlert() {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					showDialog("Restored Contacts Successfully", null, "Cool");
				}
			});
		}
		
		public void dispalyFailureAlert() {
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					showDialog("Could not restore", "Backup folder seems to be missing...", "=(");
				}
			});
		}
	}



}