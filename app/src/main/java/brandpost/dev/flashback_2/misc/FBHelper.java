package brandpost.dev.flashback_2.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by Viktor on 2014-04-29.
 */
public class FBHelper {

	public static Map<String, String> getSessionCookie(Context context) {
		SharedPreferences sessionPrefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);

		return (Map<String, String>) sessionPrefs.getAll();

	}

	public static boolean isLoggedIn(Context context) {
		SharedPreferences sessionPrefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        return sessionPrefs.contains("vbscanpassword");
    }

    public static void setSessionCookie(Map<String, String> sessionCookie, Context context) {
        SharedPreferences sessionPrefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sessionPrefs.edit();

        for (Map.Entry<String, String> entry : sessionCookie.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

	public static void clearSessionCookie(Context context) {
		SharedPreferences sessionPrefs = context.getSharedPreferences("SESSION", Context.MODE_PRIVATE);
		SharedPreferences userPrefs = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

		if(sessionPrefs.contains("vbscanpassword") || userPrefs.contains("ID")) {
			sessionPrefs.edit().clear().commit();
			userPrefs.edit().clear().commit();

			Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show();
		}
	}

    public static boolean hasNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static int getConnectionTimeout(Context context) {
        // Get timeout-value
        SharedPreferences appPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        int timeout;

        try {
            String to = appPrefs.getString("connection_timeout", "3000");
            timeout = Integer.parseInt(to);
        } catch (Exception e) {
            timeout = 3000;
            e.printStackTrace();
        }

        return timeout;
    }
}
