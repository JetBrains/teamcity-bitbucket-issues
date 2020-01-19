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

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public interface BitBucketConstants {

  String PARAM_NAME = "name";
  String PARAM_REPOSITORY = "repository";
  String PARAM_AUTH_TYPE = "authType";
  String PARAM_USERNAME = "username";
  String PARAM_PASSWORD = "secure:password";
  String PARAM_PATTERN = "pattern";

  String AUTH_ANONYMOUS = "anonymous";
  String AUTH_LOGIN_PASSWORD = "loginpassword";

}
