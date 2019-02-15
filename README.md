# Expert Systems Lab
A Java application for demonstrating the use of ontologies to represent and reason with knowledge in Expert Systems. This particular system allows users to build (virtual) pizzas and then infers interesting properties about them. It also provides explanations (justifications) for why it makes these inferences.

#### For Users: Installing and using the system

Requirements:

+ Java Runtime Environment 1.8+

Steps:

1. Use git to clone this repository or download the [zip](https://github.com/kodymoodley/expertsystemslab/archive/master.zip) archive.

2. If you cloned the repository, change into the `expertsystemslab-master/` directory. If you downloaded the zip archive, extract it and change into the `expertsystemslab-master/` directory.

3. Double-click the `pizzaexpert-${version}.jar` file. Alternatively, open a terminal or command prompt, change into the `expertsystemslab-master/` directory, and run `java -jar pizzaexpert-${version}.jar`.

**Note**: `pizzaexpert-${version}.jar` requires the following directory structure to launch correctly:

```bash
pizzaexpert-${version}.jar
src
|-- main
|	|-- resources
|	|	|-- pizza.owl
|	|	|-- logo.png
|	|	|-- Amatic-Bold.ttf
|	|	|-- AmaticSC-Regular.ttf
```

#### For Developers: Building a jar executable of the system

Requirements:

+ Apache's [Maven](http://maven.apache.org/index.html).
+ A tool for checking out a [Git](http://git-scm.com/) repository.
+ Java Development Kit 1.8

Steps:

1. Get a copy of the code:

        git clone https://github.com/kodymoodley/expertsystemslab.git
    
2. Change into the `expertsystemslab-master/` directory.

3. Type `mvn clean compile assembly:single`.  On build completion, the `target/` directory will contain an executable `pizzaexpert-${version}.jar` file. 

## Other modules used in this system

+ [OWLExplanation](https://github.com/matthewhorridge/owlexplanation)
+ [HermiT](http://www.hermit-reasoner.com/)
+ [OWLAPI](http://owlcs.github.io/owlapi/)

For specific versions check the pom.xml file.
