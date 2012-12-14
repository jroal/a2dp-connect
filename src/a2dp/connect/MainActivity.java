package a2dp.connect;

import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String PREFS = "bluetoothlauncher";

	// int w_id = 0;
	int mAppWidgetId;
	Context application;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		application = getApplication();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		// w_id = intent.getIntExtra("ID", 1);
		// Toast.makeText(this, "oncreate " + mAppWidgetId,
		// Toast.LENGTH_LONG).show();
		config(mAppWidgetId);

	}

	public void config(final int id) {

		BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
		if (mBTA == null) {
			Toast.makeText(this, "No Bluetooth", Toast.LENGTH_LONG).show();
			return;
		}
		// Toast.makeText(this, "Bluetooth", Toast.LENGTH_LONG).show();
		int i = 0;
		final String temp[][] = new String[50][2];
		if (mBTA != null) {
			Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
			// If there are paired devices

			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					temp[i][0] = device.getName();
					temp[i][1] = device.getAddress();
					if (i > 48)
						break;
					i++;
				}
			}
		}

		String[] lstring = new String[i];
		for (int j = 0; j < i; j++) {
			lstring[j] = temp[j][0] + " - " + temp[j][1];
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Bluetooth Device");
		builder.setItems(lstring, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Use MODE_WORLD_READABLE and/or MODE_WORLD_WRITEABLE to grant
				// access to other applications
				SharedPreferences preferences = getSharedPreferences(PREFS,
						MODE_WORLD_READABLE);
				SharedPreferences.Editor editor = preferences.edit();
				String ws = String.valueOf(id);
				editor.putString(ws, temp[item][1]);
				editor.commit();
				done();
			}
		});
		builder.show();

	}

	void done() {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(application);
		/*
		 * Button txt = (Button)findResourceById(R.id.WidgetButton);
		 * txt.setText("ID" + mAppWidgetId);
		 */
		Intent intent = new Intent(application, Connector.class);
		intent.putExtra("ID", mAppWidgetId);
		PendingIntent pendingIntent = PendingIntent.getService(application, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the layout for the App Widget and attach an on-click listener
		// to the button
		RemoteViews views = new RemoteViews(application.getPackageName(),
				R.layout.widget_initial_layout);
		views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		// resultValue.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		setResult(RESULT_OK, resultValue);

		//Toast.makeText(this, "done " + mAppWidgetId, Toast.LENGTH_LONG).show();
		finish();
	}
}