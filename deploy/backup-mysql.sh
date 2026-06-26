#!/bin/bash
set -e

source /home/ec2-user/readygsm/.env

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
S3_BUCKET="readygsm-prod-backup"

docker exec readygsm-mysql mysqldump \
  -u root -p"${PROD_MYSQL_ROOT_PASSWORD}" readygsm \
  | gzip | aws s3 cp - "s3://${S3_BUCKET}/mysql/backup_${TIMESTAMP}.sql.gz"
