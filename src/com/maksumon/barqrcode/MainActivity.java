package com.maksumon.barqrcode;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainActivity extends Activity {

	ImageView imageView;
	Bitmap bitmap;
	Button btnEmail;

	int imageViewSize = 0;

	boolean isBarCode = false;
	
	String fileName = "";
	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageView = (ImageView) findViewById(R.id.imageView);
		Display display = getWindowManager().getDefaultDisplay();
		imageViewSize = display.getWidth()-75;
		
		btnEmail = (Button) findViewById(R.id.btnEmail);
	}

	public void scanNow(View view)
	{
		Intent intent = new Intent(getApplicationContext(),com.google.zxing.client.android.CaptureActivity.class);
		startActivity(intent);
	}

	public void generateNow(View view)
	{
		Intent intent = new Intent(getApplicationContext(),GeneratorActivity.class);
		startActivityForResult(intent, 101);
	}

	public void emailNow(View view)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, "Attachment of BarQRCode's " + fileName);
		i.putExtra(Intent.EXTRA_TEXT   , "This " + fileName +" is generated using BarQRCode android app.");
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	private void generateQRCode(String data) {
		Writer writer = new QRCodeWriter();
		String finaldata =Uri.encode(data, "ISO-8859-1");
		try {
			BitMatrix bm = writer.encode(finaldata,BarcodeFormat.QR_CODE, imageViewSize, imageViewSize);
			bitmap = Bitmap.createBitmap(imageViewSize, imageViewSize, Config.ARGB_8888);
			for (int i = 0; i < imageViewSize; i++) {
				for (int j = 0; j < imageViewSize; j++) {
					bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			saveImage(bitmap);
		}
	}
	
	public void generateBarCode(String data){
		Writer c9 = new Code128Writer();
		try {
			BitMatrix bm = c9.encode(data,BarcodeFormat.CODE_128,imageViewSize, imageViewSize/2);
			bitmap = Bitmap.createBitmap(imageViewSize, imageViewSize/2, Config.ARGB_8888);

			for (int i = 0; i < imageViewSize; i++) {
				for (int j = 0; j < imageViewSize/2; j++) {

					bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			saveImage(bitmap);
		}
	}

	private void saveImage(Bitmap finalBitmap) {

		File myDir=new File(Environment.getExternalStorageDirectory().getPath()+"/qr_bar_codes");
		
		if(!myDir.exists()){
			myDir.mkdirs();
		}

		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String dateTime = today.monthDay+"-"+today.month+"-"+today.year;

		if(isBarCode){
			fileName = "BarCode-"+ dateTime +".jpg";
		} else {
			fileName = "QRCode-"+ dateTime +".jpg";
		}

		file = new File (myDir, fileName);
		
		if (file.exists ()){
			file.delete (); 
		}
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			Toast.makeText(this, fileName+" saved", Toast.LENGTH_SHORT).show();

			btnEmail.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, fileName+" unsaved", Toast.LENGTH_SHORT).show();
			
			btnEmail.setVisibility(View.GONE);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent i) 
	{
		super.onActivityResult(requestCode, resultCode, i);

		if (requestCode == 101 && resultCode == RESULT_OK) 
		{
			String txtContent = i.getStringExtra("txtContent");
			isBarCode = i.getBooleanExtra("toggleCode",false);

			if(isBarCode){
				generateBarCode(txtContent);
			} else {
				generateQRCode(txtContent);
			}
		}
	}
}
