package com.pokemoncontacts;

import android.graphics.Bitmap;

public class Contact {
	public String displayName;
	public String PhotoThumbnailUri;
	public String ID;
	public Bitmap thumbnailImg;
	
	public Contact(String displayName, String PhotoThumbnailUri, String ID) {
		this.displayName = displayName;
		this.PhotoThumbnailUri = PhotoThumbnailUri;
		this.ID = ID;
		this.thumbnailImg = null;
	}
}
