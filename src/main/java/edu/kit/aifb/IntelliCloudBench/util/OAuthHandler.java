/*
 * This file is part of IntelliCloudBench.
 *
 * Copyright (c) 2012, Jan Gerlinger <jan.gerlinger@gmx.de>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Institute of Applied Informatics and Formal
 * Description Methods (AIFB) nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.kit.aifb.IntelliCloudBench.util;

import java.util.Map;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi20;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.vaadin.terminal.ParameterHandler;

import edu.kit.aifb.IntelliCloudBench.model.ApplicationState;
import edu.kit.aifb.IntelliCloudBench.model.User;

@SuppressWarnings("serial")
public class OAuthHandler implements ParameterHandler {

	/* Infos for redirection to Google Login Page */
	private static final Token EMPTY_TOKEN = null;
	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	/* Infos for response verification and access token obtainment */
	private static final String VERIFIER_NAME = "code";
	private static final String GRANT_TYPE = "authorization_code";
	private static final String GET_PROFILE_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	
	private User user;
	private IOAuthListener listener;
	private String callbackUrl;

	public OAuthHandler(IOAuthListener listener, String callbackUrl) {
		

		this.callbackUrl = callbackUrl;
		this.listener = listener;
		   
	}


	public String getRedirectUrl() {
		return getService().getAuthorizationUrl(OAuthHandler.EMPTY_TOKEN);
	}

	@Override
	public void handleParameters(Map<String, String[]> parameters) {
		try {

			if (parameters.containsKey(OAuthHandler.VERIFIER_NAME)) {
				/*
				 * User logged in via Provider, now get Access Token from response
				 */
				Verifier verifier = new Verifier(parameters.get(OAuthHandler.VERIFIER_NAME)[0]);
				Token accessToken = getService().getAccessToken(null, verifier);

				/* and request User info */
				OAuthRequest userRequest = new OAuthRequest(Verb.GET, OAuthHandler.GET_PROFILE_URL);
				getService().signRequest(accessToken, userRequest);
				userRequest.addHeader("GData-Version", "3.0");
				Response userResponse = userRequest.send();

				/* Retrieve user info from JSON response */
				if (userResponse.getBody() != null) {
					Gson gson = new Gson();
					user = gson.fromJson(userResponse.getBody(), User.class);

					/* Check if this user already has an object */
					User oldUser = ApplicationState.getUserById(user.getId());
					if (oldUser != null) {
						user = oldUser;
					} else {
						ApplicationState.addUser(user);
					}

					listener.login(user);

				} else {
					listener.setErrorMessage("No user information could be retrieved.");
				}

			} else {
				if (parameters.containsKey("oauth_problems")) {
					listener.setErrorMessage(parameters.get("oauth_problems")[0]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			listener.setErrorMessage(e.toString());
		}
	}
	private OAuthService getService(){

		String apiKey = "353180287645.apps.googleusercontent.com";
		String apiSecret = "5E3J7MH2j20E5EepfkA0FIe5";
		
		return  new ServiceBuilder().provider(GoogleApi20.class).apiKey(apiKey).apiSecret(apiSecret).scope(OAuthHandler.SCOPE).
		        grantType(OAuthHandler.GRANT_TYPE).callback(callbackUrl).build();
	}
}
