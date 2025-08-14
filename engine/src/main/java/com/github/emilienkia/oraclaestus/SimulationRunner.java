package com.github.emilienkia.oraclaestus;

import com.github.emilienkia.oraclaestus.model.Session;
import com.github.emilienkia.oraclaestus.model.Simulation;
import com.github.emilienkia.oraclaestus.model.SimulationState;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.concurrent.Future.State.RUNNING;

public class SimulationRunner {

    class SimulationSession implements Session {

        Simulation simulation;
        long stepRate;
        TimeUnit stepTimeUnit;
        long stepCount = -1; // -1 means infinite steps

        private Future<?> future;

        Semaphore semaphore;

        State state = State.STOPPED;
        Exception exception = null;


        public SimulationSession(@NonNull Simulation simulation, long stepRate, TimeUnit stepTimeUnit, long stepCount) {
            this.simulation = simulation;
            this.stepRate = stepRate;
            this.stepTimeUnit = stepTimeUnit;
            this.stepCount = stepCount;
        }

        @Override
        public long getRemainingSteps() {
            return stepCount;
        }

        public void start() {
            simulation.start();
            resume();
        }

        public boolean stop() {
            if(state==State.RUNNING) {
                state = State.STOPPED;
                if (future != null) {
                    future.cancel(false);
                }
                return true;
            } else {
                return false;
            }
        }

        void step() {
            try {
                if(stepCount != 0) {
                    if (stepCount > 0) {
                        stepCount--;
                    }
                }
                simulation.step();
                if(stepCount == 0) {
                    state = State.FINISHED;
                    future.cancel(false);
                    semaphore.release();
                }
            } catch (Exception e) {
                exception = e;
                state = State.ABORTED;
                semaphore.release();
                if(future != null) {
                    future.cancel(false);
                }
                // TODO Add trace ?
            }
        }

        void loop() {
            step();
            if(state==State.RUNNING) {
                executor.submit(this::loop);
            }
        }

        @Override
        public State state() {
            return state;
        }

        @Override
        public SimulationState getSimulationState() {
            return simulation.getSimulationState();
        }

        public Exception getException() {
            return exception;
        }

        @Override
        public boolean resume() {
            if(state != State.RUNNING) {
                exception = null;
                state = State.RUNNING;
                semaphore = new Semaphore(0);
                if(stepRate > 0) {
                    future = executor.scheduleAtFixedRate(this::step, stepRate, stepRate, stepTimeUnit);
                } else {
                    future = executor.submit(this::loop);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean resume(int stepCount) {
            if(state != State.RUNNING) {
                exception = null;
                state = State.RUNNING;
                this.stepCount = stepCount;
                semaphore = new Semaphore(0);
                if(stepRate > 0) {
                    future = executor.scheduleAtFixedRate(this::step, stepRate, stepRate, stepTimeUnit);
                } else {
                    future = executor.submit(this::loop);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean restart(long stepCount) {
            if(state!=State.RUNNING) {
                exception = null;
                this.stepCount = stepCount;
                start();
                return true;
            } else {
                return false;
            }
        }

        public SimulationState get() throws ExecutionException, InterruptedException {
            if(state==State.FINISHED) {
                return simulation.getSimulationState();
            }
            if(state==State.ABORTED) {
                throw new ExecutionException("An exception occurred during the simulation", exception);
            }
            if(future!=null) {
                semaphore.acquire();
                return simulation.getSimulationState();
            } else {
                throw new CancellationException("Simulation is not running or has not been started yet.");
            }
        }

        @Override
        public SimulationState get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            if(state==State.FINISHED) {
                return simulation.getSimulationState();
            }
            if(state==State.ABORTED) {
                throw new ExecutionException("An exception occurred during the simulation", exception);
            }
            if(future!=null) {
                if(semaphore.tryAcquire(timeout, unit)){
                    return simulation.getSimulationState();
                } else {
                    throw new TimeoutException("Simulation did not finish in the given time.");
                }
            } else {
                throw new CancellationException("Simulation is not running or has not been started yet.");
            }
        }

    }



    Map<Simulation, SimulationSession> simulations = new HashMap<>();

    ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);


    public Session startSimulation(Simulation simulation) {
        return startSimulation(simulation, 0, TimeUnit.SECONDS, -1);
    }

    public Session startSimulation(Simulation simulation, long stepCount) {
        return startSimulation(simulation, 0, TimeUnit.SECONDS, stepCount);
    }

    public Session startSimulation(Simulation simulation, long stepRate, TimeUnit stepTimeUnit) {
        return startSimulation(simulation, stepRate, stepTimeUnit, -1);
    }

    public Session startSimulation(Simulation simulation, long stepRate, TimeUnit stepTimeUnit, long stepCount) {
        if(simulation==null) {
            throw new IllegalArgumentException("Simulation cannot be null");
        }
        SimulationSession session = new SimulationSession(simulation, stepRate, stepTimeUnit, stepCount);
        simulations.put(simulation, session);
        session.start();
        return session;
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

}
