package com.pokemoncontacts;

import java.io.ByteArrayOutputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;


public class ContactManager {

	static Context context;
	private static ContactPhotoChangedNotification observer;

	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
		ContactsContract.Data.DISPLAY_NAME};

	// This is the select criteria
	static final String SELECTION = "((" + 
			ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
			ContactsContract.Data.DISPLAY_NAME + " != '' ) AND (" +
			ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1))";

	public static void setObserver(ContactPhotoChangedNotification obj) {
		observer = obj;
	}

	public static void readContactsAndSetPhotos()
	{
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				null, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
				null, ContactsContract.Contacts.DISPLAY_NAME);
		String prevName = null;
		String name = null;
		Integer contactNumber = 0;
		while (cursor.moveToNext()) {
			prevName = name;
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			//String hasnum = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			//String group = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));

			String rawId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			//			
			//			String [] columns = cursor.getColumnNames();
			//			for (String columnName : columns) {
			//				String msg = cursor.getString(cursor.getColumnIndex(columnName));
			//				if (msg == null) {
			//					msg = "null";
			//				}
			//				if (name.equals("temp")) {
			//					Log.d("ContactInfo: ", columnName + "   " + msg);
			//				}
			//			}
			//			Log.d("ContactInfo: ", "----------------------------------------------------------------------------------------");
			String datavr = cursor.getString(cursor.getColumnIndex("data_version"));
			Log.d("Arvin", name + "  " + datavr);

			//String dataVersion = cursor.getString(0); //data_version
			//if (!name.equals(prevName)) {
			observer.contactUpdated(contactNumber);
			Log.d("xContactName:", name + "   " + rawId);

			Integer randomAppendix = POKEMON_GENERATION.getRandomFileAppendix();
			String image = randomAppendix.toString();
			Log.d("RANDNUM", image);

			Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/Download/assets/" + image + ".png");
			Bitmap centeredBitmap = centerBitmap(bitmap);
			//Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendPath("" + cursor.getLong(0)).build();
			setContactPicture(rawId, centeredBitmap);

			contactNumber++;
			//}

		}

	}

	public static Bitmap centerBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap newBitmap;
		if (height > width) {
			int offSetY = (height - width)/2;
			int newHeight = height - offSetY*2;
			newBitmap = Bitmap.createBitmap(bitmap, 0, offSetY, width, newHeight);
		} else {
			int offSetX = (width - height)/2;
			int newWidth = width - offSetX*2;
			if (width > 0) {
				newBitmap = Bitmap.createBitmap(bitmap, offSetX, 0, newWidth, height);
			} else {
				newBitmap = bitmap;
			}
		}
		return newBitmap;
	}

	public static Integer getNumberOfContacts() {

		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				null, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
				null, ContactsContract.Contacts.DISPLAY_NAME);
		return cursor.getCount();
	}

	public static void setContactPicture(String ID, Bitmap picture){
		ContentResolver cr = context.getContentResolver();
		Uri rawContactUri = getPicture(context, ID);
		if(rawContactUri == null){
			Log.e("rawContactUri", "is null");
			return;
		}
		ContentValues values = new ContentValues(); 
		int photoRow = -1; 
		String where = ContactsContract.Data.RAW_CONTACT_ID + " == " + 
				ContentUris.parseId(rawContactUri) + " AND " + Data.MIMETYPE + "=='" + 
				ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'"; 
		Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI,	null, where, null, null); 
		int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID); 
		if(cursor.moveToFirst()){ 
			photoRow = cursor.getInt(idIdx); 
		} 
		cursor.close(); 
		values.put(ContactsContract.Data.RAW_CONTACT_ID, 
				ContentUris.parseId(rawContactUri)); 
		values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1); 
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmapToByteArray(picture)); 
		values.put(ContactsContract.Data.MIMETYPE, 
				ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE); 
		try{
			if(photoRow >= 0){ 
				cr.update(
						ContactsContract.Data.CONTENT_URI, 
						values, 
						ContactsContract.Data._ID + " = " + photoRow, null);
			} else { 
				cr.insert(
						ContactsContract.Data.CONTENT_URI, 
						values); 
			} 
		}catch(SQLiteDiskIOException dIOe){
			//TODO: should show this to the user..
			dIOe.printStackTrace();
		}
	} 

	public static Uri getPicture(Context context, String ID){
		ContentResolver cr = context.getContentResolver();
		Uri rawContactUri = null;
		Cursor rawContactCursor =  cr.query(RawContacts.CONTENT_URI, new String[] {RawContacts._ID}, RawContacts.CONTACT_ID + " = " + ID, null, null);
		if(!rawContactCursor.isAfterLast()) {
			rawContactCursor.moveToFirst();
			rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendPath(""+rawContactCursor.getLong(0)).build();
		}
		rawContactCursor.close();

		return rawContactUri;
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		bitmap.compress(CompressFormat.PNG, 0, baos); 
		return baos.toByteArray();
	}
}
