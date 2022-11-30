import requests
#import sys
#import time

response = requests.get("http://172.16.20.39:7091/")
print("status response --")
print(response.status_code)


import psutil
#CPU times
print(psutil.cpu_times())

#Avg CPU load
print(psutil.getloadavg())

#Memory
print(psutil.virtual_memory()) 

#Disk Usage
print(psutil.disk_usage('/'))

#Disk IO utilization
print(psutil.disk_io_counters(perdisk=False))


