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

import jetbrains.buildServer.issueTracker.IssueData;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.charset.Charset;

import static org.testng.Assert.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class IssueParserTest {

  private IssueParser myParser;

  @BeforeMethod
  public void setUp() {
    myParser = new IssueParser();
  }

  @Test
  public void testParseValid_Bug() throws Exception {
    final IssueData data = myParser.parse(readTestData("bug.json"));
    assertNotNull(data);
    assertEquals(data.getId(), "4");
    assertEquals(data.getType(), "bug");
    assertFalse(data.isResolved());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void testParseInvalid() throws Exception {
    myParser.parse(readTestData("invalid.json"));
  }

  @Test
  public void testParseEnhancement() throws Exception {
    final IssueData data = myParser.parse(readTestData("enhancement.json"));
    assertNotNull(data);
    assertEquals(data.getId(), "2");
    assertEquals(data.getType(), "enhancement");
    assertTrue(data.isResolved());
  }

  private String readTestData(@NotNull final String fileName) throws Exception {
    return FileUtil.readResourceAsString(IssueParserTest.class, "/" + fileName, Charset.forName("UTF-8"));
  }

}
