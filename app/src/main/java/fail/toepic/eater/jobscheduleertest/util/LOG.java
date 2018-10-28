package fail.toepic.eater.jobscheduleertest.util;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public class LOG {
	private LOG(){}
	//dlwlrma
	public static void D(String tag,Object... obj){

		//		if (!Environ.IS_LOG) {
		//			return;
		//		}

		String loc = getLocString(new Exception(),1);

//		StringBuilder sb = new StringBuilder("");
//		for (Object o : obj) {
//			sb.append(toString(o));
//			sb.append(toString("/"));
//		}

		Log.d(tag,loc + " | "+vargToString(obj));

	}

	public static String vargToString(Object... obj){
		StringBuilder sb = new StringBuilder("");
		for (Object o : obj) {
			sb.append(toString(o));
			sb.append(toString("/"));
		}
		return sb.toString();
	}


	@NonNull
	public static String getLocString(@NonNull Exception e,int depth) {
		StackTraceElement st = e.getStackTrace()[depth];
		return getLocString(st);
	}

	//return string  (filename:lineNumber)
	@NonNull
	private static String getLocString(StackTraceElement st) {
		String loc;
		loc = "("+st.getFileName()+":"+st.getLineNumber()+")";
		return loc;
	}

	private static String toString(Object o) {

		if (o == null) {
			return "";
		}

		try{
			if (o instanceof String){
				return (String)o;
			}else if (o instanceof Intent) {
				return INTENT((Intent) o);
			}else if (o.getClass().isArray()){

				if (o instanceof Object[]) {

					Object[] objs = null ;
					objs = (Object[]) o;
					StringBuilder sb = new StringBuilder("");
					for (Object obj : objs) {
						sb.append(toString(obj));
					}
					return sb.toString();

				}else if(o instanceof int[]){
					int[] ints = (int[]) o;
					StringBuilder sb = new StringBuilder("");
					sb.append("{");
					for (int anInt : ints) {
						sb.append(toString(anInt));
						sb.append(",");
					}
					sb.append("}");
					return sb.toString();
				}

			}
			return o.toString();
		}catch (Exception e){
			Log.e("LOGGER","LOGGER ERROR : ",e);
			return "";
		}
	}


	public static String INTENT(Intent intent){
		if(intent == null ){
			return "[intent is null]" ;
		}else if (intent.getAction() == null){
			return "[intent.getaction is null]";
		}



		Bundle extras = intent.getExtras();
//		if (extras == null || extras.size() == 0) {
//			return "[INTENT : "+intent.getAction()+"]";
//		}
		StringBuilder sb = new StringBuilder();
		if(extras != null && extras.size() > 0){
			for (String key : extras.keySet()) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(key);
				sb.append(",");
				sb.append(extras.get(key));
				sb.append(" : ");
				sb.append(extras.get(key).getClass().getCanonicalName());
			}
		}

		ComponentName component = intent.getComponent();
		String out = "[INTENT : "+intent.getAction()+" | extras : "+sb.toString()+" ]";
		if(component != null){
			out += "[Component : "+component+" ]";
		}

		return  out;

	}

	public static Object TR(int i) {
		return null;
	}
}
