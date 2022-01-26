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
