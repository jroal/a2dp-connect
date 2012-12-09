package a2dp.connect;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.Toast;


public class WidgetProvider extends AppWidgetProvider {

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		//Toast.makeText(context, "onEnabled", Toast.LENGTH_LONG).show();
		/*Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("ID", 1);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);*/
		super.onEnabled(context);
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "onRecieve", Toast.LENGTH_LONG).show();
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
/*		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_initial_layout);
		Intent intent = new Intent(context, Connector.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds[0], views);*/
		//Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show();
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch
			Intent intent = new Intent(context, Connector.class);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0,
					intent, 0);
			intent.putExtra("ID", appWidgetId);
			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_initial_layout);
			views.setOnClickPendingIntent(R.id.WidgetButton, pendingIntent);

			// Tell the AppWidgetManager to perform an update on the current App
			// Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
			
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}