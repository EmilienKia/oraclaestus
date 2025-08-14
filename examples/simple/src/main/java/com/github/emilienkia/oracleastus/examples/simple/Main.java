package com.github.emilienkia.oracleastus.examples.simple;


import com.github.emilienkia.oraclaestus.ModelParserHelper;
import com.github.emilienkia.oraclaestus.SimulationRunner;
import com.github.emilienkia.oraclaestus.model.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {


static final String SOURCE =
"""
id: yoyo
name: "Yoyo test model"

registers {
    min: int = 0
    max: int = 100
    step: int = 10
    grow: boolean = true # true means we are growing, false means we are shrinking
    turn: int = 0
    current: int = 0
    
}

rules {
    s : int = rand(1, step)
    info("Current value: {} , {}", current, s)
    if(grow) {
        current += s
        if(current >= max) {
            current = max
            grow = false
        }
    } else {
        current -= s
        if(current <= min) {
            current = min
            grow = true
            turn += 1
        }
    }
}
""";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        SimulationRunner simulationRunner = new SimulationRunner();

        // Parse the model from the source string
        ModelParserHelper helper = new ModelParserHelper();
        Model model = helper.parseString(SOURCE);

        Entity test = model.createEntity("test");

        // Create a simulation with the current time and a duration of 1 second
        Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
        // Register default module to have access to rand function
        simulation.registerDefaultModules();

        // Add the entity to the simulation
        String id = simulation.addEntity(test);

        // Start the simulation, two steps per second
        TimeUnit stepTimeUnit = TimeUnit.MILLISECONDS;
        long stepRate = 500; // 500 milliseconds per step

        long turnCount = 1; // We will stop the simulation after 5 turns

        Session session = simulationRunner.startSimulation(simulation, stepRate, stepTimeUnit);

        System.out.println("Yoyo value:");
        while(true) {
            EntityState currentState = simulation.getCurrentState(id);
            System.out.println(currentState.getValue("current"));
            if(currentState.getValue("turn") instanceof Integer turn && turn >= turnCount) {
                // Exit at the fifth turn
                System.out.println("Reached " + turnCount + " turns, exiting simulation.");
                break;
            }
            try {
                // Wait approximately for the next step
                stepTimeUnit.sleep(stepRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Simulation interrupted.");
                break;
            }
        }

        // Stop the simulation
        System.out.println("Stop the simulation.");
        session.stop();

        System.out.println("Shutdown the runner.");
        simulationRunner.shutdown();
        simulationRunner.awaitTermination(5, TimeUnit.SECONDS);
    }
}