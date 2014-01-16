package webservice.security;

import java.util.Random;

public class OAuthTokenGenerator {
	public String generateToken(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		
		for (int i = 0; i < length; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		
		String output = sb.toString();
		return output;
	}
}
