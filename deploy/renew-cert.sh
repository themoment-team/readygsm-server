#!/bin/bash
# GSMSV VM(readygsm-stage)에서 cron으로 실행되는 인증서 갱신 스크립트.
# 같은 디렉터리의 .certbot-env(커밋되지 않음, AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY 포함)를 필요로 한다.
set -e
cd "$(dirname "$0")"

set -a
source ./.certbot-env
set +a

docker run --rm --entrypoint certbot \
  -v readygsm-stage_certbot_conf:/etc/letsencrypt \
  -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY \
  certbot/dns-route53 renew --dns-route53 --non-interactive

docker compose -f compose.stage.yml exec nginx nginx -s reload
