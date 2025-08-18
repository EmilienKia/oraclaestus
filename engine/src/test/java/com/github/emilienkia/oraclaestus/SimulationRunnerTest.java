package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.expressions.Addition;
import com.github.emilienkia.oraclaestus.expressions.Expression;
import com.github.emilienkia.oraclaestus.rules.Assignation;
import com.github.emilienkia.oraclaestus.expressions.ReadValue;
import com.github.emilienkia.oraclaestus.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.types.EnumerableType;
import com.github.emilienkia.oraclaestus.types.EnumerationType;
import com.github.emilienkia.oraclaestus.variables.IntegerVariable;
import com.github.emilienkia.oraclaestus.variables.Variable;
import nl.altindag.log.LogCaptor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;

class SimulationRunnerTest {

    @Test
    void testSimulationRunner() throws InterruptedException {
        SimulationRunner simulationRunner = new SimulationRunner();

        Model model = Model.builder()
                .id("accu")
                .name("accumulator")
                .register(new Identifier("accumulated"), new IntegerVariable("accumulated", 0))
                .register(new Identifier("step"), new IntegerVariable("step", 5))
                .ruleGroup(new RuleGroup().add(
                                new Assignation("accumulated",
                                new Addition(
                                        new ReadValue("accumulated"),
                                        new ReadValue("step")
                                )))).build();

        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        simulation.addEntity(model.createEntity("test"));

        Session session = simulationRunner.startSimulation(simulation, 1, TimeUnit.SECONDS);

        // Wait so long
        Thread.sleep(4_000);

        session.stop();

    }

    @Test
    void testInitAtExecutionStart() throws IOException, ExecutionException, InterruptedException {

        String source =
"""
name: "Test model"
id:   test_model

registers {
    i : int = 42
    s : string = "test"
    b : boolean = true
    f : float = 3.14
}

rules {
}
""";

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);

        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        String id = simulation.addEntity(model.createEntity("test"));

        simulation.start();

        EntityState state = simulation.getCurrentState(id);
        assertThat(state).isNotNull();
        assertThat(state.getValue("i")).isNotNull().isInstanceOf(Integer.class).isEqualTo(42);
        assertThat(state.getValue("s")).isNotNull().isInstanceOf(String.class).isEqualTo("test");
        assertThat(state.getValue("b")).isNotNull().isInstanceOf(Boolean.class).isEqualTo(true);
        assertThat(state.getValue("f")).isNotNull().isInstanceOf(Float.class).isEqualTo(3.14f);

    }

    @Test
    void testSimpleExecution() throws IOException, ExecutionException, InterruptedException {

        String source =
"""
name: "Test model"
id:   test_model

registers {
    i : int = 0
    s : string = ""
    b : boolean = false
    f : float = 0.0
}

rules {
    i += 42
    s = "test"
    b = true
    f = 3.0 + 0.14
}
""";

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);

        SimulationRunner simulationRunner = new SimulationRunner();
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        String id = simulation.addEntity(model.createEntity("test"));
        Session session = simulationRunner.startSimulation(simulation, 1);

        session.get();

        assertThat(session.getRemainingSteps()).isEqualTo(0);

        EntityState state = simulation.getCurrentState(id);
        assertThat(state).isNotNull();
        assertThat(state.getValue("i")).isNotNull().isInstanceOf(Integer.class).isEqualTo(42);
        assertThat(state.getValue("s")).isNotNull().isInstanceOf(String.class).isEqualTo("test");
        assertThat(state.getValue("b")).isNotNull().isInstanceOf(Boolean.class).isEqualTo(true);
        assertThat(state.getValue("f")).isNotNull().isInstanceOf(Float.class).isEqualTo(3.14f);
    }


    @Test
    void testEnumAssignment() throws IOException, ExecutionException, InterruptedException {
        String source =
                """
                types {
                    enum color {
                        RED,
                        GREEN,
                        BLUE
                    }
                }
                registers {
                    c: color = RED
                    d: color = RED
                }
                
                rules {
                    c = GREEN
                    d = c
                }
                """;

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);
        assertThat(model).isNotNull();

        SimulationRunner simulationRunner = new SimulationRunner();
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        String id = simulation.addEntity(model.createEntity("test"));
        Session session = simulationRunner.startSimulation(simulation, 1);

        session.get();

        assertThat(session.getRemainingSteps()).isEqualTo(0);

        EntityState state = simulation.getCurrentState(id);
        assertThat(state).isNotNull();
        assertThat(state.getValue("d")).isNotNull().isInstanceOf(EnumerableType.Instance.class);
        EnumerableType<?>.Instance d = (EnumerableType<?>.Instance) state.getValue("d");
        assertThat(d).isNotNull();
        assertThat(d.getValue()).isEqualTo(1);
        assertThat(d.getName()).isEqualTo("GREEN");
    }

    @Test
    void testSimpleFunctionExecution() throws IOException, ExecutionException, InterruptedException {

        String source =
                """
                registers {
                    i : int = 0
                }
                
                functions {
                    add (a: int, b: int) : int {
                        return a + b
                    }
                }
                
                rules {
                    i = add(i + 1, 12)
                }
                """;

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);

        SimulationRunner simulationRunner = new SimulationRunner();
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        String id = simulation.addEntity(model.createEntity("test"));
        Session session = simulationRunner.startSimulation(simulation, 1);

        session.get();

        assertThat(session.getRemainingSteps()).isEqualTo(0);

        EntityState state = simulation.getCurrentState(id);
        assertThat(state).isNotNull();
        assertThat(state.getValue("i")).isNotNull().isInstanceOf(Integer.class).isEqualTo(13);
    }

    @Test
    void testModuleFunctionExecution() throws IOException, ExecutionException, InterruptedException {

        String source =
                """
                registers {
                    pi : float = 3.141592
                    f : float
                    g: float
                    r: float
                    r2 : int
                    r3 : int
                }
                
                rules {
                    f = cos(pi / 2)
                    g = cos(pi)
                    r = rand(50.0, 100.0)
                    r2 = rand(50, 100)
                    r3 = abs(-42)
                }
                """;

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);

        SimulationRunner simulationRunner = new SimulationRunner();
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        simulation.registerDefaultModules();
        String id = simulation.addEntity(model.createEntity("test"));
        Session session = simulationRunner.startSimulation(simulation, 1);

        session.get();

        assertThat(session.getRemainingSteps()).isEqualTo(0);

        EntityState state = simulation.getCurrentState(id);
        assertThat(state).isNotNull();
        assertThat(state.getValue("f")).isNotNull().isInstanceOf(Float.class)
                .asInstanceOf(InstanceOfAssertFactories.FLOAT)
                .isCloseTo(0.0f, offset(0.0001f));
        assertThat(state.getValue("g")).isNotNull().isInstanceOf(Float.class)
                .asInstanceOf(InstanceOfAssertFactories.FLOAT)
                .isCloseTo(-1.0f, offset(0.0001f));

        assertThat(state.getValue("r")).isNotNull().isInstanceOf(Float.class)
                .asInstanceOf(InstanceOfAssertFactories.FLOAT)
                .isGreaterThanOrEqualTo(50.0f)
                .isLessThanOrEqualTo(100.0f);

        assertThat(state.getValue("r2")).isNotNull().isInstanceOf(Integer.class)
                .asInstanceOf(InstanceOfAssertFactories.INTEGER)
                .isGreaterThanOrEqualTo(50)
                .isLessThanOrEqualTo(100);

        assertThat(state.getValue("r3")).isNotNull().isInstanceOf(Integer.class)
                .asInstanceOf(InstanceOfAssertFactories.INTEGER)
                .isEqualTo(42);

    }


    @Test
    void testLogModuleFunctionExecution() throws IOException, ExecutionException, InterruptedException {

        String source =
                """
                id: "test"
                rules {
                    info("This is {} log message {}", 1, 2)
                    warn("This is warning message !!")
                }
                """;

        ModelParser helper = new ModelParser();
        Model model = helper.parseString(source);

        SimulationRunner simulationRunner = new SimulationRunner();
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        simulation.setLoggerPrefix("simulation");
        simulation.setName("simu");
        simulation.registerDefaultModules();
        String id = simulation.addEntity(model.createEntity("test"));

        LogCaptor logCaptor = LogCaptor.forName("simulation.simu." + id);

        Session session = simulationRunner.startSimulation(simulation, 1);

        session.get();

        assertThat(session.getRemainingSteps()).isEqualTo(0);

        assertThat(logCaptor.getInfoLogs()).contains("This is 1 log message 2");
        assertThat(logCaptor.getWarnLogs()).contains("This is warning message !!");

    }


}