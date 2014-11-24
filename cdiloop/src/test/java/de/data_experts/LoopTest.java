package de.data_experts;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.CdiStandaloneProcessEngineConfiguration;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LoopTest {

	@Deployment
	public static Archive<?> createDeployment() {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.importCompileAndRuntimeDependencies().resolve()
				.withTransitivity().asFile();

		return ShrinkWrap.create(WebArchive.class).addClass(MyServiceClass.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsResource("shouldWork.bpmn").addAsLibraries(libs);
	}
	

	@Test
	public void runLoop() {
		ProcessEngineConfiguration configuration = new CdiStandaloneProcessEngineConfiguration();
		configuration.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
		configuration
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
		configuration.setJdbcUsername("sa");
		configuration.setJdbcPassword("");
		configuration.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
		ProcessEngine engine = configuration.buildProcessEngine();
		engine.getRepositoryService().createDeployment()
				.addClasspathResource("shouldWork.bpmn").name("shouldWork")
				.deploy();
		RuntimeService runtimeService = engine.getRuntimeService();
		Map<String, Object> processVariables = new HashMap<>();
		processVariables.put("variables", Collections.singletonList("123"));
		runtimeService
				.startProcessInstanceByKey("shouldWork", processVariables);
	}
}
