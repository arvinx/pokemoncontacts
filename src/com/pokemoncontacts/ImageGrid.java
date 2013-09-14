package com.pokemoncontacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ImageGrid extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_grid);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageGridAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = getIntent();
				String ID = intent.getStringExtra("ID");
				Toast.makeText(ImageGrid.this, "Image Set", Toast.LENGTH_SHORT).show();
				ContactManager.setContactPicture(ID, PokemonCollection.getImage(position));
				finish();
			}
		});
	}

}
