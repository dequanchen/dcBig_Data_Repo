package com.example.helloworld_unsecure;

import com.example.helloworld_unsecure.health.TemplateHealthCheck;
import com.example.helloworld_unsecure.resources.HelloWorldResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
	//1.Run from eclipse:
	// Run as configuration: Program arguments - server hello-world.yml --> Apply
	// Run ---> 
	// Check pages: http://localhost:8080/hello-world 
	//              http://localhost:8081/
	//2. Structure:
	//   hello-world.yml ---> HelloWorldConfiguration.java ---> HelloWorldApplication.java: Call HelloWorldResource.java +- TemplateHealthCheck.java ---> Register to appn environment (server)
    //3. Run from compiled directory - git bash:
	//   cd /c/Users/M041785/OneDrive/dcJavaWorkSpaces/dcBigDataWorkSpace/BDTS-Cluster_Dashboard 
	//or cd 'C:\Users\M041785\OneDrive\dcJavaWorkSpaces\dcBigDataWorkSpace\BDTS-Cluster_Dashboard'
	//Run --->   java -jar target/hello-world-0.0.1-SNAPSHOT.jar server hello-world.yml
	// Check pages: http://localhost:8080/hello-world 
    //              http://localhost:8081/
	public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
            new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}