# Numeric-methods
Numeric method algorithm examples

The main idea behind this repository is to serve as education material for all those interested in numeric algorithms.

This project offers implementations of the following algorithms:
* **Box**
* **Golden cut**
* **Hook Jeeves**
* **Nelder Mead Simplex**
* **Newton Raphson**
* **Finding unimodal area**

## Use of the library
Let's say you wish to fit some linear data using Hook Jeeves. 
You have decided to use the following strategies:
* SOLUTION REPRESENTATION: double array
* INITIAL SOLUTIO GENERATION: random
* SOLUTION QUALITY: one divided by function value
* STOPPING CONDITIONS: Function value close to 0, Number of function evaluations

You would do this in the following way:
```java
//Function to optimize
LinearFunction linearFunction = new LinearFunction();
double[][] systemMatrix = DataSetLoader.loadMatrix(new File(System.getProperty("user.dir"),"data/linear-data.txt"));
FunctionCallCounterWrapper<double[]> function =  new FunctionCallCounterWrapper<>(
    new PrototypeBasedSystemLossFunction(systemMatrix,linearFunction,new SquareErrorFunction()));		

double acceptableErrorRate = 1e-3;
int maximumNumberOfGenerations = 100; 

//Fitness evaluator
IFitnessEvaluator<DoubleArraySolution> evaluator = new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>());

//Optimization stopper
IOptimisationStopper<DoubleArraySolution> stopper = new CompositeOptimisationStopper<>(Arrays.asList(
  new FunctionValueStopper<>(acceptableErrorRate),
  new GenerationNumberEvolutionStopper<>(maximumNumberOfGenerations)
));

//Start solution generator
IStartSolutionGenerator<DoubleArraySolution> startSolutionGenerator = new RandomStartSolutionGenerator(2,5,5);

double[] xD = new double[]{2,2};
double reductionFactor = 0.5;
double minimalStepLimit = 1e-2;

//Optimization algorithm
HookeJeevesAlgorithm optimizationAlgorithm = new HookeJeevesAlgorithm(function, 
    evaluator,stopper, startSolutionGenerator, xD, reductionFactor, minimalStepLimit);		
    
DoubleArraySolution solution = optimizationAlgorithm.run();
```
