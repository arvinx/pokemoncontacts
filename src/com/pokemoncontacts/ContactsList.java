package com.pokemoncontacts;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ContactsList extends ListActivity {

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ContactListAdapter adapter;
	static final String[] PROJECTION = new String[] {ContactsContract.Data.CONTACT_ID,
		ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.PHOTO_THUMBNAIL_URI};
	static final String SELECTION = "((" + 
			ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
			ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" +
			ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1))";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progressBar.setIndeterminate(true);
		getListView().setEmptyView(progressBar);

		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		root.addView(progressBar);

		setArrayListContacts();
		adapter = new ContactListAdapter(this, contacts);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onStart () {
		super.onStart();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}


	private void setArrayListContacts() {
		Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PROJECTION, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
		String prevName = null;
		String name = null;
		while (cursor.moveToNext()) {
			prevName = name;
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			String photoThumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI));
			if (!name.equals(prevName)) {
				contacts.add(new Contact(name, photoThumbnailUri, ID));
				Log.d("Arvin", "contact added " + name);
			}
		}
		cursor.close();
	}

	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ImageGrid.class);
		String ID = contacts.get(position).ID;
		intent.putExtra("ID", ID);
		startActivity(intent); 
    }
	
}