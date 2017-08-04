package com.github.ilms49898723.minttranslator.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceGraph {
    private Map<String, Boolean> mTags;
    private Map<String, List<String>> mEdges;
    private List<String> mVertices;

    public DeviceGraph() {
        mEdges = new HashMap<>();
        mVertices = new ArrayList<>();
    }

    public void addVertex(String node) {
        mVertices.add(node);
        mEdges.put(node, new ArrayList<>());
    }

    public void addEdge(String source, String destination) {
        mEdges.get(source).add(destination);
    }

    public List<String> topologicalSort() {
        mTags = new HashMap<>();
        for (String vertex : mVertices) {
            mTags.put(vertex, false);
        }
        List<String> result = new ArrayList<>();
        for (String vertex : mVertices) {
            if (!mTags.get(vertex)) {
                dfs(result, vertex);
            }
        }
        return result;
    }

    private void dfs(List<String> result, String source) {
        mTags.put(source, true);
        for (String outVertex : mEdges.get(source)) {
            if (mTags.get(outVertex)) {
                System.err.println("Cycle detected!");
                System.exit(1);
            }
            dfs(result, outVertex);
        }
        result.add(source);
    }
}
