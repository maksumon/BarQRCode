package com.maksumon.barqrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ToggleButton;

public class GeneratorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generator);
	}
	
	public void onBackToMain(View view){
		EditText txtContent = (EditText)findViewById(R.id.txtContent);
		ToggleButton toggleCode = (ToggleButton) findViewById(R.id.toggleCode);
		
		Intent i = getIntent();
		i.putExtra("txtContent", txtContent.getText().toString());
		i.putExtra("toggleCode", toggleCode.isChecked());
		
		setResult(RESULT_OK, i);
		
		finish();
	}
	
	public void onExit(View view){
		finish();
	}
}
