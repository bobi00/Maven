package oa.unittec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeekDay {
	//http://tool.bitefu.net/jiari/?d=20190504 (0工作日/1休息日)
	//http://tool.bitefu.net/jiari/?d=20190430&info=1
	//http://api.goseek.cn/Tools/holiday?date=20180428 (0,2工作日/1,3休息日)
	public String weekday(String url) {
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			BufferedReader in =
		            new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
		    response = new StringBuffer();

		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }
		    in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.toString();
	}
}
