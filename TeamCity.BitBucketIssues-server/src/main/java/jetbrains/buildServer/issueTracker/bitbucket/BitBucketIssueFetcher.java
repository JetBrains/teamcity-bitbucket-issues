package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.issueTracker.AbstractIssueFetcher;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.cache.EhCacheHelper;
import org.apache.commons.httpclient.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueFetcher extends AbstractIssueFetcher {

  private final Pattern ownerAndRepoPattern = Pattern.compile("/?([^/]+)/([^/]+)/?$");

  public BitBucketIssueFetcher(@NotNull EhCacheHelper cacheHelper) {
    super(cacheHelper);
  }

  @NotNull
  @Override
  public IssueData getIssue(@NotNull String host, @NotNull String id, @Nullable Credentials credentials) throws Exception {
    final String issueURL = getUrl(host, id);
    final String issueId = getIssueId(id);
    URL url;
    try {
      url = new URL(host);
      final Matcher m = ownerAndRepoPattern.matcher(url.getPath());
      if (!m.matches()) {
        throw new IllegalArgumentException("URL + [" + url.toString() + "] does not contain myOwner and repository info");
      }
      return getFromCacheOrFetch(issueURL, new FetchFunction() {
        @NotNull
        public IssueData fetch() throws Exception {
          InputStream body = fetchHttpFile(host, credentials);
          return doGetIssue(body, url);
        }
      });
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private IssueData doGetIssue(InputStream body, URL url) {
    return null;
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
  public String getUrl(@NotNull String host, @NotNull String id) {
    return String.format("%s/issues/%s", host, getIssueId(id));
  }

  private Pattern myPattern;

  public void setPattern(final Pattern pattern) {
    myPattern = pattern;
  }


}
