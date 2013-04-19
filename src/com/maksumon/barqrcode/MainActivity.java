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
	
	int imageViewSize;
	
	boolean isBarCode = false;
	boolean isQRCode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (ImageView) findViewById(R.id.imageView);
		Display display = getWindowManager().getDefaultDisplay();
		imageViewSize = display.getWidth()-50;
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
		
		//String qrData = "Http://www.google.coM";
//		int qrCodeDimention = 500;
//
//		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
//				Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
//
//		try {
//			Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
//			imgQRCode.setImageBitmap(bitmap);
//			//MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QRCode" , "MAKSumon");
//			saveImage(bitmap);
//		} catch (WriterException e) {
//			e.printStackTrace();
//		}
		
		//generateBarCode(qrData);
		//generateQRCode(qrData);
	}
	
	//Change the writers as per your need
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

		String fname = "";
		
		File myDir=new File(Environment.getExternalStorageDirectory().getPath()+"/qr_bar_codes");
		myDir.mkdirs();
		
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String dateTime = today.monthDay+"-"+today.month+"-"+today.year;
		
		if(isBarCode){
			fname = "BarCode-"+ dateTime +".jpg";
		} else if(isQRCode) {
			fname = "QRCode-"+ dateTime +".jpg";
		}
		
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete (); 
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			Toast.makeText(this, fname+" saved", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, fname+" unsaved", Toast.LENGTH_SHORT).show();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent i) 
	{
		super.onActivityResult(requestCode, resultCode, i);

		if (requestCode == 101 && resultCode == RESULT_OK) 
		{
			String txtContent = i.getStringExtra("txtContent");
			isBarCode = i.getBooleanExtra("barCode",false);
			isQRCode = i.getBooleanExtra("qrCode",false);
			
			if(isBarCode){
				generateBarCode(txtContent);
			} else if(isQRCode){
				generateQRCode(txtContent);
			}
		}
	}
}
