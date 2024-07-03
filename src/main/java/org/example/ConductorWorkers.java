package org.example;

import com.netflix.conductor.sdk.workflow.task.InputParam;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

public class ConductorWorkers {
    @WorkerTask("greet")
    public String greet(@InputParam("name") String name) {

        System.out.println("Greet Task");
        return "Hello " + name;
    }
}
