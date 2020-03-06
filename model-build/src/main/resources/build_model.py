import tensorflow as tf
import numpy as np
import sklearn
import pandas as pd
import os
import sys
import time
from tensorflow import keras
from sklearn.model_selection import train_test_split
import configparser
import logging
import requests

print(tf.__version__)
print(sys.version_info)
for model in np,pd,sklearn,tf,keras:
    print(model.__name__, model.__version__)

logging.basicConfig(
    format='%(asctime)s %(levelname)-8s %(message)s',
    level=logging.INFO,
    datefmt='%Y-%m-%d %H:%M:%S')
tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.INFO)


def pre_data(data_path_dir):
    names = []
    for i in range(0, 256):
        names.append("col_" + str(i))
    names.append("label")

    data_list = []

    dir_or_files = os.listdir(data_path_dir)
    for dir_file in dir_or_files:
        # 获取目录或者文件的路径
        dir_file_path = os.path.join(data_path_dir, dir_file)
        # 判断该路径为文件还是路径
        if os.path.isfile(dir_file_path):
            data_list.append(dir_file_path)
    data = None
    for file in data_list:
        temp_data = pd.read_csv(file, header=None, names=names)
        data = pd.concat([data, temp_data])
    return data


def build_model(hidden_layers, layers_size, learning_rate, dropout_rate, class_sum):
    model = keras.models.Sequential()
    model.add(keras.layers.Flatten(input_shape=[2, 128]))
    for i in range(hidden_layers):
        model.add(keras.layers.Dense(layers_size, activation="relu"))
        model.add(keras.layers.BatchNormalization())
    model.add(keras.layers.Dropout(rate=dropout_rate))
    model.add(keras.layers.Dense(class_sum, activation="softmax"))
    if(learning_rate > 0):
        optimizer = keras.optimizers.SGD(learning_rate)
        model.compile(loss="sparse_categorical_crossentropy",
                  optimizer=optimizer,
                  metrics=["accuracy"])
    else:
        model.compile(loss="sparse_categorical_crossentropy",
                      optimizer="SGD",
                      metrics=["accuracy"])
    return model


def fit_model(model,epochs):
    logdir = './callbacks'
    if not os.path.exists(logdir):
        os.mkdir(logdir)
    output_model_file = os.path.join(logdir, "model")

    callbacks = [
        keras.callbacks.TensorBoard(logdir),
        keras.callbacks.ModelCheckpoint(output_model_file, save_best_only=True),
        keras.callbacks.EarlyStopping(patience=20, min_delta=1e-4),
    ]
    history = model.fit(x_train, y_train, epochs=epochs,
                        validation_data=(x_valid, y_valid),
                        callbacks=callbacks)
    return history

def save_history(history_path, history):
    with open(history_path, 'w') as f:
        f.write(str(history))
    f.close()

def save_time(time_path, time):
    with open(time_path, 'w') as f:
        f.write(str(time))
    f.close()

def complete(progress_url, job_id):
    data = {"job_id": job_id}
    headers = {"Content-Type": "application/json"}
    rs = requests.post(progress_url, json=data, headers=headers)
    print(rs)



if __name__ == '__main__':

    config = configparser.ConfigParser()
    file = sys.argv[1]
    config.read(file)

    data_path = config.get('tensorflow', 'data_path')
    hidden_layers = config.getint('tensorflow', 'hidden_layers')
    layers_size = config.getint('tensorflow', 'layers_size')
    learning_rate = config.getfloat('tensorflow', 'learning_rate')
    save_path = config.get('tensorflow', 'save_path')
    epochs = config.getint('tensorflow', 'epochs')
    dropout_rate = config.getfloat('tensorflow', 'dropout_rate')
    class_sum = config.getint('tensorflow', 'class_sum')
    history_path = config.get('tensorflow', 'history_path')
    tock_path = config.get('tensorflow', 'tock_path')
    progress_url = config.get('tensorflow', 'progress_url')
    job_id = config.getint('tensorflow', 'job_id')

    print(data_path,type(data_path))
    print(hidden_layers,type(hidden_layers))
    print(layers_size,type(layers_size))
    print(learning_rate,type(learning_rate))
    print(save_path,type(save_path))
    print(epochs,type(epochs))
    print(dropout_rate,type(dropout_rate))
    print(class_sum,type(class_sum))
    print(history_path,type(history_path))
    print(tock_path,type(tock_path))
    print(progress_url,type(progress_url))

    start_time = time.time()

    data = pre_data(data_path)

    data_x = data.iloc[:, 0:-1].values
    data_y = data.iloc[:, -1:].values
    x_train, x_valid, y_train, y_valid = train_test_split(data_x, data_y, test_size=0.2, random_state=0)
    x_train = x_train.reshape(-1, 2, 128)
    x_valid = x_valid.reshape(-1, 2, 128)

    model = build_model(hidden_layers, layers_size, learning_rate, dropout_rate, class_sum)

    history = fit_model(model,epochs)

    save_history(history_path, history.history)

    end_time = time.time()
    save_time(tock_path, end_time-start_time)

    tf.keras.models.save_model(model, save_path)

    complete(progress_url, job_id)

