package org.example;

import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.tasks.SimpleTask;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

import java.util.Map;

public class WorkflowFactory {

    private WorkflowExecutor executor;


    public WorkflowFactory(WorkflowExecutor executor) {
        this.executor = executor;
    }

    public ConductorWorkflow<Map<String, String>> createWorkflow() {
        ConductorWorkflow<Map<String, String>> workflow = new ConductorWorkflow<>(this.executor);
        workflow.setName("greetings");
        workflow.setVersion(1);
        workflow.setOwnerEmail("abdul.salam@orkes.io");
        SimpleTask simpleTask = new SimpleTask("greet", "greet_ref");
        simpleTask.input("name", "${workflow.input.name}");

        workflow.add(simpleTask);
        return workflow;
    }
}
