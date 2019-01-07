[![Build Status](https://travis-ci.org/openmrs/openmrs-module-atomfeed.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-atomfeed)

# openmrs-module-atomfeed
More generic atom feed implementation

Description
-----------
The AtomFeed Server Module for OpenMRS Platform.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

Note!

By default module is built with dependencies required by the OpenMRS 2.0.5. 
In order to build this module with dependencies required by the OpenMRS 1.9.10 or 1.9.11 please use the maven profile.
Example:
```bash
mvn clean install -P openmrs-1.9
```
