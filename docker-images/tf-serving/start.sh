#!/usr/bin/env bash

docker stop tf-serving

docker run -t --rm -p 8501:8501 \
   --name tf-serving \
   -v "$(pwd)/model:/models/model" \
   -e MODEL_NAME=model \
   tensorflow/serving


