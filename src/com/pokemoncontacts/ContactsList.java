package com.pokemoncontacts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

public class ContactsList extends ListActivity {

	// This is the Adapter being used to display the list's data
	private ArrayList<Contact> contacts = new ArrayList<Contact>();

	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
		ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_THUMBNAIL_URI};

	// This is the select criteria		
	static final String SELECTION = "((" + 
			ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
			ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" +
			ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1))";
	//private HashMap<String, String> contactNamesVisited = new HashMap<String, String>(); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_contacts_list);
		// Create a progress bar to display while the list loads
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progressBar.setIndeterminate(true);
		getListView().setEmptyView(progressBar);

		// Must add the progress bar to the root of the layout, R.layout.activity_contacts_list
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		root.addView(progressBar);

//		// For the cursor adapter, specify which columns go into which views
//		String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_THUMBNAIL_URI};
//		int[] toViews = {R.id.contactName, R.id.contactImage}; // The TextView in simple_list_item_1
//
//		// Create an empty adapter we will use to display the loaded data.
//		// We pass null for the cursor, then update it in onLoadFinished()
//		mAdapter = new SimpleCursorAdapter(this, 
//				R.layout.activity_contacts_list, null,
//				fromColumns, toViews, 0);
//
//		mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
//			/** Binds the Cursor column defined by the specified index to the specified view */
//			public boolean setViewValue(View view, Cursor cursor, int columnIndex){
//				contactNamesVisited.put("apples", "");
//				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
//				if (contactNamesVisited.get("apples") == null) {
//					Log.d("Here", "Here");
//					contactNamesVisited.put(contactName, "");
//					if(view.getId() == R.id.contactImage){
//						String uriString = cursor.getString(columnIndex);
//						if (uriString != null) {
//							Uri uri = Uri.parse(uriString);
//							Bitmap bitmap;
//							try {
//								bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//								((ImageView)view).setImageBitmap(bitmap);
//							} catch (FileNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						return true; //true because the data was bound to the view
//					}
//				}
//				return false;
//			}
//		});

//		setListAdapter(mAdapter);

		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
//		getLoaderManager().initLoader(0, null, this);
		setArrayListContacts();
		ContactListAdapter adapter = new ContactListAdapter(this, contacts);
		setListAdapter(adapter);
	}


	private void setArrayListContacts() {
		Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PROJECTION, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
		String prevName = null;
		String name = null;
		while (cursor.moveToNext()) {
			prevName = name;
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String photoThumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI));
			if (!name.equals(prevName)) {
				contacts.add(new Contact(name, photoThumbnailUri));
				Log.d("Arvin", "contact added " + name);
			}
		}
		cursor.close();
	}
//	// Called when a new Loader needs to be created
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		// Now create and return a CursorLoader that will take care of
//		// creating a Cursor for the data being displayed.
//		return new CursorLoader(this,ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//				PROJECTION, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
//	}
//
//	// Called when a previously created loader has finished loading
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		// Swap the new cursor in.  (The framework will take care of closing the
//		// old cursor once we return.)
//		mAdapter.swapCursor(data);
//	}
//
//	// Called when a previously created loader is reset, making the data unavailable
//	public void onLoaderReset(Loader<Cursor> loader) {
//		// This is called when the last Cursor provided to onLoadFinished()
//		// above is about to be closed.  We need to make sure we are no
//		// longer using it.
//		mAdapter.swapCursor(null);
//	}

	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ImageGrid.class);
		startActivity(intent); 
    }
	
}