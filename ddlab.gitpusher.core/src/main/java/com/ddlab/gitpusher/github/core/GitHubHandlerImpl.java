package com.ddlab.gitpusher.github.core;

import com.ddlab.gitpusher.core.*;
import com.ddlab.gitpusher.exception.GenericGitPushException;
import com.ddlab.gitpusher.github.bean.GitHubRepo;
import com.ddlab.gitpusher.util.HTTPUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import static com.ddlab.gitpusher.github.core.GitHubConstants.*;

public class GitHubHandlerImpl implements IGitHandler {
  private UserAccount userAccount;

  public GitHubHandlerImpl(UserAccount userAccount) {
    this.userAccount = userAccount;
  }

  @Override
  public String getUserName() throws GenericGitPushException {
    GitHubRepo gitRepo = null;
    String uri = GIT_API_URI + USER_API;
    HttpGet httpGet = new HttpGet(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpGet.setHeader("Authorization", "Basic " + encodedUser);
    try {
      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
      gitRepo = getGitHubUser(gitResponse);
    } catch (GenericGitPushException e) {
      throw e;
    }
    return gitRepo.getLoginUser();
  }

  @Override
  public GitHubRepo getAllRepositories() throws Exception {
    GitHubRepo gitRepo = null;
    String uri = GIT_API_URI + REPO_API;
    HttpGet httpGet = new HttpGet(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpGet.setHeader("Authorization", "Basic " + encodedUser);
    try {
      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
      gitRepo = getAllGitHubRepos(gitResponse);
    } catch (GenericGitPushException e) {
      throw e;
    }
    return gitRepo;
  }

  @Override
  public boolean repoExists(String repoName) throws GenericGitPushException {
    boolean existsFlag = false;
    String uri = GIT_API_URI + SEARCH_REPO_API;
    MessageFormat formatter = new MessageFormat(uri);
    String loginUser = getUserName();
    uri = formatter.format(new String[] {loginUser, repoName});
    HttpGet httpGet = new HttpGet(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpGet.setHeader("Authorization", "Basic " + encodedUser);
    try {
      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
      existsFlag = gitResponse.getStatusCode().equals("200") ? true : false;
    } catch (GenericGitPushException e) {
      throw e;
    }
    return existsFlag;
  }

  @Override
  public void clone(String repoName, File dirPath) throws GenericGitPushException {
    String uri = GITHUB_REPO_CLONE_URI;
    String loginUser = getUserName();
    MessageFormat formatter = new MessageFormat(uri);
    uri = formatter.format(new String[] {loginUser, repoName});
    System.out.println("Git clone URI : " + uri);
    CredentialsProvider crProvider =
        new UsernamePasswordCredentialsProvider(
            userAccount.getUserName(), userAccount.getPassword());
    Git git = null;
    try {
      git =
          Git.cloneRepository()
              .setCredentialsProvider(crProvider)
              .setURI(uri)
              .setDirectory(dirPath)
              .call();
    } catch (GitAPIException e) {
      throw new GenericGitPushException("Unable to clone the project from GitHub...");
    } finally {
      if (git != null) git.close();
    }
  }

  private GitHubRepo getAllGitHubRepos(GitResponse gitResponse) throws GenericGitPushException {
    GitHubRepo gitRepo = null;
    if (gitResponse.getStatusCode().equals("200")) {
      IResponseParser<String, GitHubRepo> responseParser = new GitHubResponseParserImpl();
      gitRepo = responseParser.getAllRepos(gitResponse.getResponseText());
    } else if (gitResponse.getStatusCode().equals("401")) {
      throw new GenericGitPushException("Bad credentials, check the credentials ...");
    }
    return gitRepo;
  }

  private GitHubRepo getGitHubUser(GitResponse gitResponse) throws GenericGitPushException {
    GitHubRepo gitRepo = null;
    if (gitResponse.getStatusCode().equals("200")) {
      IResponseParser<String, GitHubRepo> responseParser = new GitHubResponseParserImpl();
      gitRepo = responseParser.getUser(gitResponse.getResponseText());
    } else if (gitResponse.getStatusCode().equals("401")) {
      throw new GenericGitPushException("Bad credentials, check the credentials ...");
    }
    return gitRepo;
  }

  @Override
  public String getUrlFromLocalRepsitory(File gitDirPath) throws GenericGitPushException {
    String existingRepoUrl = null;
    Git git = null;
    Repository repository = null;
    try {
      git = Git.open(gitDirPath);
      repository = git.getRepository();
      existingRepoUrl = repository.getConfig().getString("remote", "origin", "url");
    } catch (IOException e) {
      throw new GenericGitPushException(
          "Unable to read the local .git directory to find out the Repo URL");
    }
    return existingRepoUrl;
  }

  @Override
  public boolean isGitDirAvailable(File gitDirPath) {
    boolean isAvailable = false;
    Git git = null;
    try {
      git = Git.open(gitDirPath);
      isAvailable = true;
    } catch (IOException e) {
    } finally {
      if (git != null) git.close();
    }
    return isAvailable;
  }

  @Override
  public void update(File cloneDirPath, String message) throws GenericGitPushException {
    Git git = null;
    RevCommit revisionCommit = null;
    String comitMsg = GitHubConstants.GENERIC_COMIT_MSG;
    MessageFormat formatter = new MessageFormat(comitMsg);
    String userName = getUserName();
    userName = (userName == null) ? userAccount.getUserName() : userName;
    PersonIdent authorDetails = new PersonIdent(userName, userAccount.getUserName());
    message =
        (message == null) ? formatter.format(new String[] {userAccount.getUserName()}) : message;
    try {
      git = Git.open(cloneDirPath);
      git.add().addFilepattern(".").call();
      CommitCommand commitCommand =
          git.commit()
              .setAuthor(authorDetails)
              .setCommitter(authorDetails)
              .setAll(true)
              .setMessage(message);
      revisionCommit = commitCommand.call();

      System.out.println("All the files have been added and committed successfully");
      System.out.println("Going to push the files.....");

      CredentialsProvider cp =
          new UsernamePasswordCredentialsProvider(
              userAccount.getUserName(), userAccount.getPassword());
      PushCommand pushCommand = git.push();
      pushCommand.setCredentialsProvider(cp).setForce(true).setPushAll();

      Set<RemoteRefUpdate.Status> statusSet = new HashSet<RemoteRefUpdate.Status>();
      Iterable<PushResult> resultIterable = pushCommand.call();
      PushResult pushResult = resultIterable.iterator().next();
      for (final RemoteRefUpdate rru : pushResult.getRemoteUpdates()) {
        RemoteRefUpdate.Status status = rru.getStatus();
        if (status != RemoteRefUpdate.Status.OK && status != RemoteRefUpdate.Status.UP_TO_DATE) {
          // Do something
          System.out.println("Something is wrong ....");
        }
        statusSet.add(status);
      }
      if (statusSet.contains(RemoteRefUpdate.Status.OK)) {
        System.out.println("All files pushed to GitHub successfully");
      }
    } catch (IOException | GitAPIException e) {
      revertChanges(git, revisionCommit);
      throw new GenericGitPushException("" + e.getMessage());
    } finally {
      if (git != null) git.close();
    }
  }

  private void revertChanges(Git git, RevCommit revisionCommit) {
    System.out.println("Going to revert the changes");
    try {
      git.revert().call();
      //      git.revert().include(revisionCommit).call();
      //      git.reset().setMode(ResetCommand.ResetType.HARD).call();

    } catch (GitAPIException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createHostedRepo(String repoName) throws Exception {
    String jsonRepo = new HostedRepo(repoName).toJson();
    String uri = GIT_API_URI + REPO_API;
    HttpPost httpPost = new HttpPost(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpPost.setHeader("Authorization", "Basic " + encodedUser);
    StringEntity jsonBodyRequest = new StringEntity(jsonRepo);
    httpPost.setEntity(jsonBodyRequest);
    try {
      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpPost);
      IErrorResponseParser<String, String> errorParser = new RepoCreateErrorResponseParser();
      IResponseParser<String, GitHubRepo> responseParser = new GitHubResponseParserImpl();
      GitHubRepo gitRepo =
          responseParser.getNewlyCreatedHostedRepo(gitResponse.getResponseText(), errorParser);
    } catch (GenericGitPushException e) {
      throw e;
    }
  }

  @Override
  public String[] getGists() throws Exception {
    String[] gists = null;
    String uri = GITHUB_GET_GIST_API;
    MessageFormat formatter = new MessageFormat(uri);
    String loginUser = getUserName();
    uri = formatter.format(new String[] {loginUser});
    System.out.println("Now Gist URI : "+uri);
    HttpGet httpGet = new HttpGet(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpGet.setHeader("Authorization", "Basic " + encodedUser);

    try {
      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
      IGistResponseParser<String, String[]> gistParser = new GitHubGistParserImpl();
      gists = gistParser.parse(gitResponse.getResponseText());
    } catch (GenericGitPushException e) {
      throw e;
    }
    return gists;
  }

  @Override
  public void createGist(String fileName, String fileContents, String description)
      throws Exception {
    String uri = GITHUB_GET_GIST_API;
    MessageFormat formatter = new MessageFormat(uri);
    String loginUser = getUserName();
    uri = formatter.format(new String[] {loginUser});
    System.out.println("Now Gist URI : "+uri);
    HttpGet httpGet = new HttpGet(uri);
    String encodedUser =
        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
    httpGet.setHeader("Authorization", "Basic " + encodedUser);
    //    try {
    //      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
    //
    //      existsFlag = gitResponse.getStatusCode().equals("200") ? true : false;
    //    } catch (GenericGitPushException e) {
    //      throw e;
    //    }
  }

  private static class HostedRepo {
    @JsonProperty("name")
    private String name;

    public HostedRepo(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String toJson() {
      ObjectMapper mapper = new ObjectMapper();
      String toJson = null;
      try {
        toJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return toJson;
    }
  }

  //  @Override
  //  public String[] getAllRepositories() throws GenericGitPushException {
  //    String[] availableRespos = null;
  //    String uri = GIT_API_URI + REPO_API;
  //    HttpGet httpGet = new HttpGet(uri);
  //    String encodedUser =
  //        HTTPUtil.getEncodedUser(userAccount.getUserName(), userAccount.getPassword());
  //    httpGet.setHeader("Authorization", "Basic " + encodedUser);
  //
  //    try {
  //      GitResponse gitResponse = HTTPUtil.getHttpGetOrPostResponse(httpGet);
  //      availableRespos = getAllGitHubRepos(gitResponse);
  //    } catch (GenericGitPushException e) {
  //      throw e;
  //    }
  //    return availableRespos;
  //  }

  //  private String[] getAllGitHubRepos(GitResponse gitResponse) throws GenericGitPushException {
  //    String[] availableRespos = null;
  //    if (gitResponse.getStatusCode().equals("200")) {
  //      IResponseProcessor responseProcessor = new GitHubResponseProcessor();
  //      try {
  //        availableRespos = responseProcessor.processForAllRepos(gitResponse.getResponseText());
  //      } catch (Exception e) {
  //        throw new GenericGitPushException("Unable to parse the response coming from GitHub...");
  //      }
  //    } else if (gitResponse.getStatusCode().equals("401")) {
  //      throw new GenericGitPushException("Bad credentials, check the credentials ...");
  //    }
  //    return availableRespos;
  //  }
}
