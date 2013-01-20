package a2dp.connect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class Bt_iadl {

	public static String getName(BluetoothDevice device) {
		String dname;
		if (android.os.Build.VERSION.SDK_INT >= 14
				&& android.os.Build.VERSION.SDK_INT <= 16) {
			try {
				IBluetooth ibt = a2dp.connect.Bt_iadl.getIBluetooth();
				dname = ibt.getRemoteAlias(device.getAddress());

			} catch (RemoteException e) {
				dname = device.getName();

				e.printStackTrace();
			}
			if (dname == null)
				dname = device.getName();
		} else
			dname = device.getName();
		return dname;
	}

	public static IBluetoothA2dp getIBluetoothA2dp(Context context) {

		IBluetoothA2dp ibta = null;

		if (android.os.Build.VERSION.SDK_INT <= 16) { 
			try {
				Class<?> c2;
				c2 = Class.forName("android.os.ServiceManager");
				Method m2 = c2.getDeclaredMethod("getService", String.class);
				IBinder b = (IBinder) m2.invoke(null, "bluetooth_a2dp");

				Class<?> c3 = Class.forName("android.bluetooth.IBluetoothA2dp");

				Class[] s2 = c3.getDeclaredClasses();

				Class<?> c = s2[0];
				// printMethods(c);
				Method m = c.getDeclaredMethod("asInterface", IBinder.class);

				m.setAccessible(true);
				ibta = (IBluetoothA2dp) m.invoke(null, b);
			} catch (Exception e) {

			}
		} else {

			ServiceConnection mConnection = null;
			// Context context = getContext();
			if (context.bindService(new Intent(IBluetoothA2dp.class.getName()),
					mConnection, 0)) {
				ibta = (IBluetoothA2dp) mConnection;
			} else {

				// Log.e(TAG, "Could not bind to Bluetooth A2DP Service");
			}

			/*try {
				Class<?> classServiceManager = Class
						.forName("android.content.ServiceConnection");
				Method methodGetService = classServiceManager.getMethod(
						"getService", String.class);
				IBinder binder = (IBinder) methodGetService.invoke(null,
						"bluetooth_a2dp");
				ibta = IBluetoothA2dp.Stub.asInterface(binder);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}*/
		}

		return ibta;
	}

	public static IBluetooth getIBluetooth() {

		IBluetooth ibt = null;

		try {

			Class<?> c2 = Class.forName("android.os.ServiceManager");

			Method m2 = c2.getDeclaredMethod("getService", String.class);
			IBinder b = (IBinder) m2.invoke(null, "bluetooth");

			Class<?> c3 = Class.forName("android.bluetooth.IBluetooth");

			Class[] s2 = c3.getDeclaredClasses();

			Class<?> c = s2[0];
			// printMethods(c);
			Method m = c.getDeclaredMethod("asInterface", IBinder.class);

			m.setAccessible(true);
			ibt = (IBluetooth) m.invoke(null, b);

		} catch (Exception e) {

		}
		return ibt;
	}
}
