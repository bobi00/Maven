package com.maven.bobi;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maven.bobi.mail.SendEmail;

import oa.unittec.WeekDay;

public class PunchMain {
	
	private Log log = LogFactory.getLog(PunchMain.class);
	
	private String status ;
	
	private String morningDate ;
	
	private String nightDate;
	
	public static void main(String[] args) {
		new PunchMain().logo();
//		new PunchMain().runbat();
	}
	
	public void logo() {
		log.info("程序开始运行");
		while(true) {
			try {
				Date date = new Date();
				DateFormat df4 = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL); 
				String time = df4.format(date);
//				DateFormat df7 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
//				String times = df7.format(date);
				SimpleDateFormat realtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String times = realtime.format(date);
//				System.out.println(time);
//				System.out.println(times);
				if(times.contains("08:00:00")) {
					//动态日期URL
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
					String dateString = formatter.format(date);
					String url = "http://tool.bitefu.net/jiari/?d=" + dateString;
					status = new WeekDay().weekday(url);
					
					//早上随机时间
					Random random = new Random();
					int morningMinute = random.nextInt(20) + 20;
					int morningSecond = random.nextInt(50) + 10;
					
					
					
					morningDate = "08:" + morningMinute + ":" + morningSecond;
					
					int nightMinute = random.nextInt(10) + 10;
					int nightSecond = random.nextInt(50) + 10;
					nightDate = "19:" + nightMinute + ":" + nightSecond;
					
					log.info("staus: " + status);
					log.info("morningDate: " + morningDate);
					log.info("nightDate: " + nightDate);
				}
				
				
				if(morningDate != null && times.contains(morningDate)) {
					if(status != null && status.contentEquals("0")) {
						new SendEmail().sendMail("早上" ,  morningDate);
						log.info("早上打卡成功：" + time);
						runbat();
					}
				} else if (nightDate != null && times.contains(nightDate)) {
					if(status != null && status.equals("0")) {
						new SendEmail().sendMail("晚上" , nightDate );
						log.info("晚上打卡成功：" + time);
						runbat();
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void runbat() {
		
		URL  path = this.getClass().getClassLoader().getResource("punch.bat");
		log.info(path.getPath());
//		System.out.println(path.getPath());
		String cmd = "cmd /k start " + path.getPath().substring(1, path.getPath().length());// pass
//		String cmd = "cmd /k start " + System.getProperty("user.dir") + "\\src\\main\\resources\\punch.bat";// pass
		try {
			Process ps = Runtime.getRuntime().exec(cmd);
//			InputStream in = ps.getInputStream();
//			int c;
//			while ((c = in.read()) != -1) {
//				System.out.print(c);// 如果你不需要看输出，这行可以注销掉
//			}
//			in.close();
//			ps.waitFor();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("punch success！！！");
	}
	
}
