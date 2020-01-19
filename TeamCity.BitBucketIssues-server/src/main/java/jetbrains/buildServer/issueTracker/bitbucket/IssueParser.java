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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.CollectionsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class IssueParser {

  private static final Logger LOG = Logger.getInstance(IssueParser.class.getName());

  @SuppressWarnings("WeakerAccess")
  public IssueData parse(@NotNull final String issueAsString) {
    try {
      return doParse(new ObjectMapper().readValue(issueAsString, Map.class));
    } catch (Exception e) {
      LOG.error("Could not parse issue json from Bitbucket. Error message is: " + e.getMessage());
      if (LOG.isDebugEnabled()) {
        LOG.debug(
                "Could not parse issue json from Bitbucket. Response (cut to first 100 symbols): ["
                + issueAsString.substring(Math.min(100, issueAsString.length() - 1))
                + "]");
      }
      throw new RuntimeException(e);
    }
  }

  private IssueData doParse(@NotNull final Map map) {
    final String state = String.valueOf(map.get("state"));
    final String type = String.valueOf(map.get("kind"));
    return new IssueData(
            String.valueOf(map.get("id")),
            CollectionsUtil.asMap(
                    IssueData.SUMMARY_FIELD, String.valueOf(map.get("title")),
                    IssueData.STATE_FIELD, state,
                    IssueData.TYPE_FIELD, type,
                    IssueData.PRIORITY_FIELD, String.valueOf(map.get("priority"))
            ),
            "resolved".equals(state),
            "task".equals(type),
            "some url"  // todo: url
    );
  }
}
