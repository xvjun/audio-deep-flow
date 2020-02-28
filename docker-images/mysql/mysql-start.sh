#!/usr/bin/env bash
cur_dir=`pwd`
docker stop xujun-mysql
docker rm xujun-mysql
docker run --name xujun-mysql -v ${cur_dir}/conf:/etc/mysql/conf.d -v ${cur_dir}/data:/var/lib/mysql -p 3307:3306 -e MYSQL_ROOT_PASSWORD=234520 -d mysql:latest
