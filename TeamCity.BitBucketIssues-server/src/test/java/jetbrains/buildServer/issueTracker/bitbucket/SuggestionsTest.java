/*
 * Copyright 2000-2022 JetBrains s.r.o.
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

import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.issueTracker.IssueProviderEx;
import jetbrains.buildServer.issueTracker.IssueProvidersManager;
import jetbrains.buildServer.issueTracker.bitbucket.health.IssueTrackerSuggestion;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.healthStatus.ProjectSuggestedItem;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.vcs.VcsRootInstance;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class SuggestionsTest extends BaseTestCase {

  private Mockery m;
  private SBuildType myBuildType;
  private SProject myProject;
  private PluginDescriptor myPluginDescriptor;
  private PagePlaces myPagePlaces;
  private IssueProvidersManager myManager;
  private BitBucketIssueProviderType myType;
  private IssueTrackerSuggestion mySuggestion;

  private static final String PROJECT_ID = "PROJECT_ID";
  private static final String GIT = "jetbrains.git";
  private static final String HG  = "mercurial";

  @Override
  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    m = new Mockery();
    myPluginDescriptor = m.mock(PluginDescriptor.class);
    myPagePlaces = m.mock(PagePlaces.class);
    myManager = m.mock(IssueProvidersManager.class);
    myProject = m.mock(SProject.class);
    myBuildType = m.mock(SBuildType.class);
    m.checking(new Expectations() {{
      allowing(myPluginDescriptor);
      allowing(myPagePlaces);

      allowing(myProject).getOwnBuildTypes();
      will(returnValue(Collections.singletonList(myBuildType)));

      allowing(myProject).getProjectId();
      will(returnValue(PROJECT_ID));
    }});

    myType = new BitBucketIssueProviderType(myPluginDescriptor);
    mySuggestion = new IssueTrackerSuggestion(myPluginDescriptor, myPagePlaces, myManager, myType);
  }

  @Test
  public void testAlreadyUsed() {
    final IssueProviderEx bitbucketProvider = m.mock(IssueProviderEx.class);

    m.checking(new Expectations() {{
      oneOf(bitbucketProvider).getType();
      will(returnValue(myType.getType()));

      oneOf(myManager).getProviders(myProject);
      will(returnValue(Collections.singletonList(bitbucketProvider)));
    }});
    assertEmpty(mySuggestion.getSuggestions(myProject));
    m.assertIsSatisfied();
  }

  @Test
  public void testNoVcsRoots() {
    m.checking(new Expectations() {{
      oneOf(myManager).getProviders(myProject);
      will(returnValue(Collections.emptyList()));

      oneOf(myBuildType).getVcsRoots();
      will(returnValue(Collections.emptyList()));
    }});
    assertEmpty(mySuggestion.getSuggestions(myProject));
    m.assertIsSatisfied();
  }

  @Test
  public void testExistsProvider_NoVcsRoots() {
    final IssueProviderEx provider = m.mock(IssueProviderEx.class);
    m.checking(new Expectations() {{
      oneOf(provider).getType();
      will(returnValue("some_other_type"));

      oneOf(myManager).getProviders(myProject);
      will(returnValue(Collections.singletonList(provider)));

      oneOf(myBuildType).getVcsRoots();
      will(returnValue(Collections.emptyList()));
    }});
    assertEmpty(mySuggestion.getSuggestions(myProject));
    m.assertIsSatisfied();
  }

  @Test
  public void testGitHttps() {
    String repoUrl = "https://user@bitbucket.org/user/new-project.git";
    String expectedUrl = "https://bitbucket.org/user/new-project";
    testSingleUrl(repoUrl, GIT, expectedUrl);
  }

  @Test
  public void testGitSSH() {
    String repoUrl = "git@bitbucket.org:user/new-project.git";
    String expectedUrl = "https://bitbucket.org/user/new-project";
    testSingleUrl(repoUrl, GIT, expectedUrl);
  }

  @Test
  public void testHgHttps() {
    String repoUrl = "https://user@bitbucket.org/user/new-project";
    String expectedUrl = "https://bitbucket.org/user/new-project";
    testSingleUrl(repoUrl, HG, expectedUrl);
  }

  @Test
  public void testHgSSH() {
    String repoUrl = "ssh://hg@bitbucket.org/user/new-project";
    String expectedUrl = "https://bitbucket.org/user/new-project";
    testSingleUrl(repoUrl, HG, expectedUrl);
  }

  @Test
  public void testMultiple_Merge() {
    final Map<String, String> repos = new HashMap<>();
    repos.put("https://user@bitbucket.org/user/new-project.git", GIT);
    repos.put("https://user@bitbucket.org/user/new-project", GIT);
    repos.put("ssh://hg@bitbucket.org/user/new-project", HG);
    String expectedUrl = "https://bitbucket.org/user/new-project";
    testMultipleUrls(repos, Collections.singletonList(expectedUrl));
  }

  @Test
  public void testMultiple_Distinct() {
    final Map<String, String> repos = new HashMap<>();
    repos.put("https://user@bitbucket.org/user2/another-new-project", GIT);
    repos.put("ssh://hg@bitbucket.org/user/new-project", HG);
    final List<String> expected = Arrays.asList("https://bitbucket.org/user/new-project", "https://bitbucket.org/user2/another-new-project");
    testMultipleUrls(repos, expected);
  }

  @Test
  public void testParametrized() {
    String param1 = "https://user@bitbucket.org/%owner%/repo";
    String source1 = "https://user@bitbucket.org/user/new-project";
    String source2 = "https://user@bitbucket.org/user2/another-new-project";
    String expected1 = "https://bitbucket.org/user/new-project";
    String expected2 = "https://bitbucket.org/user2/another-new-project";

    final VcsRoot vcsParamRoot = m.mock(VcsRoot.class, "parametrized-root");
    final VcsRoot vcsRealRoot  = m.mock(VcsRoot.class, "real-root");

    final VcsRootInstance i1 = m.mock(VcsRootInstance.class, "instance-1");
    final VcsRootInstance i2 = m.mock(VcsRootInstance.class, "instance-2");

    m.checking(new Expectations() {{
      oneOf(myManager).getProviders(myProject);
      will(returnValue(Collections.emptyList()));

      oneOf(myBuildType).getVcsRoots();
      will(returnValue(Arrays.asList(vcsParamRoot, vcsRealRoot)));

      oneOf(vcsParamRoot).getVcsName();
      will(returnValue(GIT));

      oneOf(vcsParamRoot).getProperty("url");
      will(returnValue(param1));

      oneOf(vcsRealRoot).getVcsName();
      will(returnValue(HG));

      oneOf(vcsRealRoot).getProperty("repositoryPath");
      will(returnValue(param1));

      oneOf(myBuildType).getVcsRootInstances();
      will(returnValue(Arrays.asList(i1, i2)));

      oneOf(i1).getVcsName();
      will(returnValue(GIT));

      oneOf(i1).getProperty("url");
      will(returnValue(source1));

      oneOf(i2).getVcsName();
      will(returnValue(HG));

      oneOf(i2).getProperty("repositoryPath");
      will(returnValue(source2));
    }});
    checkSuggestions(mySuggestion.getSuggestions(myProject), Arrays.asList(expected1, expected2));
  }


  private void testSingleUrl(@NotNull final String repoUrl,
                             @NotNull final String vcsName,
                             @NotNull final String expectedUrl) {
    testMultipleUrls(Collections.singletonMap(repoUrl, vcsName), Collections.singletonList(expectedUrl));
  }

  private void testMultipleUrls(@NotNull final Map<String, String> repoUrls, @NotNull final List<String> expectedUrls) {
    final Map<String, VcsRoot> vcsRoots = new HashMap<>();
    repoUrls.entrySet().stream().forEach(entry -> vcsRoots.put(entry.getKey(), m.mock(VcsRoot.class, "vcsroot-" + entry.getKey())));

    m.checking(new Expectations() {{
      oneOf(myManager).getProviders(myProject);
      will(returnValue(Collections.emptyList()));

      oneOf(myBuildType).getVcsRoots();
      will(returnValue(new ArrayList<>(vcsRoots.values())));

    }});

    vcsRoots.entrySet().stream().forEach(entry -> {
      final String vcsName = repoUrls.get(entry.getKey());
      final String propertyName = GIT.equals(vcsName) ? "url" : "repositoryPath";
      m.checking(new Expectations() {{
        oneOf(entry.getValue()).getVcsName();
        will(returnValue(vcsName));

        oneOf(entry.getValue()).getProperty(propertyName);
        will(returnValue(entry.getKey()));
      }});
    });
    checkSuggestions(mySuggestion.getSuggestions(myProject), expectedUrls);
  }


  @SuppressWarnings("unchecked")
  private void checkSuggestions(@NotNull final List<ProjectSuggestedItem> items, @NotNull final List<String> expected) {
    if (!expected.isEmpty()) {
      assertEquals(1, items.size());
      final Map<String, Object> data = items.get(0).getAdditionalData();
      assertNotNull(data);
      final Map<String, Map<String, Object>> suggested = (Map<String, Map<String, Object>>) data.get("suggestedTrackers");
      assertNotNull(suggested);
      assertEquals(expected.size(), suggested.size());
      for (String key: expected) {
        Map<String, Object> val = suggested.get(key);
        assertNotNull(val);
        assertEquals(key, val.get("repoUrl"));
      }
    } else {
      assertEmpty(items);
    }
  }
}
