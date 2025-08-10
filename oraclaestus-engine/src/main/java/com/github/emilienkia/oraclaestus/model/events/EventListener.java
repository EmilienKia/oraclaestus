package com.github.emilienkia.oraclaestus.model.events;

public interface EventListener {
    void onStateChange(StateChangeEvent event);
}
