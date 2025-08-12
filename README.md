Oraclaestus
================

Oraclaestus is an open-source entity simulator.
Its aims to provide a simple environment for simulating entities, their behaviors and their interactions along the time.
Entities can be assets, devices, or even software components.
This simulator is primarily designed to be used in test environments as a companion of the software to test,
by simulating the behavior of the software's dependencies.

## Features

- **Entity Simulation**: Simulate entities with customizable states and behaviors.
- **Time Management**: Control the simulation time, allowing for fast-forwarding.

## Installation

To install Oraclaestus, you just have to add oracleastus to your project, with the wanted version:

### Maven:
```xml
<dependency>
    <groupId>com.github.emilienkia.oracleastus</groupId>
    <artifactId>engine</artifactId>
    <version>${oraclaestus-engine.version}</version> <!-- Replace with the desired version, e.g., 0.1.0 -->
    <scope>test</scope> <!-- or 'compile' if you want to use it in production -->
</dependency>
```

## Usage

### Create a simulation

First, you need to create a simulation runner.

From the following package and its subpackages:
```java
import com.github.emilienkia.oraclaestus.model.*;
```

You must instantiate a `SimulationRunner` object, which is the main orchestrator of the simulator.
The simulation runner is responsible to manage the underlying resources, like threads and pools,
shared between the simulations it will run. 

```java
SimulationRunner simulationRunner = new SimulationRunner();
```

Then, you can create a `Simulation` object, which is the main entry point of the simulation.
It is responsible to manage the entities, their behaviors and the execution of the simulation.

At creation, you may provide time and date data.
These data are used as the simulated date of execution of the simulation, and the time step seen by the simulation.

Note: this time vision of the simulation may be different of the real simulation execution. See later.

```java
Simulation simulation = new Simulation(LocalDateTime.now(), Duration.ofSeconds(1));
```

You should register the default modules in the simulation.
It allows the simulation to have access to functions exposed by these modules.
These modules are the core of the simulation, and provide basic functionalities to the entities like logging or basic
mathematical functions.

```java
simulation.registerDefaultModules();
```

### Create or load a model

Next, you need to create a model for your entities.

#### Loading a model from a file

The easiest way to create a model is to load it from a file.
You can create a model from a file by using the `ModelParserHelper` class.

```java
ModelParserHelper helper = new ModelParserHelper();
// From a string in memory
Model model = helper.parseString(source);
// From a string in memory
Model model = helper.parseFile("path/to/model.json");
// Or from a stream
Model model = helper.parse(someInputStream);
```


#### Creating a model programmatically

Or you can create a model programmatically.

TODO: Add example of model creation programmatically.

### Instantiate entities in simulation
Once you have a model, you can instantiate entities in the simulation.

You just have to call the `createAsset` method of the model, passing the id of the asset you want to create.
Then you just have to call the `addAsset` method of the simulation, passing the asset you want to add.

```java
String id = simulation.addAsset(model.createAsset("test"));
```

Note: the id returned by the `addAsset` method is the id of the asset in the simulation.
It may be used to manage it afterward.

### Run the simulation
Once you have created your entities, you can run the simulation.

You can run the simulation by calling one of the `startSimulation` methods of the `SimulationRunner` with your simulation object.

```java
SimulationRunner.SimulationSession session = simulationRunner.startSimulation(simulation);
```

The `startSimulation` method will return a `SimulationSession` object, which is the main entry point to interact with the simulation while it is running.

The ``startSimulation`` method can take many parameters:
- `long stepCount`: The number of steps the simulation will execute.
If not specified, the simulation will run indefinitely until stopped.
- `long stepRate` and `TimeUnit stepTimeUnit`: The time rate at which the simulation will be executed.
Basically the frequency between two steps.
This time rate is the real time rate of the simulation, not the time rate visible by the simulation.
If not specified, the simulation will run at the maximum speed, without any pause.


### Query the simulation

At any time, you can query the simulation to get the current state of the entities, by their id (returned by addAsset(...)).
And look for the current value of any register of the entity.

```java
State state = simulation.getCurrentState(id);
Object value = state.getValue("anyRegisterName");
```


## Documentation



## License
Oraclaestus is Open Source software released under the Apache 2.0 license.


