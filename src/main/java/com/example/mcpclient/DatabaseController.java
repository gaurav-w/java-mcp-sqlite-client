package com.example.mcpclient;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DatabaseController {

    private final McpAsyncClient mcpClient;

    public DatabaseController(List<McpAsyncClient> mcpClients) {
        this.mcpClient = mcpClients.get(0);
    }

    @GetMapping("/tools")
    public Mono<String> listTools() {
        return mcpClient.listTools()
                .map(tools -> "Available tools from server:\n" +
                        tools.tools().stream()
                                .map(t -> "- " + t.name() + ": " + t.description())
                                .collect(Collectors.joining("\n")));
    }

    @GetMapping("/call/listTables")
    public Mono<String> listTables() {
        return mcpClient.callTool(new McpSchema.CallToolRequest("listTables", Map.of()))
                .map(result -> "Result:\n" + extractTextResult(result));
    }

    @GetMapping("/call/describeTable")
    public Mono<String> describeTable(@RequestParam String tableName) {
        return mcpClient.callTool(new McpSchema.CallToolRequest("describeTable", Map.of("tableName", tableName)))
                .map(result -> "Result:\n" + extractTextResult(result));
    }

    @GetMapping("/call/runSelectQuery")
    public Mono<String> runSelectQuery(@RequestParam String sql) {
        return mcpClient.callTool(new McpSchema.CallToolRequest("runSelectQuery", Map.of("sql", sql)))
                .map(result -> "Result:\n" + extractTextResult(result));
    }

    private String extractTextResult(McpSchema.CallToolResult result) {
        if (result.content() != null && !result.content().isEmpty()) {
            return result.content().stream()
                    .filter(c -> c instanceof McpSchema.TextContent)
                    .map(c -> ((McpSchema.TextContent) c).text())
                    .collect(Collectors.joining("\n"));
        }
        return "No content returned.";
    }
}
