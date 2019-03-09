import os
import json
import time
import multiprocessing
import logging
from kafka import KafkaConsumer, KafkaProducer


def on_send_success(record_metadata):
    pass


def on_send_error(excp):
    print('I am an errback', exc_info=excp)
    print(excp)
    # handle exception


def consume_data(id):
    try:
        consumer = KafkaConsumer('raw-data',
                                 group_id='my-group',
                                 bootstrap_servers=['localhost:9092'],
                                 auto_offset_reset='earliest',
                                 value_deserializer=lambda m: json.loads(m.decode('ascii')))

        producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                                 value_serializer=lambda m: json.dumps(m).encode('ascii'))

        print("consumer-[{}] ready...")
        for message in consumer:
            print("pid {}".format(os.getpid()))

            # message value and key are raw bytes -- decode if necessary!
            # e.g., for unicode: `message.value.decode('utf-8')`
            print("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                                 message.offset, message.key,
                                                 message.value))

            payload = {}
            payload['timestamp'] = time.time_ns()
            payload['message'] = "hello from python {}".format(message.value)
            producer.send('test1', payload).add_callback(on_send_success).add_errback(on_send_error)
            print("produced {}".format(payload))
    except Exception as e:
        print("Error in consumer-[{}] : {}".format(id, e))


if __name__ == "__main__":
    cpu_count = multiprocessing.cpu_count()
    processes = [multiprocessing.Process(target=consume_data, args=(i,)) for i in range(cpu_count)]

    print("starting...")
    for p in processes:
        p.start()

    print("started {} processes".format(len(processes)))
    for p in processes:
        p.join()

    print("done")




