package zhinanzheng.com;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Mythread {
	private static final String TAG="gps";
	public static final int UPDATE_TEXT = 1;
	private static final String IP="192.168.155.1";
	//private static final String IP="192.168.13.219";
	//private static final String IP="192.168.13.188";
	private static Handler mhandler;
	
	/**
	 * ִ���첽HTTP����֮ǰ�� ��Ҫ�ȵ����������
	 * 
	 * @param handler
	 */
	public static void setHandle(Handler handler) {
		mhandler = handler;
	}

	/**
	 * ������Ϣ
	 * 
	 * @param result
	 */
	private static void sendHandlerMsg(String result) {
		if (mhandler != null) {
			Message message = new Message();
			message.what = UPDATE_TEXT;
			message.obj = result;
			mhandler.sendMessage(message);
		}
	}

	/**
	 * �첽��Get����
	 *
	 */
	public static void doGetAsyn() {
		new Thread() {
			public void run() {
			//	String result = doGet(urlStr);
			//	sendHandlerMsg(result);
				
				try
			      {
			    	   Driver d =(Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
			           //String url="jdbc:mysql://127.0.0.1:3306/mysql";
			    	  // String url="jdbc:mysql://192.168.2.100:3306/mysql";
			    	   String url="jdbc:mysql://"+IP+":3306/mysql";
			           String  user="root";
			           String password="lielie";//�������MySQL����
			           Connection conn=DriverManager.getConnection(url,user,password);
			           Statement stmt=conn.createStatement();
			           String query_database="use gps";
			           //String query_database="use temptest";
			           stmt.executeQuery(query_database);
			           PreparedStatement ps;
			          // String sqldata = "use temptest";
			         //   ps = conn.prepareStatement(sqldata);
			           String sql = "select * from gpsdata";
			           ps = conn.prepareStatement(sql);
			           ResultSet rs = ps.executeQuery();
			           StringBuilder str=new StringBuilder();
			           Log.i(TAG,"This is for mysql data");
			           System.out.println("This is for mysql data");
			           while(rs.next())	    {
			        	   
			   				str.append(String.valueOf(rs.getInt("id"))+"\t");
			   				str.append(rs.getString("Longitude")+"\t");
			   				str.append(rs.getString("Latitude")+"\t");
			   				str.append(rs.getString("direction")+"\n");
		      	   
			        	   System.out.println(String.valueOf(rs.getInt("id")));
			   				System.out.println(rs.getString("Longitude"));
			   				System.out.println(rs.getString("Latitude"));
			   				System.out.println(rs.getString("direction"));
			   				
			   			}
			           System.out.println(str);
			           //Message message = new Message();
		              // message.what = UPDATE_TEXT;
		               //message���ػ�ý��
			          // message.obj = str.toString();
		              // mhandler.sendMessage(message);
			           sendHandlerMsg(str.toString());
			           rs.close();
			       	stmt.close();
			       	ps.close();
			       	conn.close();
			      } catch(Exception e)
			     {
			          System.out.println("Error:  "+e.toString());
			     }
				};
		}.start();
	}

	
	public static boolean execSQL(Connection conn, String sql) {  
        boolean execResult = false;  
        if (conn == null) {  
            return execResult;  
        }  
  
        Statement statement = null;  
  
        try {  
            statement = conn.createStatement();  
            if (statement != null) {  
                execResult = statement.execute(sql);  
            }  
        } catch (SQLException e) {  
            execResult = false;  
        }  
  
        return execResult;  
    }  
	//���룬ɾ������������
	public static void execSQL(final String sql) {
		 final boolean execResult=false;
		new Thread() {
			public void run() {
		
				try
			      {
			    	   Driver d =(Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
				       //String url="jdbc:mysql://192.168.2.100:3306/mysql";
				       String url="jdbc:mysql://"+IP+":3306/mysql";
			           String  user="root";
			           String password="lielie";//�������MySQL����
			           Connection conn=DriverManager.getConnection(url,user,password);
			           Statement stmt=conn.createStatement();
			           if (stmt!=null){
			        	   String query_database="use gps";
				           stmt.executeQuery(query_database);
				          stmt.execute(sql);
				          //execResult=true;
			           }

			       	stmt.close();
			       	conn.close();
			      } catch(Exception e)
			     {
			          System.out.println("Error:  "+e.toString());
			     }
				};
		}.start();
		//return execResult;
	}
	/*
	public void run(){
		
		try
	      {
	    	   Driver d =(Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
	           //String url="jdbc:mysql://127.0.0.1:3306/mysql";
	    	   String url="jdbc:mysql://192.168.2.100:3306/mysql";
	           String  user="root";
	           String password="**";//�������MySQL����
	           Connection conn=DriverManager.getConnection(url,user,password);
	           Statement stmt=conn.createStatement();
	           String query_database="use temptest";
	           stmt.executeQuery(query_database);
	           PreparedStatement ps;
	          // String sqldata = "use temptest";
	         //   ps = conn.prepareStatement(sqldata);
	           String sql = "select * from temp";
	           ps = conn.prepareStatement(sql);
	           ResultSet rs = ps.executeQuery();
	           StringBuilder str=new StringBuilder();
	           Log.i(TAG,"This is for mysql data");
	           while(rs.next())	    {
	        	   
	   				str.append(String.valueOf(rs.getInt("id"))+"\t");
	   				str.append(rs.getString("temp_l")+"\t");
	   				str.append(rs.getString("temp_h")+"\t");
	   				str.append(rs.getString("eq")+"\n");
      	   
	        	   System.out.println(String.valueOf(rs.getInt("id")));
	   				System.out.println(rs.getString("temp_l"));
	   				System.out.println(rs.getString("temp_h"));
	   				System.out.println(rs.getString("eq"));
	   				
	   			}
	           System.out.println(str);
	           Message message = new Message();
               message.what = UPDATE_TEXT;
               //message���ػ�ý��
	           message.obj = str.toString();
               mhandler.sendMessage(message);
	           rs.close();
	       	stmt.close();
	       	ps.close();
	       	conn.close();
	      } catch(Exception e)
	     {
	          System.out.println("Error:  "+e.toString());
	     }
		}
			*/
	


}
