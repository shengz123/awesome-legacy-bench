# Awesome Legacy Bench for Java

A benchmark of tasks for legacy system transformation in Java project.

## Tasks

- **Remove unused modules - factor and factor-core**: 
  - Remove the factor module from the project
  - Ensure all the modules are maven build success 

- **Java version upgrade**: Upgrade Java version from 1.8 to 17
  - Upgrade all the relevant dependencies to compatible versions
  - Ensure all the modules are maven build success under Java 17

- **Replace Hbase with Mango DB**: Replace HBase with MongoDB as the database
  - Update the pom.xml file to include the necessary dependencies for MongoDB
  - Modify the code that interacts with HBase to use MongoDB instead
  - Ensure all the modules are maven build success after replacing HBase with MongoDB

- **Integrate with LLM**: Extend query module to support LLM integration
  - Use Spring AI to integrate with LLM
  - LLM API is https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation
  - API Key is Bearer sk-xxxxx
  - Request body example:
    ```json
      {
    "model": "qwen-turbo",
    "input":{
        "messages":[      
            {
                "role": "system",
                "content": "You are a helpful assistant."
            },
            {
                "role": "user",
                "content": "Who are you?"
            }
        ]
    },
    "parameters": {
        "result_format": "message"
    }
}
    ```
  - Response body example:
    ```json
      {
        "output": {
          "choices": [
            {
              "finish_reason": "stop",
              "message": {
              "role": "assistant",
              "content": "我是通义千问，阿里巴巴集团旗下的超大规模语言模型。我能够帮助你回答问题、创作文字，如写故事、公文、技术文档等，还能进行逻辑推理、表达观点，玩游戏等。如果你有任何问题或需要帮助，欢迎随时告诉我！"
              }
            }
          ]
        },
        "usage": {
          "total_tokens": 79,
          "output_tokens": 57,
          "input_tokens": 22,
          "prompt_tokens_details": {
            "cached_tokens": 0
          }
        },
          "request_id": "7696c8c1-d447-9914-b786-0cf375de09e5"
      }
    ```
  - Allow the LLM chat api to call function of query
  - Ensure all the modules are maven build success after integrating with LLM
  - Ensure QueryMain can be started without any error

- **Generate functional tests on restful api**: Create a new module for functional tests
  - Use RestAssured to write tests for restful api to test query and llm api
  - Ensure the functional tests module can be built successfully and run test successfully

- **Generate unit tests**: Create unit tests for the existing codebase

- **Generate containerization dockerfile**: Generate the dockerfile for the microservice

- **Write Documentation**: Document the project

