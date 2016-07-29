package com.mavenbox.model;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This model represents an entry in the scroller
 */
public class Workspace {

  private String name;
  private Repository repository;
  private Git git;

  public Workspace(File dir) throws IOException, GitAPIException {
    this.name = dir.getName();

    // Open an existing repository
    repository = new FileRepositoryBuilder().setGitDir(new File(dir, ".git")).build();
    git = new Git(repository);

    List<Ref> call = git.branchList().call();
    for(Ref ref : call) {
      System.out.println(ref.getName());
    }
  }

  public String getName() {
    if(isDirty()) {
      return name + "*";
    }
    return name;
  }

  public boolean isDirty() {
    return repository.getRepositoryState().canCommit();
  }
}
