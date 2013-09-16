package com.pokemoncontacts;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PokemonCollection {
	
	private static Bitmap [] pokemonImages = new Bitmap [POKEMON_GENERATION.getTotalPokemon()];
	private static int [] arrayIndex;
	public static POKEMON_GENERATION [] generationsSelected = new POKEMON_GENERATION [6];
	
	public static Bitmap getImage(int position, Context context) {
		int base = (generationsSelected[5] != null) ?  (POKEMON_GENERATION.GENERATION_5.getEnd() + 1) : 0;
		position = arrayIndex[position];
    	if (pokemonImages[position] == null) {
			InputStream is;
			try {
				String folder = getSubAssetDir();
				is = context.getAssets().open(folder + (position - base + 1) + ".png");
				Bitmap bitmap = BitmapFactory.decodeStream(is);
	    		Bitmap centeredBitmap = ContactManager.centerBitmap(bitmap);
	    		pokemonImages[position] = centeredBitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//Log.e("Error Image Load", "could not open");
				e.printStackTrace();
			}
    	}
    	
    	return pokemonImages[position];
	}
	
	public static String getSubAssetDir() {
		String folder;
		if (generationsSelected[5] != null) {
			folder = Constants.IMAGES_SCREENSHOTS;
		} else {
			 folder = Constants.IMAGES_ICON;
		}
		return folder;
	}
	
	public static int getTotalPokemon() {
        int total = 0;
        for (POKEMON_GENERATION generation : generationsSelected) {
        	if (generation != null) {
        		total += generation.getEnd() - generation.getStart();
        	}
        }
        return total;
	}
	
	public static void setIndices() {
		arrayIndex = new int [getTotalPokemon()];
		int i;
		int index = 0;
		for (POKEMON_GENERATION generation : generationsSelected) {
			if (generation != null) {
				int size = generation.getEnd() - generation.getStart();
				i = 0;
				while (i < size) {
					int base = (generation == POKEMON_GENERATION.GENERATION_X) ?  (POKEMON_GENERATION.GENERATION_5.getEnd() + 1) : 0;
					arrayIndex[index] = generation.getStart() + base + i;
					index++;
					i++;
				}
			}
		}
	}
}
