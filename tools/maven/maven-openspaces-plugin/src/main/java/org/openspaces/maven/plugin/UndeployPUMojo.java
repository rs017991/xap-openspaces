/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openspaces.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Goal that undeploys a processing unit.
 *
 * @goal undeploy
 * @requiresProject false
 */
public class UndeployPUMojo extends AbstractMojo {

    /**
     * groups
     *
     * @parameter expression="${groups}"
     */
    private String groups;


    /**
     * locators
     *
     * @parameter expression="${locators}"
     */
    private String locators;


    /**
     * timeout
     *
     * @parameter expression="${timeout}" default-value="10000"
     */
    private String timeout;


    /**
     * puName
     *
     * @parameter expression="${puName}"
     */
    private String puName;


    /**
     * Project instance, used to add new source directory to the build.
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;


    /**
     * executes the mojo.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        Utils.handleSecurity();

        List projects = Utils.resolveProjects(project, puName);

        // sort the projects by the order parameter
        Collections.sort(projects, new PUProjectSorter(false));

        for (Iterator projIt = projects.iterator(); projIt.hasNext();) {
            MavenProject proj = (MavenProject) projIt.next();
            getLog().info("Undeploying processing unit: " + proj.getBuild().getFinalName());
            String[] attributesArray = createAttributesArray(proj.getBuild().getFinalName());
            try {
                Class deployClass = Class.forName("org.openspaces.pu.container.servicegrid.deploy.Undeploy", true, Thread.currentThread().getContextClassLoader());
                deployClass.getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{attributesArray});
            } catch (InvocationTargetException e) {
                throw new MojoExecutionException(e.getTargetException().getMessage(), e.getTargetException());
            } catch (Exception e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }


    /**
     * Creates the attributes array
     *
     * @return attributes array
     */
    private String[] createAttributesArray(String name) {
        ArrayList Attlist = new ArrayList();
        Utils.addAttributeToList(Attlist, "-groups", groups);
        Utils.addAttributeToList(Attlist, "-locators", locators);
        Utils.addAttributeToList(Attlist, "-timeout", timeout);
        Attlist.add(name);
        getLog().info("Arguments list: " + Attlist);
        String[] attArray = new String[Attlist.size()];
        Attlist.toArray(attArray);
        return attArray;
    }
}