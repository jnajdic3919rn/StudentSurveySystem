from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.common.exceptions import WebDriverException
import requests
import time
from bs4 import BeautifulSoup
from flask import Flask, request
from flask_cors import CORS
import json
import sys
from rabbitmq_utils import setup_rabbitmq, publish_to_survey_queue, publish_to_comment_queue

app = Flask(__name__)
CORS(app)


@app.route('/scrape', methods=['GET'])
def scrape_data():
    url = request.args.get('url')
    survey_name = request.args.get('survey')
    survey_type = request.args.get('surveyType')
    survey_year = request.args.get('surveyYear')
    faculty = request.args.get('faculty')

    print(url)
    print(survey_name)
    print(survey_type)

    if not url:
        response_data = {
            message: "Missing 'url' parameter"
        }
        response = app.response_class(
        response=json.dumps(response_data),
        status=400,
        mimetype='application/json'
        )
        return response
    
    
    service = Service()
    
    #service.executable_path = "./chromedriver/Linux/115/chromedriver"
    print("serviceee")
    
    options = webdriver.ChromeOptions()
    #driver = webdriver.Chrome(service=service, options=options)
    #options.add_argument(f"--webdriver-path={service.executable_path}")

    #service_args = ['--log-path=/chromedriver.log']

    # Create the WebDriver instance with the specified options and service_args
    driver = webdriver.Chrome(options=options)
    #driver = webdriver.Chrome(options=options, service_log_path='/chromedriver.log')

    try:
        driver.get(url)
    # If the URL is okay and loads without any errors, this code will not raise an exception.
        print("URL ok.")
    except WebDriverException as e:
    # If there's an issue loading the URL, WebDriverException will be raised.
        driver.quit()
        response_data = 'URL nije ispravan'
        response = app.response_class(
        response=json.dumps(response_data),
        status=400,
        mimetype='application/json'
        )
        return response

    time.sleep(30)
    print("uspeh")

    # Extract cookies from the WebDriver
    cookies = driver.get_cookies()

    # Close the Selenium WebDriver
    driver.quit()

    # Convert cookies to requests-compatible format
    cookie_dict = {cookie['name']: cookie['value'] for cookie in cookies}

    print("cookies")
    # Create an authenticated session for scraping
    session = requests.Session()
    session.cookies.update(cookie_dict)
    print("session")

    print("Stigo")
    # Now you can use the 'session' for web scraping
    protected_url = url
    webpage_response = session.get(protected_url)

    # Setup RabbitMQ
    connection, channel = setup_rabbitmq()

    soup = BeautifulSoup(webpage_response.content, 'html.parser')
        # Find all links to sections
    section_links = soup.find_all('a')
    # Create a directory to store files

    # Initialize a list to store section names
    section_name_list = []

    for link in section_links:
        section_name = link.get_text().strip()
        base_url = protected_url.rsplit('/', 1)[0]
        section_url = base_url + "/" + link.get('href')
        print("SEKCIJAAAAA")
        print(section_name)
        if section_url:
            section_response = session.get(section_url)
            section_content = section_response.content.decode('utf-8')

            grade_tag = "Ukupna prosečna ocena:"
            grade_index = section_content.find(grade_tag)
            end_index = section_content.find('<', grade_index)
            if grade_index != -1:
                grade = section_content[grade_index + len(grade_tag):end_index]
                grade = grade.strip()
                grade = float(grade)
            else:
                grade = 0.0
            
            votes_tag = "Ukupno glasalo:"
            votes_index = section_content.find(votes_tag)
            end_index = section_content.find('<', votes_index)
            if votes_index != -1:
                votes = section_content[votes_index + len(votes_tag):end_index]
                votes = votes.strip()
                votes = int(votes)
            else:
                votes = 0

            print("Votes")
            print(votes)
            print("Grade")
            print(grade)
            subject_data = {
                "subjectName": section_name,
                "votes": int(votes),
                "grade": float(grade)
            }

            section_name_list.append(subject_data)
            
    survey_data = {
        "surveyTitle": survey_name,
        "year": survey_year,
        "subjectData": section_name_list,
        "surveyType": survey_type,
        "url": url,
        "faculty": faculty
    }

    send_to_data_microservice(survey_data)

    for link in section_links:
        section_name = link.get_text().strip()
        base_url = protected_url.rsplit('/', 1)[0]
        section_url = base_url + "/" + link.get('href')
        print("SEKCIJAAAAA")
        print(section_name)
        if section_url:
            section_response = session.get(section_url)
            section_content = section_response.content.decode('utf-8')

            #print(section_content)
            # Define the target starting and ending tags
            start_tag_1 = "<h3>Ostale kritike, sugestije i pohvale:</h3>"
            start_tag_2 = "<h3>Ostale kritike, pohvale i sugestije:</h3>"
            end_tag = "</body>"

            found = False
            # Find the starting index of the content
            start_index = section_content.find(start_tag_1)
            if start_index != -1:
                found = True
                
            if found == False:
                start_index = section_content.find(start_tag_2)
                if start_index == -1:
                    continue

            # Find the ending index of the content
            end_index = section_content.find(end_tag, start_index)
            if end_index == -1:
                content = section_content[start_index + len(start_tag_1):]
            else:
                # Extract and print the content
                content = section_content[start_index + len(start_tag_1):end_index]
            
            # Split the content by <br> tags
            content_parts = content.split("<br>-----------------------------------------------------------------<br>")

            # Print each part
            for part in content_parts:
                message = part.strip()  # Get the message
                if message:

                    # Convert JSON object to a string
                    comment_data = {
                        "surveyUrl": url,
                        "subjectName": section_name,
                        "comment": message,
                        "faculty": faculty,
                        "surveyType": survey_type
                    }

                    publish_to_comment_queue(channel, comment_data)

    connection.close()
    
    session.close()
    response_data = {
            "message": "Uspešno preuzeto! Rezultati ankete će uskoro biti dostupni."
        }
    response = app.response_class(
    response=json.dumps(response_data),
    status=200,
    mimetype='application/json'
    )
    return response

def send_to_data_microservice(data):
    microservice_url = "http://localhost:8000/data/survey_info"
    headers = {'Content-Type': 'application/json'}
    #payload = json.dumps(data)

    #print(payload)

    response = requests.post(microservice_url, json=data, headers=headers)

    if response.status_code == 200:
        print("Data sent successfully to the Java microservice.")
        return True
    else:
        print(f"Failed to send data to the Java microservice. Status code: {response.status_code}")
        return False

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)