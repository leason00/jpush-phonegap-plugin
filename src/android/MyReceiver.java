package cn.jpush.phonegap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            handlingNotificationOpen(context, intent);
        	//打开自定义的Activity
        	//Intent i = new Intent(context, TestActivity.class);
        	//i.putExtras(bundle);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	//context.startActivity(intent);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}


    /**
	 * 实现自定义推送声音
	 * @param context
	 * @param bundle
     */

	private void processCustomMessage(Context context, Bundle bundle) {

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);



		NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

		notification.setAutoCancel(true)
				.setContentText("自定义推送声音")
				.setContentTitle("极光测试")
				.setSmallIcon(1)
				;



		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
 		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (null != extraJson && extraJson.length() > 0) {


					String sound = extraJson.getString("sound");

					if("test.mp3".equals(sound)){
						//notification.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.test));
						notification.setSound(Uri.parse("http://10.10.252.252/sound.mp3"));
					}


				}
			} catch (JSONException e) {

			}

		}


		Intent mIntent = new Intent(context,TestActivity.class);

		mIntent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

		notification.setContentIntent(pendingIntent);



		notificationManager.notify(2, notification.build());

	}
    /**
         * 点击通知打开app
         * @param context
         * @param intent
         */
	private void handlingNotificationOpen(Context context, Intent intent) {
        //String title = intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        //JPushPlugin.openNotificationTitle = title;

        //String alert = intent.getStringExtra(JPushInterface.EXTRA_ALERT);
        //JPushPlugin.openNotificationAlert = alert;

        //Map<String, Object> extras = getNotificationExtras(intent);
        //JPushPlugin.openNotificationExtras = extras;

        //JPushPlugin.transmitNotificationOpen(title, alert, extras);

        Intent launch = context.getPackageManager().getLaunchIntentForPackage(
                context.getPackageName());
        launch.addCategory(Intent.CATEGORY_LAUNCHER);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(launch);
    }
}
