package com.mavenbox.ui.projects;

import callete.api.Callete;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

/**
 * For GitHub SSH authentication
 */
public class SSHAuthenticationProvider implements TransportConfigCallback {
  private final static String PASSWORD = Callete.getConfiguration().getString("git.password");
  private final static String PRIVATE_KEY = Callete.getConfiguration().getString("git.privateKey");

  private SshSessionFactory sshSessionFactory;

  public SSHAuthenticationProvider() {
    sshSessionFactory = new JschConfigSessionFactory() {


      @Override
      protected void configure(OpenSshConfig.Host host, Session session) {
        CredentialsProvider provider = new CredentialsProvider() {
          @Override
          public boolean isInteractive() {
            return false;
          }

          @Override
          public boolean supports(CredentialItem... items) {
            return true;
          }

          @Override
          public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
            for (CredentialItem item : items) {
              ((CredentialItem.StringType) item).setValue(PASSWORD);
            }
            return true;
          }
        };
        UserInfo userInfo = new CredentialsProviderUserInfo(session, provider);
        session.setUserInfo(userInfo);
      }

      @Override
      protected JSch createDefaultJSch(FS fs) throws JSchException {
        JSch defaultJSch = super.createDefaultJSch(fs);
        defaultJSch.addIdentity(PRIVATE_KEY);
        return defaultJSch;
      }
    };
  }

  @Override
  public void configure(Transport transport) {
    SshTransport sshTransport = (SshTransport) transport;
    sshTransport.setSshSessionFactory(sshSessionFactory);
  }


}
