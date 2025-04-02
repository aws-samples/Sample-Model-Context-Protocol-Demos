# Sample: MCP Agent with Spring AI and Bedrock

Provides a sample Spring AI MCP Server that runs on ECS; which is used by a Spring AI Agent using Bedrock; which also runs on ECS and is exposed publicly via a Load Balancer.

```mermaid
flowchart LR
    subgraph aws[AWS]
        alb[Application Load Balancer]
        
        subgraph vpc[VPC]
            server[MCP Server\nECS Service]
            client[MCP Client / Bedrock Agent\nECS Service]
        end
        
        subgraph services[AWS Services]
            bedrock[Bedrock]
        end
    end
    
    internet((Internet))
    
    %% Connections
    internet <--> alb
    alb --> client
    client <--> bedrock
    client <--> server

    %% Styling
    style aws fill:#f5f5f5,stroke:#232F3E,stroke-width:2px
    style vpc fill:#E8F4FA,stroke:#147EBA,stroke-width:2px
    style services fill:#E8F4FA,stroke:#147EBA,stroke-width:2px

    style alb fill:#FF9900,color:#fff,stroke:#FF9900
    style server fill:#2196f3,color:#fff,stroke:#2196f3
    style client fill:#2196f3,color:#fff,stroke:#2196f3
    style bedrock fill:#FF9900,color:#fff,stroke:#FF9900
    style internet fill:#fff,stroke:#666,stroke-width:2px

    %% Link styling
    linkStyle default stroke:#666,stroke-width:2px
```

## Setup

1. Setup Bedrock in the AWS Console, [request access to Nova Pro](https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess)
1. [Setup auth for local development](https://docs.aws.amazon.com/cli/v1/userguide/cli-chap-authentication.html)

## Run Locally

Start the MCP Server:
```
./mvnw -pl server spring-boot:run
```

Start the MCP Client / Agent:
```
./mvnw -pl client spring-boot:run
```

Make a request to the server REST endpoint:

In IntelliJ, open the `client.http` file and run the request.

Or via `curl`:
```
curl -X POST --location "http://localhost:8080/inquire" \
    -H "Content-Type: application/json" \
    -d '{"question": "Get employees that have skills related to Java, but not Java"}'
```

## Run on AWS

Prereqs:
- [Create an ECR Repo named `mcp-agent-spring-ai-server` and one named `mcp-agent-spring-ai-client`](https://us-east-1.console.aws.amazon.com/ecr/private-registry/repositories/create?region=us-east-1)
- [Auth `docker` to ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/registry_auth.html)
- [Install Rain](https://github.com/aws-cloudformation/rain)

Build and push the MCP Server & MCP Client to ECR:
```
export ECR_REPO=<your account id>.dkr.ecr.us-east-1.amazonaws.com

./mvnw -pl server spring-boot:build-image -Dspring-boot.build-image.imageName=$ECR_REPO/mcp-agent-spring-ai-server
docker push $ECR_REPO/mcp-agent-spring-ai-server:latest

./mvnw -pl client spring-boot:build-image -Dspring-boot.build-image.imageName=$ECR_REPO/mcp-agent-spring-ai-client
docker push $ECR_REPO/mcp-agent-spring-ai-client:latest
```

Deploy the Agent:
```
rain deploy infra.cfn mcp-agent-spring-ai
```

End-to-end Test with `curl`:
```
curl -X POST --location "http://YOUR_LB_HOST/inquire" \
-H "Content-Type: application/json" \
-d '{"question": "Get employees that have skills related to Java, but not Java"}'
```
