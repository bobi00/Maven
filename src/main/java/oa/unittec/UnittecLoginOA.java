package oa.unittec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UnittecLoginOA {

	private HttpClient httpClient;
	private String CommonCookie;
	private String checkCodeValue;

	public static void main(String[] args) {
		new UnittecLoginOA().init();
		;
	}

	String url = "http://ucenter.unittec.com/cas/login?service=http://oa.unittec.com/accounts/login/?next=%2Fdocument%2Fhome%2F";

	public void init() {
		httpClient = new HttpClient();
		String html = getLogoHtml(url);
		String checkStr = getCheckPath(html);
		String checkCodeURL = "http://ucenter.unittec.com" + checkStr;
		// 创建浏览器对象
		checkCodeValue = checkStr.split("/")[checkStr.split("/").length - 1];
		System.out.println(checkCodeValue);

		GetMethod getMethod1 = new GetMethod(checkCodeURL);
		getMethod1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:66.0) Gecko/20100101 Firefox/66.0");
		getMethod1.setRequestHeader("Cookie", CommonCookie);
		try {
			// 设置HttpClient接收Cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.executeMethod(getMethod1);

			// 验证码保存路径
			File storeFile = new File("D:vc.gif");
			InputStream is = getMethod1.getResponseBodyAsStream();
			FileOutputStream fos = new FileOutputStream(storeFile);
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				fos.write(b, 0, n);
			}
			is.close();
			fos.close();

			// 该处代码是用于验证码自动识别
//			txtSecretCode = ImagePreProcess.getAllOrc("D:\\java爬虫jar包\\verifycode\\vc.gif");
			Scanner sc = new Scanner(System.in);
			String uu = sc.next().trim();
			logo(uu);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取logo的HTML
	public String getLogoHtml(String url) {
		StringBuffer response = null;
		try {
			GetMethod getMethod = new GetMethod(url);
			getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:66.0) Gecko/20100101 Firefox/66.0");
			// 设置HttpClient接收Cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.executeMethod(getMethod);
			//获取访问后的Cookie
			Cookie[] cookies1 = httpClient.getState().getCookies();
			StringBuffer tmpcookies1 = new StringBuffer();
			for(Cookie c1 : cookies1) {
				tmpcookies1.append(c1.toString());
				System.out.println("访问页面cookies : "+c1.toString());
//				cookie1 = tmpcookies1.toString();
				//cookie1 = c1.toString();
			}
			CommonCookie = tmpcookies1.toString();
			InputStream is = getMethod.getResponseBodyAsStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\r\n");
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
//		System.out.println(response.toString());
//		try {
//			URL obj = new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setUseCaches(false);
//			
//			conn.setRequestProperty("Host", "ucenter.unittec.com");
//			conn.setRequestProperty("User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:66.0) Gecko/20100101 Firefox/66.0");
//			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//			conn.setRequestProperty("Referer", "http://oa.unittec.com/document/home/");
//			conn.setRequestProperty("Connection", "keep-alive");
//
//			Map<String, List<String>> map = conn.getHeaderFields();
//			System.out.println(map);
//			CommonCookie = map.get("Set-Cookie").get(0).split("; ")[0];
//
////			conn.setRequestProperty("Cookie", cookie);
//			
//			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			String inputLine;
//			response = new StringBuffer();
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine + "\r\n");
//			}
//			in.close();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return response.toString();
	}

	// 获取验证码路径
	private String getCheckPath(String html) {
		Document doc = Jsoup.parse(html);
		String loginform = doc.getElementsByTag("img").toString();
		loginform = loginform.substring(loginform.indexOf("src") + 5, loginform.indexOf("alt") - 2).trim();
		return loginform;
	}

	// 模拟登录
	public void logo(String uu) {

		System.out.println(CommonCookie + " CommonCookie ");
		String csrfmiddlewaretoken = CommonCookie.substring(CommonCookie.indexOf("=") + 1, CommonCookie.length());
		
		String next = "";

		String username = "";

		String password = "";

		String captcha_0 = checkCodeValue;

		String captcha_1 = uu;

		System.out.println(checkCodeValue + "   checkCodeValue  ");
		// 模拟登录，按实际服务器端要求选用Post 或 Get请求方式
		PostMethod postMethod = new PostMethod(url);
		
		// 设置相同的cookie
		postMethod.setRequestHeader("Cookie", CommonCookie);
//		postMethod.setRequestHeader("Host", "ucenter.unittec.com");
		postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:66.0) Gecko/20100101 Firefox/66.0");
//		postMethod.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		postMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//		postMethod.setRequestHeader("Referer", "http://ucenter.unittec.com/cas/login?service=http%3A%2F%2Foa.unittec.com%2Faccounts%2Flogin%2F%3Fnext%3D%252Fdocument%252Fhome%252F");
//		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//		postMethod.setRequestHeader("Connection", "keep-alive");
		// 设置登录时需要的信息，用户名和密码
//		NameValuePair[] data = { 
//				new NameValuePair("csrfmiddlewaretoken", csrfmiddlewaretoken),
//				new NameValuePair("next", next), 
//				new NameValuePair("username", username),
//				new NameValuePair("password", password), 
//				new NameValuePair("captcha_0", captcha_0),
//				new NameValuePair("captcha_1", captcha_1)
////						new NameValuePair("remember", remember)
//		};
//		postMethod.setRequestBody(data);
		
		
		Part[] parts = {
			      new StringPart("csrfmiddlewaretoken", csrfmiddlewaretoken),
			      new StringPart("next", next),
			      new StringPart("username", username),
			      new StringPart("password", password),
			      new StringPart("captcha_0", captcha_0),
			      new StringPart("captcha_1", captcha_1),
			  };
		postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));;
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			// html = postMethod.getResponseBodyAsString();
			System.out.println(statusCode);
			StringBuffer response = null;
			InputStream is;
			try {
				is = postMethod.getResponseBodyAsStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine + "\r\n");
				}
				System.out.println("----------------------------------");
				System.out.println(response.toString());
				System.out.println("----------------------------------");
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 重定向
			if (statusCode == 302) {
				System.out.println("模拟登录成功!");

				// 设置HttpClient接收Cookie，用与浏览器一样的策略
				httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
				// 获取登录后的Cookie
				Cookie[] cookies = httpClient.getState().getCookies();
				StringBuffer tmpcookies = new StringBuffer();
				for (Cookie c : cookies) {
					tmpcookies.append(c.toString() + ";");
					System.out.println("登录页面cookies : " + c.toString());
				}
			} else {
				System.out.println("登录失败");
				System.out.println(httpClient.getState().getCookies()[0].toString());
				System.out.println(captcha_0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
