package com.svasthhealthcare.svasthcms;

import java.util.Timer;
import java.util.TimerTask;

import com.svasthhealthcare.svasthcms.R;

import android.app.Activity;
//import android.app.ListActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner; // Uncomment for Lollipop
//import android.bluetooth.le.ScanCallback; // Uncomment for Lollipop
//import android.bluetooth.le.ScanResult; // Uncomment for Lollipop
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.os.Handler; //not in the original
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
//import android.view.View.OnClickListener; //not called in this. Need to fix.
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
//import android.R.color;

public class Main extends Activity{ //ListActivity changed to Activity
	private BluetoothAdapter mBluetoothAdapter;
	//private BluetoothLeScanner mBluetoothLeScanner; // Uncomment for Lollipop
	private BluetoothDevice mDevice;
	private Button connect = null;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 3000; //Original was 3000
	private Dialog mDialog;
	//private boolean mScanning;
    //private Handler mHandler;
	//private int divierId;
	//private View divider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		//mBluetoothLeScanner = mBluetoothManager.getBluetoothLeScanner();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}
		/*
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		*/

		connect = (Button) findViewById(R.id.btn);
		connect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				scanLeDevice();

				showRoundProcessDialog(Main.this,
						R.layout.loading_process_dialog_anim);
				Timer mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						Intent deviceListIntent = new Intent(
								getApplicationContext(), MainScreen.class); // Chat.class
						Bundle bundle = new Bundle();
						bundle.putParcelable(MainScreen.EXTRAS_DEVICE, mDevice); // Chat.EXTRAS_DEVICE
						deviceListIntent.putExtras(bundle);
						startActivity(deviceListIntent);
						mDialog.dismiss();
						Main.this.finish();
					}
				}, SCAN_PERIOD);

			}
		});
	}
	
	/*
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
	
	public void showRoundProcessDialog(Context mContext, int layout) {
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);
		//divierId = mDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		//divider = mDialog.findViewById(divierId);
		//divider.setBackgroundColor(0x0106000d);
		mDialog.show();
		mDialog.setContentView(layout);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	private void scanLeDevice() {
		new Thread() {

			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mScanCallback); //Error here
				//mBluetoothLeScanner.startScan(mScanCallback);
				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mBluetoothAdapter.stopLeScan(mScanCallback); //Error here
				//mBluetoothLeScanner.stopScan(mScanCallback);
			}
		}.start();
	}

	//private ScanCallback mScanCallback = new ScanCallback(){
	private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback(){
	//public void onScanResult (int callbackType, ScanResult result) {}
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//&& device.getName().contains("Biscuit")
					if (device != null) {
						mDevice = device;
					}
				}
			});
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable BlueTooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		super.onStop();

		this.finish();
	}
}
