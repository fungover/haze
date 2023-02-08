package org.fungover.haze;

public class Auth {
	private String password;


	public void setPassword(String password) {
		this.password = password;
	}

	private boolean authenticate(String password) {
		if (passwordNotSet())
			return true;
		return this.password.equals(password);
	}

	private boolean passwordNotSet() {
		return this.password.isBlank();
	}


}
