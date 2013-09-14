package com.pokemoncontacts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class PokemonCollection {
	
	private static Bitmap [] pokemonImages = new Bitmap [POKEMON_GENERATION.GENERATION_5.getEnd()];
	private static int [] arrayIndex;
	public static POKEMON_GENERATION [] generationsSelected = new POKEMON_GENERATION [5];
	
	public static Bitmap getImage(int position) {
		position = arrayIndex[position];
    	if (pokemonImages[position] == null) {
    		Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/Download/assets/" + (position+1) + ".png");
    		Bitmap centeredBitmap = ContactManager.centerBitmap(bitmap);
    		pokemonImages[position] = centeredBitmap;
    	}
    	//Log.d("PATH", Environment.getExternalStorageDirectory().getPath());
    	return pokemonImages[position];
	}
	
	public static int getTotalPokemon() {
        int total = 0;
        for (POKEMON_GENERATION generation : generationsSelected) {
        	if (generation != null) {
        		total += generation.getEnd() - generation.getStart();
        	}
        }
        Log.d("TOTAL:", String.valueOf(total));
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
					arrayIndex[index] = generation.getStart() + i;
					index++;
					i++;
				}
			}
		}
	}
}
