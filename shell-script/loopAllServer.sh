#!/bin/bash
#2019.06.18
SERLIST=(
node1
node2
node3
)

if [ -z "$*" ];then
  echo -e "Usage:$0 'command'"
else
  for h in ${SERLIST[*]};do echo "==$h==" && ssh $h -n "$*";done
fi
