package com.example.mypocket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	// public static String appidString="20160326000016685";
	// public static String apppassword="pM71tphqDmKeOF3UOikM";
	public String urlString = "http://fanyi.youdao.com/openapi.do?keyfrom=nevergetme&key=7205790&type=data&doctype=json&version=1.1&q=";
	// String SALT = Long.toString(new Date().getTime());// 随机数，官方提供的是获取时间
	public String SIGN = "";
	Handler mHandler;
	String receiveString = "";
	private static final String TAG = "LYGK";
	TextView mytView = null;
	private static SQLiteDatabase mydb = null;
	private final static String DATABASE_NAME = "exam_apm.db";
	private final static String CREAT_TABLE = "("
			+ "number integer primary key AUTOINCREMENT,"
			+ "word_save varchar(30) unique," + "meaning text not null,"
			+ "times int" + "othernum integer"
			+ "date timestamp not null default CURRENT_TIMESTAMP)";
	Button ShowText = null;
	ImageButton button = null, ADD = null, SeeWhat = null,redoButton=null;
	EditText editText = null;
	String inputString = null;
	int stateSearch = 0;
	private SharedPreferences sp;
	final int RIGHT = 0;
	final int LEFT = 1;
	private GestureDetector gestureDetector;
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** 上一首 按钮点击 ID */
	public final static int BUTTON_PREV_ID = 1;
	/** 播放/暂停 按钮点击 ID */
	public final static int BUTTON_PALY_ID = 2;
	/** 下一首 按钮点击 ID */
	public final static int BUTTON_NEXT_ID = 3;
	public ButtonBroadcastReceiver bReceiver;
	public NotificationManager mNotificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mydb = this.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		mytView = (TextView) findViewById(R.id.show);
		mytView.setMovementMethod(ScrollingMovementMethod.getInstance());
		button = (ImageButton) findViewById(R.id.insure);
		ADD = (ImageButton) findViewById(R.id.add);
		ADD.setEnabled(false);
		ADD.setVisibility(0x00000004);
		ShowText = (Button) findViewById(R.id.buttonshow);
		editText = (EditText) findViewById(R.id.input);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				inputString = editText.getText().toString();
				if (inputString.equals(""))
					return;
				String tempString = "";
				tempString = searchString(inputString);
				if (tempString.equals("0")) {
					StartRequestFromPHP(inputString);
					ADD.setEnabled(true);
					ADD.setVisibility(0x00000000);
				} else {
					ADD.setEnabled(false);
					ADD.setVisibility(0x00000004);
					ShowText.setText(inputString);
					mytView.setText(tempString);
					// mHandler.sendEmptyMessage(2);
				}
			}
		});
		ADD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addToDB(inputString, receiveString);
				Toast.makeText(MainActivity.this, "加入词单完成！", 3).show();
				ADD.setEnabled(false);
				ADD.setVisibility(0x00000004);
			}
		});
		try {
			mydb.execSQL("create table heABC" + CREAT_TABLE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("-----出现异常------");
		}
		
		// StartRequestFromPHP();
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// progressDialog.dismiss();
					ShowText.setText(inputString);
					mytView.setText(receiveString);
					// Toast.makeText(MainActivity.this, "数据已经是最新的！", 3).show();
					break;
				case 2:
					// 此消息代表查询失败；
					stateSearch = 3;
					ADD.setEnabled(false);
					ADD.setVisibility(0x00000004);
					mytView.setText(inputString);
					// Toast.makeText(MainActivity.this, "数据更新完成！", 3).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
				// showToast("数据库是最新的！");
			}
		};
		gestureDetector = new GestureDetector(MainActivity.this,
				onGestureListener);
		// changeEditor(countState);
		stateSearch = sp.getInt("count", 0);
		initButtonReceiver();
		showCustomizeNotification(searchNumberWords(stateSearch));
	}

	void myNotifyExam() {

	}

	// 自定义显示的通知 ，创建RemoteView对象
	private void showCustomizeNotification(String str) {

		CharSequence title = "i am new";
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		Notification noti = new Notification(icon, title, when + 10000);
		noti.flags = Notification.FLAG_INSISTENT;

		// 1、创建一个自定义的消息布局 view.xml
		// 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
		RemoteViews remoteView = new RemoteViews(this.getPackageName(),
				R.layout.view_custom_button);
		// remoteView.setImageViewResource(R.id.custom_song_icon,
		// R.drawable.ic_launcher);
		remoteView.setTextViewText(R.id.tv_custom_song_singer, str);
		noti.flags = Notification.FLAG_ONGOING_EVENT;
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.pervious, intent_prev);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.next, intent_next);

		noti.contentView = remoteView;
		// 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)

		// 这儿点击后简单启动Settings模块
		PendingIntent contentIntent = PendingIntent.getActivity(
				MainActivity.this, 0, new Intent(this, MainActivity.class), 0);
		noti.contentIntent = contentIntent;

		NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mnotiManager.notify(0, noti);
		showMyWord(stateSearch);

		// mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next,
		// intent_next);
	}

	public void initButtonReceiver() {
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	public class ButtonBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {
				// 通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_PREV_ID:
					if (stateSearch <= 1)
						stateSearch = 2;
					showCustomizeNotification(searchNumberWords(--stateSearch));
					Log.d(TAG, "向上");
					break;
				case BUTTON_NEXT_ID:
					if (stateSearch >= retrunCount())
						stateSearch--;
					showCustomizeNotification(searchNumberWords(++stateSearch));
					Log.d(TAG, "向下");
					break;
				default:
					break;
				}
			}
		}
	}

	void myNotify() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "我的通知栏标题";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// 定义下拉通知栏时要展现的内容信息
		Context context = getApplicationContext();
		CharSequence contentTitle = "我的通知栏标展开标题";
		CharSequence contentText = "我的通知栏展开详细内容";
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(1, notification);
	}

	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				new Intent(), flags);
		return pendingIntent;
	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();

			if (x > 0) {
				doResult(RIGHT);
			} else if (x < 0) {
				doResult(LEFT);
			}
			return true;
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	public void doResult(int action) {

		switch (action) {
		case RIGHT:
			if (stateSearch >= retrunCount())
				stateSearch--;
			showMyWord(++stateSearch);
			showCustomizeNotification(searchNumberWords(stateSearch));
			System.out.println("go right");
			break;

		case LEFT:
			if (stateSearch <= 1)
				stateSearch = 2;
			// showMyWord(2);
			showMyWord(--stateSearch);
			showCustomizeNotification(searchNumberWords(stateSearch));
			System.out.println("go left");
			break;
		}
		ADD.setEnabled(false);
		ADD.setVisibility(0x00000004);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//showSum();
			Toast.makeText(MainActivity.this, "List中有"+showSum()+"个词条！", 3).show();
			return true;
		}
		else if(id==R.id.action_delete)
		{
			editText.setText("");
			ADD.setEnabled(false);
			ADD.setVisibility(0x00000004);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void StartRequestFromPHP(final String strsearString) {
		new Thread() {
			public void run() {
				try {
					SentInquire(strsearString);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void SentInquire(String q) {
		HttpClient httpClient = new DefaultHttpClient();
		String ServerUrl = urlString + q;
		String data_backString = "";
		HttpPost httpRequst = new HttpPost(ServerUrl);
		try {
			HttpResponse response = httpClient.execute(httpRequst);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				data_backString = EntityUtils.toString(response.getEntity());
				System.out.println(data_backString);
				Log.i(TAG, "result = " + data_backString);
				try {
					// JSONArray jsonArray = new JSONArray(data_backString);
					JSONObject jsonObject2 = new JSONObject(data_backString);
					// String string=jsonObject2.getString("translation");
					// receiveString=string;

					// JSONObject jsonObject3=new
					// JSONObject(jsonObject2.getString("basic"));
					JSONObject jsonObject3 = (JSONObject) jsonObject2
							.get("basic");
					receiveString = jsonObject3.getString("explains");
					JSONArray jsonArray1 = new JSONArray(receiveString);
					receiveString = "";
					for (int i = 0; i < jsonArray1.length(); i++) {
						receiveString += jsonArray1.getString(i) + "\n";
					}
					// String[] strarray=receiveString.split(",");

					/*
					 * for (int i = 0; i < strarray.length; i++) { //String
					 * mytemp
					 * =strarray[i].substring(strarray[i].indexOf("'"),strarray
					 * [i].indexOf("'")); receiveString+=mytemp+"\n"; }
					 */

					JSONArray jsonArray = jsonObject2.getJSONArray("web");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObjectSon = (JSONObject) jsonArray
								.opt(i);
						receiveString += jsonObjectSon.getString("key");

						receiveString += ":";
						// String valueTemp=jsonObjectSon.getString("value");
						JSONArray valueArray = new JSONArray(
								jsonObjectSon.getString("value"));
						for (int j = 0; j < valueArray.length(); j++) {
							receiveString += valueArray.getString(j);
						}
						receiveString += "\n";
					}
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.out.println("-----出现异常------");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	public static String md5(String string) {

		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	};

	public static String searchString(String str) {
		String temp = "select word_save,meaning from heABC where word_save='"
				+ str + "'";
		// String temp = "select * from heABC";
		Cursor c = null;
		try {
			c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int count = c.getCount();
		if (count == 0) {
			return "0";
		}
		while (c.moveToNext()) {
			String tString = c.getString(c.getColumnIndex("meaning"));
			return tString;
		}
		return "0";
	}

	public static String searchNumber(int number) {
		String temp = "select word_save,meaning from heABC where number="
				+ number;
		// String temp = "select * from heABC";
		Cursor c = null;
		try {
			c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int count = c.getCount();
		if (count == 0) {
			return "0";
		}
		while (c.moveToNext()) {
			String tString = c.getString(c.getColumnIndex("word_save")) + ":\n"
					+ c.getString(c.getColumnIndex("meaning"));
			return tString;
		}
		return "0";
	}

	public static String searchNumberWords(int number) {
		String temp = "select word_save,meaning from heABC where number="
				+ number;
		// String temp = "select * from heABC";
		Cursor c = null;
		try {
			c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int count = c.getCount();
		if (count == 0) {
			return "0";
		}
		while (c.moveToNext()) {
			String tString = c.getString(c.getColumnIndex("word_save"));
			return tString;
		}
		return "0";
	}

	public void addToDB(String words, String meaninput) {
		String temp = "insert into heABC(word_save,meaning) values('" + words
				+ "','" + meaninput + "')";
		// Cursor c=null;
		try {
			mydb.execSQL(temp);
			// c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	int retrunCount() {
		String temp = "select * from heABC";
		Cursor c = null;
		try {
			c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c.getCount();
	}

	void changeEditor(int num) {
		Editor editor = sp.edit();
		editor.putInt("count", num);
		editor.commit();
	}

	void showMyWord(int num) {
		String temp = "";
		if (num > retrunCount()) {
			num = retrunCount();
		}
		temp = searchNumber(num);
		ShowText.setText(searchNumberWords(num));
		mytView.setText(temp);
		changeEditor(num);
	}
	int showSum()
	{
		String temp = "select word_save from heABC";
		// String temp = "select * from heABC";
		Cursor c = null;
		try {
			c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c.getCount();
	}
	String showNoti(int num) {
		String temp = "";
		if (num > retrunCount()) {
			num = retrunCount();
		}
		temp = searchNumberWords(num);
		return temp;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		PackageManager pm = getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(
				new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME), 0);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityInfo ai = homeInfo.activityInfo;
			Intent startIntent = new Intent(Intent.ACTION_MAIN);
			startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startIntent
					.setComponent(new ComponentName(ai.packageName, ai.name));
			startActivitySafely(startIntent);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
		}
	}
}
