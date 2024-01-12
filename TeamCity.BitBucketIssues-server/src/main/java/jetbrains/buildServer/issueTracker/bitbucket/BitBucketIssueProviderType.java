

package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.issueTracker.IssueProviderType;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.AUTH_ANONYMOUS;
import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.PARAM_AUTH_TYPE;
import static jetbrains.buildServer.issueTracker.bitbucket.BitBucketConstants.PARAM_PATTERN;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueProviderType extends IssueProviderType {

  @NotNull
  private final String myConfigUrl;

  @NotNull
  private final String myPopupUrl;

  @NotNull
  private static final String DEFAULT_ISSUE_PATTERN = "#(\\d+)";

  public BitBucketIssueProviderType(@NotNull final PluginDescriptor pluginDescriptor) {
    myConfigUrl = pluginDescriptor.getPluginResourcesPath("admin/editIssueProvider.jsp");
    myPopupUrl = pluginDescriptor.getPluginResourcesPath("popup.jsp");
  }

  @NotNull
  @Override
  public String getType() {
    return "BitBucketIssues";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Bitbucket";
  }

  @NotNull
  @Override
  public String getEditParametersUrl() {
    return myConfigUrl;
  }

  @NotNull
  @Override
  public String getIssueDetailsUrl() {
    return myPopupUrl;
  }

  @NotNull
  @Override
  public Map<String, String> getDefaultProperties() {
    return new HashMap<String, String>() {{
      put(PARAM_AUTH_TYPE, AUTH_ANONYMOUS);
      put(PARAM_PATTERN, DEFAULT_ISSUE_PATTERN);
    }};
  }
}