package a2dp.connect;

import java.lang.reflect.Method;
import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Connector extends Service {
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
	
	private String PREFS = "bluetoothlauncher";
	private String LOG_TAG = "A2DP_Connect";
	//private static final String MY_UUID_STRING = "af87c0d0-faac-11de-a839-0800200c9a67";
	Context application;
	int w_id;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			w_id = extras.getInt("ID", 0);
			//w_id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			//Toast.makeText(application, "ID = " + w_id, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(application, "Oops", Toast.LENGTH_LONG).show();
			done();
		}
		
		SharedPreferences preferences = getSharedPreferences(PREFS,
				MODE_WORLD_READABLE);
		String bt_mac = preferences.getString(String.valueOf(w_id), "");
		if(bt_mac != null)
			if(bt_mac.length() == 17)
			{
				Toast.makeText(application, getString(R.string.Connecting) + "  " + bt_mac, Toast.LENGTH_LONG).show();
				connectBluetoothA2dp(bt_mac);
			
			}else{
				Toast.makeText(application, getString(R.string.InvalidDevice) + " " + bt_mac, Toast.LENGTH_LONG).show();
				done();
			}
				
		else{
			Toast.makeText(application, getString(R.string.NullDevice), Toast.LENGTH_LONG).show();
			done();
		}
		super.onStart(intent, startId);
	}
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */

	public void onCreate() {
		//super.onCreate();
		application = getApplication();
		

		//w_id = itent.getIntExtra("ID", 1);

			
		
	}
	

	private void connectBluetoothA2dp(String device) {
		new ConnectBt().execute(device);
	}

	private class ConnectBt extends AsyncTask<String, Void, Boolean> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			done();
		}

		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
			if (mBTA == null || !mBTA.isEnabled())
				return false;

			Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
			BluetoothDevice device = null;
			for (BluetoothDevice dev : pairedDevices) {
				if (dev.getAddress().equalsIgnoreCase(arg0[0]))
					device = dev;
			}
			if (device == null)
				return false;
/*			mBTA.cancelDiscovery();
			mBTA.startDiscovery();*/

			if (android.os.Build.VERSION.SDK_INT < 11) {

				IBluetoothA2dp ibta = getIBluetoothA2dp();
				try {
					Log.d(LOG_TAG, "Here: " + ibta.getSinkPriority(device));
					if (ibta != null && ibta.getSinkState(device) == 0)
						ibta.connectSink(device);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error " + e.getMessage());
				}
			} else {
				IBluetoothA2dp ibta = getIBluetoothA2dp();
				try {
					Log.d(LOG_TAG, "Here: " + ibta.getPriority(device));
					if (ibta != null && ibta.getConnectionState(device) == 0)
						ibta.connect(device);
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error " + e.getMessage());
				}
			}
			return true;
		}

		private IBluetoothA2dp getIBluetoothA2dp() {

			IBluetoothA2dp ibta = null;

			try {

				Class<?> c2 = Class.forName("android.os.ServiceManager");

				Method m2 = c2.getDeclaredMethod("getService", String.class);
				IBinder b = (IBinder) m2.invoke(null, "bluetooth_a2dp");

				Log.d(LOG_TAG, "Test2: " + b.getInterfaceDescriptor());

				Class<?> c3 = Class.forName("android.bluetooth.IBluetoothA2dp");

				Class[] s2 = c3.getDeclaredClasses();

				Class<?> c = s2[0];
				// printMethods(c);
				Method m = c.getDeclaredMethod("asInterface", IBinder.class);

				m.setAccessible(true);
				ibta = (IBluetoothA2dp) m.invoke(null, b);

			} catch (Exception e) {
				Log.e(LOG_TAG, "Error " + e.getMessage());
			}
			return ibta;
		}
	}

	private void done(){
		//this.finish();
		this.stopSelf();
		
	}






	
}
