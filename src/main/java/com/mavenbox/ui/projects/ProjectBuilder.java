package com.mavenbox.ui.projects;

import callete.api.Callete;
import callete.api.util.SystemCommandExecutor;
import com.mavenbox.serial.ArduinoCommandFactory;
import com.mavenbox.ui.UIControl;
import com.mavenbox.ui.notifications.Notification;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Executor for building a maven workspace.
 */
public class ProjectBuilder extends Task<Void> {
  private final static Logger LOG = LoggerFactory.getLogger(ProjectBuilder.class);

  private final static String LOGIN = Callete.getConfiguration().getString("git.login");
  private final static String PASSWORD = Callete.getConfiguration().getString("git.password");

  private File dir;
  private boolean pull;
  private boolean make;
  private boolean push;
  private boolean dirty;
  private Branch branch;
  private Git git;

  public ProjectBuilder(Git git, File dir, Branch branch, boolean pull, boolean make, boolean push, boolean dirty) {
    this.git = git;
    this.dir = dir;
    this.branch = branch;
    this.pull = pull;
    this.make = make;
    this.push = push;
    this.dirty = dirty;
  }

  @Override
  public Void call() throws Exception {
    LOG.info("Triggering build of " + branch.getName());

    String blinkCommand = ArduinoCommandFactory.createBlinkCommand(true);
    UIControl.getInstance().getArduinoClient().sendCommand(blinkCommand);


    try {
      if(pull && dirty) {
        stash();
      }

      boolean checkedOut = checkout();

      //-------- PULL -------------------
      if(pull) {
        if(!pull(branch)) {
          return null;
        }
      }

      //-------- MAKE -------------------
      if(make) {
        if(!make()) {
          return null;
        }
      }

      //-------- PUSH -------------------
      if(!checkedOut && push) {
        if(!push()) {
          return null;
        }
      }

      if(pull && dirty) {
        unstash();
      }

      LOG.info("Build of " + branch.getName() + " finished.");
      return null;
    }
    finally {
      String cmd = ArduinoCommandFactory.createBlinkCommand(false);
      UIControl.getInstance().getArduinoClient().sendCommand(cmd);
    }
  }

  private boolean checkout() {
    String selectedBranch = branch.getName();

    try {
      String currentBranch = git.getRepository().getBranch();
      if(!selectedBranch.equals(currentBranch)) {
        CheckoutCommand checkout = git.checkout();
        checkout.setName(selectedBranch);
        checkout.call();
        UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Checkout of '" + selectedBranch + "' successful", true, 600));
        return true;
      }
      return false;
    } catch (Exception e) {
      LOG.error("Error during checkout of branch: " + e.getMessage(), e);
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Checkout of '" + selectedBranch + "' failed", false, 600));
    }
    return false;
  }

  private boolean make() {
    try {
      String[] cmd = Callete.getConfiguration().getStringArray("workspace.mvn.cmd");
      SystemCommandExecutor executor = new SystemCommandExecutor(Arrays.asList(cmd));
      executor.setDir(dir);
      executor.enableLogging(true);
      executor.executeCommand();

      String result = executor.getStandardOutputFromCommand().toString();
      boolean successful = result.contains("BUILD SUCCESS");

      if(successful) {
        UIControl.getInstance().getNotificationService().showNotification(new Notification("Maven", "Maven Build successful", true, 400));
      }
      return true;
    } catch (Exception e) {
      LOG.error("Failed to execute pull: " + e.getMessage());
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Maven", "Maven Build failed", false, 400));
    }
    return false;
  }


  private boolean stash() {
    try {
      StashCreateCommand stashCreateCommand = git.stashCreate();
      stashCreateCommand.call();
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Stash created", true, 400));
    } catch (GitAPIException e) {
      LOG.error("Error creating stash: " + e.getMessage(), e);
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Stash failed", false, 400));
      return false;
    }
    return true;
  }


  private boolean unstash() {
    try {
      StashApplyCommand stashApplyCommand = git.stashApply();
      stashApplyCommand.call();
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Stash applied", true, 400));
    } catch (GitAPIException e) {
      LOG.error("Error applying stash: " + e.getMessage(), e);
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Failed to apply Git stash failed", false, 500));
      return false;
    }
    return true;
  }

  private boolean pull(Branch branch) {
    PullCommand pullCommand = git.pull();
    pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(LOGIN, PASSWORD));
    pullCommand.setRemote(Constants.DEFAULT_REMOTE_NAME);
    pullCommand.setRemoteBranchName(branch.getName());
    pullCommand.setRebase(true);
    try {
      PullResult result = pullCommand.call();
      LOG.info("Git pull fetched from " + result.getFetchedFrom());
      if(result.isSuccessful()) {
        UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Pull successful", true, 400));
      }
    } catch (Exception e) {
      LOG.error("Failed to execute pull: " + e.getMessage(), e);
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Pull failed", false, 400));
      return false;
    }
    return true;
  }


  private boolean push() {
    try {
      PushCommand pushCommand = git.push();
      pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(LOGIN, PASSWORD));
      pushCommand.call();
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Push successful", true, 400));
      return true;
    } catch (GitAPIException e) {
      LOG.error("Push command failed: " + e.getMessage(), e);
      UIControl.getInstance().getNotificationService().showNotification(new Notification("Git", "Git Push failed", false, 400));
    }
    return false;
  }
}
