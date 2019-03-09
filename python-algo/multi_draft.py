import json
import time
from multiprocessing import Process
from time import sleep

import psutil as psutil
from kafka import KafkaConsumer, KafkaProducer
from datetime import datetime

import os



def consume_data():
    print("pid {}".format(os.getpid()))


if __name__ == "__main__":
    print("h")

    t1 = Process(target=consume_data)
    t2 = Process(target=consume_data)
    t1.start()
    t2.start()




