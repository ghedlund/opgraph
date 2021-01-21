OpGraph
=======

The OpGraph project provides a framework for building complex operations from simpler ones. Written in Java, the framework includes:

* a graph data structure for constructing operations,
* a processing context for fine control over execution,
* a context structure for controlling the data flow in the graph,
* an application api for creating a custom editor to create and edit graphs, and
* XML persistence.

For more information, please see [http://thegedge.github.com/opgraph](http://thegedge.github.com/opgraph). This project is a work in progress, so any and all contributions (issues, suggestions, pull requests) are welcome.

Compilation
-----------

This project uses Maven for project management:

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and execute `mvn clean package`

License
-------

Copyright (C) 2012-2021 Jason Gedge and Greg Hedlund.

See LICENSE.txt for license details.
