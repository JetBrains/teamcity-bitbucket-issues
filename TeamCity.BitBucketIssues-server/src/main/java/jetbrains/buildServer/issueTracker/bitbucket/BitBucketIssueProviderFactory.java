package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.issueTracker.AbstractIssueProviderFactory;
import jetbrains.buildServer.issueTracker.IssueFetcher;
import jetbrains.buildServer.issueTracker.IssueProvider;
import jetbrains.buildServer.issueTracker.IssueProviderType;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class BitBucketIssueProviderFactory extends AbstractIssueProviderFactory {

  public BitBucketIssueProviderFactory(@NotNull final IssueProviderType type,
                                       @NotNull final IssueFetcher fetcher) {
    super(type, fetcher);
  }

  @NotNull
  @Override
  public IssueProvider createProvider() {
    return new BitBucketIssueProvider(myType.getType(), myFetcher);
  }
}
