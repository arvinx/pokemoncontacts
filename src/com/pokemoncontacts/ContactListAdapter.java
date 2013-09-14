package com.pokemoncontacts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends ArrayAdapter<Contact> {
	private final Context context;
	private final ArrayList<Contact> contacts;
	
	public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
		super(context, R.layout.activity_contacts_list, contacts);
		this.context = context;
		this.contacts = contacts;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_contacts_list, parent, false);
		}
		Log.d("Arvin", "Inflating view");
		Contact contact = contacts.get(position);
		TextView textView = (TextView) convertView.findViewById(R.id.contactName);
		textView.setText(contact.displayName);
		
		ImageView imageView = (ImageView) convertView.findViewById(R.id.contactImage);
		
		if (contact.PhotoThumbnailUri != null) {
			Uri uri = Uri.parse(contact.PhotoThumbnailUri);
			Bitmap bitmap;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return convertView;
	}

}
