package fail.toepic.eater.jobscheduleertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fail.toepic.eater.jobscheduleertest.util.LOG;

public class AlarmReceiver2 extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// an Intent broadcast.

		LOG.D("dlwlrma","receive2",intent);
//		Toast.makeText(context, LOG.vargToString(intent),Toast.LENGTH_SHORT).show();
	}
}
