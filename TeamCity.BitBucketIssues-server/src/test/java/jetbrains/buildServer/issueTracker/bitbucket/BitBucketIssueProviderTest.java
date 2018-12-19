package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueProviderTest extends BaseTestCase {

  private Mockery m;

  private PluginDescriptor myDescriptor;
  private BitBucketIssueProviderType myType;
  private BitBucketIssueProvider myProvider;

  @Override
  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    m = new Mockery() {{
      setImposteriser(ClassImposteriser.INSTANCE);
    }};
    myDescriptor  = m.mock(PluginDescriptor.class);
    m.checking(new Expectations() {{
      allowing(myDescriptor).getPluginResourcesPath(with(any(String.class)));
      will(returnValue("path"));
    }});
    myType = new BitBucketIssueProviderType(myDescriptor);
    final BitBucketIssueFetcher fetcher = m.mock(BitBucketIssueFetcher.class);
    m.checking(new Expectations() {{
      allowing(fetcher);
    }});
    final BitBucketIssueProviderFactory factory = new BitBucketIssueProviderFactory(myType, fetcher);
    myProvider = (BitBucketIssueProvider) factory.createProvider();
  }

  @Test
  public void testUseBitbucketCloud_Implicit() throws Exception {
    final String owner = "owner";
    final String repo = "repo";
    myProvider.setProperties(getProperties(owner + "/" + repo));
    assertEquals("https://bitbucket.org/owner/repo/", myProvider.getProperties().get("host"));
    assertEquals(getExpectedFetchHost(owner, repo), getActualFetchHost());
  }

  @Test
  public void testUseBitbucketCloud_Explicit() throws Exception {
    final String owner = "owner";
    final String repo = "repo";
    myProvider.setProperties(getProperties("https://bitbucket.org/owner/repo"));
    assertEquals("https://bitbucket.org/owner/repo/", myProvider.getProperties().get("host"));
    assertEquals(getExpectedFetchHost(owner, repo), getActualFetchHost());
  }

  private Map<String, String> getProperties(@NotNull final String repo) {
    final Map<String, String> result = myType.getDefaultProperties();
    result.put(BitBucketConstants.PARAM_REPOSITORY, repo);
    return result;
  }

  @SuppressWarnings("SameParameterValue")
  private String getExpectedFetchHost(String owner, String repo) {
    return "https://api.bitbucket.org/2.0/repositories/" + owner + "/" + repo + "/issues/";
  }

  private String getActualFetchHost() throws Exception {
    Field fetchHostField = null;
    Class clazz = myProvider.getClass();
    do {
      Optional<Field> of = Arrays.asList(clazz.getDeclaredFields()).stream()
              .filter(f -> "myFetchHost".equals(f.getName())).findFirst();
      if (of.isPresent()) {
        fetchHostField = of.get();
      }
      clazz = clazz.getSuperclass();
    } while (!"java.lang.Object".equals(clazz.getName()) && fetchHostField == null);
    if (fetchHostField != null) {
      fetchHostField.setAccessible(true);
      return (String)fetchHostField.get(myProvider);
    }
    return null;
  }

  @Override
  @AfterMethod
  public void tearDown() throws Exception {
    super.tearDown();
    m.assertIsSatisfied();
  }
}
