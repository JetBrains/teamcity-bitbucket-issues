package jetbrains.buildServer.issueTracker.bitbucket;

import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class IssueParserTest {

  private IssueParser myParser;

  @BeforeMethod
  public void setUp() throws Exception {
    myParser = new IssueParser();
  }

  @Test
  public void testParseValid_Bug() throws Exception {
    final IssueData data = myParser.parse(readTestData("bug.json"));
    assertNotNull(data);
    assertEquals("1", data.getId());
    assertEquals("bug", data.getType());
    assertFalse(data.isResolved());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void testParseInvalid() throws Exception {
    myParser.parse(readTestData("invalid.json"));
  }

  private String readTestData(@NotNull final String fileName) throws Exception {
    return FileUtil.readResourceAsString(IssueParserTest.class, "/" + fileName, Charset.forName("UTF-8"));
  }

}
