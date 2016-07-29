package com.mavenbox.model;

import callete.api.Callete;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Factory for creating job objects that are added via JobNodes to the main scroller.
 */
public class Workspaces {

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
        if(new File(project, "pom.xml").exists()) {
          Workspace workspace = new Workspace(project);
          workspaces.add(workspace);
        }
      }
    }
  }

  public static List<Workspace> getWorkspaces() {
    return workspaces;
  }
}
