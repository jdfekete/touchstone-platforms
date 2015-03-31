#NEW TOUCHSTONE EXPERIMENT - GENERAL STEPS

  1. Create your experiment folder in your Touchstone workspace. For example, one can create a `PointingAndDistractors` folder to conduct an experiment that measures pointing performance when there are more or less distractors in the graphical scene. (see Figure 1)
  1. Design your experiment. In the `design-platform` folder, run the script `./launch-design.sh` (or `.\launch-design.bat` for Windows users).
  1. Generate code skeletons for your experiment.
  1. Fill in the incomplete code for your experiment components.
  1. Run your experiment. In your experiment folder (e.g. `PointingAndDistractors`), use the command `./build.sh run` (or `.\build.bat run` for Windows users) (see Figure 2)
  1. (optional) Export your experiment as a TouchStone plug-in. In your experiment folder (e.g. `PointingAndDistractors`), use the command `./build.sh xml` (or `.\build.bat xml` for Windows users). The TouchStone plug-in is the combination of a xml description file (`PointingAndDistractors/plugin.xml`) and a library of experiment components (`PointingAndDistractors/build/plugin.xml` in our example).

| **Figure 1** |
|:-------------|
|![http://www.lri.fr/~appert/touchstone/images/newExp-1-pic.png](http://www.lri.fr/~appert/touchstone/images/newExp-1-pic.png)|

| **Figure 2** |
|:-------------|
|![http://www.lri.fr/~appert/touchstone/images/newExp-2-pic.png](http://www.lri.fr/~appert/touchstone/images/newExp-2-pic.png)|