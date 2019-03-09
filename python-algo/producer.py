from time import sleep
from json import dumps
from kafka import KafkaProducer
from datetime import datetime

if __name__ == "__main__":
    print("h")
    producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                             value_serializer=lambda x:
                             dumps(x).encode('utf-8'))

    for e in range(5):
        data = {'timestamp' : datetime.now().microsecond, 'message' : "hello"}
        producer.send('test1', value=data)
        print("produce {}".format(data))
        sleep(1)
