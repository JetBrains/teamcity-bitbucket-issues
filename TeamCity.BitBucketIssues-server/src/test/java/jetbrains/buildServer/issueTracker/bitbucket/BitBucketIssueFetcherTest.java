/*
 * Copyright 2000-2020 JetBrains s.r.o.
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

import java.util.regex.Pattern;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.issueTracker.errors.NotFoundException;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import jetbrains.buildServer.util.cache.ResetCacheRegisterImpl;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    final EhCacheUtil helper = new EhCacheUtil(new ServerPaths(createTempDir().getAbsolutePath()),
            EventDispatcher.create(BuildServerListener.class),
            new ResetCacheRegisterImpl());

    myFetcher = new BitBucketIssueFetcher(helper, myParser, () -> null);
    myFetcher.setPattern(Pattern.compile("#(\\d+)"));
  }

  @Test
  public void testGetIssueAnonymously() throws Exception {
    myFetcher.getIssue(getUrl(owner, repo), "#2", null);
  }

  @Test(expectedExceptions = NotFoundException.class)
  public void testGetIssueAnonymously_IssueNotFound() throws Exception {
    myFetcher.getIssue(getUrl(owner, repo), "#100000", null);
  }

  @SuppressWarnings("SameParameterValue")
  private String getUrl(@NotNull final String owner, @NotNull final String repo) {
    return String.format("https://api.bitbucket.org/2.0/repositories/%s/%s/issues/", owner, repo);
  }
}
