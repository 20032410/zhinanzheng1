package zhinanzheng.com;



import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import zhinanzheng.com.gpsdata;
import android.telephony.SmsManager;
/**
 * @author  都市游侠 troyjie@yahoo.cn
 * @version  1.0
 */
public class Zhinanzheng extends Activity implements SensorEventListener{
	
	static final String TAG = "gps";
	ImageView image;  //指南针图片
	float currentDegree = 0f; //指南针图片转过的角度
	private TextView angleview=null;
	private TextView long_lat=null;
	private String str_long_lat;

	SensorManager mSensorManager; //管理器
	//20150326
	private Criteria ct;
	private LocationManager lm;
	private Location loc;
	private String provider;
	//20150327 短信发送
	private EditText sms_telphone=null;
	private Button 	 sms_but=null;
	protected static final Intent Intent1 = null;
	PendingIntent sentIntent;
	SmsManager smsManager;
	
	//public string for write
	public String Longitude_write=null;//经度
	public String Latitude_write=null;//纬度
	public String direction_write=null;//带*的方位
	public String direction_temp=null;//纯数字方位
	public String fangwei_write=null;//东南西北
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //20150324 li
        angleview=(TextView)findViewById(R.id.angle);
        long_lat=(TextView)findViewById(R.id.longtitude);
        //20150327 li发送短信
        sms_telphone=(EditText)findViewById(R.id.sms_tel);
        sms_but=(Button)findViewById(R.id.sms_button);
    	smsManager=SmsManager.getDefault();
		 sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0); 
//        SmsManager smsManager=SmsManager.getDefault();
//        PendingIntent sentIntent = PendingIntent.getBroadcast(Zhinanzheng.this, 0, Intent1, 0);
        sms_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//20150327 新增短信，方位位置GPS状态信息
			String sms_text="目前状态："+direction_write+", 经度"+Longitude_write+", 纬度"+Latitude_write;
			
			
			long_lat.setText(sms_text);
			//20170421
			//将获得的经纬度，传送到mysql数据库，手动按，更新
			//UPDATE `gpsdata` SET `Longitude`='103.93', `Latitude`='34.23' WHERE (`id`='1');
			//UPDATE `gpsdata` SET `Longitude`='102', `Latitude`='62', `direction`='x110' WHERE (`id`='1');
			//String add="UPDATE `gpsdata` SET `Longitude`="+Longitude_write+","+"`Latitude`="+Latitude_write+","+"`direction`="+direction_temp+","+"`fangwei`="+"'east'"+";";
			//String add="UPDATE `gpsdata` SET `Longitude`="+Longitude_write+","+"`Latitude`="+Latitude_write+";";
			String add="UPDATE `gpsdata` SET `Longitude`="+Longitude_write+","+"`Latitude`="+Latitude_write+","+"`direction`="+direction_temp+","+"`fangwei`="+"'"+fangwei_write+"'"+";";
			addResult(add);
			//getResult();
			 getResult();

			//取消短信发送，改为获取最新的gps
			//smsManager.sendTextMessage(sms_telphone.getText().toString(), null, sms_text, sentIntent, null);
//			Toast.makeText(this,"发送短信为"+sms_text,Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "短信发送完成", Toast.LENGTH_SHORT).show();	
			Toast.makeText(getApplicationContext(), sms_text, Toast.LENGTH_LONG).show();
			}
		});

        
        image = (ImageView)findViewById(R.id.znzImage);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE); //获取管理服务
        //20150326 li
        initLocation();
        
    }
    
    @Override 
    protected void onResume(){
    	super.onResume();
    	//注册监听器
    	mSensorManager.registerListener(this
    			, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }
    
    //取消注册
    @Override
    protected void onPause(){
    	mSensorManager.unregisterListener(this);
    	super.onPause();
    	
    }
    
    @Override
    protected void onStop(){
    	mSensorManager.unregisterListener(this);
    	super.onStop();
    	
    }

    //传感器值改变
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
		
	}
    //精度改变
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//获取触发event的传感器类型
		int sensorType = event.sensor.getType();
		
		switch(sensorType){
		case Sensor.TYPE_ORIENTATION:
			float degree = event.values[0]; //获取z转过的角度
			//穿件旋转动画
			RotateAnimation ra = new RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF,0.5f
					,Animation.RELATIVE_TO_SELF,0.5f);
		 ra.setDuration(300);//动画持续时间
		 image.startAnimation(ra);
		 currentDegree = -degree;
		 //20150324 li 增加方位显示
		 //增加方位判别西北西南
		 int degreeturn=0;
		 degreeturn=(int)degree;
		// float f1=new float(degree);
		// degreeturn=degreef1.int
		 //angleview.setText("方位为："+degreeturn+"︒");
		 /*
		 if(degreeturn>0&&degreeturn<23)
		 {angleview.setText("方位为："+"北"+degreeturn+"︒");
		  }
		 	else if(degreeturn<68){
			 angleview.setText("方位为："+"东北"+degreeturn+"︒"); }
		 	else if(degreeturn<113){
		 		angleview.setText("方位为："+"东"+degreeturn+"︒");
		 	}
		 	else if(degreeturn<158){
		 		angleview.setText("方位为："+"东南"+degreeturn+"︒");
		 	}
		 	else if(degreeturn<203){
		 		angleview.setText("方位为："+"南"+degreeturn+"︒");
		 	}
		 	else if(degreeturn<248){
		 		angleview.setText("方位为："+"西南"+degreeturn+"︒");
		 	}
		 	else if(degreeturn<293){
		 		angleview.setText("方位为："+"西"+degreeturn+"︒");
		 	}
		 	else if(degreeturn<338){
		 		angleview.setText("方位为："+"西北"+degreeturn+"︒");
		 	}
		 	else
		 	{angleview.setText("方位为："+"北"+degreeturn+"︒");}
		*/
		 angletran(degreeturn);
		 break;
		
		}
	}
	//方位换算
	public void angletran(int angletran0)
	{
		//int angletran0=angletran1;
		direction_temp=String.valueOf(angletran0);
		if(angletran0>0&&angletran0<22.5)
		 {angleview.setText("方位为："+"北"+angletran0+"︒");
		 direction_write="北"+String.valueOf(angletran0);
		 fangwei_write="北";
		  }
		 	else if(angletran0<67.5){
			 angleview.setText("方位为："+"东北"+angletran0+"︒"); 
			 direction_write="东北"+String.valueOf(angletran0);
			 fangwei_write="东北";}
		 	else if(angletran0<112.5){
		 		angleview.setText("方位为："+"东"+angletran0+"︒");
		 		direction_write="东"+String.valueOf(angletran0);
		 		fangwei_write="东";
		 	}
		 	else if(angletran0<157.5){
		 		angleview.setText("方位为："+"东南"+angletran0+"︒");
		 		direction_write="东南"+String.valueOf(angletran0);
		 		fangwei_write="东南";
		 	}
		 	else if(angletran0<202.5){
		 		angleview.setText("方位为："+"南"+angletran0+"︒");
		 		direction_write="南"+String.valueOf(angletran0);
		 		fangwei_write="南";
		 	}
		 	else if(angletran0<247.5){
		 		angleview.setText("方位为："+"西南"+angletran0+"︒");
		 		direction_write="西南"+String.valueOf(angletran0);
		 		fangwei_write="西南";
		 	}
		 	else if(angletran0<292.5){
		 		angleview.setText("方位为："+"西"+angletran0+"︒");
		 		direction_write="西"+String.valueOf(angletran0);
		 		fangwei_write="西";
		 	}
		 	else if(angletran0<337.5){
		 		angleview.setText("方位为："+"西北"+angletran0+"︒");
		 		direction_write="西北"+String.valueOf(angletran0);
		 		fangwei_write="西北";
		 	}
		 	else
		 	{angleview.setText("方位为："+"北"+angletran0+"︒");
		 	direction_write="北"+String.valueOf(angletran0);
		 	fangwei_write="北";}
	}
	

	private final LocationListener locationListener = new LocationListener()
	{

		@Override
		public void onLocationChanged(Location arg0)
		{
	//		showInfo(getLastPosition(), 2);
			getLastPosition();
		}

		@Override
		public void onProviderDisabled(String arg0)
		{
//			showInfo(null, -1);
		}

		@Override
		public void onProviderEnabled(String arg0)
		{
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2)
		{
		}

	};

	private void initLocation()
	{
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			ct = new Criteria();
			ct.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
			ct.setAltitudeRequired(true);// 显示海拔
			ct.setBearingRequired(true);// 显示方向
			ct.setSpeedRequired(true);// 显示速度
			ct.setCostAllowed(false);// 不允许有花费
			ct.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
			provider = lm.getBestProvider(ct, true);
			// 位置变化监听,默认5秒一次,距离10米以上
			lm.requestLocationUpdates(provider, 5000, 10, locationListener);
			
		} else
//			showInfo(null, -1);
		{}
	}
	
	/**
	 * 
	 * lielieli 20151125 远程数据库
	 */
	public static final int UPDATE_TEXT = 1;
	private String s_show=null;
	private static final String TAG2="gps";
	
	private String result=null;
	private TextView text_show0=null;
	

	/**
	 * 执行增加请求
	 */
	private void addResult(String sql) {
		//Mythread.setHandle(mHandle);// 设置接收结果的handle
		Mythread.execSQL(sql);// 执行异步get请求
		// HttpUtils.doPostAsyn(uriApi, params);
	}
	
	/**
	 * 执行http请求
	 */
	private void getResult() {
		Mythread.setHandle(mHandle);// 设置接收结果的handle
		Mythread.doGetAsyn();// 执行异步get请求
		// HttpUtils.doPostAsyn(uriApi, params);
	}
	
	public Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null || msg.obj == null)
				return;
			  switch (msg.what){
	            case UPDATE_TEXT:
	                //text_show.setText("Nice yo see you again");
	            	result = msg.obj.toString();// get请求返回的结果
	  			// mSetText(msg.obj.toString());
	              // Toast.makeText(getApplicationContext(),msg.obj.toString(), Toast.LENGTH_SHORT).show();
	              // Toast.makeText(this,msg.obj.toString(), Toast.LENGTH_SHORT).show();
	            	Log.d(TAG,"current gpslocation : by lielieli 20151125 \t"+msg.obj.toString());
	                break;
	            default:
	                break;
	        }
			
              
		//	parseResult(result);// 解析json
		}
	};

	private gpsdata getLastPosition()
	{
		gpsdata result = new gpsdata();
		loc = lm.getLastKnownLocation(provider);
		if (loc != null)
		{
//			result.Latitude = (int) (loc.getLatitude() * 1E6);
//			result.Longitude = (int) (loc.getLongitude() * 1E6);
			result.Latitude=(float)(Math.round(loc.getLatitude()*100))/100;//显示小树点2位
			result.Longitude=(float)(Math.round(loc.getLongitude()*100))/100;

//			result.Latitude = ((int)(loc.getLatitude()*1E2))/100 ;
//			result.Longitude = ((int)(loc.getLongitude()*1E2))/100;
			result.High = loc.getAltitude();
			result.Direct = loc.getBearing();
			result.Speed = loc.getSpeed();
			Date d = new Date();
			d.setTime(loc.getTime() + 28800000);// UTC时间,转北京时间+8小时
			result.GpsTime = DateFormat.format("yyyy-MM-dd kk:mm:ss", d).toString();
			d = null;
//			String Text = "当前位置为: " + "Latitude = " + result.Latitude + " Longitude = " +result.Longitude;
			String Text = "当前位置为: " + "纬度 " + result.Latitude + " 经度 " +result.Longitude;
			Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_LONG).show();
			long_lat.setText(Text);
			
			//renew the mysql database gps,table gpsdata
			//20170421
			//当前经纬度
			Log.d(TAG, "current Longitude : " + result.Longitude);
			Log.d(TAG,"current Latitude:" + result.Latitude);
			//Log.d(TAG,"current tmep : by lielieli 20151125 \t"+dat[2]);
			System.out.println(Text);
			System.out.println("current Longitude : " + result.Longitude);
			System.out.println("current Latitude:" + result.Latitude);
			//String temptest=String.valueOf(Tool.builduInt(dat[2]));
			String Longitude_test=String.valueOf(result.Longitude);
			String Latitude_test=String.valueOf(result.Latitude);
			
			//for write the gps data in public
			Longitude_write=String.valueOf(result.Longitude);
			Latitude_write=String.valueOf(result.Latitude);
			//System.out.println("This is temptest context \t"+temptest);
			//String add="INSERT INTO `temp` (`temp_l`, `temp_h`, `eq`) VALUES ('"+temptest+" ','"+temptest+" ', '测量设备2')";
			/*** 插入数据  ***/
			//String add="INSERT INTO `temp` (`temp_l`, `temp_h`, `eq`) VALUES ("+temptest+" ,"+temptest+" ,'测量设备2')";
			/*** 更新数据  ***/
			//String add="UPDATE `temp` SET `temp_l`="+temptest+";";
			//String add="UPDATE `temp` SET `temp_l`="+temptest+";";
			//UPDATE `gpsdata` SET `Longitude`='103.91', `Latitude`='34.21' WHERE (`id`='1')
			//String add_logitude="UPDATE `gpsdata` SET `Logitude`="+Longitude_test+";";
			//String add_logitude="UPDATE `gpsdata` SET `Logitude`="+Longitude_test+";";
			System.out.println("get the Longitude for write"+Longitude_write);
			System.out.println("get the Longitude for write"+Latitude_write);
			System.out.println("start input gpsdata");
			//String add="UPDATE `gpsdata` SET `Longitude`="+Longitude_write+","+"`Latitude`="+Latitude_write+";";
			//远程更新mysql数据库的gps坐标信息
			String add="UPDATE `gpsdata` SET `Longitude`="+Longitude_write+","+"`Latitude`="+Latitude_write+","+"`direction`="+direction_temp+","+"`fangwei`="+"'"+fangwei_write+"'"+";";
			//addResult(add_logitude);
			addResult(add);
			getResult();
			//String add_latitude="UPDATE `gpsdata` SET `Latitude`="+Latitude_test+";";
			//addResult(add_latitude);
			//getResult();
			// getResult();
			
		}
		return result;
	}

}