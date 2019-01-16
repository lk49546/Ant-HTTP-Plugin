package hr.fina.ci.ant.http.task;

import java.util.Collection;
import java.util.HashSet;

import org.apache.tools.ant.types.DataType;

/**
 * Element that contains collection of headers
 * 
 * @author lkelava
 *
 */
public class HeadersNode extends DataType implements Validable {
	private final Collection<HeaderNode> headers;

	public HeadersNode() {
		super();
		this.headers= new HashSet<>();
	}
	
	public void addConfiguredHeader(HeaderNode header) {
		headers.add(header);
	}

	public Collection<HeaderNode> getHeaders() {
		return headers;
	}
	
	/* (non-Javadoc)
	 * @see hr.fina.ci.ant.http.task.Validable#isValid()
	 */
	public boolean isValid() {
		return !headers.isEmpty();
	}
	
}
