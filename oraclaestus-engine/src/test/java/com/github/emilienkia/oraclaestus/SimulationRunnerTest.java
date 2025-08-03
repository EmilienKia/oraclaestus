package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.Model;
import com.github.emilienkia.oraclaestus.model.Simulation;
import com.github.emilienkia.oraclaestus.model.expressions.Addition;
import com.github.emilienkia.oraclaestus.model.rules.Assignation;
import com.github.emilienkia.oraclaestus.model.expressions.ReadValue;
import com.github.emilienkia.oraclaestus.model.rules.RuleGroup;
import com.github.emilienkia.oraclaestus.model.variables.IntegerVariable;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

class SimulationRunnerTest {

    @Test
    void testSimulationRunner() throws InterruptedException {
        SimulationRunner simulationRunner = new SimulationRunner();

        Model model = Model.builder()
                .id("accu")
                .name("accumulator")
                .register("accumulated", new IntegerVariable("accumulated", 0))
                .register("step", new IntegerVariable("step", 5))
                .ruleGroup(new RuleGroup().add(
                                new Assignation("accumulated",
                                new Addition(
                                        new ReadValue("accumulated"),
                                        new ReadValue("step")
                                )))).build();

        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        simulation.addAsset(model.createAsset("test"));

        simulationRunner.startSimulation(simulation, 1, TimeUnit.SECONDS);

        // Wait so long
        Thread.sleep(10_000);

        simulationRunner.stopSimulation(simulation);

    }


}