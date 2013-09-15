package com.pokemoncontacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;



public class MainActivity extends Activity {
	ProgressDialog mProgressDialog;
	AlertDialog.Builder alertCompletion;

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
			new UpdateContacts().execute();
		} else {
			showOptionsError();
		}
	}
	
	public void actionCustom(View view) {
		boolean valid = setSelectedGenerations(view);
		if (valid) {
			Intent intent = new Intent(this, ContactsList.class);
			startActivity(intent);
		} else {
			showOptionsError();
		}
	}
	
	private void checkFirstTimeRunning() {
		String PREFS_NAME = "MyPrefsFile";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getBoolean(Constants.FIRST_RUN_KEY, true)) {
			Log.d("FIRSTRUN", "YES");
			backupContactPhotos();
		    settings.edit().putBoolean(Constants.FIRST_RUN_KEY, false).commit(); 
		}
	}
	
	private void backupContactPhotos() {
		
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
	
	private void showOptionsError() {
		alertCompletion = new AlertDialog.Builder(
		        MainActivity.this);
		alertCompletion.setTitle("Whoooops");
		alertCompletion.setMessage("Make sure you have at least one pokemon generation selected!");
		alertCompletion.setPositiveButton("OK", null);
		alertCompletion.create();
		alertCompletion.show();
	}

	private class UpdateContacts extends AsyncTask<Void, Integer, Void> implements ContactPhotoChangedNotification {

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
				    setUpAlertCompletionDialog();
				    alertCompletion.show();
				  }
				});
		}
		
		public void setUpAlertCompletionDialog() {
			alertCompletion = new AlertDialog.Builder(
			        MainActivity.this);
			alertCompletion.setTitle("Updated Contacts Successfully");
			alertCompletion.setPositiveButton("Cool", null);
			alertCompletion.create();
		}
	}

}