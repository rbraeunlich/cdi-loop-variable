package de.data_experts;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.cdi.BusinessProcess;

@Named
@ApplicationScoped
public class MyServiceClass implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private BusinessProcess businessProcess;

	public void doSomething() {
		Object variable = businessProcess.getVariable("variable");
		Objects.requireNonNull(variable);
		System.out.println(variable);
	}

}
