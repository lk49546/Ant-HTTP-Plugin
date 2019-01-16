package hr.fina.ci.ant.http.task;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.fina.ci.ant.http.client.HttpClient;
import hr.fina.ci.ant.http.client.HttpPOSTClient.HttpPOSTClientBuilder;
import hr.fina.ci.ant.http.client.HttpResponse;

/**
 * Task used for HTTP Post with ant. POST can handle form-data parameters and multipart file.
 * 
 * @author lkelava
 *
 */
public class HttpPostTask extends Task {
	
	private static Logger LOGGER = LoggerFactory.getLogger(HttpPostTask.class);
	private static final Integer SUCCESS_CODE = 200;
	
	private String url;
	private String printResponseInfo;
	private Boolean failOnUnexpectedBehaviour = true;
	private HeadersNode headers;
	private CredentialsNode credentials;
	private ParamsNode params;
	private FileNode file;
	
	// http client (post, get, put... implementation)
	private HttpClient httpClient;
	

	public HttpPostTask() {
		super();
	}

	@Override
	public void init() throws BuildException {
		super.init();
	}

	/**
	 * HTTP POST client initialization
	 */
	private void initHttpClient() {
		HttpPOSTClientBuilder builder = new HttpPOSTClientBuilder();
		LOGGER.info("init; url={}", url);
		builder.url(url);
		
		if (credentials != null && credentials.isValid()) {
			builder.username(credentials.getUsername()).password(credentials.getPassword());
		}
		
		if (headers != null && headers.isValid()) {
			for (HeaderNode headerNode : headers.getHeaders()) {
				if (headerNode != null && headerNode.isValid()) {
					builder.header(headerNode.getName(), headerNode.getValue());
				}
			}
		}
		
		if (params != null && params.isValid()) {
			for (ParamNode paramNode : params.getParams()) {
				if (paramNode != null && paramNode.isValid()) {
					builder.params(paramNode.getName(), paramNode.getValue());
				}
			}
		}
		
		if (file != null && file.isValid()) {
			builder.file(file.getName(), file.getFile());
		}
		
		this.httpClient = builder.build();
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		super.execute();
		initHttpClient();
		
		LOGGER.info("POST method execution started on URL: {}", url);
		
		if (credentials != null && credentials.isValid()) {
			LOGGER.info("User info\nUsername: {}\nPassword: *********", credentials.getUsername());
		}
		
		try {
			HttpResponse httpResponse = httpClient.invoke();
			LOGGER.info("******************************Response*****************************\n{}", httpResponse.getResponse());
			LOGGER.info("******************************Response Code************************\n{}", httpResponse.getResponseCode());
			LOGGER.info("******************************Response Message*********************\n{}", httpResponse.getResponseMessage());
			
			if (!httpResponse.getResponseCode().equals(SUCCESS_CODE) && failOnUnexpectedBehaviour) {
				throw new BuildException("Post failed, success code not equal to %d", SUCCESS_CODE);
			}
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException e) {
			LOGGER.error("Error happened, please check logs, {}", e);
		}
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addConfiguredHeaders(HeadersNode headers) {
		this.headers = headers;
	}

	public void addConfiguredCredentials(CredentialsNode credentials) {
		this.credentials = credentials;
	}

	public void addConfiguredParams(ParamsNode params) {
		this.params = params;
	}

	public void addConfiguredFile(FileNode fileNode) {
		this.file = fileNode;
	}

	public String getPrintResponseInfo() {
		return printResponseInfo;
	}

	public void setPrintResponseInfo(String printResponseInfo) {
		this.printResponseInfo = printResponseInfo;
	}

	public Boolean getFailOnUnexpectedBehaviour() {
		return failOnUnexpectedBehaviour;
	}

	public void setFailOnUnexpectedBehaviour(Boolean failOnUnexpectedBehaviour) {
		this.failOnUnexpectedBehaviour = failOnUnexpectedBehaviour;
	}
	
}
