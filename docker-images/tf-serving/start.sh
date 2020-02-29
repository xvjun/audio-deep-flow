#!/usr/bin/env bash

docker stop tf-serving

docker run -t --rm -p 8501:8501 \
   --name tf-serving \
   -v "$(pwd)/saved_model:/models/zsy" \
   -e MODEL_NAME=zsy \
   tensorflow/serving


