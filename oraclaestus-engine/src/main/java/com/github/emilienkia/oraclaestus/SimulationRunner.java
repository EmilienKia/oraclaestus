package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.Simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimulationRunner {

    class SimulationSession {

        Simulation simulation;
        long stepRate;
        TimeUnit stepTimeUnit;

        private ScheduledFuture<?> future;


        public SimulationSession(Simulation simulation, long stepRate, TimeUnit stepTimeUnit) {
            this.simulation = simulation;
            this.stepRate = stepRate;
            this.stepTimeUnit = stepTimeUnit;
        }

        public void start() {
            if (future == null || future.isCancelled() || future.isDone() && simulations != null) {
                future = executor.scheduleAtFixedRate(this::step, stepRate, stepRate, stepTimeUnit);
            }
        }

        public void stop() {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        }

        void step() {
            try {
                simulation.step();
            } catch (Exception e) {
                System.err.println("Error during simulation step: " + e.getMessage());
                e.printStackTrace();
                stop(); // Stop the simulation on error
            }
        }
    }

    Map<Simulation, SimulationSession> simulations = new HashMap<>();

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> future = null;

    public void stopSimulation(Simulation simulation) {
        if(simulation != null && simulations.containsKey(simulation)) {
            SimulationSession simuInfo = simulations.get(simulation);
            simuInfo.stop();
        }
    }

    public void startSimulation(Simulation simulation, long stepRate, TimeUnit stepTimeUnit) {
        SimulationSession simuInfo = new SimulationSession(simulation, stepRate, stepTimeUnit);
        simulations.put(simulation, simuInfo);
        simuInfo.start();
    }



}
