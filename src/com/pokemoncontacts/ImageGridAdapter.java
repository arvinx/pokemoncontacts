package com.pokemoncontacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter {
    private Context mContext;
    private Bitmap [] pokemonImages = new Bitmap [POKEMON_GENERATION.GENERATION_5.getEnd()];

    public ImageGridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return POKEMON_GENERATION.GENERATION_5.getEnd();
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
        	Log.d("ImageViewGrid", "view width" + imageView.getWidth());
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(bitmapForPosition(position));
        return imageView;
    }

    private Bitmap bitmapForPosition(int position) {
    	//TODO put in memory for fast access.
    	if (pokemonImages[position] == null) {
    		pokemonImages[position] = BitmapFactory.decodeFile("/sdcard/Download/assets/" + (position+1) + ".png");
    	}
    	//Log.d("PATH", Environment.getExternalStorageDirectory().getPath());
    	return pokemonImages[position];
    }

}