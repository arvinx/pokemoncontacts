package com.pokemoncontacts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.util.Log;


public class ContactManager {

	static Context context;
	private static ContactPhotoChangedNotification observer;
	public static List<Integer> randomIndices = new ArrayList<Integer>();
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
		ContactsContract.Data.DISPLAY_NAME};
	static final String[] PROJECTION_BACKUP = new String[] {ContactsContract.Data.CONTACT_ID
		, ContactsContract.Data.PHOTO_URI};
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
				PROJECTION_BACKUP, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
		Integer contactNumber = 0;
		randomIndices.clear();
		while (cursor.moveToNext() && contactNumber <= cursor.getCount()) {
			String rawId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			observer.contactUpdated(contactNumber);
			Integer randomAppendix = getUniqueRandomIndex();
			String image = randomAppendix.toString();
			InputStream is;
			try {
				is = context.getAssets().open(PokemonCollection.getSubAssetDir() + image + ".png");
				Bitmap bmp = BitmapFactory.decodeStream(is);
				Bitmap centeredBitmap = centerBitmap(bmp);
				setContactPicture(rawId, centeredBitmap, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//Log.e("Error Image Load", "could not open");
				//e.printStackTrace();
			}
			contactNumber++;
		}
		cursor.close();

	}
	
	private static Integer getUniqueRandomIndex() {
		Integer randomAppendix = POKEMON_GENERATION.getRandomFileAppendix();
		while (randomIndices.contains(randomAppendix)) {
			randomAppendix = POKEMON_GENERATION.getRandomFileAppendix();
			Log.d("DBG manager", "STUCK IN GETTING INDEX LOOP");
		}
		randomIndices.add(randomAppendix);
		return randomAppendix;
	}

	public static void restoreContacts() {
		ArrayList<String> restoredContacts = new ArrayList<String>();
		File backupDirectory = new File(Environment.getExternalStorageDirectory().toString() +  "/pokemoncontacts_backup");
		 if (backupDirectory.isDirectory()) {
		        String[] children = backupDirectory.list();
		        for (int i=0; i<children.length; i++) {
		        	String savedId = restoreContact(backupDirectory, children[i]);
		        	restoredContacts.add(savedId);
		        	observer.contactUpdated(i+1);
		        }
		 }
		 restoreToDefaultForOtherContacts(restoredContacts);
	}
	
	private static String restoreContact(File backupDirectory, String filename) {
		int index = filename.indexOf(".");
		String ID = filename.substring(0, index);
		String photoLocation = backupDirectory.toString() + "/" + filename;
		Bitmap bitmap = BitmapFactory.decodeFile(photoLocation);
		Bitmap centeredBitmap = ContactManager.centerBitmap(bitmap);
		try {
			setContactPicture(ID, centeredBitmap, false);
		} catch (Exception ie) {
			ie.printStackTrace();
		}
		return ID;
	}
	
	private static void restoreToDefaultForOtherContacts(ArrayList<String> restoredContacts) {
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {ContactsContract.Data.CONTACT_ID}, SELECTION, null, null);
		while (cursor.moveToNext()) {
			String ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			if (!restoredContacts.contains(ID)) {
				InputStream is;
				try {
					is = context.getAssets().open(Constants.IMAGES_OTHER + "default_user.jpg");
					Bitmap bmp = BitmapFactory.decodeStream(is);
					setContactPicture(ID, bmp, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//Log.e("Error Image Load", "could not open");
					e.printStackTrace();
				}
			}
		}
		cursor.close();
	}
	
	public static int numContactsToRestore() {
		File backupDirectory = new File(Environment.getExternalStorageDirectory().toString() +  "/pokemoncontacts_backup");
	    if (backupDirectory.isDirectory()) {
	        return backupDirectory.list().length;
	    }
	    return 0;
	}
	
	public static void backupContactPhotos() {
		File backupDirectory = new File(Environment.getExternalStorageDirectory().toString() +  "/pokemoncontacts_backup");
		deleteDir(backupDirectory);
		backupDirectory.mkdirs();

		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				PROJECTION_BACKUP, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
		Integer contactNumber = 0;
		while (cursor.moveToNext()) {
			String ID = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI));
			if (photoUri != null) {
				Uri uri = Uri.parse(photoUri);
				Bitmap bitmap;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
					File outpt = new File(backupDirectory.toString(), ID + ".png");
					FileOutputStream outptOs = new FileOutputStream( outpt );
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, outptOs);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//Log.e("Error backingup", "error file not found");
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//Log.e("Error backingup", "error io");
					//e.printStackTrace();
				}
			}
			observer.contactUpdated(contactNumber);
			contactNumber++;
		}
		cursor.close();
	}
	
	private static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    return dir.delete();
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
				PROJECTION_BACKUP, SELECTION, null, ContactsContract.Contacts.DISPLAY_NAME);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public static void setContactPicture(String ID, Bitmap picture, boolean isPersonalProfile){
		Uri rawContactUri;
		if (isPersonalProfile) {
			rawContactUri = getPersonalProfilePicture(context, ID);
		} else {
			rawContactUri = getPicture(context, ID);
		}
		setContactPictureHelper(ID, picture, rawContactUri);
	}

	private static void setContactPictureHelper(String ID, Bitmap picture, Uri rawContactUri) {
		ContentResolver cr = context.getContentResolver();
		if(rawContactUri == null){
			//Log.e("rawContactUri", "is null");
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
	
	public static Uri getPersonalProfilePicture(Context context, String ID){
		ContentResolver cr = context.getContentResolver();
		Uri rawContactUri = null;
		Cursor rawContactCursor =  cr.query(ContactsContract.Profile.CONTENT_URI, new String[] {ContactsContract.Data._ID}, ContactsContract.Data._ID + " = " + ID, null, null);
		if(!rawContactCursor.isAfterLast()) {
			rawContactCursor.moveToFirst();
			rawContactUri = ContactsContract.Profile.CONTENT_URI.buildUpon().appendPath(""+rawContactCursor.getLong(0)).build();
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
