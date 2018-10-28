package fail.toepic.eater.jobscheduleertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import fail.toepic.eater.jobscheduleertest.util.LOG;

public class AlarmReceiver1 extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// an Intent broadcast.

		LOG.D("dlwlrma","receive",intent);
		Toast.makeText(context, LOG.vargToString(intent),Toast.LENGTH_SHORT).show();



		try {

			if (intent != null) {
				int key = intent.getIntExtra(MainActivity.KEY_KEY,-1);
				TextView tv = MainActivity.getView(key);
				if (tv != null) {
					tv.setText("REVICE");
				}
			}
		} catch (Exception ignored) {

		}
	}
}
