FROM python:3.7

COPY requirements.txt  /app/
RUN pip install -r /app/requirements.txt
CMD  ["python","/app/model/build_model.py","/app/model/model.conf"]


