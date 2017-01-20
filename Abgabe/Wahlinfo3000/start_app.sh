#!/bin/bash#!/bin/bash
glassfish4/bin/asadmin start-domain
glassfish4/bin/asadmin deploy --force wahlinfo3000.war
open http://localhost:8080/wahlinfo3000