package com.ddlab.tornado.github;

import com.ddlab.gitpusher.core.IGitHandler;
import com.ddlab.gitpusher.core.UserAccount;
import com.ddlab.gitpusher.github.GitHubHandlerImpl;

public class GitHubUtil {

  public static String[] getAllRepositories(String userName, String password) throws Exception {
    UserAccount userAccount = new UserAccount(userName, password);
    IGitHandler gitHandler = new GitHubHandlerImpl(userAccount);
    String[] allRepos = gitHandler.getAllRepositories();
    System.out.println("1111111111------Coming here -------");
    for (String repoName : allRepos) {
      System.out.println("Repo Name : " + repoName);
    }
    System.out.println("2222222222------Coming here -------");
    return allRepos;
  }
}
