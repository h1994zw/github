package com.example.masterexam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	String user = "", password = "", result = "";
	private static final String TAG = "LYGK";
	private final static int REQUEST_CODE=1;
	String selectFenxiString=null;
	Handler mHandler;
	//EditText myshow;
	int getYear = 0, statePos = 0, answerState = 1;
	int[] answerSum = new int[20];
	String[] answerBlanket = new String[20];
	String[] answerRead1 = new String[5];
	String answer20 = null;
	Spinner yearChoose = null, sortChoose = null, partChoose = null;
	ImageButton startButton = null;
	String backString="";
	// RadioGroup mygroup = null;
	String groupString = null;
	private static SQLiteDatabase mydb = null;
	private final static String DATABASE_NAME = "master_e.db";
	private final static String CREAT_TABLE = "(" + "year integer primary key,"
			+ "part1 varchar(20)," + "part2 varchar(20)," + "part3 varchar(5))";
	private final static String CREAT_TABLE_DATA = "create table mydataRate1("
			+ "number integer primary key AUTOINCREMENT,"
			+"myanswer varcher(30),"
			+ "myrate int not null," + "year int,state int,part int"
			+ "date timestamp not null default CURRENT_TIMESTAMP)";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mydb = this.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,
				null);
		try {
			mydb.execSQL("create table answer" + CREAT_TABLE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("-----出现异常------");
		}
		try {
			mydb.execSQL(CREAT_TABLE_DATA);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("-----出现异常------");
		}
		init();
		// myshow = (TextView) findViewById(R.id.show);
		// myshow.setMovementMethod(ScrollingMovementMethod.getInstance());
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// progressDialog.dismiss();
					// myshow.setText(result);
					Toast.makeText(MainActivity.this, "数据更新完成", 3).show();
					break;
				case 1:
					Toast.makeText(MainActivity.this, "数据已存在，无需更新！", 3).show();
					break;
				case 12:
					
					if (answerState == 2) {//5个选项
						int temp = AccuracyMy(answer20);
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("提示")
								.setMessage(
										"你的答案为" + backString
												+ "\n你的正确率为：" + temp + "%")
								.setPositiveButton("我知道了", null).show();
						InsertIntoMyData(temp, yearChoose.getSelectedItem()
								.toString(), answerState,
								(int) partChoose.getSelectedItemId());
					}
					else if(answerState==1){//20个选项
						int temp = AccuracyMy(answer20);
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("提示")
								.setMessage(
										"你的答案为" + backString
												+ "\n你的正确率为：" + temp + "%")
								.setPositiveButton("我知道了", null).show();
						InsertIntoMyData(temp, yearChoose.getSelectedItem()
								.toString(), answerState, 1);
					}
					break;
				case 13:
					Toast.makeText(MainActivity.this, "你没做过啊！", 3).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
				// showToast("数据库是最新的！");
			}
		};
		if (retrunSum(2016) == 0) {
			StartRequestFromPHP();
		}
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
			// myshow.setText(getFromDB(2016));
			int tempState = 1;
			answerState = (int) sortChoose.getSelectedItemId() + 1;
			if(answerState==2)
				tempState = (int) partChoose.getSelectedItemId();
			
			String tempSS = "";
			tempSS += searchFromMyData(yearChoose.getSelectedItem().toString(),
					answerState, tempState);
			if(tempSS.equals("0")){
				mHandler.sendEmptyMessage(13);
				return true;
			}
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, ChartActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("canshu", tempSS);
			bundle.putString("select", selectFenxiString);
			bundle.putInt("state", answerState);
			intent.putExtras(bundle);
			startActivity(intent);
			
			//new AlertDialog.Builder(MainActivity.this).setTitle("提示")
			//		.setMessage(tempSS).setPositiveButton("我知道了", null).show();
			return true;
		} else if (id == R.id.refresh) {
			//StartRequestFromPHP();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void StartRequestFromPHP() {
		new Thread() {
			public void run() {
				try {
					SentInquire();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void SentInquire() {
		if (retrunSum(2016) != 0) {
			mHandler.sendEmptyMessage(1);
			return;
		}
		HttpClient httpClient = new DefaultHttpClient();
		String ServerUrl = "http://139.129.33.103/testproduct/myinquire.php";
		String data_backString = "";
		HttpPost httpRequst = new HttpPost(ServerUrl);
		try {
			HttpResponse response = httpClient.execute(httpRequst);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				data_backString = EntityUtils.toString(response.getEntity());
				System.out.println(data_backString);
				Log.i(TAG, "result = " + data_backString);
				result = "";
			}
			try {
				JSONArray jsonArray = new JSONArray(data_backString);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					addToDB(jsonObject2.getInt("year"),
							jsonObject2.getString("blanket"),
							jsonObject2.getString("compresion"),
							jsonObject2.getString("orderlisr"));
				}
				mHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void addToDB(int year, String part1, String part2, String part3) {
		String temp = "insert into answer values(" + year + ",'" + part1
				+ "','" + part2 + "','" + part3 + "')";
		// Cursor c=null;
		try {
			mydb.execSQL(temp);
			// c = mydb.rawQuery(temp, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static String getFromDB(int year) {
		String temp = "select * from answer where year=" + year;
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
			String tString = c.getString(c.getColumnIndex("part1"));
			return tString;
		}
		return "0";
	}

	public static int retrunSum(int year) {
		String temp = "select * from answer where year=" + year;
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

	public static String returnpart(String yearinput, int choose) {
		String temp = null, selectString = null;
		if (choose == 1)// part1
		{
			temp = "select part1 from answer where year=" + yearinput;
			selectString = "part1";
		} else if (choose == 2)// part2
		{
			temp = "select part2 from answer where year=" + yearinput;
			selectString = "part2";
		} else if (choose == 3)// part3
		{
			temp = "select part3 from answer where year=" + yearinput;
			selectString = "part3";
		}
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
			String tString = c.getString(c.getColumnIndex(selectString));
			return tString;
		}
		return "0";

	}

	public void init() {
		startButton = (ImageButton) findViewById(R.id.enter);
		yearChoose = (Spinner) findViewById(R.id.spinner1);
		sortChoose = (Spinner) findViewById(R.id.spinner2);
		partChoose = (Spinner) findViewById(R.id.spinner3);
		startButton.setOnClickListener(new MyClickListener());
	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.enter:
				answer20 = returnpart(yearChoose.getSelectedItem().toString(),
						(int) sortChoose.getSelectedItemId() + 1);
				answerState = (int) sortChoose.getSelectedItemId() + 1;
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, SelectActivity.class);
				Bundle bundle = new Bundle();  
				bundle.putInt("answerState", answerState);
				
				intent.putExtras(bundle);
				startActivityForResult(intent,REQUEST_CODE);
				
				/*if (answerState == 3) {
					//SetState(0);
					new AlertDialog.Builder(MainActivity.this).setTitle("提示")
							.setMessage("请在输入框中输入答案，大写，A-G！！！")
							.setPositiveButton("我知道了", null).show();
				} else {
					//SetState(1);
					initSum();
					new AlertDialog.Builder(MainActivity.this).setTitle("提示")
							.setMessage("请选择！！！")
							.setPositiveButton("我知道了", null).show();
				}
				startButton.setEnabled(false);
				startButton.setVisibility(0x00000004);*/
				//myshow.setText("第" + (statePos + 1) + "题");
				// myshow.setText(yearChoose.getSelectedItem().toString());
				break;
			default:
				break;
			}
		}
	}

	
	public int AccuracyMy(String anString) {
		int i = 20, count = 0;
		//String sssString = null;
		//sssString = myshow.getText().toString();
		String[] ss1 = backString.split("");
		String[] ss = anString.split("");

		if (answerState == 3) {
			return count * 20;
		} else if (answerState == 1) {
			for (int j = 0; j < i; j++)
			{
				if (ss[j + 1].equals(ss1[j+1]))
					count++;
			}
				
			return count * 5;
		} else {
			i = 5;
			int mynum = (int) partChoose.getSelectedItemId();// 0-1-2-3
			for (int j = 0; j < i; j++)// 0-4,5-9,10-14,15-19
			{
				if (ss[j + mynum * 5 + 1].equals(ss1[j+1]))
					count++;
			}
			return count * 20;
		}
	}

	void initSum() {
		for (int i = 0; i < 20; i++) {
			answerSum[i] = 0;
			answerBlanket[i] = "";
			if (i < 5)
				answerRead1[i] = "";
		}

	}

	/*Boolean examFull() {
		int i = 5;
		if (answerState == 3) {
			if (myshow.getText().toString().length() == 5)
				return true;
			else
				return false;
		}

		if (answerState == 1)
			i = 20;
		for (int j = 0; j < i; j++) {
			if (answerSum[j] == 0)
				return false;
		}
		return true;
	}
*/
	public int checked(String string) {
		if (string.equals("A"))
			return 0;
		else if (string.equals("B"))
			return 1;
		else if (string.equals("C"))
			return 2;
		else
			return 3;
	}

	public String searchFromMyData(String str1, int str2, int str3) {
		String string = "select myrate,myanswer from mydataRate1 where year=" + str1
				+ " and state=" + str2 + " and part=" + str3;
		// String string = "select myrate from mydataRate";
		Cursor c = null;
		selectFenxiString="";
		try {
			c = mydb.rawQuery(string, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int count = c.getCount();
		if (count == 0) {
			return "0";
		}
		String tString = "";
		while (c.moveToNext()) {
/*			if(answerState==1)
				selectFenxiString+=c.getString(c.getColumnIndex("myanswer"));
			else {*/
				selectFenxiString+=c.getString(c.getColumnIndex("myanswer"))+",";

			tString += ""+c.getInt(c.getColumnIndex("myrate"))+"," /*+ "% "+c.getString(c.getColumnIndex("myanswer"))+"\n"*/;
		}
		return tString;
	}
/*
 * 五个选项插入
 * 二十个选项
 */
	public void InsertIntoMyData(int myrate, String str2, int str3, int str4) {
		String string = "insert into mydataRate1(myrate,myanswer,year,state,part) values("
				+ myrate + ",'" + backString+"'," +str2 + "," + str3 + "," + str4 + ")";
		// myrate int not null,"
		// + "year int,state int,part int"
		try {
			mydb.execSQL(string);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			isExit.setTitle("系统提示");
			isExit.setMessage("确定要退出吗");
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			isExit.show();
		}
		else if(keyCode==KeyEvent.KEYCODE_MENU)
		{
			super.openOptionsMenu();  // 调用这个，就可以弹出菜单
		}
		return true;
	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				// mydb.close();
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode==REQUEST_CODE)
		{
			if (resultCode==SelectActivity.RESULT_CODE)
			{
				Bundle bundle=data.getExtras();
				
				if(answerState==1)
					backString=bundle.getString("answerBlanket");
				else {
					backString=bundle.getString("answerRead1");
				}
				mHandler.sendEmptyMessage(12);
				//Toast.makeText(MainActivity.this, backString+answerState, Toast.LENGTH_LONG).show();
			}
		}
	}
}
