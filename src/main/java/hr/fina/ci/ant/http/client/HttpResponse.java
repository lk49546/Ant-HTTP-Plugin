package hr.fina.ci.ant.http.client;

public class HttpResponse {
	private final String response;
	private final Integer responseCode;
	private final String responseMessage;
	
	private HttpResponse(String response, Integer responseCode, String responseMessage) {
		super();
		this.response = response;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	
	public String getResponse() {
		return response;
	}
	public Integer getResponseCode() {
		return responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}

	public static class HttpResponseBuilder {
		private String response;
		private Integer responseCode;
		private String responseMessage;
		
		public HttpResponseBuilder() {
			super();
		}
		public HttpResponseBuilder response(String response) {
			this.response = response;
			return this;
		}
		public HttpResponseBuilder responseCode(Integer responseCode) {
			this.responseCode = responseCode;
			return this;
		}
		public HttpResponseBuilder responseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
			return this;
		}
		public HttpResponse build() {
			return new HttpResponse(response, responseCode, responseMessage);
		}
	}
}
