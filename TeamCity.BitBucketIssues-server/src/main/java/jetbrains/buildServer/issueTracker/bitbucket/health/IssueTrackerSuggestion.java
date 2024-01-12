

package jetbrains.buildServer.issueTracker.bitbucket.health;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jetbrains.buildServer.issueTracker.IssueProvidersManager;
import jetbrains.buildServer.issueTracker.bitbucket.BitBucketIssueProviderType;
import jetbrains.buildServer.parameters.ReferencesResolverUtil;
import jetbrains.buildServer.serverSide.BuildTypeSettings;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.healthStatus.ProjectSuggestedItem;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.healthStatus.suggestions.ProjectSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class IssueTrackerSuggestion extends ProjectSuggestion {

  private static final String GIT_VCS_NAME = "jetbrains.git";
  private static final String GIT_FETCH_URL_PROPERTY = "url";

  private static final String HG_VCS_NAME = "mercurial";
  private static final String HG_FETCH_URL_PROPERTY = "repositoryPath";

  private static final Logger LOG = Logger.getInstance(IssueTrackerSuggestion.class.getName());

  @NotNull
  private final String myViewUrl;

  @NotNull
  private final IssueProvidersManager myIssueProvidersManager;

  @NotNull
  private final BitBucketIssueProviderType myType;

  public IssueTrackerSuggestion(@NotNull final PluginDescriptor pluginDescriptor,
                                @NotNull PagePlaces pagePlaces,
                                @NotNull final IssueProvidersManager issueProvidersManager,
                                @NotNull final BitBucketIssueProviderType type) {
    super("addBitbucketIssueTracker", "Suggest to add a Bitbucket Issue Tracker", pagePlaces);
    myIssueProvidersManager = issueProvidersManager;
    myType = type;
    myViewUrl = pluginDescriptor.getPluginResourcesPath("health/addBitbucketIssueTracker.jsp");
  }

  @Override
  public List<ProjectSuggestedItem> getSuggestions(@NotNull final SProject project) {
    final String type = myType.getType();
    boolean alreadyUsed = myIssueProvidersManager.getProviders(project).stream().anyMatch(it -> it.getType().equals(type));
    final List<ProjectSuggestedItem> result = new ArrayList<>();
    if (!alreadyUsed) {
      final List<SBuildType> buildTypes = project.getOwnBuildTypes();
      Set<String> paths = getPathsFromVcsRoots(buildTypes);
      if (paths.stream().anyMatch(ReferencesResolverUtil::mayContainReference)) {
        paths = getPathsFromInstances(buildTypes);
      }
      if (!paths.isEmpty()) {
        final Map<String, Map<String, Object>> results = new HashMap<>();
        paths.stream().map(this::toSuggestion).filter(Objects::nonNull).forEach(p -> results.put(p.first, p.second));
        if (!results.isEmpty()) {
          result.add(new ProjectSuggestedItem(getType(), project, Collections.singletonMap("suggestedTrackers", results)));
        }
      }
    }
    return result;
  }

  private Set<String> getPathsFromVcsRoots(@NotNull final List<SBuildType> buildTypes) {
    return extractFetchUrls(buildTypes.stream().map(BuildTypeSettings::getVcsRoots));
  }

  private Set<String> getPathsFromInstances(@NotNull final List<SBuildType> buildTypes) {
    return extractFetchUrls(buildTypes.stream().map(SBuildType::getVcsRootInstances));
  }

  private Set<String> extractFetchUrls(@NotNull final Stream<List<? extends VcsRoot>> stream) {
    return stream.flatMap(List::stream)
            .map(this::getFetchUrl)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
  }

  @Nullable
  private <T extends VcsRoot> String getFetchUrl(@NotNull final T vcsRoot) {
    final String vcsName = vcsRoot.getVcsName();
    if (GIT_VCS_NAME.equals(vcsName)) {
      return vcsRoot.getProperty(GIT_FETCH_URL_PROPERTY);
    } else if (HG_VCS_NAME.equals(vcsName)) {
      return vcsRoot.getProperty(HG_FETCH_URL_PROPERTY);
    } else {
      return null;
    }
  }

  private static final Pattern SSH_NO_PROTOCOL_PATTERN = Pattern.compile("git@bitbucket\\.org:([^/]+)/([^/]+)\\.git");


  @Nullable
  private Pair<String, Map<String, Object>> toSuggestion(@NotNull final String fetchUrl) {
    // check for ssh with no protocol
    String suggestedName;
    final Matcher m = SSH_NO_PROTOCOL_PATTERN.matcher(fetchUrl);
    if (m.matches()) {
      // we have git url with no protocol.
      suggestedName = m.group(1) + "/" + m.group(2);
    } else {
      try {
        final URI uri = new URI(fetchUrl);
        if ("bitbucket.org".equals(uri.getHost())) {
          suggestedName = StringUtil.removeSuffix(uri.getPath().substring(1), ".git", false);
        } else {
          return null;
        }
      } catch (URISyntaxException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Could not extract Bitbucket issue tracker suggestion from fetch URL: [" + fetchUrl + "]");
        }
        return null;
      }
    }
    final String repoUrl = "https://bitbucket.org/" + suggestedName;
    final Map<String, Object> result = new HashMap<>();
    result.put("type", myType.getType());
    result.put("suggestedName", suggestedName);
    result.put("repoUrl", repoUrl);
    return new Pair<>(repoUrl, result);
  }


  @NotNull
  @Override
  public String getViewUrl() {
    return myViewUrl;
  }
}