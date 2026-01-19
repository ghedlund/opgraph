# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build all modules
mvn clean package

# Run all tests
mvn clean test

# Run tests for a specific module
mvn test -pl core
mvn test -pl common-nodes

# Run a single test class
mvn test -pl core -Dtest=TestOpGraph

# Run a single test method
mvn test -pl core -Dtest=TestOpGraph#testCycleDetection

# Skip tests during build
mvn package -DskipTests
```

## Project Structure

Multi-module Maven project (Java 11):

```
opgraph/
├── core/           # DAG data structure, execution engine, extension system
├── library/        # Node discovery and registration (depends on: core)
├── xml-io/         # XML serialization/deserialization (depends on: core)
├── app/            # GUI editor API and components (depends on: core, library, xml-io)
└── common-nodes/   # Pre-built operation nodes (depends on: core, app, xml-io)
```

## Architecture Overview

OpGraph is a framework for building complex operations from composable DAG-based workflows.

**Core Execution Flow:**
1. `OpGraph` holds `OpNode` vertices connected by `OpLink` edges
2. `Processor` executes nodes in topological order, passing data via `OpContext`
3. Nodes read inputs from context, perform computation, write outputs to context
4. Links transfer data from source `OutputField` to destination `InputField`

**Key Classes (all in `ca.phon.opgraph`):**
- `OpNode` - Abstract base class; extend to create custom nodes
- `OpGraph` - DAG container extending `DirectedAcyclicGraph<OpNode, OpLink>`
- `Processor` - Execution engine with `step()`, `run()`, `reset()` methods
- `OpContext` - HashMap-based data context with parent-child hierarchy
- `InputField` / `OutputField` - Define node inputs/outputs with optional type validation
- `OpLink` - Connects output field of one node to input field of another

**Extension System:**
- `Extendable` interface allows attaching capabilities to nodes/graphs
- `CompositeNode` extension enables sub-graphs (macros)
- `CustomProcessing` extension for custom execution hooks
- `NodeMetadata` stores UI positioning

**Node Registration:**
- Use `@OpNodeInfo` annotation for metadata (name, description, category)
- `NodeLibrary` discovers and instantiates node types

## Creating a Custom Node

```java
@OpNodeInfo(name="MyNode", description="Description", category="MyCategory")
public class MyNode extends OpNode {
    public static final InputField INPUT = new InputField("input", "desc", String.class);
    public static final OutputField OUTPUT = new OutputField("output", "desc", true, String.class);

    public MyNode() {
        putField(INPUT);
        putField(OUTPUT);
    }

    @Override
    public void operate(OpContext context) throws ProcessingException {
        String input = (String) context.get(INPUT);
        context.put(OUTPUT, processedResult);
    }
}
```

## Key Exceptions

- `ProcessingException` - Base exception for node execution errors
- `RequiredInputException` - Missing required input field value
- `InvalidTypeException` - Type validation failure
- `CycleDetectedException` - Graph would contain a cycle
