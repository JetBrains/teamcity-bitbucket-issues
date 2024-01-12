

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