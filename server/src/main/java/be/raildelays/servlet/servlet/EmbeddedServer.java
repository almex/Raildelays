/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.servlet.servlet;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;

/**
 * @author Almex
 * @since 2.0
 */
public class EmbeddedServer {

    public static void main(String[] args) throws Exception {
        Undertow server = null;
        final String[] contextPaths = new String[]{"/spring/bootstrap-context.xml", "/jobs/main-job-context.xml"};

        try {
            ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths);
            DeploymentInfo servletBuilder = Servlets.deployment()
                    .setClassLoader(EmbeddedServer.class.getClassLoader())
                    .setContextPath("/myapp")
                    .setDeploymentName("test.war")
                    .addServlets(
                            Servlets.servlet(
                                    "MessageServlet",
                                    JaxRsServlet.class,
                                    () -> new ImmediateInstanceHandle<>(new JaxRsServlet(new JerseyConfig()))
                            )
                                    .addMapping("/*")
                                    .addInitParam("message", "Hello world!"),
                            Servlets.servlet(
                                    "MyServlet",
                                    JaxRsServlet.class,
                                    () -> new ImmediateInstanceHandle<>(new JaxRsServlet(new JerseyConfig()))
                            )
                                    .addInitParam("message", "MyServlet")
                                    .addMapping("/myservlet"));
            DeploymentManager manager = Servlets.defaultContainer()
                    .addDeployment(servletBuilder);

            manager.deploy();

            PathHandler path = Handlers.path(Handlers.redirect("/myapp"))
                    .addPrefixPath("/myapp", manager.start());

            server = Undertow.builder()
                    .addHttpListener(8080, "localhost")
                    .setHandler(path)
                    .build();


            //-- Initialize contexts
            applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
            applicationContext.start();
            //-- Starting server
            server.start();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
//            if (server != null) {
//                server.stop();
//            }
        }
    }
}
