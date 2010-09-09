/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.weld.examples.login.test.selenium;

import static org.jboss.arquillian.api.RunModeType.AS_CLIENT;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.selenium.annotation.Selenium;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.examples.login.Credentials;
import org.jboss.weld.examples.login.LoggedIn;
import org.jboss.weld.examples.login.Login;
import org.jboss.weld.examples.login.User;
import org.jboss.weld.examples.login.Users;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Tests login examples in Weld
 * 
 * @author kpiwko
 * @author plenyi
 */
@Run(AS_CLIENT)
public class CommonLoginTest extends Arquillian
{
   protected String MAIN_PAGE = "/home.jsf";
   protected String LOGGED_IN = "xpath=//li[contains(text(),'Welcome')]";
   protected String LOGGED_OUT = "xpath=//li[contains(text(),'Goodbye')]";

   protected String USERNAME_FIELD = "id=loginForm:username";
   protected String PASSWORD_FIELD = "id=loginForm:password";

   protected String LOGIN_BUTTON = "id=loginForm:login";
   protected String LOGOUT_BUTTON = "id=loginForm:logout";

   @Selenium
   private DefaultSelenium selenium;

   @Deployment
   public static WebArchive createDeployment()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "weld-login.war")
      .addClasses(Credentials.class, LoggedIn.class, Login.class, User.class, Users.class)
      .addLibrary(MavenArtifactResolver.resolve("org.glassfish.web:el-impl:2.2"))
      .addWebResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
      .addWebResource(new File("src/main/webapp/WEB-INF/faces-config.xml"), "faces-config.xml")
      .addWebResource(new File("src/main/resources/import.sql"), ArchivePaths.create("classes/import.sql"))
      .addResource(new File("src/main/webapp/index.html"), ArchivePaths.create("index.html"))
      .addResource(new File("src/main/webapp/home.xhtml"), ArchivePaths.create("home.xhtml"))
      .addResource(new File("src/main/webapp/template.xhtml"), ArchivePaths.create("template.xhtml"))
      .addResource(new File("src/main/webapp/users.xhtml"), ArchivePaths.create("users.xhtml"))      
      .addManifestResource(new File("src/main/resources/META-INF/persistence.xml"))
      .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
      
      System.out.println(war.toString(true));

      return war;
   }

   @Test
   public void loginTest()
   {
      selenium.open("http://localhost:8080/weld-login/home.jsf");
      
      assertFalse(selenium.isElementPresent(LOGGED_IN), "User should not be logged in!");
      selenium.type(USERNAME_FIELD, "demo");
      selenium.type(PASSWORD_FIELD, "demo");
      selenium.click(LOGIN_BUTTON);
      selenium.waitForPageToLoad("60000");
      assertTrue(selenium.isElementPresent(LOGGED_IN), "User should be logged in!");
   }

   @Test(dependsOnMethods = { "loginTest" })
   public void logoutTest()
   {
      selenium.click(LOGOUT_BUTTON);
      selenium.waitForPageToLoad("60000");
      assertTrue(selenium.isElementPresent(LOGGED_OUT), "User should not be logged in!");
   }
}
