package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.issueTracker.errors.NotFoundException;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.cache.EhCacheHelper;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import jetbrains.buildServer.util.cache.ResetCacheRegisterImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueFetcherTest extends BaseTestCase {

  private static final String owner = "atlassianlabs";
  private static final String repo  = "stash-java-client";

  private BitBucketIssueFetcher myFetcher;

  private final IssueParser myParser = new IssueParser();

  @Override
  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    final EhCacheHelper helper = new EhCacheUtil(new ServerPaths(createTempDir().getAbsolutePath()),
            EventDispatcher.create(BuildServerListener.class),
            new ResetCacheRegisterImpl());

    myFetcher = new BitBucketIssueFetcher(helper, myParser);
    myFetcher.setPattern(Pattern.compile("#(\\d+)"));
  }

  @Test
  public void testGetIssueAnonymously() throws Exception {
    IssueData data = myFetcher.getIssue("https://api.bitbucket.org/1.0/repositories/atlassianlabs/stash-java-client/issues/", "#2", null);
    assertNotNull(data);
    System.out.println(data.toString());
  }

  @Test(expectedExceptions = NotFoundException.class)
  public void testGetIssueAnonymously_IssueNotFound() throws Exception {
    IssueData data = myFetcher.getIssue("https://api.bitbucket.org/1.0/repositories/atlassianlabs/stash-java-client/issues/", "#10000", null);
    assertNotNull(data);
    System.out.println(data.toString());
  }
}
