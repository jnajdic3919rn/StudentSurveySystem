import pika
import json

queue_names = ['survey_data', 'comment_data']

def setup_rabbitmq():
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost', port=5672))
    channel = connection.channel()
    
    # Declare queues and setup RabbitMQ related configurations

    for queue_name in queue_names:
        channel.queue_declare(queue=queue_name, durable=True)

    return connection, channel

def publish_to_survey_queue(channel, survey_data):
    # Publish survey data to the survey queue
    survey_message = json.dumps(survey_data)

    channel.basic_publish(exchange='', routing_key=queue_names[0], body=survey_message)

def publish_to_comment_queue(channel, comment_data):
    # Publish comment data to the comment queue
    comment_message = json.dumps(comment_data)

    channel.basic_publish(exchange='', routing_key=queue_names[1], body=comment_message)
    