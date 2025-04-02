package springaimcpserverecs

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@SpringBootApplication
class Application {
    @Bean
    fun myTools(helloService: HelloService): MethodToolCallbackProvider =
        MethodToolCallbackProvider.builder().toolObjects(helloService).build()
}

@Service
class HelloService {

    @Tool(description = "says hello to someone")
    fun sayHello(@ToolParam(description = "name of person") name: String): String =
        "hello, $name"

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
