package org.example;

import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import io.orkes.conductor.client.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        simpleTask("http://localhost:8080/api");
    }

    private static void simpleTask(String url) throws InterruptedException, ExecutionException, TimeoutException {
        ApiClient apiClient = configureApiClient(url);

        CreateWorkflow workflowCreation = createSimpleWorkflow(apiClient);

        Workflow workflow = workflowCreation.workflowFuture().get(10, TimeUnit.SECONDS);

        System.out.println(workflow.getOutput());
        workflowCreation.workflowClient().shutdown();
        System.exit(0);
    }

    private static CreateWorkflow createSimpleWorkflow(ApiClient apiClient) {
        OrkesClients orkesClients = new OrkesClients(apiClient);
        TaskClient taskClient = orkesClients.getTaskClient();
        WorkflowClient workflowClient = orkesClients.getWorkflowClient();
        MetadataClient metadataClient = orkesClients.getMetadataClient();

        WorkflowExecutor workflowExecutor = new WorkflowExecutor(taskClient, workflowClient, metadataClient, 10);
        workflowExecutor.initWorkers("org.example");

        WorkflowFactory workflowFactory = new WorkflowFactory(workflowExecutor);
        ConductorWorkflow<Map<String,String>> workflow = workflowFactory.createWorkflow();
        CompletableFuture<Workflow> workflowFuture = workflow.executeDynamic(Map.of("name", "Orkes"));
        CreateWorkflow result = new CreateWorkflow(workflowClient, workflow, workflowFuture);
        return result;
    }

    private record CreateWorkflow(WorkflowClient workflowClient, ConductorWorkflow<Map<String, String>> workflow, CompletableFuture<Workflow> workflowFuture) {
    }

    private static ApiClient configureApiClient(String url) {
        ApiClient apiClient = new ApiClient(url);
        apiClient.setWriteTimeout(30_000);
        apiClient.setReadTimeout(30_000);
        apiClient.setConnectTimeout(30_000);
        return apiClient;
    }
}
