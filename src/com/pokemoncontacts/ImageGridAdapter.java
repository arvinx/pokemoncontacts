package com.pokemoncontacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter {
    private Context mContext;

    public ImageGridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        PokemonCollection.setIndices();
        return PokemonCollection.getTotalPokemon();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if (PokemonCollection.pokemonImages[PokemonCollection.arrayIndex[position]] == null) {
        	new ImageLoader(imageView).execute(position);
        } else {
        	imageView.setImageBitmap(PokemonCollection.getImage(position, mContext));
        }
        
        return imageView;
    }
    
    private class ImageLoader extends AsyncTask<Integer, Void, Bitmap> {
    	ImageView imageView;
    	
    	public ImageLoader(ImageView imageView) {
    		this.imageView = imageView;
    	}
    	
		@Override
		protected Bitmap doInBackground(Integer... params) {
			return PokemonCollection.getImage(params[0], mContext);
		}
    	
		@Override
		protected void onPostExecute(Bitmap image) {
			imageView.setImageBitmap(ContactManager.centerBitmap(image));
		}
    }
    
	
}