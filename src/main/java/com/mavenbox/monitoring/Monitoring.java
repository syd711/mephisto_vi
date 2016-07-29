package com.mavenbox.monitoring;

import callete.api.Callete;

/**
 * Central point for monitoring actions.
 */
public class Monitoring {

  public static void init() {
    int index = 1;
    while(true) {
      String nameKey = "monitoring." + index + ".name";
      String hostKey = "monitoring." + index + ".host";

      if(Callete.getConfiguration().containsKey(nameKey)) {
        String name = Callete.getConfiguration().getString(nameKey);
        String host = Callete.getConfiguration().getString(hostKey);

        new HostWatchDog(index, name, host).start();
      }
      else {
        break;
      }
      index++;
    }
  }

}
