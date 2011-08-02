package a2dp.connect;

import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String PREFS = "bluetoothlauncher";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toast.makeText(this, "Made it to MainActivity oncreate", Toast.LENGTH_LONG).show();
		Intent intent = getIntent();
		int w_id = intent.getIntExtra("ID", 1);
		
		config(String.valueOf(w_id));
    }
    
public void config(final String id) {
		
		BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
		if (mBTA == null) {
			Toast.makeText(this, "No Bluetooth", Toast.LENGTH_LONG).show();
			return;
		}
		//Toast.makeText(this, "Bluetooth", Toast.LENGTH_LONG).show();
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
				editor.putString("widget" + id, temp[item][1]);
				editor.commit();
			}
		});
		// AlertDialog alert = builder.create();
		// alert.show();
		builder.show();
	}
}