This is a copy of the spring pet clinic app found here https://github.com/spring-projects/spring-petclinic.

It has a circuit breaker embedded into it at localhost:8080/oups, or whatever port you're running this on. You can trip the circuit breaker by refreshing that page over and over. This application requires the configuration server found here https://github.com/sunbor/r4j-demo-config to be running.
