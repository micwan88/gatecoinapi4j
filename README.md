# Welcome to GatecoinApi4j

[![Build Status](https://travis-ci.org/micwan88/gatecoinapi4j.svg?branch=master)](https://travis-ci.org/micwan88/gatecoinapi4j)
[![Coverage Status](https://coveralls.io/repos/github/micwan88/gatecoinapi4j/badge.svg?branch=master)](https://coveralls.io/github/micwan88/gatecoinapi4j?branch=master)

This is a java implementation of [Gatecoin](https://gatecoin.com/) api included RESTful call and PubNub subscribe service (Real time data streaming over Web Socket).

###Contribution
This library is created for my own interest and it is not yet finished.
So welcome anyone contribute to it.

### Building a jar for your project
```
git clone https://github.com/micwan88/gatecoinapi4j.git
cd gatecoinapi4j
gradle clean jar
```
After that, you can find your gatecoinapi4j.jar in ${project.projectDir}/build/libs