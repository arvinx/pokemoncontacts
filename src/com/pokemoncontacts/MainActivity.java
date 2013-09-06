package com.pokemoncontacts;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



public class MainActivity extends Activity {
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContactManager.context = this;
    }

    public void actionRandomGenerate(View view) 
    {
    	view = (View) view.getParent();
    	TextView editText = (TextView) findViewById(R.id.textView1);
//    	LinearLayout generationOptions = (LinearLayout) findViewById(R.id.selectGenerationView);
//    	
//    	ObjectAnimator fadeIn = ObjectAnimator.ofFloat(generationOptions, "alpha", 0f, 1f);
//    	ObjectAnimator mover = ObjectAnimator.ofFloat(generationOptions, "translationY", -200, 0);
//    	AnimatorSet animatorSet = new AnimatorSet();
//    	animatorSet.play(mover).with(fadeIn);
//    	animatorSet.start();
    	
    	//ContactManager.readContactsAndSetPhotos();
    }
 
}
//String [] columns = cursor.getColumnNames();
//for (String columnName : columns) {
//	String msg = cursor.getString(cursor.getColumnIndex(columnName));
//	if (msg == null) {
//		msg = "null";
//	}
//	Log.d("ContactInfo: ", columnName + "   " + msg);
//}
//Log.d("ContactInfo: ", "----------------------------------------------------------------------------------------");
//i++;
//if (i > 10) {
//	break;
//}
//}
