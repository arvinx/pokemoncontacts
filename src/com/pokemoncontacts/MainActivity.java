package com.pokemoncontacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends Activity {
	ProgressDialog mProgressDialog;
	AlertDialog.Builder alertCompletion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ContactManager.context = this;
	}

	public void actionRandomGenerate(View view) 
	{
		view = (View) view.getParent();
		//TextView editText = (TextView) findViewById(R.id.textView1);
		//    	LinearLayout generationOptions = (LinearLayout) findViewById(R.id.selectGenerationView);
		//    	
		//    	ObjectAnimator fadeIn = ObjectAnimator.ofFloat(generationOptions, "alpha", 0f, 1f);
		//    	ObjectAnimator mover = ObjectAnimator.ofFloat(generationOptions, "translationY", -200, 0);
		//    	AnimatorSet animatorSet = new AnimatorSet();
		//    	animatorSet.play(mover).with(fadeIn);
		//    	animatorSet.start();

		new UpdateContacts().execute("");
	}

	private class UpdateContacts extends AsyncTask<String, Integer, Void> implements ContactPhotoChangedNotification {

		private Integer numberOfContacts = ContactManager.getNumberOfContacts() - 190;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ContactManager.setObserver(this);
			setUpProgressDialog();
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(String... arg0) {
			ContactManager.readContactsAndSetPhotos();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// Update the ProgressBar
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
			alertCompletion.setPositiveButton("OK", null);
			alertCompletion.create();
		}
		
	}

}
//String [] columns = cursor.getColumnNames();
//for (String columnName : columns) {
//	String msg = cursor.getString(cursor.getColumnIndex(columnName));
//	if (msg == null) {
//		msg = "null";
//	}
//	Log.d("ContactInfo: ", columnName + "   " + msg);
//}
//Log.d("ContactInfo: ", "----------------------------------------------------------------------------------------");
//i++;
//if (i > 10) {
//	break;
//}
//}
