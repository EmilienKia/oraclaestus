package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.Simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.concurrent.Future.State.RUNNING;

public class SimulationRunner {

    public class SimulationSession implements Future<Void> {

        Simulation simulation;
        long stepRate;
        TimeUnit stepTimeUnit;
        long stepCount = -1; // -1 means infinite steps

        private /*Scheduled*/ Future<?> future;

        public SimulationSession(Simulation simulation, long stepRate, TimeUnit stepTimeUnit, long stepCount) {
            this.simulation = simulation;
            this.stepRate = stepRate;
            this.stepTimeUnit = stepTimeUnit;
            this.stepCount = stepCount;
        }

        public long getRemainingSteps() {
            return stepCount;
        }

        public void start() {
            if ((future == null || future.isCancelled() || future.isDone()) && simulations != null) {
                simulation.start();
                if(stepRate > 0) {
                    future = executor.scheduleAtFixedRate(this::step, stepRate, stepRate, stepTimeUnit);
                } else {
                    future = executor.submit(this::loop);
                }
            }
        }

        public void stop() {
            if (future != null && !future.isCancelled() && !future.isDone()) {
                future.cancel(true);
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

        void loop() {
            // If stepCount is >0, at least run 1 step and decrement count
            // If stepCount is -1, we run indefinitely
            while(stepCount != 0) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                if(stepCount>0) {
                    stepCount--;
                }
                step();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future != null && future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return future != null && future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future != null && future.isDone();
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            if(future != null) {
                future.get();
            }
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if(future != null) {
                future.get(timeout, unit);
            }
            return null;
        }

        @Override
        public Throwable exceptionNow() {
            if(future != null) {
                return future.exceptionNow();
            }
            return null;
        }

        @Override
        public State state() {
            if(future != null) {
                return future.state();
            }
            return RUNNING;
        }
    }




    Map<Simulation, SimulationSession> simulations = new HashMap<>();

    ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);

    public void stopSimulation(Simulation simulation) {
        if(simulation != null && simulations.containsKey(simulation)) {
            SimulationSession simuInfo = simulations.get(simulation);
            simuInfo.stop();
        }
    }

    public void stopSimulation(SimulationSession simulation) {
        simulation.stop();
    }


    public SimulationSession startSimulation(Simulation simulation) {
        return startSimulation(simulation, 0, TimeUnit.SECONDS, -1);
    }

    public SimulationSession startSimulation(Simulation simulation, long stepCount) {
        return startSimulation(simulation, 0, TimeUnit.SECONDS, stepCount);
    }

    public SimulationSession startSimulation(Simulation simulation, long stepRate, TimeUnit stepTimeUnit) {
        return startSimulation(simulation, stepRate, stepTimeUnit, -1);
    }

    public SimulationSession startSimulation(Simulation simulation, long stepRate, TimeUnit stepTimeUnit, long stepCount) {
        SimulationSession simuInfo = new SimulationSession(simulation, stepRate, stepTimeUnit, stepCount);
        simulations.put(simulation, simuInfo);
        simuInfo.start();
        return simuInfo;
    }



}
