package ext.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class MailAuthenticator extends Authenticator {
	
	private String userName = null;
	
    private String password = null;  
      
    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MailAuthenticator() { 
    	
    }  
      
    public MailAuthenticator(String username, String password) {     
        this.userName = username;     
        this.password = password;     
    }    
      
    protected PasswordAuthentication getPasswordAuthentication(){    
        return new PasswordAuthentication(userName, password);    
    } 

}
