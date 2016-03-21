package jetbrains.buildServer.issueTracker.bitbucket.auth;

import jetbrains.buildServer.issueTracker.IssueFetcherAuthenticator;
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
    return false;
  }

  @Override
  public void applyAuthScheme(@NotNull HttpMethod httpMethod) {
  }

  @Nullable
  @Override
  public Credentials getCredentials() {
    return myCredentials;
  }
}
