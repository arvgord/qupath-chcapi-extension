// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.oauth20;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.quantumsoft.qupathcloud.exception.QuPathCloudException;
import com.quantumsoft.qupathcloud.repository.Repository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * OAuth20 class for access to the Google Cloud Healthcare API using OAuth 2.0 protocol.
 */
public class OAuth20 {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String CLIENT_SECRETS_FILE_NAME = "client_secrets.json";
  private static final Path STORE_DIRECTORY = Paths.get(".store/oauth20-0.2.0-m2");
  private static final List<String> SCOPES = Arrays.asList(
      "https://www.googleapis.com/auth/cloud-healthcare",
      "https://www.googleapis.com/auth/cloudplatformprojects.readonly");
  private static final String USER_ID = "user";
  private static final int TIME_WHEN_REFRESH_TOKEN_IN_SECONDS = 60;
  private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private final File DATA_STORE_DIR;
  private AuthorizationCodeInstalledApp authenticator;
  private Credential credential;

  /**
   * Instantiates a new OAuth20.
   *
   * @param baseQupathDirectory the base QuPath directory
   */
  public OAuth20(Path baseQupathDirectory) {
    DATA_STORE_DIR = baseQupathDirectory.resolve(STORE_DIRECTORY).toFile();
  }

  /**
   * Gets credential.
   *
   * @return the credential
   * @throws QuPathCloudException if an error occurs
   */
  public synchronized Credential getCredential() throws QuPathCloudException {
    try {
      if (authenticator == null) {
        login();
      }
      if (credential.getExpiresInSeconds() < TIME_WHEN_REFRESH_TOKEN_IN_SECONDS) {
        credential.refreshToken();
        credential = authenticator.authorize(USER_ID);
      }
    } catch (IOException | GeneralSecurityException e) {
      throw new QuPathCloudException(e);
    }
    LOGGER.trace("Got credential");
    return credential;
  }

  /**
   * Invalidates credentials.
   *
   * @throws IOException if IOException occurs
   */
  public void invalidateCredentials() throws IOException {
    if (authenticator != null) {
      authenticator.getReceiver().stop();
      authenticator.getFlow().getCredentialDataStore().clear();
      authenticator = null;
    }
  }

  private AuthorizationCodeInstalledApp getAuthenticator()
      throws QuPathCloudException, IOException, GeneralSecurityException {
    InputStream is = getClass().getClassLoader().getResourceAsStream(CLIENT_SECRETS_FILE_NAME);
    if (is == null) {
      throw new QuPathCloudException("Failed to load " + CLIENT_SECRETS_FILE_NAME);
    }
    GoogleClientSecrets clientSecrets = GoogleClientSecrets
        .load(JSON_FACTORY, new InputStreamReader(is, StandardCharsets.UTF_8));
    Details details = clientSecrets.getDetails();
    String clientId = details.getClientId();
    String clientSecret = details.getClientSecret();
    if (clientId.length() == 0 || clientSecret.length() == 0) {
      throw new QuPathCloudException("Empty client secret or client id!");
    }
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
        .Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(dataStoreFactory)
        .addRefreshListener(new DataStoreCredentialRefreshListener(USER_ID, dataStoreFactory))
        .setAccessType("offline")
        .setApprovalPrompt("force")
        .build();
    Repository.INSTANCE.getIsLoggedInProperty().set(false);
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver());
  }

  private void login() throws QuPathCloudException, IOException, GeneralSecurityException {
    LOGGER.debug("Login user");
    authenticator = getAuthenticator();
    authorize();
  }

  private void authorize() throws IOException {
    try {
      credential = authenticator.authorize(USER_ID);
    } catch (NullPointerException e) {
      //nope
    }
  }
}
