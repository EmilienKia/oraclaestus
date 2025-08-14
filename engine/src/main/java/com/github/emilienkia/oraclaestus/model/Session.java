package com.github.emilienkia.oraclaestus.model;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Session {

    enum State {
        RUNNING,
        STOPPED,
        FINISHED,
        ABORTED
    }


    long getRemainingSteps();

    SimulationState getSimulationState();

    Exception getException();

    State state();

    default boolean isRunning() {
        return state() == State.RUNNING;
    }

    default boolean isStopped() {
        return state() == State.STOPPED;
    }

    default boolean isFinished() {
        return state() == State.FINISHED;
    }

    default boolean isAborted() {
        return state() == State.ABORTED;
    }

    boolean stop();

    boolean resume();

    boolean resume(int stepCount);

    default boolean restart() {
        return restart(0);
    }

    boolean restart(long stepCount);

    SimulationState get() throws ExecutionException, InterruptedException;
    SimulationState get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException;
}
