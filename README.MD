hello again

# Building

- Run the root project's `shadowJar` and `build` tasks
- Run the `mergeJars` task
- Use the version.jar file (without -deps)

# Setting up MultiMC

- Create a new MultiMC instance with the version you want.
- Replace minecraft with the .jar file you built
- Customize minecraft and edit the `"mainClass"` variable to `"io.github.betterclient.client.launch.Main"`