package a2dp.connect;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.IBluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Connector extends Activity {
	private String PREFS = "bluetoothlauncher";
	private String LOG_TAG = "A2DP_Connect";
	private static final String MY_UUID_STRING = "af87c0d0-faac-11de-a839-0800200c9a67";
	Context application;
	int w_id;
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = getApplication();
		Toast.makeText(application, "At connector", Toast.LENGTH_LONG).show();
		Intent intent = getIntent();
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

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			done();
			super.onPostExecute(result);
		}

		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
		protected void onPreExecute() {}
		
		@Override
		protected Boolean doInBackground(String... arg0) {

			boolean try2 = true;
			
			Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
			BluetoothDevice device = null;
			for (BluetoothDevice dev : pairedDevices) {
				if(dev.getAddress().equalsIgnoreCase(arg0[0]))device = dev;
			}
			if(device == null)return false;
			
			IBluetoothA2dp ibta = getIBluetoothA2dp();
			try {
				Log.d(LOG_TAG, "Here: " + ibta.getSinkPriority(device));
				if (ibta.connectSink(device))
					Toast.makeText(application,
							"Connected 1: " + device.getName(),
							Toast.LENGTH_LONG).show();
				try2 = false;
			} catch (Exception e) {
				Log.e(LOG_TAG, "Error " + e.getMessage());
				try2 = true;
			}

			// if the above does not work, give below a try...
			if (try2) {
				// UUID for your application
				UUID MY_UUID = UUID.fromString(MY_UUID_STRING);
				// Get the adapter
				BluetoothAdapter btAdapter = BluetoothAdapter
						.getDefaultAdapter();
				// The socket
				BluetoothSocket socket = null;
				Log.d(LOG_TAG, "BT connect 1 failed, trying 2...");
				try {
					// Your app UUID string (is also used by the server)
					socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					Log.e(LOG_TAG, "Error " + e.getMessage());
				}
				// For performance reasons
				btAdapter.cancelDiscovery();
				try {
					// Be aware that this is a blocking operation. You probably
					// want
					// to use this in a thread
					socket.connect();
					Toast.makeText(application,
							"Connected 2: " + device.getName(),
							Toast.LENGTH_LONG).show();
				} catch (IOException connectException) {
					// Unable to connect; close the socket and get out
					Log.e(LOG_TAG, "Error " + connectException.getMessage());
					try {
						socket.close();
					} catch (IOException closeException) {
						Log.e(LOG_TAG, "Error " + closeException.getMessage());
					}
					return false;
				}
			}

			// Now manage your connection (in a separate thread)
			// myConnectionManager(socket);

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
		this.finish();
	}
	
}
