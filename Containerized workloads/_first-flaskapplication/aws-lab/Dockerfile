FROM python:3.6

COPY requirements.txt requirements.txt

RUN pip install --no-cache -r requirements.txt

EXPOSE 5000

CMD ["python", "server.py"]

COPY . .

# docker build -t python-app .
# docker run -i --rm -p 8080:5000 python-app        
# -p port mapping host:docker-app
# curl http://0.0.0.0:5000/

# TAGGING using IMAGE ID
# docker tag 7548d8df0984 python-app:v1

#TAGGING while building
#docker build -t python-app-v2:v2 ./

# https://github.com/aws-els-cpt/eks/