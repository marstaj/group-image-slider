package cz.via.slidecaster.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import cz.via.slidecaster.R;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.exception.NetworkException;
import cz.via.slidecaster.exception.UnknownException;

/**
 * Basic web client for issuing get and post requests.
 */
public class WebClient {

	private static DefaultHttpClient client = ThreadedClient.getInstance();
	protected static Context context;

	private static String execute(HttpUriRequest request, String coding) throws ApplicationException {

		// TODO - error handling

		String data = null;

		try {
			data = convertStreamToString(client.execute(request).getEntity().getContent(), coding);
			return data;
		} catch (IllegalStateException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (ClientProtocolException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (IOException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (Exception e) {
			throw new UnknownException(context.getString(R.string.unknown_exception_message));
		}
	}

	/**
	 * Sends http GET request.
	 * 
	 * @throws ApplicationException
	 */
	public static String sendRequest(String url, String coding) throws ApplicationException {
		return execute(new HttpGet(url), coding);
	}

	/**
	 * Sends http POST request.
	 * 
	 * @throws ApplicationException
	 */
	public static String sendRequest(String url, String coding, NameValuePair... params) throws ApplicationException {
		HttpPost post = new HttpPost(url);
		try {
			if (params != null) {
				post.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return execute(post, coding);
	}

	/**
	 * Sends JSON request
	 * 
	 * @throws ApplicationException
	 */
	public static String sendRequest(String url, String coding, JSONObject json, String ajaxMethod) throws ApplicationException {
		HttpPost post = new HttpPost(url);
		StringEntity se;
		try {
			se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
			post.setHeader("Content-type", "application/json");
			post.setHeader("X-AjaxPro-Method", ajaxMethod);

		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
			// e.printStackTrace();
		}
		return execute(post, coding);
	}

	/**
	 * Utility function for convenient creation of request parameters.
	 */
	public static NameValuePair param(String name, String value) {
		return new BasicNameValuePair(name, value);
	}

	/**
	 * Converts input stream to a string.
	 */
	public static String convertStreamToString(InputStream is, String coding) throws IOException {
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, coding)); // iso-8859-2
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		return writer.toString();
	}

	public static void setContext(Context c) {
		context = c;
	}

}
