package com.example.masterexam;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class ChartActivity extends Activity {
	private LineChartView line_chart_view, select_chart;
	private LineChartData data, data2;
	private LineChartData[] data1 = new LineChartData[5];
	LineChartView chart = null;
	LinearLayout mylayout = null;
	private LinearLayout mGallery;
	private int[] mImgIds;
	private LayoutInflater mInflater;
	String mysString = null;
	String select = null;
	int stateInt=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chart);
		mInflater = LayoutInflater.from(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		stateInt=bundle.getInt("state");
		mysString = bundle.getString("canshu");
		select = bundle.getString("select");
		initView();
		// chart=new LineChartView(this);
		// mylayout=(LinearLayout)findViewById(R.id.liner);
		// line_chart_view = (LineChartView) findViewById(R.id.line_chart);
		// select_chart = (LineChartView) findViewById(R.id.line_select1);
		// mylayout.addView(chart);
		/*
		 * List<Line> lines = initLine(); generateTempoData(); Viewport viewport
		 * = initViewPort(); Viewport viewport1 = initViewPort(1);
		 */
		/*
		 * select_chart.setLineChartData(data);
		 * select_chart.setMaximumViewport(viewport1);
		 * select_chart.setCurrentViewport(viewport1);
		 */
		/*
		 * for(int i=0;i<5;i++) { line[i]=initLine(i);
		 * data1[i]=initData(line[i],0);
		 * select_chart[i].setLineChartData(data1[i]);
		 * select_chart[i].setMaximumViewport(viewport1);
		 * select_chart[i].setCurrentViewport(viewport1); }
		 */
		// data2 = initData(lines);
		/*
		 * line_chart_view.setLineChartData(data2); //
		 * line_chart_view.setLineChartData(data);
		 * line_chart_view.setMaximumViewport(viewport);
		 * line_chart_view.setCurrentViewport(viewport);
		 */

	}

	private void initView() {
		mGallery = (LinearLayout) findViewById(R.id.id_gallery);
		for (int i = 0; i < 1; i++) {
			List<Line> lines = initLine();
			// generateTempoData();
			Viewport viewport = initViewPort();

			data2 = initData(lines);
			View view = mInflater.inflate(R.layout.show_chart, mGallery, false);
			LineChartView myviewChartView = (LineChartView) view
					.findViewById(R.id.newchart);
			myviewChartView.setLineChartData(data2); //
			myviewChartView.setMaximumViewport(viewport);
			myviewChartView.setCurrentViewport(viewport);

			mGallery.addView(view);
		}
		Viewport viewport1 = initViewPort(1);
		for (int i = 0; i < 35-stateInt*15; i++) {
			List<Line> line = initLine(i);
			data2 = initData(line,i);
			View view = mInflater.inflate(R.layout.show_chart, mGallery, false);
			LineChartView myviewChartView = (LineChartView) view
					.findViewById(R.id.newchart);
			myviewChartView.setLineChartData(data2); //
			myviewChartView.setMaximumViewport(viewport1);
			myviewChartView.setCurrentViewport(viewport1);

			mGallery.addView(view);
		}
	}

	/**
	 * ����4���߾�
	 */
	private Viewport initViewPort() {
		Viewport viewport = new Viewport();
		String[] ss = mysString.split(",");
		viewport.top = 100;
		viewport.bottom = 0;
		viewport.left = 0;
		viewport.right = ss.length;
		return viewport;
	}

	private Viewport initViewPort(int number) {
		Viewport viewport = new Viewport();
		String[] ss = mysString.split(",");
		viewport.top = 20;
		viewport.bottom = 0;
		viewport.left = 0;
		viewport.right = ss.length;
		return viewport;
	}

	/**
	 * ��ʼ��������
	 * 
	 * @return
	 */
	private List<Line> initLine() {
		List<Line> lineList = new ArrayList<>();
		String[] ss1 = mysString.split(",");
		String[] selectIn = select.split(",");
		List<PointValue> pointValueList = new ArrayList<>();
		for (int i = 0; i < ss1.length; i++) {
			PointValue pointValue = new PointValue(i, Integer.parseInt(ss1[i]));
			pointValueList.add(pointValue);
		}
		Line line = new Line(pointValueList);
		line.setColor(getResources().getColor(R.color.viewfinder_laser));
		line.setShape(ValueShape.CIRCLE);
		lineList.add(line);
		return lineList;
	}

	private List<Line> initLine(int number) {
		List<Line> lineList = new ArrayList<>();
		String[] ss1 = mysString.split(",");
		String[] selectIn = select.split(",");
		List<PointValue> pointValueList = new ArrayList<>();
		for (int i = 0; i < ss1.length; i++) {//20-1  5-2    35-select*15
			PointValue pointValue = new PointValue(i,
					changeInt(selectIn[i].charAt(number)));
			pointValueList.add(pointValue);
		}
		Line line = new Line(pointValueList);
		line.setColor(getResources().getColor(R.color.viewfinder_laser));
		line.setShape(ValueShape.CIRCLE);
		lineList.add(line);
		return lineList;
	}

	private void generateTempoData() {
		// I got speed in range (0-50) and height in meters in range(200 -
		// 300). I want this chart to display both
		// information. Differences between speed and height values are
		// large and chart doesn't look good so I need
		// to modify height values to be in range of speed values.

		// The same for displaying Tempo/Height chart.
		String[] ss1 = mysString.split(",");
		String[] selectIn = select.split(",");
		Line[] line = new Line[5];
		// Color color[5]={Color.BLACK,};
		List<PointValue>[] values = new List[5];
		List<Line> lines = new ArrayList<Line>();

		// Height line, add it as first line to be drawn in the background.
		for (int i = 0; i < 5; i++) {
			values[i] = new ArrayList<PointValue>();
		}

		for (int i = 0; i < ss1.length; ++i) {
			// Some random height values, add +200 to make line a little
			// more natural
			for (int number = 0; number < 5; number++) {
				values[number].add(new PointValue(i, changeInt(selectIn[i]
						.charAt(number))));
			}

		}

		for (int i = 0; i < 5; i++) {
			line[i] = new Line(values[i]);
			// line[i].setHasPoints(false);
			// line[i].setFilled(true);
			// line[i].setStrokeWidth(1);
			line[i].setShape(ValueShape.CIRCLE);
		}
		line[0].setColor(Color.BLACK);// ��
		line[2].setColor(Color.BLUE);// ��
		line[3].setColor(Color.RED);// ��
		line[1].setColor(Color.GRAY);// ��
		line[4].setColor(Color.GREEN);// ��
		for (int i = 0; i < 5; i++)
			lines.add(line[i]);
		// Tempo line is a little tricky because worse tempo means bigger
		// value for example 11min per km is worse
		// than 2min per km but the second should be higher on the chart. So
		// you need to know max tempo and
		// tempoRange and set
		// chart values to minTempo - realTempo.

		// Data and axes
		data = new LineChartData(lines);
		Axis axisX = new Axis();
		Axis axisY = new Axis().setHasLines(true);
		axisX.setName("1��2��3��4��5��");
		// axisX,set
		// ǰ���ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setPrependedText("aaaa".toCharArray()));
		// ����ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setAppendedText("aaaa".toCharArray()));
		// axisX.setFormatter(new SimpleAxisValueFormatter());
		axisY.setName("ѡ��");
		// ������
		data.setAxisYLeft(axisY);
		data.setAxisXBottom(axisX);
		ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();
		ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
		String[] ss = mysString.split(",");
		for (int j = 0; j < ss.length; j++) {
			axisValuesX.add(new AxisValue(j).setLabel(j + ""));// ���X����ʾ�Ŀ̶�ֵ
		}
		for (int i = 1; i < 5; i++) {
			axisValuesY.add(new AxisValue(i * 4).setLabel(changeInt(i)));
		}
		axisX.setValues(axisValuesX);
		axisY.setValues(axisValuesY);
		axisX.setLineColor(Color.BLACK);// ��Ч��
		axisY.setLineColor(Color.BLUE);// ��Ч��

		axisX.setTextColor(Color.BLACK);// ����X��������ɫ
		axisY.setTextColor(Color.BLUE);// ����Y��������ɫ

		axisX.setTextSize(20);// ����X�����ִ�С
		axisX.setTypeface(Typeface.SERIF);// ����������ʽ

		// axisX.setHasTiltedLabels(true);//����X������������ת45��
		axisX.setHasLines(true);// �Ƿ���ʾX��������
		axisY.setHasLines(true);

		// axisX.setInside(true);//����X��������X���ڲ�
		// Distance axis(bottom X) with formatter that will ad [km] to
		// values, remember to modify max label charts
		// value.
		/*
		 * Axis distanceAxis = new Axis(); distanceAxis.setName("Distance");
		 * distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
		 * distanceAxis.setMaxLabelChars(4); distanceAxis.setFormatter(new
		 * SimpleAxisValueFormatter() .setAppendedText("km".toCharArray()));
		 * distanceAxis.setHasLines(true);
		 * distanceAxis.setHasTiltedLabels(true);
		 * data.setAxisXBottom(distanceAxis);
		 * 
		 * // Tempo uses minutes so I can't use auto-generated axis because //
		 * auto-generation works only for decimal // system. So generate custom
		 * axis values for example every 15 // seconds and set custom labels in
		 * format // minutes:seconds(00:00), you could do it in formatter but
		 * here // will be faster. List<AxisValue> axisValues = new
		 * ArrayList<AxisValue>(); for (float i = 0; i < tempoRange; i += 0.25f)
		 * { // I'am translating float to minutes because I don't have data //
		 * in minutes, if You store some time data // you may skip translation.
		 * axisValues.add(new AxisValue(i) .setLabel(formatMinutes(tempoRange -
		 * i))); }
		 * 
		 * Axis tempoAxis = new Axis(axisValues).setName("Tempo [min/km]")
		 * .setHasLines(true).setMaxLabelChars(4)
		 * .setTextColor(ChartUtils.COLOR_RED); data.setAxisYLeft(tempoAxis);
		 * 
		 * // *** Same as in Speed/Height chart. // Height axis, this axis need
		 * custom formatter that will translate // values back to real height
		 * values. data.setAxisYRight(new Axis().setName("Height [m]")
		 * .setMaxLabelChars(3) .setFormatter(new HeightValueFormatter(scale,
		 * sub, 0)));
		 * 
		 * // Set data chart.setLineChartData(data);
		 * 
		 * // Important: adjust viewport, you could skip this step but in this
		 * // case it will looks better with custom // viewport. Set // viewport
		 * with Y range 0-12; Viewport v = chart.getMaximumViewport();
		 * v.set(v.left, tempoRange, v.right, 0); chart.setMaximumViewport(v);
		 * chart.setCurrentViewport(v);
		 */

	}

	/**
	 * ��ʼ������
	 * 
	 * @return
	 */
	private LineChartData initData(List<Line> lines, int number) {

		LineChartData data = new LineChartData(lines);
		// ��ʼ����
		Axis axisX = new Axis();
		Axis axisY = new Axis().setHasLines(true);
		axisX.setName("��"+(number+1)+"��");
		// axisX,set
		// ǰ���ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setPrependedText("aaaa".toCharArray()));
		// ����ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setAppendedText("aaaa".toCharArray()));
		// axisX.setFormatter(new SimpleAxisValueFormatter());
		axisY.setName("ѡ��");
		// ������
		data.setAxisYLeft(axisY);
		data.setAxisXBottom(axisX);
		ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();
		ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
		String[] ss = mysString.split(",");
		for (int j = 0; j < ss.length; j++) {
			axisValuesX.add(new AxisValue(j).setLabel(j + ""));// ���X����ʾ�Ŀ̶�ֵ
		}
		for (int i = 1; i < 5; i++) {
			axisValuesY.add(new AxisValue(i * 4).setLabel(changeInt(i)));
		}
		axisX.setValues(axisValuesX);
		axisY.setValues(axisValuesY);
		axisX.setLineColor(Color.BLACK);// ��Ч��
		axisY.setLineColor(Color.BLUE);// ��Ч��

		axisX.setTextColor(Color.BLACK);// ����X��������ɫ
		axisY.setTextColor(Color.BLUE);// ����Y��������ɫ

		axisX.setTextSize(20);// ����X�����ִ�С
		axisX.setTypeface(Typeface.SERIF);// ����������ʽ

		// axisX.setHasTiltedLabels(true);//����X������������ת45��
		axisX.setHasLines(true);// �Ƿ���ʾX��������
		axisY.setHasLines(true);

		// axisX.setInside(true);//����X��������X���ڲ�

		// ���ø�ֵ ����Ϊ������ Ĭ��Ϊ0
		// data.setBaseValue(Float.NEGATIVE_INFINITY);

		return data;
	}

	private LineChartData initData(List<Line> lines) {

		LineChartData data = new LineChartData(lines);
		// ��ʼ����
		Axis axisX = new Axis();
		Axis axisY = new Axis().setHasLines(true);
		axisX.setName("����");
		// axisX,set
		// ǰ���ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setPrependedText("aaaa".toCharArray()));
		// ����ַ�
		// axisX.setFormatter(new
		// SimpleAxisValueFormatter().setAppendedText("aaaa".toCharArray()));
		// axisX.setFormatter(new SimpleAxisValueFormatter());
		axisY.setName("��ȷ��");
		// ������
		data.setAxisYLeft(axisY);
		data.setAxisXBottom(axisX);
		ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();
		ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
		String[] ss = mysString.split(",");
		for (int j = 0; j < ss.length; j++) {
			axisValuesX.add(new AxisValue(j).setLabel(j + ""));// ���X����ʾ�Ŀ̶�ֵ
		}
		for (int i = 1; i <= 5; i++) {
			axisValuesY.add(new AxisValue(i * 20).setLabel(i * 20 + "%"));
		}
		axisX.setValues(axisValuesX);
		axisY.setValues(axisValuesY);
		axisX.setLineColor(Color.BLACK);// ��Ч��
		axisY.setLineColor(Color.BLUE);// ��Ч��

		axisX.setTextColor(Color.BLACK);// ����X��������ɫ
		axisY.setTextColor(Color.BLUE);// ����Y��������ɫ

		axisX.setTextSize(20);// ����X�����ִ�С
		axisX.setTypeface(Typeface.SERIF);// ����������ʽ

		// axisX.setHasTiltedLabels(true);//����X������������ת45��
		axisX.setHasLines(true);// �Ƿ���ʾX��������
		axisY.setHasLines(true);

		// axisX.setInside(true);//����X��������X���ڲ�

		// ���ø�ֵ ����Ϊ������ Ĭ��Ϊ0
		// data.setBaseValue(Float.NEGATIVE_INFINITY);

		return data;
	}

	int changeInt(char string) {
		if (string == 'A') {
			return 4;
		} else if (string == 'B') {
			return 8;
		} else if (string == 'C') {
			return 12;
		} else {
			return 16;
		}
	}

	String changeInt(int string) {
		if (string == 1) {
			return "A";
		} else if (string == 2) {
			return "B";
		} else if (string == 3) {
			return "C";
		} else {
			return "D";
		}
	}
}
