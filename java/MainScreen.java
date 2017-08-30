package com.svasthhealthcare.svasthcms;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.NotificationManager;
import android.app.Notification;

//import svasthhealthcare.cms.DisplayMessageActivity;
import com.svasthhealthcare.svasthcms.R;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
//import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreen extends Activity{
	//public class Main_Screen extends Activity implements OnClickListener{
	// Will check the above error
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
	}
	// Function below is not used. I might have to use it.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main__screen, menu);
		return true;
	}
	// Might have to edit the function below to make the app working correctly if in case it crashes.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	private int motorControlByteValue = 0;
	private String[] fsrValues = new String[64];
	private int[] alarmTriggerArray = new int[64];
	public static int pressureAlarmValue = 150;
	public static int pressureAlarmCounter = 5;
	private final static String TAG = MainScreen.class.getSimpleName(); // Chat.class.getSimpleName()
	private static String wholeData = "";
	private static String Pressure_Str = "";
	public static final String EXTRAS_DEVICE = "EXTRAS_DEVICE";
	public final static String EXTRA_MESSAGE = "com.svasthhealthcare.svasthcms.MESSAGE";
	private BluetoothDevice device;
	//private TextView tv = null; // look into it
	//private EditText et = null; // look into it
	private TextView DoNotShowOnScreen = null;
	private EditText Humidity = null, Temperature = null, HeatIndex = null; // newly inserted
	//private Button btn = null;
	private Button PressureButton = null, MotorButton = null, AlarmButton = null;
	private String HumidityStr = "H", TemperatureStr = "T", HeatIndexStr = "HI", PressureStr = "P"; // newly inserted
	private String mDeviceName;
	private String mDeviceAddress;
	private RBLService mBluetoothLeService;
	private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
	public final int mNotificationId = 001;
	public NotificationManager mNotifyMgr;
	public NotificationCompat.Builder mBuilder;
	/*
	public void highPressureNotification(){
		
		Intent NotificationFSRintent = new Intent(this, FSRScreen.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, NotificationFSRintent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.svasthcmslogo)
			    .setContentTitle("High Pressure Area Detected")
			    .setContentText("Please reposition yourself at regular intervals")
			    .setContentIntent(resultPendingIntent);
	}
	*/
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.svasthcmslogo)
			    //.setLargeIcon below will use a bitmap image to display notification when expanded, currently I don't have it.
			    //.setLargeIcon(getResources().getDrawable(R.drawable.svasthcmslogo).getBitmap())
			    //.setStyle(new Notification.BigPictureStyle()
			    //.bigPicture(R.drawable.svasthcmslogo))
			    .setContentTitle("High Pressure Area Detected")
			    .setContentText("Please reposition yourself")
			    .setDefaults(Notification.DEFAULT_ALL | Notification.FLAG_SHOW_LIGHTS)
			    //.setDefaults(Notification.DEFAULT_LIGHTS)
			    .setLights(0xFFFF0000, 1000, 2000)
			    .setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Please reposition yourself at regular intervals"));
		
		//"Please reposition yourself at regular intervals"
		//tv = (TextView) findViewById(R.id.textView);
		//tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		//et = (EditText) findViewById(R.id.editText);
		
		DoNotShowOnScreen = (TextView) findViewById(R.id.DoNotShow);
		Humidity = (EditText) findViewById(R.id.HumidityValueField);
		Temperature = (EditText) findViewById(R.id.TemperatureValueField);
		HeatIndex = (EditText) findViewById(R.id.HeatIndexValueField);
		PressureButton = (Button) findViewById(R.id.PressureMap);
		MotorButton = (Button) findViewById(R.id.MotorControl);
		AlarmButton = (Button) findViewById(R.id.Alarm);
		
		//Some extra code, it might help to remove the NULL pointer exception. Remove it if it can't.///
		PressureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendFSRData(v);
			}
		});
		
		MotorButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (motorControlByteValue == 0){
					//Motor is turned On
					Toast.makeText(MainScreen.this, "Motor is turned ON", Toast.LENGTH_SHORT).show();
				}
				else if(motorControlByteValue == 1){
					//Motor is turned Off
					Toast.makeText(MainScreen.this, "Motor is turned OFF", Toast.LENGTH_SHORT).show();
				}
				turnMotorOnOff();
			}
		});
		
		AlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToAlarmSettings();
			}
		});
	    //End of some extra code///
		
		/*
		btn = (Button) findViewById(R.id.send);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BluetoothGattCharacteristic characteristic = map
						.get(RBLService.UUID_BLE_SHIELD_TX);

				String str = et.getText().toString() + "\r\n";
				final byte[] tx = str.getBytes();

				characteristic.setValue(tx);
				mBluetoothLeService.writeCharacteristic(characteristic);

				et.setText("");
			}
		});
		*/
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		device = bundle.getParcelable(EXTRAS_DEVICE);

		mDeviceAddress = device.getAddress();
		mDeviceName = device.getName();

		getActionBar().setTitle(mDeviceName);
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
			//NavUtils.navigateUpFromSameTask(this);
			//return true;
			//mBluetoothLeService.initialize();
			System.exit(0);
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/	
	
	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBluetoothLeService.disconnect();
		mBluetoothLeService.close();

		System.exit(0);
	}
	
	public void sendFSRData(View view) {
        // Do something in response to button
    	Intent FSRintent = new Intent(this, FSRScreen.class);
    	String message = Pressure_Str;
    	FSRintent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(FSRintent);
    }
	
	public void turnMotorOnOff(){
		BluetoothGattCharacteristic characteristic = map
				.get(RBLService.UUID_BLE_SHIELD_TX);

		String motorControlValue = Integer.toString(motorControlByteValue) + "\r\n";
		final byte[] motorControlByte = motorControlValue.getBytes();

		characteristic.setValue(motorControlByte);
		mBluetoothLeService.writeCharacteristic(characteristic);
		
		//Toast.makeText(this, "Value send for motor control is: " + motorControlValue, Toast.LENGTH_SHORT).show();
		
		if (motorControlByteValue == 0){
			//Motor is turned On
			//Toast.makeText(this, "Motor is turned ON", Toast.LENGTH_SHORT).show();
			motorControlByteValue = 1;
		}
		else if(motorControlByteValue == 1){
			//Motor is turned Off
			//Toast.makeText(this, "Motor is turned OFF", Toast.LENGTH_SHORT).show();
			motorControlByteValue = 0;
		}
	}
	
	public void goToAlarmSettings(){
		// Do something in response to button
    	Intent Alarmintent = new Intent(this, AlarmSettings.class);
    	String message = Pressure_Str;
    	Alarmintent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(Alarmintent);
	}
	
	private void populateData(String wholeDataChunk){
		String[] set = wholeDataChunk.split("#");
		String oneset  = set[0];

		String[] onesetarray = oneset.split("@");
		HumidityStr = onesetarray[0];
		TemperatureStr = onesetarray[1];
		HeatIndexStr = onesetarray[2];
		PressureStr = onesetarray[3];

		String[] HumidityArray = HumidityStr.split(":");
		String[] TemperatureArray = TemperatureStr.split(":");
		String[] HeatIndexArray = HeatIndexStr.split(":");
		String[] PressureArray = PressureStr.split(":");

		String Humidity_Str = HumidityArray[1];
		String Temperature_C_Str = TemperatureArray[1];
		String Temperature_F_Str = TemperatureArray[2];
		String HeatIndex_Str = String.format("%.2f", (((Float.parseFloat(HeatIndexArray[1]) - 32)*5)/9));
		Pressure_Str = PressureArray[1];
		Humidity.setText(Humidity_Str+" % relative");
		Temperature.setText(Temperature_C_Str+" C / "+Temperature_F_Str+" F");
		HeatIndex.setText(HeatIndex_Str+" C");
		
		//Sending data to FSRScreen
	    //Intent FSRintent = new Intent(this, FSRScreen.class);
	    //String message = Pressure_Str;
	    //FSRintent.putExtra(EXTRA_MESSAGE, message);
	    //startActivity(FSRintent);
		checkIfUserNeedsToBeNotified(Pressure_Str);
	}
	
	public void checkIfUserNeedsToBeNotified(String pressureValues){
		 fsrValues = pressureValues.split(",");
		 for(int i=0; i<fsrValues.length; i++){
		    	if (Integer.valueOf(fsrValues[i])>=pressureAlarmValue){
		    		alarmTriggerArray[i]++;
		    	}
		    }
		 for (int i=0; i<alarmTriggerArray.length; i++){
			 if (alarmTriggerArray[i] >=pressureAlarmCounter){
				 alarmTriggerArray[i]=0;
				 if (motorControlByteValue==0){
					 turnMotorOnOff();
					 Toast.makeText(this, "Motor is turned ON", Toast.LENGTH_SHORT).show();
					 mNotifyMgr.notify(mNotificationId, mBuilder.build());
				 }
				 if (motorControlByteValue==1){
					 Toast.makeText(this, "Motor is already ON", Toast.LENGTH_SHORT).show();
				 }
			 }
		 }
	}
	
	private void getWholeData (String dataChunk){
		//wholeData = "";
		//System.out.println(dataChunk);
		String TrimmedDataChunk = dataChunk.trim();
		//System.out.println(TrimmedDataChunk);
		//String TrimmedDataChunk = dataChunk;
		//int lengthTrimmedDataChunk = TrimmedDataChunk.length();
		//String lastCharacter = TrimmedDataChunk.substring(lengthTrimmedDataChunk-1);
		//System.out.println(lastCharacter);
		wholeData = wholeData.concat(TrimmedDataChunk);
		//System.out.print("Hello Jung, the length is: ");
		//System.out.println(Integer.toString(wholeData.length()));
		if (TrimmedDataChunk.contains("#")){
			//System.out.println(Integer.toString(wholeData.length()));
			//System.out.println(wholeData);
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
			DoNotShowOnScreen.append(data);
			
			final int scrollAmount = DoNotShowOnScreen.getLayout().getLineTop(
					DoNotShowOnScreen.getLineCount())
					- DoNotShowOnScreen.getHeight();
			// if there is no need to scroll, scrollAmount will be <=0
			if (scrollAmount > 0)
				DoNotShowOnScreen.scrollTo(0, scrollAmount);
			else
				DoNotShowOnScreen.scrollTo(0, 0);
		}
	}

			//tv.setVisibility(View.VISIBLE); // added in this version
			//tv.append(data);
			
			// find the amount we need to scroll. This works by
			// asking the TextView's internal layout for the position
			// of the final line and then subtracting the TextView's height
			/*
			final int scrollAmount = tv.getLayout().getLineTop(
					tv.getLineCount())
					- tv.getHeight();
			// if there is no need to scroll, scrollAmount will be <=0
			if (scrollAmount > 0)
				tv.scrollTo(0, scrollAmount);
			else
				tv.scrollTo(0, 0);
			*/

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
