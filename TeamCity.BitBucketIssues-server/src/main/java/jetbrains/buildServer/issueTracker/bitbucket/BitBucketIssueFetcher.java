package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.cache.EhCacheHelper;
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

  public BitBucketIssueFetcher(@NotNull final EhCacheHelper cacheHelper,
                               @NotNull final IssueParser parser) {
    super(cacheHelper);
    myParser = parser;
  }

  @NotNull
  @Override
  public IssueData getIssue(@NotNull String host, @NotNull String id, @Nullable Credentials credentials) throws Exception {
    //here host == sanitized fetchHost
    final String issueId = getIssueId(id);
    final String issueURL = host + issueId;
    System.out.println(issueURL);
    return getFromCacheOrFetch(issueURL, () -> {
      InputStream body = fetchHttpFile(issueURL, credentials);
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
    return String.format("%sissues/%s", host, issueId);
  }

  private Pattern myPattern;

  public void setPattern(final Pattern pattern) {
    myPattern = pattern;
  }

}
