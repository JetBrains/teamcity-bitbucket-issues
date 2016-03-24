package jetbrains.buildServer.issueTracker.bitbucket;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.issueTracker.AbstractIssueProvider;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import jetbrains.buildServer.issueTracker.IssueFetcherAuthenticator;
import jetbrains.buildServer.issueTracker.bitbucket.auth.BitBucketAuthenticator;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces;
import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueProvider extends AbstractIssueProvider {

  private static final Pattern FULL_HOST_PATTERN = Pattern.compile("^https://(.+)/([^/]+)/([^/]+)/?$");

  private static final Pattern OWNER_AND_REPO_PATTERN = Pattern.compile("^/?([^/]+)/([^/]+)/?$");

  private static final Logger LOG = Logger.getInstance(BitBucketIssueProvider.class.getName());

  public BitBucketIssueProvider(@NotNull String type, @NotNull IssueFetcher fetcher) {
    super(type, fetcher);
  }

  @NotNull
  @Override
  protected IssueFetcherAuthenticator getAuthenticator() {
    return new BitBucketAuthenticator(myProperties);
  }

  @Override
  public void setProperties(@NotNull Map<String, String> map) {
    super.setProperties(map);
    // host - http endpoint for browser
    // fetch host - http endpoint for rest api
    final URL htmlUrl = getFullUrl(map);
    if (htmlUrl != null) {
      myHost = sanitizeHost(htmlUrl.toString());
      myProperties.put("host", myHost);
      final Matcher m = FULL_HOST_PATTERN.matcher(myHost);
      if (m.matches()) {
        myFetchHost = getAPIUrl(htmlUrl, m.group(2), StringUtil.removeSuffix(m.group(3), ".git", false));
      }
    }
  }

  @Nullable
  private String getAPIUrl(@NotNull final URL htmlUrl, @NotNull final String owner, @NotNull final String repo) {
    try {
      return new URL("https", "api." + htmlUrl.getHost(), "/1.0/repositories/" + owner + "/" + repo + "/issues/").toString();
    } catch (MalformedURLException e) {
      LOG.warn(e);
    }
    return null;
  }

  @Nullable
  private static URL getFullUrl(@NotNull final Map<String, String> properties) {
    String result = properties.get(PARAM_REPOSITORY);
    final Matcher matcher = OWNER_AND_REPO_PATTERN.matcher(result);
    if (matcher.matches()) {
      result = "https://bitbucket.org/" + matcher.group(1) + "/" + matcher.group(2);
    }
    try {
      return new URL(result);
    } catch(MalformedURLException e) {
      LOG.warn(e);
    }
    return null;
  }

  @NotNull
  @Override
  protected Pattern compilePattern(@NotNull Map<String, String> properties) {
    final Pattern result = super.compilePattern(properties);
    ((BitBucketIssueFetcher) myFetcher).setPattern(result);
    return result;
  }

  @NotNull
  @Override
  protected String extractId(@NotNull final String match) {
    Matcher m = myPattern.matcher(match);
    if (m.find() && m.groupCount() >= 1) {
      return m.group(1);
    } else {
      return super.extractId(match);
    }
  }

  @Override
  protected final String getIssueViewUrl(@NotNull final String id) {
    return myHost + "issues/" + id;
  }

  @NotNull
  @Override
  public PropertiesProcessor getPropertiesProcessor() {
    return MY_PROCESSOR;
  }

  private static final PropertiesProcessor MY_PROCESSOR = new PropertiesProcessor() {
    public Collection<InvalidProperty> process(Map<String, String> map) {
      final List<InvalidProperty> result = new ArrayList<>();

      if (checkNotEmptyParam(result, map, PARAM_AUTH_TYPE, "Authentication type must be specified")) {
        // we have auth type. check against it
        final String authTypeParam = map.get(PARAM_AUTH_TYPE);
        if (authTypeParam.equals(AUTH_LOGIN_PASSWORD)) {
          checkNotEmptyParam(result, map, PARAM_USERNAME, "Username must be specified");
          checkNotEmptyParam(result, map, PARAM_PASSWORD, "Password must be specified");
        }
        if (checkNotEmptyParam(result, map, PARAM_PATTERN, "Issue pattern must not be empty")) {
          try {
            String patternString = map.get(PARAM_PATTERN);
            //noinspection ResultOfMethodCallIgnored
            Pattern.compile(patternString);
          } catch (PatternSyntaxException e) {
            result.add(new InvalidProperty(PARAM_PATTERN, "Syntax of issue pattern is not correct"));
          }
        }

        if (checkNotEmptyParam(result, map, PARAM_REPOSITORY, "Repository must be specified")) {
          URL url = getFullUrl(map);
          if (url == null) {
            result.add(new InvalidProperty(PARAM_REPOSITORY, "Either a valid URL or owner/repo must be specified"));
          }
        }
      }
      return result;
    }

    private boolean checkNotEmptyParam(@NotNull final Collection<InvalidProperty> invalid,
                                       @NotNull final Map<String, String> map,
                                       @NotNull final String propertyName,
                                       @NotNull final String errorMessage) {
      if (isEmptyOrSpaces(map.get(propertyName))) {
        invalid.add(new InvalidProperty(propertyName, errorMessage));
        return false;
      }
      return true;
    }
  };
}
