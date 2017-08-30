package com.svasthhealthcare.svasthcms;

import com.svasthhealthcare.svasthcms.MainScreen;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//import java.util.Timer;
//import java.util.UUID;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattService;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
import android.content.Context;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Handler;
//import android.os.IBinder;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class FSRScreen extends Activity {

	//private static final int numRows = 8;
	//private static final int numColumns = 8;
	private TextView FSRDataTextView = null;
	private String[] fsrValues = new String[64];
	private Map<String, int[]> rgbValues = new HashMap<String, int[]>();
	private final static String TAG = FSRScreen.class.getSimpleName();
	private static String wholeData = "";
	private static String Pressure_Str = "";
	public static final String EXTRAS_DEVICE = "EXTRAS_DEVICE";
	public final static String EXTRA_MESSAGE = "com.svasthhealthcare.svasthcms.MESSAGE";
	private BluetoothDevice device;
	private String PressureStr = "P"; // newly inserted
	private String mDeviceName;
	private String mDeviceAddress;
	private RBLService mBluetoothLeService;
	private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
	private int[] rgbColor = new int[64];
	
	private final ServiceConnection fsrServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				getGattService(mBluetoothLeService.getSupportedGattService());
			} else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
			}
		}
	};
	
    /*
	public int[] rgb_calculator(int pv){
		 int[] rgb_pattern = {0,0,255};
		 if (pv <= 511)
		 {
			 if (pv % 2 != 0)
			 {
				 pv = pv - 1;
			 }
			 pv = pv/2;
			 rgb_pattern[0] = 0;
			 rgb_pattern[1] = pv;
			 rgb_pattern[2] = 255-pv;
		 }
		 else if (pv > 511)
		 {
			 if (pv % 2 != 0)
			 {
				 pv = pv - 1;
			 }
			 pv = pv/2;
			 pv = pv - 256;
			 rgb_pattern[0] = pv;
			 rgb_pattern[1] = 255-pv;
			 rgb_pattern[2] = 0;
		 }
		 return rgb_pattern;
	 }
	 */
	
	public int[] rgb_calculator(int pv){
		 int[] rgb_pattern = {0,0,255};
		 //Just for showing of the app, mapping has ignored the values above 255.
		    if (pv > 255)
		     {
		    	 pv = 255;
		     }
			 rgb_pattern[0] = pv;
			 rgb_pattern[1] = 255-pv;
			 rgb_pattern[2] = 0;
		 return rgb_pattern;
	 }
	/*
	public int[] rgb_calculator(int pv){
		 int[] rgb_pattern = {0,0,255};
			 if (pv % 4 != 0)
			 {
			 	 int remainder = pv % 4;
				 pv = pv - remainder;
			 }
			 pv = pv/4;
			 rgb_pattern[0] = pv;
			 rgb_pattern[1] = 255-pv;
			 rgb_pattern[2] = 0;
		 return rgb_pattern;
	 }
	*/
	private void createDPI(int[] rgbColorValues){
		//Get the 64 ImageViews 
		ImageView ImageView11 = (ImageView) findViewById(R.id.imageView11);
	    ImageView ImageView12 = (ImageView) findViewById(R.id.imageView12);
	    ImageView ImageView13 = (ImageView) findViewById(R.id.imageView13);
	    ImageView ImageView14 = (ImageView) findViewById(R.id.imageView14);
	    ImageView ImageView15 = (ImageView) findViewById(R.id.imageView15);
	    ImageView ImageView16 = (ImageView) findViewById(R.id.imageView16);
	    ImageView ImageView17 = (ImageView) findViewById(R.id.imageView17);
	    ImageView ImageView18 = (ImageView) findViewById(R.id.imageView18);
	    
	    ImageView ImageView21 = (ImageView) findViewById(R.id.imageView21);
	    ImageView ImageView22 = (ImageView) findViewById(R.id.imageView22);
	    ImageView ImageView23 = (ImageView) findViewById(R.id.imageView23);
	    ImageView ImageView24 = (ImageView) findViewById(R.id.imageView24);
	    ImageView ImageView25 = (ImageView) findViewById(R.id.imageView25);
	    ImageView ImageView26 = (ImageView) findViewById(R.id.imageView26);
	    ImageView ImageView27 = (ImageView) findViewById(R.id.imageView27);
	    ImageView ImageView28 = (ImageView) findViewById(R.id.imageView28);
	    
	    ImageView ImageView31 = (ImageView) findViewById(R.id.imageView31);
	    ImageView ImageView32 = (ImageView) findViewById(R.id.imageView32);
	    ImageView ImageView33 = (ImageView) findViewById(R.id.imageView33);
	    ImageView ImageView34 = (ImageView) findViewById(R.id.imageView34);
	    ImageView ImageView35 = (ImageView) findViewById(R.id.imageView35);
	    ImageView ImageView36 = (ImageView) findViewById(R.id.imageView36);
	    ImageView ImageView37 = (ImageView) findViewById(R.id.imageView37);
	    ImageView ImageView38 = (ImageView) findViewById(R.id.imageView38);
	    
	    ImageView ImageView41 = (ImageView) findViewById(R.id.imageView41);
	    ImageView ImageView42 = (ImageView) findViewById(R.id.imageView42);
	    ImageView ImageView43 = (ImageView) findViewById(R.id.imageView43);
	    ImageView ImageView44 = (ImageView) findViewById(R.id.imageView44);
	    ImageView ImageView45 = (ImageView) findViewById(R.id.imageView45);
	    ImageView ImageView46 = (ImageView) findViewById(R.id.imageView46);
	    ImageView ImageView47 = (ImageView) findViewById(R.id.imageView47);
	    ImageView ImageView48 = (ImageView) findViewById(R.id.imageView48);
	   
	    ImageView ImageView51 = (ImageView) findViewById(R.id.imageView51);
	    ImageView ImageView52 = (ImageView) findViewById(R.id.imageView52);
	    ImageView ImageView53 = (ImageView) findViewById(R.id.imageView53);
	    ImageView ImageView54 = (ImageView) findViewById(R.id.imageView54);
	    ImageView ImageView55 = (ImageView) findViewById(R.id.imageView55);
	    ImageView ImageView56 = (ImageView) findViewById(R.id.imageView56);
	    ImageView ImageView57 = (ImageView) findViewById(R.id.imageView57);
	    ImageView ImageView58 = (ImageView) findViewById(R.id.imageView58);
	    
	    ImageView ImageView61 = (ImageView) findViewById(R.id.imageView61);
	    ImageView ImageView62 = (ImageView) findViewById(R.id.imageView62);
	    ImageView ImageView63 = (ImageView) findViewById(R.id.imageView63);
	    ImageView ImageView64 = (ImageView) findViewById(R.id.imageView64);
	    ImageView ImageView65 = (ImageView) findViewById(R.id.imageView65);
	    ImageView ImageView66 = (ImageView) findViewById(R.id.imageView66);
	    ImageView ImageView67 = (ImageView) findViewById(R.id.imageView67);
	    ImageView ImageView68 = (ImageView) findViewById(R.id.imageView68);
	    
	    ImageView ImageView71 = (ImageView) findViewById(R.id.imageView71);
	    ImageView ImageView72 = (ImageView) findViewById(R.id.imageView72);
	    ImageView ImageView73 = (ImageView) findViewById(R.id.imageView73);
	    ImageView ImageView74 = (ImageView) findViewById(R.id.imageView74);
	    ImageView ImageView75 = (ImageView) findViewById(R.id.imageView75);
	    ImageView ImageView76 = (ImageView) findViewById(R.id.imageView76);
	    ImageView ImageView77 = (ImageView) findViewById(R.id.imageView77);
	    ImageView ImageView78 = (ImageView) findViewById(R.id.imageView78);
	    
	    ImageView ImageView81 = (ImageView) findViewById(R.id.imageView81);
	    ImageView ImageView82 = (ImageView) findViewById(R.id.imageView82);
	    ImageView ImageView83 = (ImageView) findViewById(R.id.imageView83);
	    ImageView ImageView84 = (ImageView) findViewById(R.id.imageView84);
	    ImageView ImageView85 = (ImageView) findViewById(R.id.imageView85);
	    ImageView ImageView86 = (ImageView) findViewById(R.id.imageView86);
	    ImageView ImageView87 = (ImageView) findViewById(R.id.imageView87);
	    ImageView ImageView88 = (ImageView) findViewById(R.id.imageView88);
	    
		ImageView11.setBackgroundColor(rgbColorValues[0]);
		ImageView12.setBackgroundColor(rgbColorValues[1]);
		ImageView13.setBackgroundColor(rgbColorValues[2]);
		ImageView14.setBackgroundColor(rgbColorValues[3]);
		ImageView15.setBackgroundColor(rgbColorValues[4]);
		ImageView16.setBackgroundColor(rgbColorValues[5]);
		ImageView17.setBackgroundColor(rgbColorValues[6]);
		ImageView18.setBackgroundColor(rgbColorValues[7]);
	
		ImageView11.invalidate();
		ImageView12.invalidate();
		ImageView13.invalidate();
		ImageView14.invalidate();
		ImageView15.invalidate();
		ImageView16.invalidate();
		ImageView17.invalidate();
		ImageView18.invalidate();
		
		ImageView21.setBackgroundColor(rgbColorValues[8]);
		ImageView22.setBackgroundColor(rgbColorValues[9]);
		ImageView23.setBackgroundColor(rgbColorValues[10]);
		ImageView24.setBackgroundColor(rgbColorValues[11]);
		ImageView25.setBackgroundColor(rgbColorValues[12]);
		ImageView26.setBackgroundColor(rgbColorValues[13]);
		ImageView27.setBackgroundColor(rgbColorValues[14]);
		ImageView28.setBackgroundColor(rgbColorValues[15]);
		
		ImageView21.invalidate();
		ImageView22.invalidate();
		ImageView23.invalidate();
		ImageView24.invalidate();
		ImageView25.invalidate();
		ImageView26.invalidate();
		ImageView27.invalidate();
		ImageView28.invalidate();
		
		ImageView31.setBackgroundColor(rgbColorValues[16]);
		ImageView32.setBackgroundColor(rgbColorValues[17]);
		ImageView33.setBackgroundColor(rgbColorValues[18]);
		ImageView34.setBackgroundColor(rgbColorValues[19]);
		ImageView35.setBackgroundColor(rgbColorValues[20]);
		ImageView36.setBackgroundColor(rgbColorValues[21]);
		ImageView37.setBackgroundColor(rgbColorValues[22]);
		ImageView38.setBackgroundColor(rgbColorValues[23]);
		
		ImageView31.invalidate();
		ImageView32.invalidate();
		ImageView33.invalidate();
		ImageView34.invalidate();
		ImageView35.invalidate();
		ImageView36.invalidate();
		ImageView37.invalidate();
		ImageView38.invalidate();
		
		ImageView41.setBackgroundColor(rgbColorValues[24]);
		ImageView42.setBackgroundColor(rgbColorValues[25]);
		ImageView43.setBackgroundColor(rgbColorValues[26]);
		ImageView44.setBackgroundColor(rgbColorValues[27]);
		ImageView45.setBackgroundColor(rgbColorValues[28]);
		ImageView46.setBackgroundColor(rgbColorValues[29]);
		ImageView47.setBackgroundColor(rgbColorValues[30]);
		ImageView48.setBackgroundColor(rgbColorValues[31]);
		
		ImageView41.invalidate();
		ImageView42.invalidate();
		ImageView43.invalidate();
		ImageView44.invalidate();
		ImageView45.invalidate();
		ImageView46.invalidate();
		ImageView47.invalidate();
		ImageView48.invalidate();
		
		ImageView51.setBackgroundColor(rgbColorValues[32]);
		ImageView52.setBackgroundColor(rgbColorValues[33]);
		ImageView53.setBackgroundColor(rgbColorValues[34]);
		ImageView54.setBackgroundColor(rgbColorValues[35]);
		ImageView55.setBackgroundColor(rgbColorValues[36]);
		ImageView56.setBackgroundColor(rgbColorValues[37]);
		ImageView57.setBackgroundColor(rgbColorValues[38]);
		ImageView58.setBackgroundColor(rgbColorValues[39]);
		
		ImageView51.invalidate();
		ImageView52.invalidate();
		ImageView53.invalidate();
		ImageView54.invalidate();
		ImageView55.invalidate();
		ImageView56.invalidate();
		ImageView57.invalidate();
		ImageView58.invalidate();
		
		ImageView61.setBackgroundColor(rgbColorValues[40]);
		ImageView62.setBackgroundColor(rgbColorValues[41]);
		ImageView63.setBackgroundColor(rgbColorValues[42]);
		ImageView64.setBackgroundColor(rgbColorValues[43]);
		ImageView65.setBackgroundColor(rgbColorValues[44]);
		ImageView66.setBackgroundColor(rgbColorValues[45]);
		ImageView67.setBackgroundColor(rgbColorValues[46]);
		ImageView68.setBackgroundColor(rgbColorValues[47]);
		
		ImageView61.invalidate();
		ImageView62.invalidate();
		ImageView63.invalidate();
		ImageView64.invalidate();
		ImageView65.invalidate();
		ImageView66.invalidate();
		ImageView67.invalidate();
		ImageView68.invalidate();
		
		ImageView71.setBackgroundColor(rgbColorValues[48]);
		ImageView72.setBackgroundColor(rgbColorValues[49]);
		ImageView73.setBackgroundColor(rgbColorValues[50]);
		ImageView74.setBackgroundColor(rgbColorValues[51]);
		ImageView75.setBackgroundColor(rgbColorValues[52]);
		ImageView76.setBackgroundColor(rgbColorValues[53]);
		ImageView77.setBackgroundColor(rgbColorValues[54]);
		ImageView78.setBackgroundColor(rgbColorValues[55]);
		
		ImageView71.invalidate();
		ImageView72.invalidate();
		ImageView73.invalidate();
		ImageView74.invalidate();
		ImageView75.invalidate();
		ImageView76.invalidate();
		ImageView77.invalidate();
		ImageView78.invalidate();
		
		ImageView81.setBackgroundColor(rgbColorValues[56]);
		ImageView82.setBackgroundColor(rgbColorValues[57]);
		ImageView83.setBackgroundColor(rgbColorValues[58]);
		ImageView84.setBackgroundColor(rgbColorValues[59]);
		ImageView85.setBackgroundColor(rgbColorValues[60]);
		ImageView86.setBackgroundColor(rgbColorValues[61]);
		ImageView87.setBackgroundColor(rgbColorValues[62]);
		ImageView88.setBackgroundColor(rgbColorValues[63]);
		
		ImageView81.invalidate();
		ImageView82.invalidate();
		ImageView83.invalidate();
		ImageView84.invalidate();
		ImageView85.invalidate();
		ImageView86.invalidate();
		ImageView87.invalidate();
		ImageView88.invalidate();
	}
		//GridLayout squareGridColor = (GridLayout) findViewById(R.id.colorSquareGrid);
		//ImageView backgroundImg = (ImageView) findViewById(R.id.imageView11);
        //backgroundImg.setBackgroundColor(Color.rgb(0, 0, 0));
		//for (int row = 0; row < numRows; row++){
			//for (int column = 0; column < numColumns; column++){
				//Field boxNumber = R.id.imageView.getField(Integer.toString(row+1) + Integer.toString(column+1));
				//ImageView boxNumberOfFSR = (ImageView) findViewById(boxNumber);
				//boxNumberOfFSR.setBackgroundColor(rgbColorValues[0]);
		//	}
	//}
			/*
			TableRow gridRow = new TableRow(this);
			gridRow.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT,
					1.0f));
			squareGridColor.addView(gridRow);
			*/
			//for (int column = 0; column < numColumns; column++){
				/*
				ImageView FSRColorBox = (ImageView) findViewById(R.id.colorsquare);;
				FSRColorBox.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT,
						1.0f));
				gridRow.addView(FSRColorBox);
				*/
		//	}
	//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pressure_map);
		//DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		//System.out.println("Width - " + Float.toString(metrics.xdpi));
		//System.out.println("Height - " + Float.toString(metrics.ydpi));
		
		//To allow up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainScreen.EXTRA_MESSAGE);
	    
	    //Get the TextView
	    FSRDataTextView = (TextView) findViewById(R.id.FSRData);
	    
	    // Show the data
	    //FSRDataTextView.setText(message);
	    
	   //Scroll code
    	/*
    	final int scrollAmount = FSRDataTextView.getLayout().getLineTop(
    			FSRDataTextView.getLineCount())
				- FSRDataTextView.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0)
			FSRDataTextView.scrollTo(0, scrollAmount);
		else
			FSRDataTextView.scrollTo(0, 0);
		*/
       //End of Scroll code
		
	    fsrValues = message.split(",");
	    for(int i=0; i<fsrValues.length; i++){
	    	rgbValues.put(Integer.toString(i), rgb_calculator(Integer.valueOf(fsrValues[i])));
	    }
	    
	    for(int i=0; i<fsrValues.length; i++){
	    	int[] individualColorComponent = rgbValues.get(Integer.toString(i)); 
	    	rgbColor[i] = Color.rgb(individualColorComponent[0], individualColorComponent[1], individualColorComponent[2]);
	    	//FSRDataTextView.setText(Integer.toString(rgbColor[i]));
	    }
	    createDPI(rgbColor);
	    keepRecievingData();
	}
	
	public void keepRecievingData(){
		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, fsrServiceConnection, BIND_AUTO_CREATE);
	}
	
	private void populateData(String wholeDataChunk){
		String[] set = wholeDataChunk.split("#");
		String oneset  = set[0];

		String[] onesetarray = oneset.split("@");
		PressureStr = onesetarray[3];

		String[] PressureArray = PressureStr.split(":");

		Pressure_Str = PressureArray[1];
		
		FSRDataTextView.setText(Pressure_Str);
		
		/*
		final int scrollAmount = FSRDataTextView.getLayout().getLineTop(
				FSRDataTextView.getLineCount())
				- FSRDataTextView.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0)
			FSRDataTextView.scrollTo(0, scrollAmount);
		else
			FSRDataTextView.scrollTo(0, 0);
		*/
		keepUpdatingDPI(Pressure_Str);
	}
	
	private void keepUpdatingDPI(String FSRValues){
		fsrValues = FSRValues.split(",");
	    for(int i=0; i<fsrValues.length; i++){
	    	rgbValues.put(Integer.toString(i), rgb_calculator(Integer.valueOf(fsrValues[i])));
	    }
	    
	    for(int i=0; i<fsrValues.length; i++){
	    	int[] individualColorComponent = rgbValues.get(Integer.toString(i)); 
	    	rgbColor[i] = Color.rgb(individualColorComponent[0], individualColorComponent[1], individualColorComponent[2]);
	    	//FSRDataTextView.setText(Integer.toString(rgbColor[i]));
	    }
	    createDPI(rgbColor);
	}
	
	private void getWholeData (String dataChunk){
		String TrimmedDataChunk = dataChunk.trim();
		wholeData = wholeData.concat(TrimmedDataChunk);
		if (TrimmedDataChunk.contains("#")){
			if (wholeData.length() >= 190){ 
				//Can also use starting characters "Humidity" check but let's keep this for now
				populateData(wholeData);
			}
			wholeData = "";
		}
	}
	
	private void displayData(byte[] byteArray) {
		if (byteArray != null) {
			String data = new String(byteArray);
			//System.out.println(data.length());
			//Temperature.setText(Integer.toString(data.length()));
			getWholeData(data);
			/*
			DoNotShowOnScreen.append(data);
			
			final int scrollAmount = DoNotShowOnScreen.getLayout().getLineTop(
					DoNotShowOnScreen.getLineCount())
					- DoNotShowOnScreen.getHeight();
			// if there is no need to scroll, scrollAmount will be <=0
			if (scrollAmount > 0)
				DoNotShowOnScreen.scrollTo(0, scrollAmount);
			else
				DoNotShowOnScreen.scrollTo(0, 0);
			*/
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//mBluetoothLeService.disconnect();
		//mBluetoothLeService.close();
		
		//System.exit(0);
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fsrscreen, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) { //R.id.action_settings		
			//MainScreen.onResume();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		BluetoothGattCharacteristic characteristic = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
		map.put(characteristic.getUuid(), characteristic);

		BluetoothGattCharacteristic characteristicRx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
		mBluetoothLeService.setCharacteristicNotification(characteristicRx,
				true);
		mBluetoothLeService.readCharacteristic(characteristicRx);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

		return intentFilter;
	}
}
