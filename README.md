# elbts

** WORK IN PROGRESS **

What is elbts?
--------------

A transformer to turn AWS load balancer logs into time series data. Main intent of elbts project is monitoring the possible aspects of services with no effort for service developers. 


Features
--------
 - [x] AWS Classic load balancer log file parser
 - [] AWS Application load balancer log file parser
 - [x] KairosDB Mrtics generator and persister
 - [] InfluxDB Metrics generator and persister
 - [x] Tagging merics by endpoint paths and resource names
 - [] Integration with swagger files to tag the metrics automatically
 
