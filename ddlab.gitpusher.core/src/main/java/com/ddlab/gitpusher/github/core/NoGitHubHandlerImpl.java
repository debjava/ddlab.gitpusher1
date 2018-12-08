package com.ddlab.gitpusher.github.core;

import com.ddlab.gitpusher.core.IGitHandler;
import com.ddlab.gitpusher.core.NoGitHandler;
import com.ddlab.gitpusher.exception.GenericGitPushException;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class NoGitHubHandlerImpl implements NoGitHandler {

  @Override
  public void handle(File projectDir, IGitHandler gitHandler) throws Exception {
    String homeGitPath = GitHubConstants.HOME_GIT_PATH;
    String repoName = projectDir.getName();
    File homeGitDirPath = new File(homeGitPath);
    boolean repoExistsFlag = gitHandler.repoExists(repoName);
    if (repoExistsFlag) {
      System.out.println(
          "A repository with the same project name already exists in Github, create different name");
      throw new GenericGitPushException(
          "A repository with the same project name already exists in Github."
              + "You can refactor the existing project with a different name.");
    } else {
      createCloneCopy(repoName, projectDir, gitHandler);
      // update the project
      gitHandler.update(projectDir, null);
    }
  }

  private void createCloneCopy(String repoName, File projectDir, IGitHandler gitHandler)
      throws Exception {
    // Create a Hosted Repo
    File tempCloneDir = null;
    try {
      gitHandler.createHostedRepo(repoName);
      tempCloneDir = new File(GitHubConstants.HOME_GIT_PATH);
      // clone project
      gitHandler.clone(repoName, tempCloneDir);
      // copy to project directory
      FileUtils.copyDirectory(tempCloneDir, projectDir);
    } catch (Exception e) {
      throw e;
    } finally {
      String dotGitDir = tempCloneDir.getAbsolutePath() + File.separator + ".git";
      FileUtils.deleteQuietly(new File(dotGitDir));
    }
  }
}
