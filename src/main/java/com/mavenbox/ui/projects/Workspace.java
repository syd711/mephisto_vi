package com.mavenbox.ui.projects;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This model represents an entry in the scroller
 */
public class Workspace {
  private final static Logger LOG = LoggerFactory.getLogger(Workspace.class);

  private String name;
  private Repository repository;
  private Git git;
  private File dir;

  public Workspace(File dir) throws IOException, GitAPIException {
    this.dir = dir;
    this.name = dir.getName();

    // Open an existing repository
    repository = new FileRepositoryBuilder().setGitDir(new File(dir, ".git")).build();
    git = new Git(repository);
  }

  public List<Branch> getBranches() {
    List<Branch> branchList = new ArrayList<>();
    try {
      List<Ref> branches = git.branchList().call();

      for(Ref branch : branches) {
        branchList.add(new Branch(this, branch));
      }
    } catch (GitAPIException e) {
      LOG.error("Failed to read branch list: " + e.getMessage(), e);
    }
    return branchList;
  }

  public String getName() {
    if(isDirty()) {
      return name + "*";
    }
    return name;
  }

  public boolean isDirty() {
    try {
      Status status = git.status().call();
      return !status.getUncommittedChanges().isEmpty();
    } catch (GitAPIException e) {
      LOG.error("Failed to determine status: " + e.getMessage(), e);
    }
    return false;
  }

  /**
   * Executes the workspace build depending on the current settings.
   * @param branch the branch to build
   */
  public void build(Branch branch, boolean pull, boolean make, boolean push) {
    new Thread(new ProjectBuilder(git, dir, branch, pull, make, push, isDirty())).start();
  }

  public boolean isActive(Branch branch) {
    try {
      String name = branch.getName();
      String activeName = git.getRepository().getBranch();
      return name.equals(activeName);
    } catch (IOException e) {
      LOG.error("Failed to determine active branch name: " + e.getMessage(), e);
    }
    return false;
  }
}
