package com.pokemoncontacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ImageGrid extends Activity {
	Context mContext = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageGridAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = getIntent();
				String ID = intent.getStringExtra("ID");
				boolean isPersonalProfile = intent.getBooleanExtra(Constants.IS_PERSONAL_PROFILE, false);
				//Toast.makeText(ImageGrid.this, "Image Set", Toast.LENGTH_SHORT).show();
				ContactManager.setContactPicture(ID, PokemonCollection.getImage(position, mContext), isPersonalProfile);
				finish();
			}
		});
	}

}
