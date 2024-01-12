

package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.issueTracker.bitbucket.auth.BitBucketAuthenticator;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketAuthenticatorTest extends BaseTestCase {

  private Map<String, String> myProperties;

  @Override
  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    myProperties = new HashMap<>();
  }

  @Test
  public void testAnonymous() throws Exception {
    myProperties.put(PARAM_AUTH_TYPE, AUTH_ANONYMOUS);
    myProperties.put(PARAM_USERNAME, "username");
    myProperties.put(PARAM_PASSWORD, "password");
    assertNull(getCredentials());
  }

  @Test
  public void testLoginPassword() throws Exception {
    myProperties.put(PARAM_AUTH_TYPE, AUTH_LOGIN_PASSWORD);
    myProperties.put(PARAM_USERNAME, "username");
    myProperties.put(PARAM_PASSWORD, "password");
    final Credentials crd = getCredentials();
    assertTrue(crd instanceof UsernamePasswordCredentials);
    final UsernamePasswordCredentials upc = (UsernamePasswordCredentials) crd;
    assertEquals("username", upc.getUserName());
    assertEquals("password", upc.getPassword());
  }

  @Nullable
  private Credentials getCredentials() {
    return new BitBucketAuthenticator(myProperties).getCredentials();
  }
}