package a2dp.connect;

import java.lang.reflect.Method;
import java.util.Set;
import android.app.Activity;
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
	private String PREFS = "bluetoothlauncher";
	private String LOG_TAG = "A2DP_Connect";
	//private static final String MY_UUID_STRING = "af87c0d0-faac-11de-a839-0800200c9a67";
	Context application;
	int w_id;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		application = getApplication();
		Toast.makeText(application, "At connector", Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		w_id = intent.getIntExtra("ID", 1);
		SharedPreferences preferences = getSharedPreferences(PREFS,
				MODE_WORLD_READABLE);
		String bt_mac = preferences.getString("widget" + w_id, "");
		if(bt_mac != null)
			if(bt_mac.length() == 17)
			{
				connectBluetoothA2dp(bt_mac);
			}
			else
				done();
		else
			done();
		
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
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
