FROM python:3.6.5-alpine

WORKDIR /app

COPY requirements.txt .

RUN apk --no-cache add autoconf g++ gcc && \
    pip install --no-cache-dir -r requirements.txt && \
    apk del g++ autoconf gcc

COPY ./app .

EXPOSE 5000

CMD ["python", "main.py"]
