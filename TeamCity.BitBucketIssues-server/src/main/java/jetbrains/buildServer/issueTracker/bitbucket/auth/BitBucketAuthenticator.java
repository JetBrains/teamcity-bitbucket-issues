/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.issueTracker.bitbucket.auth;

import jetbrains.buildServer.issueTracker.IssueFetcherAuthenticator;
import jetbrains.buildServer.util.HTTPRequestBuilder;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketAuthenticator implements IssueFetcherAuthenticator {

  private Credentials myCredentials = null;

  public BitBucketAuthenticator(@NotNull final Map<String, String > properties) {
    final String authType = properties.get(PARAM_AUTH_TYPE);
    if (AUTH_LOGIN_PASSWORD.equals(authType)) {
      final String username = properties.get(PARAM_USERNAME);
      final String password = properties.get(PARAM_PASSWORD);
      myCredentials = new UsernamePasswordCredentials(username, password);
    }
  }

  @Override
  public boolean isBasicAuth() {
    return true;
  }

  @Override
  public void applyAuthScheme(@NotNull HttpMethod httpMethod) {
  }

  @Override
  public void applyAuthScheme(@NotNull final HTTPRequestBuilder requestBuilder) {
  }

  @Nullable
  @Override
  public Credentials getCredentials() {
    return myCredentials;
  }
}
