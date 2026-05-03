# Java MCP SQLite Client

This project is a Java-based Model Context Protocol (MCP) client built with **Spring Boot** and **Spring AI**. It is designed to connect to an MCP server (such as `java-mcp-sqlite-server`) to interact with a SQLite database by calling the tools exposed by the server.

The client communicates with the MCP server using **Server-Sent Events (SSE)**.

## Prerequisites

- **Java 21** or higher
- **Maven**
- A running **MCP Server** (by default, the client expects an SSE MCP server running at `http://localhost:8080/mcp/sse`).

## Configuration

The main configuration is located in `src/main/resources/application.properties`.

```properties
# The client runs on port 8081 to avoid conflict with the default server port 8080
server.port=8081

# Connect to the remote MCP Server via SSE
spring.ai.mcp.client.name=sqlite-client
spring.ai.mcp.client.type=sse
spring.ai.mcp.client.sse.url=http://localhost:8080/mcp/sse
```

Make sure the `spring.ai.mcp.client.sse.url` points to your running MCP Server.

## Building and Running

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   Or run the generated jar:
   ```bash
   java -jar target/mcp-database-client-0.0.1-SNAPSHOT.jar
   ```

The application will start on port `8081` and attempt to connect to the configured MCP server.

## Endpoints

The `DatabaseController` exposes several REST endpoints that interact with the connected MCP server. You can access these endpoints using a browser, `curl`, or an API client like Postman.

### 1. List Available Tools
Lists all tools exposed by the connected MCP server.

**Request:**
```bash
curl http://localhost:8081/tools
```

### 2. List Tables
Calls the `listTables` tool on the MCP server to retrieve a list of all tables in the SQLite database.

**Request:**
```bash
curl http://localhost:8081/call/listTables
```

### 3. Describe Table
Calls the `describeTable` tool on the MCP server to get the schema of a specific table.

**Request:**
```bash
curl "http://localhost:8081/call/describeTable?tableName=your_table_name"
```

### 4. Run Select Query
Calls the `runSelectQuery` tool on the MCP server to execute a custom `SELECT` query.

**Request:**
```bash
curl "http://localhost:8081/call/runSelectQuery?sql=SELECT * FROM your_table_name LIMIT 10"
```
*(Note: Be sure to properly URL-encode the `sql` parameter when using `curl` or browser.)*

## Architecture

This client leverages `spring-ai-starter-mcp-client-webflux` to handle the asynchronous communication with the MCP server. The `McpAsyncClient` bean is auto-configured and injected into the `DatabaseController`, where it is used to send `CallToolRequest` objects to the server and process the resulting `CallToolResult`.
