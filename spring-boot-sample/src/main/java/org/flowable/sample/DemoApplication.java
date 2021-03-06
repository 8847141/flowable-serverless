/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.flowable.sample;

import java.util.function.Function;

import org.flowable.engine.ProcessEngine;
import org.flowable.serverless.ServerlessUtil;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionalSpringApplication;

import experiment.MyJavaDelegate;

/**
 * Build and run with
 * java -noverify -jar flowable-function-spring-boot-sample-6.4.0-SNAPSHOT.jar
 *
 * Try with
 * curl localhost:8084 -d test
 */
@SpringBootConfiguration
public class DemoApplication implements Function<String, String> {

  public static ProcessEngine processEngine = ServerlessUtil.initializeProcessEngineForBpmnModel(commandContext -> SimpleServiceTask.createSimpleServiceTaskBpmnModel());;

  public static void main(String[] args) {
    FunctionalSpringApplication.run(DemoApplication.class, args);
  }

  @Override
  public String apply(String value) {
    String processInstanceId = processEngine.getRuntimeService().startProcessInstanceById(ServerlessUtil.PROCESS_DEFINITION_ID).getId();
    return "[Spring Cloud] - new process instance " + processInstanceId + " started. Number of delegation executions = " + MyJavaDelegate.COUNTER.get();
  }

}