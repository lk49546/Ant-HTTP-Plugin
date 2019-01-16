package hr.fina.ci.ant.http.task;

import org.apache.tools.ant.types.DataType;

/**
 * Contains attributes used for basic auth
 * 
 * @author lkelava
 *
 */
public class CredentialsNode extends DataType implements Validable {
	private String username;
	private String password;
		
	public CredentialsNode() {
		super();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/* (non-Javadoc)
	 * @see hr.fina.ci.ant.http.task.Validable#isValid()
	 */
	public boolean isValid() {
		return username != null && !username.isEmpty();
	}
	

}
