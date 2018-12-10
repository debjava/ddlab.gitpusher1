package com.ddlab.gitpusher.github.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitHubConstants {
  // Product specific constants
  public static final String HOME_DIR = System.getProperty("user.home");
  public static final String TEMP_GIT_PATH = "DDLAB";
  public static final String HOME_GIT_PATH = getTempGitLocation();
  public static final String DATE_PATTERN = "dd-MMM-yyyy hh:mm a";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
  public static final String GENERIC_COMIT_MSG = "Code committed and pushed by {0} "+"on "+getTodayDateTime()
                    + " "+ "using DDLAB Gitpusher tool";

  // GITHUB Details
  public static final String GIT_API_URI = "https://api.github.com";
  public static final String REPO_API = "/user/repos"; // To get all repos, also used for creating a repo
  public static final String USER_API = "/user"; // To get user details
  public static final String GIST_API = "/users/{0}/gists";
  public static final String SEARCH_REPO_API =
      "/repos/{0}/{1}"; // To search the repo /repos/<loginUser>/<repoName>
  public static String GITHUB_REPO_CLONE_URI =
      "https://github.com/{0}/{1}.git"; // "https://github.com/debjava/Hello-World.git"
  
  public static final String GITHUB_GET_GIST_API = GIT_API_URI+GIST_API;

  private static String getTempGitLocation() {
    File tempGitDir = new File(HOME_DIR + File.separator + TEMP_GIT_PATH);
    if (!tempGitDir.exists()) tempGitDir.mkdirs();
    return tempGitDir.getAbsolutePath();
  }

  public static final String getTodayDateTime() {
    return DATE_FORMAT.format(new Date());
  }
}
