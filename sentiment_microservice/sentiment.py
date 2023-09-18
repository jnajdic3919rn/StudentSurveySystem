#!/usr/bin/env python
# coding: utf-8

from transformers import pipeline
import pika
import re
import json
from googletrans import Translator

pip = pipeline("sentiment-analysis")

#connection = pika.BlockingConnection(pika.ConnectionParameters(host='172.17.0.2', port=5672))  # Change to the RabbitMQ server's hostname or IP address
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost', port=5672))
channel = connection.channel() 

queue_names = ['comment_data', 'comment_result']

for queue_name in queue_names:
    channel.queue_declare(queue=queue_name, durable=True)

translator = Translator()

def callback(ch, method, properties, body):
    comment_data = eval(body.decode('utf-8'))  # Convert the JSON string to a Python dictionary
    survey_url = comment_data["surveyUrl"]
    subject_name = comment_data["subjectName"]
    comment = comment_data["comment"]
    faculty = comment_data["faculty"]
    survey_type = comment_data["surveyType"]

    translated = translator.translate(comment, src='sr', dest='en')
    sentences = re.split(r'[.!?]+\s*(?=[A-ZŽŠĆČĐ])', comment)
    sentences = [s.strip() for s in sentences if s.strip()]

    translated_sentences = re.split(r'[.!?]+\s*(?=[A-ZŽŠĆČĐ])', translated.text)
    translated_sentences = [ts.strip() for ts in translated_sentences if ts.strip()]

    
    print("Received Survey Title:", survey_url)
    print("Subject Name:", subject_name)
    print("Comment:", comment)
    
    # Remove empty sentences
    # Perform your processing here
    print("Translated:", translated)
    
    for i in range(min(len(translated_sentences), len(sentences))): 
        
        result = pip(translated_sentences[i])
    
        object_to_send = {
            "surveyUrl": survey_url,
            "subjectName": subject_name,
            "comment": sentences[i],
            "label": result[0]['label'],
            "faculty": faculty,
            "surveyType": survey_type
        }
        message = json.dumps(object_to_send)

        channel.basic_publish(exchange='', routing_key=queue_names[1], body=message)

# Set up consumer
channel.basic_consume(queue=queue_names[0], on_message_callback=callback, auto_ack=True)

print('Waiting for messages...')
channel.start_consuming()