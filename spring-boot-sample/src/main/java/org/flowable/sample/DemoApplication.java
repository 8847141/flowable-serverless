package org.flowable.sample;

import java.util.function.Function;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.event.FlowableEventSupport;
import org.flowable.engine.ProcessEngine;
import org.flowable.serverless.NoDbProcessEngineConfiguration;
import org.flowable.serverless.ServerlessProcessDefinitionUtil;
import org.flowable.serverless.Util;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;

import experiment.MyJavaDelegate;

/**
 * Build and run with
 * java -noverify -jar flowable-processor-spring-boot-sample-1.0-SNAPSHOT.jar
 *
 * Try with curl localhost:8080 -d test
 */
@SpringBootConfiguration
public class DemoApplication implements Function<String, String> {

  public static ProcessEngine processEngine;

  static {

    long start = System.currentTimeMillis();

    NoDbProcessEngineConfiguration engineConfiguration = new NoDbProcessEngineConfiguration();
    processEngine = engineConfiguration.buildProcessEngine();
    BpmnModel bpmnModel = SimpleServiceTask.createSimpleServiceTaskBpmnModel();

    // TODO: move to processor?
    bpmnModel.setEventSupport(new FlowableEventSupport());

    // This is trickier to move
    Util.processFlowElements(bpmnModel.getMainProcess().getFlowElements(), bpmnModel.getMainProcess());

    // END TODO

    ServerlessProcessDefinitionUtil.deployServerlessProcessDefinition(bpmnModel, engineConfiguration);

    long end = System.currentTimeMillis();
    System.out.println("Flowable engine booted up in " + (end - start) + " ms");
  }


  public static void main(String[] args) {
    FunctionalSpringApplication.run(DemoApplication.class, args);
  }

  @Override
  public String apply(String value) {
    String processInstanceId = processEngine.getRuntimeService().startProcessInstanceById(ServerlessProcessDefinitionUtil.PROCESS_DEFINITION_ID).getId();
    return "[Spring Cloud] - new process instance " + processInstanceId + " started. Number of delegation executions = " + MyJavaDelegate.COUNTER.get();
  }

}