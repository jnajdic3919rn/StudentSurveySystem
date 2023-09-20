from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.common.exceptions import WebDriverException
import requests
import time
from bs4 import BeautifulSoup
import jwt
from flask import Flask, request
from flask_cors import CORS
import json
import sys
from rabbitmq_utils import setup_rabbitmq, publish_to_survey_queue, publish_to_comment_queue

SECRET_KEY = 'najtajnovitijikljuczajwtkojijesvetikadvideo'

app = Flask(__name__)
CORS(app)


@app.route('/scrape', methods=['GET'])
def scrape_data():
    # Retrieve the "Authorization" header
    authorization_header = request.headers.get('Authorization')

    # Check if the header exists or is empty
    if not authorization_header:
        # Return a 401 Unauthorized status code with a message
        return(401, 'Unauthorized')

    try:
        # Extract the token from the header (assuming it's in the format "Bearer <token>")
        token = authorization_header.split()[1]

        # Verify and decode the JWT using secret key
        decoded_token = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])

        # You can access the claims from the decoded token as needed
        roles = decoded_token.get('roles', [])

        if 'ROLE_ADMIN' not in roles:
            return(401, 'Unauthorized')
    
    except jwt.ExpiredSignatureError:
        # Handle the case where the token has expired
        return(401, 'Token has expired')
    except jwt.DecodeError:
        # Handle the case where the token is invalid or cannot be decoded
        return(401, 'Invalid token')

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
    
    options = webdriver.ChromeOptions()

    # Create the WebDriver instance with the specified options and service_args
    driver = webdriver.Chrome(options=options)

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

    current_url = driver.current_url

    # Check if the current URL matches the original one
    if current_url != url:
        print("Unexpected navigation occurred.")
        # The URL has changed, indicating unexpected navigation
        driver.quit()
        response_data = 'Došlo je do greške'
        response = app.response_class(
        response=json.dumps(response_data),
        status=400,
        mimetype='application/json'
        )
        return response
        # Handle the situation as needed (e.g., show an error message or take appropriate action)

    # Extract cookies from the WebDriver
    cookies = driver.get_cookies()

    # Close the Selenium WebDriver
    driver.quit()

    # Check if the user is logged in based on the cookies
    if not cookies:
        # No cookies found, indicating that the user did not log in within the timeout
        print("User did not log in within the timeout")
        response_data = 'Došlo je do greške'
        response = app.response_class(
        response=json.dumps(response_data),
        status=400,
        mimetype='application/json'
        )
        return response
    else:
        # Cookies found, indicating that the user logged in
        print("User is logged in")

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
        
        if section_url:
            section_response = session.get(section_url)
            section_content = section_response.content.decode('utf-8')

            subject_data = extract_survey_data_from_content(section_content=section_content, section_name=section_name)

            section_name_list.append(subject_data)
            
    survey_data = {
        "surveyTitle": survey_name,
        "year": survey_year,
        "subjectData": section_name_list,
        "surveyType": survey_type,
        "url": url,
        "faculty": faculty
    }

    send_to_data_microservice(survey_data, authorization_header)

    for link in section_links:
        section_name = link.get_text().strip()
        base_url = protected_url.rsplit('/', 1)[0]
        section_url = base_url + "/" + link.get('href')
      
        if section_url:
            section_response = session.get(section_url)
            section_content = section_response.content.decode('utf-8')

            content_parts = extract_content_parts(section_content=section_content)
            
            # Print each part
            if content_parts is not None:
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
    response_data = "Uspešno preuzeto! Rezultati ankete će uskoro biti dostupni."
    response = app.response_class(
    response=json.dumps(response_data),
    status=200,
    mimetype='application/json'
    )
    return response

def extract_survey_data_from_content(section_content, section_name):
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

    subject_data = {
        "subjectName": section_name,
        "votes": int(votes),
        "grade": float(grade)
    }

    return subject_data


def extract_content_parts(section_content):
    # Define the target starting and ending tags
    start_tag_1 = "<h3>Ostale kritike, sugestije i pohvale:</h3>"
    start_tag_2 = "<h3>Ostale kritike, pohvale i sugestije:</h3>"
    end_tag = "</body>"

    found = False
    # Find the starting index of the content
    start_index = section_content.find(start_tag_1)
    if start_index != -1:
        found = True
                
    if not found:
        start_index = section_content.find(start_tag_2)
        if start_index == -1:
            return None

    # Find the ending index of the content
    end_index = section_content.find(end_tag, start_index)
    if end_index == -1:
        content = section_content[start_index + len(start_tag_1):]
    else:
        # Extract and print the content
        content = section_content[start_index + len(start_tag_1):end_index]
    
    # Split the content by <br> tags
    content_parts = content.split("<br>-----------------------------------------------------------------<br>")

    return content_parts


def send_to_data_microservice(data, token):
    microservice_url = "http://localhost:8000/data/survey_info"
    headers = {'Content-Type': 'application/json',
               'Authorization':token}
    
    response = requests.post(microservice_url, json=data, headers=headers)

    if response.status_code == 200:
        print("Data sent successfully to the Java microservice.")
        return True
    else:
        print(f"Failed to send data to the Java microservice. Status code: {response.status_code}")
        return False

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)