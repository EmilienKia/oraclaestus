Log module
================

This log module provides basic logging functions to entities.
It allows entities to log messages with different levels of severity, such as debug, info, warning, error, and trace.
Entities can use these functions to log messages during their execution.
The log messages are forwarded to underlying [SLF4J API](https://slf4j.org/).

### Available functions

- `trace(message: string, ...)`: Logs a trace message, which is the most detailed level of logging.
- `debug(message: string, ...)`: Logs a debug message, which is useful for debugging purposes.
- `info(message: string, ...)`: Logs an informational message, which is used to report general information about the execution.
- `warn(message: string, ...)`: Logs a warning message, which indicates a potential problem or something that requires attention.
- `error(message: string, ...)`: Logs an error message, which indicates a serious problem that has occurred during execution.

These methods all have the same format: a first message text and variable additional arguments.
The message text can contain placeholders '{}' for the additional arguments, which will be formatted into the message, according to SLF4J format.

### Entities loggers

Entities have their own logger to which the messages are sent.

The logger is on the form : `<logger-prefix>.<simulation-name>.<entity-id>`.

The logger prefix is `com.github.emilienkia.oraclaestus.model.Simulation` by default,
but can be changed by calling `setLoggerPrefix(...)` before starting the simulation.

For example, for a simulation called `simu-0001` and an entity `yoyo-test` :
`com.github.emilienkia.oraclaestus.model.Simulation.simu-0001.yoyo-test`

