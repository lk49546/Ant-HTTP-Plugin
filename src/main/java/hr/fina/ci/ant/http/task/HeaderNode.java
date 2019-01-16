package hr.fina.ci.ant.http.task;

import org.apache.tools.ant.types.DataType;

/**
 * Key-value pair for header
 * 
 * @author lkelava
 *
 */
public class HeaderNode extends DataType implements Validable {
	
	private String name;
	private String value;
	
	public HeaderNode() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see hr.fina.ci.ant.http.task.Validable#isValid()
	 */
	@Override
	public boolean isValid() {
		return name != null && !name.isEmpty();
	}
	
	
}
