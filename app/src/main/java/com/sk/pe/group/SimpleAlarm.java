package com.sk.pe.group;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;

import java.util.Date;

import fail.toepic.eater.jobscheduleertest.util.LOG;

import static com.sk.pe.group.SimpleAlarm.JobService.createPersistableBundleFromIntent;

public class SimpleAlarm {

	/* 스테틱 클래스 외부 생성을 막기 위한 프라이빗 생성자. */
	private SimpleAlarm(){}

	/**
	 *  SDK 23이상에서는  잡스케줄러를, 19이상에서는 알람메니져의 setExact를, 그 미만에서는 알람메니저의 Set 을 사용 하여.
	 *  지정된 시각 (1/1000초)에 전달된 브로드케스트가 발생 하도록 한다.
	 * @param mills   // 알람이 발생할 시간? 단위는 밀리초.
	 * @param componentNames  // 명시적 알람 전환을 위한 타겟 지정용. 단 마시멜로우 이상에서만 동작한다.
	 */
	public static void Set(Context context, long mills, int alarmKey, Intent alarmIntent, ComponentName... componentNames){

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			SetJobScheduler(context,mills,alarmKey,alarmIntent,componentNames);
		}else{

			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			if (alarmManager == null) {
				return;
			}
			PendingIntent pIntent = PendingIntent.getBroadcast(context, alarmKey, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			PendingIntent pIntent = PendingIntent.getBroadcast(context, alarmKey, alarmIntent,0);
			if (Build.VERSION.SDK_INT >= 19) {
				alarmManager.setExact(AlarmManager.RTC_WAKEUP, mills, pIntent);
			} else {
				alarmManager.set(AlarmManager.RTC_WAKEUP, mills, pIntent);
			}
		}
	}

	/**
	 * 마시멜로우 이상에서 잡스케줄러를 이용한 알람 설정을 위하여 작성 된 함수
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private static void SetJobScheduler(Context context, long mills, int alarmKey, Intent alarmIntent, ComponentName[] componentNames) {

		LOG.D("dlwlrma","SetJobSchedular : ",alarmIntent);

		long delay = mills - getNowTimemillis();
		if(delay < 0L){
			delay = 10000L;
		}

		PersistableBundle bundle = createPersistableBundleFromIntent(alarmIntent,componentNames);

		if (bundle == null) {
			return;
		}

		JobInfo job = new JobInfo.Builder(alarmKey,new ComponentName(context,JobService.class))
				.setExtras(bundle)
				.setMinimumLatency(delay)
				.build();
		JobScheduler mJobService = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

		if (mJobService == null) {
			return;
		}
		mJobService.schedule(job);
	}



	/**
	 * @return 현재 시각(milli second)을 반환 한다.
	 */
	private static long getNowTimemillis() {
		return new Date().getTime();
	}

	/**
	 * 인텐트와 알람키를 받아서 해당 알람을 취소 한다.  알람인텐트(엑션값만 영향을 주는 것으로 추정)와 알람키를 키로 하여 알람을 취소 합니다.
	 * @param context  알람메니저나 잡스케줄러를 생성하는데 사용되는 컨텍스트 값입니다.
	 * @param alarmKey  취소할 알람 키값을 지정합니다.
	 * @param alarmIntent  취소할 알람의 인텐트를 지정합니다.
	 */
	public static void cancel(Context context,int alarmKey,Intent alarmIntent) {

		if (context == null || alarmIntent == null) {
			return;
		}
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, alarmKey, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (am != null) {
			am.cancel(pIntent);
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			JobScheduler mJobService = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
			if (mJobService != null) {
				mJobService.cancel(alarmKey);
			}
		}

	}

	/**
	 *  모든 알람을 취소합니다. 단 알람매니저는 해당 기능을 지원하지 않기 때문에 -JobScheduler를 사용하는 - APK 21 이상 에서만 정상 취소 됩니다.
	 *  NOTE : 만약 해당 기능이 필요하다면 자체적으로 알람에 대한 키값을 트레킹 하거나. WorkManager를 사용하는 것을 고려 할 것.
	 */
	@RequiresApi(android.os.Build.VERSION_CODES.M)
	public static void cancelAll(Context context) {
//		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//		if (am != null) {
//			//알람 매니져 일괄 삭제 지원 안함.
//		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			JobScheduler mJobService = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
			if (mJobService != null) {
				mJobService.cancelAll();
			}
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.M)
	public static class JobService extends android.app.job.JobService{

		private static String KEY_ACTION = "ACTION";
		private static String KEY_COMPONENT_NAME = "COMPONENT_NAME";
		private static String KEY_EXTRAS = "EXTRAS";

		@Override
		public boolean onStartJob(JobParameters jobParameters) {

			PersistableBundle componentNames = jobParameters.getExtras()
					.getPersistableBundle(KEY_COMPONENT_NAME);

			assert componentNames != null;
			for (String componentName : componentNames.keySet()) {
				Intent intent = jobParametersToIntent(jobParameters);
				if (componentNames.getString(componentName) != null) {
					//noinspection ConstantConditions  //뭐라는지 모르겠음.
					intent.setComponent(new ComponentName(componentNames.getString(componentName),componentName));
				}
				getApplication().sendBroadcast(intent);
			}

			// onStartJob 이후에 진행잘 작업이 없음으로 false를 반환 한다.
			return false;
		}



		@Override
		public boolean onStopJob(JobParameters jobParameters) {
			// onStartJob 이 false 를 반환 했음으로 실제로 사용되지 않는코드다.
			return false;
		}

		@RequiresApi(api = Build.VERSION_CODES.M)
		public static PersistableBundle createPersistableBundleFromIntent(Intent intent,
																		  ComponentName[] componentNames) {

			PersistableBundle pb = new PersistableBundle();

			String action = intent.getAction();
			// action
			if (action != null) {

				pb.putString(KEY_ACTION,action);
			}
			// 이하 미지원 필요시 작성 할 것.
			// type
			// add category
			// set flag
			// set data

			// componentName
			PersistableBundle cnBundle = new PersistableBundle();
			for (ComponentName componentName : componentNames) {
				cnBundle.putString(componentName.getClassName(),componentName.getPackageName());  // 한패키지에 여러 클래스가 지정 될 수 있기 때문에  클래스를 키로 패키지를 값으로 전달한다.
			}
			if (cnBundle.size() > 0) {
				pb.putPersistableBundle(KEY_COMPONENT_NAME,cnBundle);
			}


//			// extras.
			Bundle extras = intent.getExtras();
			if(extras != null && extras.size() > 0){
				PersistableBundle extraPersistableBundle = BUNDLE.toPersistable(extras);
				pb.putPersistableBundle(KEY_EXTRAS,extraPersistableBundle);
			}

			return pb;
		}

		@RequiresApi(api = Build.VERSION_CODES.M)
		public static Intent jobParametersToIntent(JobParameters jobParameters) {

			PersistableBundle extras = jobParameters.getExtras();
			String action = extras.getString(KEY_ACTION);

			Intent intent;
			if (action != null && !action.trim().isEmpty()) {
				intent = new Intent(action);
			}else{
				intent = new Intent();
			}


			PersistableBundle persistExtra = extras.getPersistableBundle(KEY_EXTRAS);

			intent.putExtras(BUNDLE.fromPersistable(persistExtra));


			return intent;
		}

	}

	@SuppressWarnings("WeakerAccess")
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public static class BUNDLE{
		public static PersistableBundle toPersistable(Bundle bundle){
			PersistableBundle pb = new PersistableBundle();

			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				if (isPersistableBundleType(value)) {
					putIntoBundle(pb, key, value);
				}
			}

			return pb;
		}

		public static Bundle fromPersistable(PersistableBundle persistable){
			Bundle b = new Bundle();
			b.putAll(persistable);
			return b;
		}

		/**
		 * Checks if the specified object can be put into a {@link PersistableBundle}.
		 *
		 * @see <a href="https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/PersistableBundle.java#49">PersistableBundle Implementation</a>
		 */
		public static boolean isPersistableBundleType(Object value) {
			return ((value instanceof PersistableBundle) ||
					(value instanceof Integer) || (value instanceof int[]) ||
					(value instanceof Long) || (value instanceof long[]) ||
					(value instanceof Double) || (value instanceof double[]) ||
					(value instanceof String) || (value instanceof String[]) ||
					(value instanceof Boolean) || (value instanceof boolean[])
			);
		}

		/**
		 * Attempts to insert the specified key value pair into the specified bundle.
		 *
		 * @throws IllegalArgumentException if the value type can not be put into the bundle.
		 */
		public static void putIntoBundle(BaseBundle baseBundle, String key, Object value) {
			if (value == null) {
				throw new IllegalArgumentException("Unable to determine type of null values");
			} else if (value instanceof Integer) {
				baseBundle.putInt(key, (int) value);
			} else if (value instanceof int[]) {
				baseBundle.putIntArray(key, (int[]) value);
			} else if (value instanceof Long) {
				baseBundle.putLong(key, (long) value);
			} else if (value instanceof long[]) {
				baseBundle.putLongArray(key, (long[]) value);
			} else if (value instanceof Double) {
				baseBundle.putDouble(key, (double) value);
			} else if (value instanceof double[]) {
				baseBundle.putDoubleArray(key, (double[]) value);
			} else if (value instanceof String) {
				baseBundle.putString(key, (String) value);
			} else if (value instanceof String[]) {
				baseBundle.putStringArray(key, (String[]) value);
			} else if (value instanceof Boolean) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
					baseBundle.putBoolean(key, (boolean) value);
				}
			} else if (value instanceof boolean[]) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
					baseBundle.putBooleanArray(key, (boolean[]) value);
				}
			} else if (value instanceof PersistableBundle) {
				baseBundle.putAll((PersistableBundle) value);
			}
		}

	}
}
