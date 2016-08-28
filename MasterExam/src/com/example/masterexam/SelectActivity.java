package com.example.masterexam;

import java.util.Arrays;

import com.example.masterexam.MainActivity.MyClickListener;

import android.support.v7.app.ActionBarActivity;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SelectActivity extends ActionBarActivity {
	ImageButton MyA = null, MyB = null, MyC = null, MyD = null;
	ImageButton startButton = null, leftButton = null,
			rightButton = null, finishButton = null;
	TextView myshow=null;
	int getYear = 0, statePos = 0, answerState = 2;
	int[] answerSum = new int[20];
	String[] answerBlanket = new String[20];
	String[] answerRead1 = new String[5];
	public final static int RESULT_CODE=1; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();		
		answerState=bundle.getInt("answerState");
		init();
		initSum();
	}
	
	void init()
	{
		MyA = (ImageButton) findViewById(R.id.chooseA);
		MyB = (ImageButton) findViewById(R.id.chooseB);
		MyC = (ImageButton) findViewById(R.id.chooseC);
		MyD = (ImageButton) findViewById(R.id.chooseD);
		leftButton = (ImageButton) findViewById(R.id.left);
		rightButton = (ImageButton) findViewById(R.id.right);
		finishButton = (ImageButton) findViewById(R.id.endAnswer);
		leftButton.setOnClickListener(new MyClickListener());
		rightButton.setOnClickListener(new MyClickListener());
		finishButton.setOnClickListener(new MyClickListener());
		MyA.setOnClickListener(new MyClickListener());
		MyB.setOnClickListener(new MyClickListener());
		MyC.setOnClickListener(new MyClickListener());
		MyD.setOnClickListener(new MyClickListener());
		myshow=(TextView)findViewById(R.id.inputSort);
	}
	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.left:
				leftOfChoose();
				break;
			case R.id.right:
				rightOfChoose();
				break;
			case R.id.endAnswer:
				if (!examFull()) {
					new AlertDialog.Builder(SelectActivity.this).setTitle("提示")
							.setMessage("没答完！" + answerReturn())
							.setPositiveButton("我知道了", null).show();
					break;
				}
				else {
					String string1="",string2="";
					for(int i=0;i<answerBlanket.length;i++)
					{
						string1+=answerBlanket[i];
					}
					for(int i=0;i<answerRead1.length;i++)
					{
						string2+=answerRead1[i];
					}
					Intent intent=new Intent();  
					intent.putExtra("answerBlanket", string1);
					intent.putExtra("answerRead1", string2);
					setResult(RESULT_CODE, intent);  
					finish();
				}
				break;
			case R.id.chooseA:
				// groupString = MyA.getText().toString();
				if (answerState == 1) {
					answerBlanket[statePos] = "A";
				} else if (answerState == 2) {
					answerRead1[statePos] = "A";
				}
				answerSum[statePos] = 1;
				setColorButton(0);
				break;
			case R.id.chooseB:
				if (answerState == 1) {
					answerBlanket[statePos] = "B";
				} else if (answerState == 2) {
					answerRead1[statePos] = "B";
				}
				answerSum[statePos] = 1;
				setColorButton(1);
				break;
			case R.id.chooseC:
				if (answerState == 1) {
					answerBlanket[statePos] = "C";
				} else if (answerState == 2) {
					answerRead1[statePos] = "C";
				}
				answerSum[statePos] = 1;
				setColorButton(2);
				break;
			case R.id.chooseD:
				if (answerState == 1) {
					answerBlanket[statePos] = "D";
				} else if (answerState == 2) {
					answerRead1[statePos] = "D";
				}
				answerSum[statePos] = 1;
				setColorButton(3);
				break;
			default:
				break;
			}
		}
	}
	public void leftOfChoose() {
		if (statePos <= 0) {
			leftButton.setVisibility(0x00000004);
			leftButton.setEnabled(false);
			return;
		}

		rightButton.setVisibility(0x00000000);
		rightButton.setEnabled(true);
		statePos--;
		if (answerSum[statePos] == 1) {
			if (answerState == 1)
				setColorButton(checked(answerBlanket[statePos]));
			else {
				setColorButton(checked(answerRead1[statePos]));
			}
		}
		else {
			setColorButton(4);
		}
		myshow.setText("第" + (statePos + 1) + "题");

	}

	public void rightOfChoose() {
		if (statePos >= 19 && answerState == 1) {
			rightButton.setVisibility(0x00000004);
			rightButton.setEnabled(false);
			return;
		} else if (statePos >= 4 && answerState == 2) {
			rightButton.setVisibility(0x00000004);
			rightButton.setEnabled(false);
			return;
		}
		leftButton.setVisibility(0x00000000);
		leftButton.setEnabled(true);
		statePos++;
		if (answerSum[statePos] == 1) {
			if (answerState == 1)
				setColorButton(checked(answerBlanket[statePos]));
			else {
				setColorButton(checked(answerRead1[statePos]));
			}
		}
		else {
			setColorButton(4);
		}
		myshow.setText("第" + (statePos + 1) + "题");
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	void setColorButton(int k)// 0A 1B 2C 3D else quanbubuxuan
	{
		if (k == 0)// A
		{
			MyA.setImageDrawable(getResources()
					.getDrawable(R.drawable.mya1));
			MyB.setImageDrawable(getResources()
					.getDrawable(R.drawable.myb0));
			MyC.setImageDrawable(getResources()
					.getDrawable(R.drawable.myc0));
			MyD.setImageDrawable(getResources()
					.getDrawable(R.drawable.myd0));
		} else if (k == 1)// B
		{
			MyA.setImageDrawable(getResources()
					.getDrawable(R.drawable.mya0));
			MyB.setImageDrawable(getResources()
					.getDrawable(R.drawable.myb1));
			MyC.setImageDrawable(getResources()
					.getDrawable(R.drawable.myc0));
			MyD.setImageDrawable(getResources()
					.getDrawable(R.drawable.myd0));
		} else if (k == 2)// C
		{
			MyA.setImageDrawable(getResources()
					.getDrawable(R.drawable.mya0));
			MyB.setImageDrawable(getResources()
					.getDrawable(R.drawable.myb0));
			MyC.setImageDrawable(getResources()
					.getDrawable(R.drawable.myc1));
			MyD.setImageDrawable(getResources()
					.getDrawable(R.drawable.myd0));
		} else if (k == 3)// D
		{
			MyA.setImageDrawable(getResources()
					.getDrawable(R.drawable.mya0));
			MyB.setImageDrawable(getResources()
					.getDrawable(R.drawable.myb0));
			MyC.setImageDrawable(getResources()
					.getDrawable(R.drawable.myc0));
			MyD.setImageDrawable(getResources()
					.getDrawable(R.drawable.myd1));
		} else {//
			MyA.setImageDrawable(getResources()
					.getDrawable(R.drawable.mya0));
			MyB.setImageDrawable(getResources()
					.getDrawable(R.drawable.myb0));
			MyC.setImageDrawable(getResources()
					.getDrawable(R.drawable.myc0));
			MyD.setImageDrawable(getResources()
					.getDrawable(R.drawable.myd0));
		}
	}

	void myChooseSetEnable(boolean boolin) {
		/*
		 * mygroup.setVisibility(0x00000004); mygroup.setEnabled(false);
		 */
		if (boolin) {
			MyA.setVisibility(0x00000000);
			MyA.setEnabled(true);
			MyB.setVisibility(0x00000000);
			MyB.setEnabled(true);
			MyC.setVisibility(0x00000000);
			MyC.setEnabled(true);
			MyD.setVisibility(0x00000000);
			MyD.setEnabled(true);
		} else {
			MyA.setVisibility(0x00000004);
			MyA.setEnabled(false);
			MyB.setVisibility(0x00000004);
			MyB.setEnabled(false);
			MyC.setVisibility(0x00000004);
			MyC.setEnabled(false);
			MyD.setVisibility(0x00000004);
			MyD.setEnabled(false);
		}
	}
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
	public String answerReturn() {
		int i = 5;
		String myString = "第";
		if (answerState == 1)
			i = 20;
		for (int j = 0; j < i; j++) {
			if (answerSum[j] == 0)
				myString += " " + (j+1);
		}
		myString += "题没答";
		return myString;
	}
	Boolean examFull() {
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
	void initSum() {
		for (int i = 0; i < 20; i++) {
			answerSum[i] = 0;
			answerBlanket[i] = "";
			if (i < 5)
				answerRead1[i] = "";
		}

	}
}
