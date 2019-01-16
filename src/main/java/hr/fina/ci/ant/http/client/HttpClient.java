package hr.fina.ci.ant.http.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * HttpClient interface, defines invoke method used for triggering Http method on designated URL
 * 
 * @author lkelava
 *
 */
public interface HttpClient {
	/**
	 * 
	 * 
	 * @return String representation of response
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	HttpResponse invoke() throws IOException, NoSuchAlgorithmException, KeyManagementException;

}
