#!/bin/bash
# readygsm-prod EC2에서 cron으로 실행되는 Let's Encrypt 인증서 갱신 스크립트.
# EC2 인스턴스 프로파일(IAM Role)의 Route53 권한을 사용하므로 별도 자격증명이 필요 없다.
set -e

docker run --rm --entrypoint certbot \
  -v /etc/letsencrypt:/etc/letsencrypt \
  certbot/dns-route53 renew --dns-route53 --non-interactive

docker compose -f /home/ec2-user/readygsm/compose.prod.yml exec nginx nginx -s reload
