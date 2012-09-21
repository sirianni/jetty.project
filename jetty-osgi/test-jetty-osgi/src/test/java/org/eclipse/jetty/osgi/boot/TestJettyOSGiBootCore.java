//
//  ========================================================================
//  Copyright (c) 1995-2012 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.osgi.boot;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.osgi.boot.internal.serverfactory.DefaultJettyAtJettyHomeHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * Pax-Exam to make sure the jetty-osgi-boot can be started along with the httpservice web-bundle.
 * Then make sure we can deploy an OSGi service on the top of this.
 */
@RunWith( JUnit4TestRunner.class )
public class TestJettyOSGiBootCore
{
    /**
     * Jetty-osgi including webapp support and also jetty-client.
     * Sets the system property jetty.home.bunde=org.eclipse.jetty.osgi.boot
     * to use the jetty server configuration embedded in
     *
     * @return list of options
     */
    public static List<Option> provisionCoreJetty()
    {
    	List<Option> res = new ArrayList<Option>();
    	// get the jetty home config from the osgi boot bundle.
    	res.add(PaxRunnerOptions.vmOptions("-Djetty.port=9876 -D" + DefaultJettyAtJettyHomeHelper.SYS_PROP_JETTY_HOME_BUNDLE + "=org.eclipse.jetty.osgi.boot"));
        // res.add(CoreOptions.equinox());

//    	res.add(CoreOptions.bootDelegationPackages("javax.transaction", "javax.transaction.*"));
//    	res.add(PaxRunnerOptions.vmOption("-Dorg.osgi.framework.system.packages=javax.accessibility,javax.activation,javax.activity," +
//    			"javax.annotation,javax.annotation.processing,javax.crypto,javax.crypto.interfaces,javax.crypto.spec," +
//    			"javax.imageio,javax.imageio.event,javax.imageio.metadata,javax.imageio.plugins.bmp,javax.imageio.plugins.jpeg,javax.imageio.spi,javax.imageio.stream," +
//    			"javax.jws,javax.jws.soap,javax.lang.model,javax.lang.model.element,javax.lang.model.type,javax.lang.model.util," +
//    			"javax.management,javax.management.loading,javax.management.modelmbean,javax.management.monitor,javax.management.openmbean," +
//    			"javax.management.relation,javax.management.remote,javax.management.remote.rmi,javax.management.timer," +
//    			"javax.naming,javax.naming.directory,javax.naming.event,javax.naming.ldap,javax.naming.spi," +
//    			"javax.net,javax.net.ssl,javax.print,javax.print.attribute,javax.print.attribute.standard,javax.print.event," +
//    			"javax.rmi,javax.rmi.CORBA,javax.rmi.ssl,javax.script,javax.security.auth,javax.security.auth.callback," +
//    			"javax.security.auth.kerberos,javax.security.auth.login,javax.security.auth.spi,javax.security.auth.x500,javax.security.cert," +
//    			"javax.security.sasl,javax.sound.midi,javax.sound.midi.spi,javax.sound.sampled,javax.sound.sampled.spi,javax.sql," +
//    			"javax.sql.rowset,javax.sql.rowset.serial,javax.sql.rowset.spi,javax.swing,javax.swing.border,javax.swing.colorchooser," +
//    			"javax.swing.event,javax.swing.filechooser,javax.swing.plaf,javax.swing.plaf.basic,javax.swing.plaf.metal,javax.swing.plaf.multi," +
//    			"javax.swing.plaf.synth,javax.swing.table,javax.swing.text,javax.swing.text.html,javax.swing.text.html.parser,javax.swing.text.rtf," +
//    			"javax.swing.tree,javax.swing.undo,javax.tools,javax.xml,javax.xml.bind,javax.xml.bind.annotation,javax.xml.bind.annotation.adapters," +
//    			"javax.xml.bind.attachment,javax.xml.bind.helpers,javax.xml.bind.util,javax.xml.crypto,javax.xml.crypto.dom,javax.xml.crypto.dsig,javax.xml.crypto.dsig.dom," +
//    			"javax.xml.crypto.dsig.keyinfo,javax.xml.crypto.dsig.spec,javax.xml.datatype,javax.xml.namespace,javax.xml.parsers,javax.xml.soap," +
//    			"javax.xml.stream,javax.xml.stream.events,javax.xml.stream.util,javax.xml.transform,javax.xml.transform.dom,javax.xml.transform.sax," +
//    			"javax.xml.transform.stax,javax.xml.transform.stream,javax.xml.validation,javax.xml.ws,javax.xml.ws.handler,javax.xml.ws.handler.soap," +
//    			"javax.xml.ws.http,javax.xml.ws.soap,javax.xml.ws.spi,javax.xml.xpath,org.ietf.jgss,org.omg.CORBA,org.omg.CORBA.DynAnyPackage," +
//    			"org.omg.CORBA.ORBPackage,org.omg.CORBA.TypeCodePackage,org.omg.CORBA.portable,org.omg.CORBA_2_3,org.omg.CORBA_2_3.portable," +
//    			"org.omg.CosNaming,org.omg.CosNaming.NamingContextExtPackage,org.omg.CosNaming.NamingContextPackage,org.omg.Dynamic,org.omg.DynamicAny," +
//    			"org.omg.DynamicAny.DynAnyFactoryPackage,org.omg.DynamicAny.DynAnyPackage,org.omg.IOP,org.omg.IOP.CodecFactoryPackage,org.omg.IOP.CodecPackage," +
//    			"org.omg.Messaging,org.omg.PortableInterceptor,org.omg.PortableInterceptor.ORBInitInfoPackage,org.omg.PortableServer," +
//    			"org.omg.PortableServer.CurrentPackage,org.omg.PortableServer.POAManagerPackage,org.omg.PortableServer.POAPackage," +
//    			"org.omg.PortableServer.ServantLocatorPackage,org.omg.PortableServer.portable,org.omg.SendingContext,org.omg.stub.java.rmi," +
//    			"org.w3c.dom,org.w3c.dom.bootstrap,org.w3c.dom.css,org.w3c.dom.events,org.w3c.dom.html,org.w3c.dom.ls,org.w3c.dom.ranges," +
//    			"org.w3c.dom.stylesheets,org.w3c.dom.traversal,org.w3c.dom.views,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers," +
//    			"javax.transaction;partial=true;mandatory:=partial,javax.transaction.xa;partial=true;mandatory:=partial"));

    	res.addAll(coreJettyDependencies());
    	return res;
    }

    public static List<Option> coreJettyDependencies() {
    	List<Option> res = new ArrayList<Option>();
    	res.add(mavenBundle().groupId( "org.eclipse.jetty.orbit" ).artifactId( "javax.servlet" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.osgi" ).artifactId( "org.eclipse.osgi" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.osgi" ).artifactId( "org.eclipse.osgi.services" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-deploy" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-server" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-servlet" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-util" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-http" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-xml" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-webapp" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-io" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-continuation" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-security" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty.websocket" ).artifactId( "websocket-client" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty.websocket" ).artifactId( "websocket-core" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty.websocket" ).artifactId( "websocket-server" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-servlets" ).versionAsInProject().noStart());
    	res.add(mavenBundle().groupId( "org.eclipse.jetty" ).artifactId( "jetty-client" ).versionAsInProject().noStart());
    	return res;
    }

    @Inject
    BundleContext bundleContext = null;


    @Configuration
    public static Option[] configure()
    {
        ArrayList<Option> options = new ArrayList<Option>();
        options.addAll(provisionCoreJetty());
        options.addAll(Arrays.asList(options(
            // install log service using pax runners profile abstraction (there are more profiles, like DS)
            //logProfile(),
            // this is how you set the default log level when using pax logging (logProfile)
            CoreOptions.systemProperty( "org.ops4j.pax.logging.DefaultServiceLog.level" ).value( "INFO" ),

        //	CoreOptions.equinox(), CoreOptions.felix(),//.version("3.0.0"),

            mavenBundle().groupId( "org.eclipse.jetty.osgi" ).artifactId( "jetty-osgi-boot" ).versionAsInProject().start(),
            mavenBundle().groupId( "org.eclipse.jetty.osgi" ).artifactId( "jetty-httpservice" ).versionAsInProject().start(),

            mavenBundle().groupId( "org.eclipse.equinox.http" ).artifactId( "servlet" ).versionAsInProject().start()
        )));
        return options.toArray(new Option[options.size()]);
    }

    /**
     * You will get a list of bundles installed by default
     * plus your testcase, wrapped into a bundle called pax-exam-probe
     */
    @Test
    public void testHttpService() throws Exception
    {
//      ServletContextHandler sch = null;
//      sch.addServlet("className", "pathSpec").setInitOrder("0");


        Map<String,Bundle> bundlesIndexedBySymbolicName = new HashMap<String, Bundle>();
        for( Bundle b : bundleContext.getBundles() )
        {
            bundlesIndexedBySymbolicName.put(b.getSymbolicName(), b);
            //System.err.println("got " + b.getSymbolicName());
        }
        Bundle osgiBoot = bundlesIndexedBySymbolicName.get("org.eclipse.jetty.osgi.boot");
        Assert.assertNotNull("Could not find the org.eclipse.jetty.osgi.boot bundle", osgiBoot);
        Assert.assertTrue(osgiBoot.getState() == Bundle.ACTIVE);

        Bundle httpServiceOSGiBundle = bundlesIndexedBySymbolicName.get("org.eclipse.jetty.osgi.httpservice");
        Assert.assertNotNull(httpServiceOSGiBundle);
        Assert.assertTrue(httpServiceOSGiBundle.getState() == Bundle.ACTIVE);

        Bundle equinoxServlet = bundlesIndexedBySymbolicName.get("org.eclipse.equinox.http.servlet");
        Assert.assertNotNull(equinoxServlet);
        //interestingly with equinox the bundle is not started. probably a difference in pax-exam and
        //the way the bundles are activated. the rest of the test goes fine.
        Assert.assertTrue(equinoxServlet.getState() == Bundle.ACTIVE);

        //in the OSGi world this would be bad code and we should use a bundle tracker.
        //here we purposely want to make sure that the httpService is actually ready.
        ServiceReference sr  =  bundleContext.getServiceReference(HttpService.class.getName());
        Assert.assertNotNull("The httpServiceOSGiBundle is started and should have deployed a service reference for HttpService" ,sr);
        HttpService http = (HttpService)bundleContext.getService(sr);
        http.registerServlet("/greetings", new HttpServlet() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void doGet(HttpServletRequest req,
                    HttpServletResponse resp) throws ServletException,
                    IOException {
                resp.getWriter().write("Hello");
            }
        }, null, null);

        //now test the servlet
        HttpClient client = new HttpClient();
        try
        {
            client.start();
            Response response = client.GET("http://127.0.0.1:9876/greetings").get(5, TimeUnit.SECONDS);;
            Assert.assertEquals(HttpStatus.OK_200, response.status());

            String content = new String(((HttpContentResponse)response).content());
            Assert.assertEquals("Hello", content);
        }
        finally
        {
            client.stop();
        }
    }

}
