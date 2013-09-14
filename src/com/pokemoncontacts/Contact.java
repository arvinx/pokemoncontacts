package com.pokemoncontacts;

public class Contact {
	public String displayName;
	public String PhotoThumbnailUri;
	public String ID;
	
	public Contact(String displayName, String PhotoThumbnailUri, String ID) {
		this.displayName = displayName;
		this.PhotoThumbnailUri = PhotoThumbnailUri;
		this.ID = ID;
	}
}
