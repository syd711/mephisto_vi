package com.mavenbox.ui.projects;

import callete.api.Callete;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Matthias on 07.09.2016.
 */
public class PullTest {

  private final static String LOGIN = Callete.getConfiguration().getString("git.login");
  private final static String PASSWORD = Callete.getConfiguration().getString("git.password");

  @Test
  public void testPull() throws IOException, GitAPIException {
    File dir = new File("G:\\dev\\workspace\\callete");
    Repository repository = new FileRepositoryBuilder().setGitDir(new File(dir, ".git")).build();
    Git git = new Git(repository);

    List<Ref> branches = git.branchList().call();
    for(Ref branch : branches) {
      System.out.println("Branch: " + branch.getName());
      if(branch.getName().endsWith("master")) {
        PullCommand pullCommand = git.pull();
//        pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(LOGIN, PASSWORD));
        pullCommand.setTransportConfigCallback(new SSHAuthenticationProvider());
        pullCommand.setRemote(Constants.DEFAULT_REMOTE_NAME);
        pullCommand.setRemoteBranchName(branch.getName());
        pullCommand.setRebase(true);
        try {
          PullResult result = pullCommand.call();
          if(result.isSuccessful()) {
            System.out.println("Pull successful");
            MergeResult mergeResult = result.getMergeResult();
          }
          return;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
