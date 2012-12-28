package cz.via.slidecaster.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import cz.via.slidecaster.R;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.exception.NetworkException;
import cz.via.slidecaster.exception.UnknownException;

/**
 * Basic web client for issuing get and post requests.
 */
public class WebClient {

	protected static DefaultHttpClient client = ThreadedClient.getInstance();
	protected static Context context;

	private static String execute(HttpUriRequest request, String coding) throws ApplicationException {

		// TODO - error handling

		String data = null;

		try {
			HttpResponse re = client.execute(request);
			HttpEntity en = re.getEntity();
			int statusCode = re.getStatusLine().getStatusCode();
			if (statusCode != 200 && statusCode != 204) {
				return null;
			} else {
				if (en == null) {
					data = "";
				} else {
					InputStream in = en.getContent();
					data = convertStreamToString(in, coding);
				}
				return data;
			}
		} catch (IllegalStateException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (ClientProtocolException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (IOException e) {
			throw new NetworkException(context.getString(R.string.network_exception_message));
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownException(context.getString(R.string.unknown_exception_message));
		}
	}

	public static String sendRequest(String url, String coding, Header[] headers) throws ApplicationException {
		HttpGet get = new HttpGet(url);
		get.setHeaders(headers);
		return execute(get, coding);
	}

	public static String sendDeleteRequest(String url, String coding, Header[] headers) throws ApplicationException {
		HttpDelete delete = new HttpDelete(url);
		delete.setHeaders(headers);
		return execute(delete, coding);
	}

	public static String sendRequest(String url, String coding, String json, Header[] headers) throws ApplicationException {
		HttpPost post = new HttpPost(url);
		StringEntity se;
		try {
			se = new StringEntity(json);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setHeaders(headers);
			post.setEntity(se);
			post.setHeader("Content-type", "application/json");

		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
			// e.printStackTrace();
		}
		return execute(post, coding);
	}

	public static String sendRequest(String url, String coding, File file, Header[] headers) throws ApplicationException {

		HttpPost post = new HttpPost(url);
		FileBody fileContent = new FileBody(file);
		post.setHeaders(headers);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("file", fileContent);
		post.setEntity(reqEntity);
		// post.setHeader("Content-type", "image/jpeg");
		return execute(post, coding);
	}

	public static String sendPutRequest(String url, String coding, String json, Header[] headers) throws ApplicationException {
		HttpPut put = new HttpPut(url);
		StringEntity se;
		try {
			se = new StringEntity(json);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			put.setHeaders(headers);
			put.setEntity(se);
			put.setHeader("Content-type", "application/json");

		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
			// e.printStackTrace();
		}
		return execute(put, coding);
	}

	public static String sendPutRequest(String url, String coding, Header[] headers) throws ApplicationException {
		HttpPut put = new HttpPut(url);
		put.setHeaders(headers);
		put.setHeader("Content-type", "application/json");
		return execute(put, coding);
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
