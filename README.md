# FSM2BSV

FSM2BSV is a compiler that translates a UML state diagram to Bluespec SystemVerilog (BSV) - a high-level hardware description language.  
The state machine design can be done using any UML tool that supports XMI 2.0 export feature.  
The [Cparser project](https://github.com/sergiodurand/Cparser) could be used as a complement for FSM2BSV if you want to use the C language to describe state behaviors.  

FSM2BSV makes use of the following libraries:
* Apache Velocity: a Java-based template engine.
* Mycila XML Tool: XML manipulation library in Java.

This project was part of my Masters in Computing Science at University of Sao Paulo / Brazil.  
For more details about this project please refer to:

* **A tool for generating code from Bluespec SystemVerilog based on finite state machine described in UML and C** (*available only in Portuguese*)  
Master's Dissertation  
DOI: [10.11606/D.55.2012.tde-15042013-102159](https://doi.org/10.11606/D.55.2012.tde-15042013-102159)  

* **A tool to support Bluespec SystemVerilog coding based on UML diagrams**  
Published in IECON 2012 - 38th Annual Conference on IEEE Industrial Electronics Society  
DOI: [10.1109/IECON.2012.6389493](https://doi.org/10.1109/IECON.2012.6389493)  
