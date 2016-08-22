package com.mavenbox.ui.projects;

import callete.api.Callete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Factory for creating job objects that are added via JobNodes to the main scroller.
 */
public class Workspaces {
  private final static Logger LOG = LoggerFactory.getLogger(Workspaces.class);

  private static List<Workspace> workspaces = new ArrayList<>();

  public static void init() {
    Iterator<String> it = Callete.getConfiguration().getKeys("workspace");
    while(it.hasNext()) {
      String folder = Callete.getConfiguration().getString(it.next());
      File dir = new File(folder);

      File[] projects = dir.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          return pathname.isDirectory();
        }
      });

      for(File project : projects) {
        if(isValidWorkspace(project)) {
          try {
            Workspace workspace = new Workspace(project);
            workspaces.add(workspace);
          } catch (Exception e) {
            LOG.error("Failed to load git repository for " + project.getAbsolutePath() + ": " + e.getMessage(), e);
          }
        }
      }
    }
  }

  private static boolean isValidWorkspace(File folder) {
    boolean isMaven = new File(folder, "pom.xml").exists();
    boolean isVersioned = new File(folder, ".git").exists();
    return isMaven && isVersioned;
  }

  public static List<Workspace> getWorkspaces() {
    return workspaces;
  }
}
