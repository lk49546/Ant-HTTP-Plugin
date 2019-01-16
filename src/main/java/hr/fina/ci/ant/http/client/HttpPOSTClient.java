package hr.fina.ci.ant.http.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.fina.ci.ant.http.client.HttpResponse.HttpResponseBuilder;

/**
 * HttpClient implementation, implemens invoke method. It obtains atributes from
 * HttpPostTask class and creates post request on some url. It enables two
 * possible wazs of posting, one with multipart file, and other with plain old
 * x-www-form-urlencoded Content/Tzpe.
 *
 * @author lkelava
 * 
 */
public class HttpPOSTClient implements HttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpPOSTClient.class);

	private static final String QUERY_EQUAL = "=";
	private static final String QUERY_AMPERSAND = "&";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TWO_HYPHENS = "--";
	private static final String BOUNDARY_HYPHENS = "---------------------------";
	private static final String LINE_END = "\r\n";
	private static final String HTTP_POST = "POST";

	private final String pageUrl;
	private final String username;
	private final String password;

	private final Map<String, String> headers;
	private final Map<String, String> params;
	private final FilePackage filePackage;

	private HttpPOSTClient(String pageUrl, String username, String password, Map<String, String> headers,
			FilePackage filePackage, Map<String, String> params) {
		super();
		this.pageUrl = pageUrl;
		this.username = username;
		this.password = password;
		this.headers = headers;
		this.filePackage = filePackage;
		this.params = params;
	}

	public HttpResponse invoke() throws IOException, NoSuchAlgorithmException, KeyManagementException {

		URL url = new URL(fixURL(pageUrl));
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setRequestMethod(HTTP_POST);
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setDoOutput(true);

		// sets basic auth
		if (null != username && username.length() > 0) {
			final String userpass = username + ":" + (null == password ? "" : password);
			final String basicAuth = "Basic " + Base64.encodeBase64String(userpass.getBytes());
			httpUrlConnection.setRequestProperty("Authorization", basicAuth);
		}

		// sets headers
		if (!headers.isEmpty()) {
			for (final Entry<String, String> header : headers.entrySet()) {
				httpUrlConnection.setRequestProperty(header.getKey(), header.getValue());
			}
		}

		// 1st case: multipart file
		if (filePackage != null) {
			String currentTime = Long.toString(System.currentTimeMillis());
			httpUrlConnection.setRequestProperty(CONTENT_TYPE,
					"multipart/form-data; boundary=" + BOUNDARY_HYPHENS + currentTime);
			try (DataOutputStream outputStream = new DataOutputStream(httpUrlConnection.getOutputStream())) {

				// writing header to output stream
				String boundary = BOUNDARY_HYPHENS + currentTime;
				outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
				outputStream.writeBytes("Content-Disposition: form-data; name=\"userfile\"; filename=\""
						+ filePackage.getKey() + "\"" + LINE_END);
				outputStream.writeBytes("Content-Type: application/x-zip-compressed" + LINE_END);

				outputStream.writeBytes(LINE_END);

				readFile(filePackage.getFile(), outputStream);

				outputStream.writeBytes(LINE_END);
				outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);

				// posting params
				Iterator<String> keyIterator = params.keySet().iterator();
				while (keyIterator.hasNext()) {
					String key = keyIterator.next();
					String value = params.get(key);
					outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"");
					outputStream.writeBytes(LINE_END);
					outputStream.writeBytes(LINE_END);
					outputStream.writeBytes(value);
					outputStream.writeBytes(LINE_END);

					if (keyIterator.hasNext()) {
						outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
					} else {
						outputStream.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_END);
					}
				}
				return convertToHttpResponse(httpUrlConnection);
			} finally {
				httpUrlConnection.disconnect();
			}
			// 2nd case: x-www-form-urlencoded
		} else {
			try (DataOutputStream outputStream = new DataOutputStream(httpUrlConnection.getOutputStream())) {
				outputStream.writeBytes(generateParams());
				return convertToHttpResponse(httpUrlConnection);
			} finally {
				httpUrlConnection.disconnect();
			}
		}
	}

	private HttpResponse convertToHttpResponse(HttpURLConnection httpUrlConnection) throws IOException {
		HttpResponseBuilder builder = new HttpResponseBuilder();
		return builder.response(readResponse(httpUrlConnection)).responseCode(httpUrlConnection.getResponseCode())
				.responseMessage(httpUrlConnection.getResponseMessage()).build();
	}

	/**
	 * Returns correct url
	 * 
	 * @param pageURL
	 * @return fixed url, lower case and prefixed with correct protocol (http or
	 *         https)
	 */
	private String fixURL(String pageURL) {
		StringBuilder builder = new StringBuilder();
		LOGGER.info("Pocetni URL: {}", pageURL);
		if (!pageURL.startsWith("http://") && !pageURL.startsWith("https://")) {
			builder.append("http://");
		}
		builder.append(pageURL.toLowerCase());
		LOGGER.info("Zavrsni URL: {}", builder);
		return builder.toString();
	}

	/**
	 * Returns parameters
	 * 
	 * @return parameters in format param=value&param2=value2
	 */
	private String generateParams() {
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<String> keyIterator = params.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = params.get(key);
			stringBuilder.append(key).append(QUERY_EQUAL).append(value);
			if (keyIterator.hasNext()) {
				stringBuilder.append(QUERY_AMPERSAND);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Reads file from File object into output stream
	 * 
	 * @param file
	 * @param outputStream
	 * @throws IOException
	 */
	private void readFile(File file, DataOutputStream outputStream) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			int maxBufferSize = 50 * 1024 * 1024;
			int bytesAvailable = fileInputStream.available();
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
		}
	}

	/**
	 * @param con
	 * @return String representation of response
	 * @throws IOException
	 */
	private String readResponse(HttpURLConnection con) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String line;
			StringBuilder response = new StringBuilder();

			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			return response.toString();
		}
	}

	public String pageUrl() {
		return pageUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Builder for HttpPOSTClient.
	 * 
	 * @author lkelava
	 *
	 */
	public static class HttpPOSTClientBuilder {
		private String pageUrl;
		private String username;
		private String password;

		private Map<String, String> headers;
		private Map<String, String> params;
		private FilePackage filePackage;

		public HttpPOSTClientBuilder() {
			super();
			params = new HashMap<>();
			headers = new HashMap<>();
		}

		public HttpPOSTClientBuilder url(String url) {
			this.pageUrl = url;
			return this;
		}

		public HttpPOSTClientBuilder username(String username) {
			this.username = username;
			return this;
		}

		public HttpPOSTClientBuilder password(String password) {
			this.password = password;
			return this;
		}

		public HttpPOSTClientBuilder header(String name, String value) {
			headers.put(name, value);
			return this;
		}

		public HttpPOSTClientBuilder params(String name, String value) {
			params.put(name, value);
			return this;
		}

		public HttpPOSTClientBuilder file(String name, File value) {
			filePackage = new FilePackage(name, value);
			return this;
		}

		public HttpClient build() {
			return new HttpPOSTClient(pageUrl, username, password, headers, filePackage, params);
		}

	}

}
