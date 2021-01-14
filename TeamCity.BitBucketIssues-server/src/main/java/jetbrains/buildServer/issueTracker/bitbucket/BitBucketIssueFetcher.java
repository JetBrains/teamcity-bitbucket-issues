/*
 * Copyright 2000-2021 JetBrains s.r.o.
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

package jetbrains.buildServer.issueTracker.bitbucket;

import java.security.KeyStore;
import jetbrains.buildServer.http.SimpleCredentials;
import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.BasicIssueFetcherAuthenticator;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.issueTracker.IssueFetcherUtil;
import jetbrains.buildServer.util.ssl.SSLTrustStoreProvider;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueFetcher extends AbstractIssueFetcher {

  @NotNull
  private final IssueParser myParser;

  @NotNull
  private final SSLTrustStoreProvider mySslTrustStoreProvider;

  public BitBucketIssueFetcher(
    @NotNull final EhCacheUtil cacheUtil,
    @NotNull final IssueParser parser,
    @NotNull final SSLTrustStoreProvider sslTrustStoreProvider
  ) {
    super(cacheUtil);
    myParser = parser;
    mySslTrustStoreProvider = sslTrustStoreProvider;
  }

  @NotNull
  @Override
  public IssueData getIssue(@NotNull String host, @NotNull String id, @Nullable Credentials credentials) throws Exception {
    //here host == sanitized fetchHost
    final String issueId = getIssueId(id);
    final String issueURL = host + issueId;
    return getFromCacheOrFetch(issueURL, () -> {
      SimpleCredentials simpleCredentials = IssueFetcherUtil
        .retrieveCredentials(new BasicIssueFetcherAuthenticator(credentials));
      KeyStore trustStore = mySslTrustStoreProvider.getTrustStore();
      InputStream body = getHttpFile(issueURL, simpleCredentials, true, trustStore);
      return myParser.parse(IOUtils.toString(body, "UTF-8"));
    });
  }

  private String getIssueId(@NotNull final String idString) {
    final Matcher matcher = myPattern.matcher(idString);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return idString;
    }
  }

  @NotNull
  @Override
  public String getUrl(@NotNull String host, @NotNull String issueId) {
    return host + issueId;
  }

  private Pattern myPattern;

  public void setPattern(final Pattern pattern) {
    myPattern = pattern;
  }

}
