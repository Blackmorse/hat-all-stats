FROM  ubuntu:18.04

RUN  apt-get update && \
     apt-get install --yes software-properties-common  && \
     apt-add-repository --yes --update ppa:ansible/ansible && \
     apt-get update  && \
     apt-get install --yes ansible

COPY ./entrypoint.sh /entrypoint.sh
ENTRYPOINT ["bash","/entrypoint.sh"]