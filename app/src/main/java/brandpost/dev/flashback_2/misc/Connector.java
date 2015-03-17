package brandpost.dev.flashback_2.misc;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Viktor on 2014-04-05.
 */

public class Connector {

	public static Document Connect(String url, Context context) throws IOException {
		Document document = null;

		if(FBHelper.hasNetworkConnection(context)) {
			// Get Session-cookie
			Map<String, String> cookie = FBHelper.getSessionCookie(context);
			// Get connection timeout setting
			int connectionTimeout = FBHelper.getConnectionTimeout(context);

			if(!cookie.isEmpty()) {
				document = cookieConnect(url, cookie, connectionTimeout, context);
			} else {
                document = basicConnect(url, connectionTimeout);
			}
		} else {
			return null;
		}

		return document;
	}

	public static Document basicConnect(String baseurl, int connectionTimeout) throws IOException {
		URL url= new URL(baseurl);
		HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

		connection.setConnectTimeout(connectionTimeout);

		Document doc = null;
        InputStream response = null;

        int responseCode = connection.getResponseCode();

        if(responseCode == HttpsURLConnection.HTTP_OK) {
            response = connection.getInputStream();
        }

        doc = Jsoup.parse(response, null, baseurl);

        response.close();

		return doc;
	}

	private static Document cookieConnect(String baseurl, Map<String, String> cookies, int connectionTimeout, Context context) throws IOException {
		URL url;
		try {
			url = new URL(baseurl);
		} catch (IOException e) {
			throw new IOException("Malformed url - " + baseurl);
		}

		HttpsURLConnection connection = null;

		try {
			connection = (HttpsURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new IOException("Failed to open connection");
		}

		connection.setRequestMethod("GET");
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(0);
		connection.setUseCaches(true);


		// Convert cookie to correct format
		String cookiestring = cookies.toString().trim().replace("{", "").replace("}", "").replace(",", ";");

		// Some headers
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
		connection.setRequestProperty("Referer", "https://www.flashback.org/");
		connection.setRequestProperty("Cookie", cookiestring);

		// Get cookies from response
		List<String> newcookies = connection.getHeaderFields().get("Set-Cookie");

		if(newcookies != null) {
			// Get "old" cookies
			Map<String, String> oldcookies = FBHelper.getSessionCookie(context);

			// Store in Map
			for (String newcookie : newcookies) {
				String key;
				String value;
				String temp = newcookie.split(";", 2)[0];
				key = temp.split("=")[0];
				value = temp.split("=")[1];
				oldcookies.put(key, value);
			}
			FBHelper.setSessionCookie(oldcookies, context);
		}

        Document doc = null;
        InputStream response = null;

        response = connection.getInputStream();
        doc = Jsoup.parse(response, null, baseurl);

        response.close();

        return doc;
	}
}