FROM python:3

WORKDIR /project

COPY . .

ENV HTTP_PROXY=http://172.30.100.1:8080
ENV HTTPS_PROXY=http://172.30.100.1:8080

RUN pip install -r requirements.txt

CMD [ "python", "code/app.py" ]
