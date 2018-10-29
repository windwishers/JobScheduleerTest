package fail.toepic.eater.jobscheduleertest;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sk.pe.group.SimpleAlarm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	// 코드 복잡도를 낮추기 위해 메모리 릭을 방기함.  //실코드에서 이런짓 하지 말 것.
	@SuppressLint("StaticFieldLeak")
	public static TextView a_status;
	@SuppressLint("StaticFieldLeak")
	public static TextView b_status;
	@SuppressLint("StaticFieldLeak")
	public static TextView c_status;

	public static final int A_KEY =1;
	public static final int B_KEY =2;
	public static final int C_KEY =3;
	public static final String KEY_KEY ="KEY_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.a_send).setOnClickListener(this);
		findViewById(R.id.a_cancel).setOnClickListener(this);
		a_status = findViewById(R.id.a_status);
		findViewById(R.id.b_send).setOnClickListener(this);
		findViewById(R.id.b_cancel).setOnClickListener(this);
		b_status = findViewById(R.id.b_status);
		findViewById(R.id.c_send).setOnClickListener(this);
		findViewById(R.id.c_cancel).setOnClickListener(this);
		c_status = findViewById(R.id.c_status);
		findViewById(R.id.cancel_all).setOnClickListener(this);


	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.a_send:
				fire(A_KEY);
				break;
			case R.id.a_cancel:
				cancel(A_KEY);
				break;
			case R.id.b_send:
				fire(B_KEY);
				break;
			case R.id.b_cancel:
				cancel(B_KEY);
				break;
			case R.id.c_send:
				fire(C_KEY);
				break;
			case R.id.c_cancel:
				cancel(C_KEY);
				break;
			case R.id.cancel_all:
				cancelAll();
				break;
		}
		Toast.makeText(this,"hit "+view.getId(),Toast.LENGTH_SHORT).show();
	}

	private void fire(int key){
		clear(key);

		Intent alarmIntent = new Intent(Const.ACTION_ALARMTEST);
		alarmIntent.putExtra(KEY_KEY,key);

		long mills = System.currentTimeMillis()+(6 * 1000 ); //test

        		SimpleAlarm.Set(this,mills,key,alarmIntent
				,new ComponentName(this,AlarmReceiver1.class),new ComponentName("fail.toepic.eater.jobscheduleertest","fail.toepic.eater.jobscheduleertest.AlarmReceiver1")
		);

    }

	private void clear(int key) {
		TextView tv =  getView(key);
		if (tv != null) {
			tv.setText("");
		}
	}

	static public TextView getView(int key) {
		switch (key) {
			case A_KEY:
				return a_status;
			case B_KEY:
				return b_status;
			case C_KEY:
				return c_status;
			default:
				return null;

		}
	}

	private void cancel(int key){
		Intent intent = new Intent(Const.ACTION_ALARMTEST);
		SimpleAlarm.cancel(this,key,intent);
	}

	private void cancelAll(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			SimpleAlarm.cancelAll(this);
		}
	}

//	public class GlobalReceiver extends BroadcastReceiver{
//
//		public GlobalReceiver() {
//		}
//
//		@Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"Fire",Toast.LENGTH_SHORT).show();
//        }
//    }

//
//	public void onClick(View view) {
//		LOG.D("dlwlrma","Test");
////		Intent intent = new Intent(Const.ACTION_ALARMTEST);
////		intent.putExtra("111","222");
////		intent.putExtra("222",333);
////		sendBroadcast(intent);
//
//		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//		Intent alarmIntent = new Intent(Const.ACTION_ALARMTEST);
//		alarmIntent.putExtra("111","알람 Text 1");
//		alarmIntent.putExtra("222",333);
//		alarmIntent.putExtra("444",6425);
//
//
//
//
//		PendingIntent pIntent = PendingIntent.getBroadcast(this, Const.ALARM_KEY, alarmIntent, 0);
//		long mills = System.currentTimeMillis()+(6 * 1000 ); //test
//
//
////		alarmManager.set(AlarmManager.RTC_WAKEUP, mills, pIntent);
//		SimpleAlarm.Set(this,mills,Const.ALARM_KEY,alarmIntent
//				,new ComponentName(this,AlarmReceiver1.class),new ComponentName("fail.toepic.eater.jobscheduleertest","fail.toepic.eater.jobscheduleertest.AlarmReceiver1")
//				,new ComponentName(this,AlarmReceiver1.class),new ComponentName("fail.toepic.eater.jobscheduleertest","fail.toepic.eater.jobscheduleertest.AlarmReceiver2")
//		);
//	}


}
