import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";

// ── 설정 ──────────────────────────────────────────────────
const config = new pulumi.Config();
const env            = config.require("environment");
const instanceType   = config.get("ec2InstanceType") ?? "t3.small";
const keyPairName    = config.require("ec2KeyPairName");
const vpcCidr        = config.get("vpcCidr")         ?? "10.0.0.0/16";
const subnetCidr     = config.get("subnetCidr")      ?? "10.0.1.0/24";
const az             = config.get("availabilityZone") ?? "ap-northeast-2a";
const hostedZoneName = config.require("hostedZoneName");
const appDomainName  = config.require("appDomain");
const dnsTtl         = config.getNumber("dnsTtl")    ?? 300;

const prefix = `readygsm-${env}`;
const commonTags = { Project: "readygsm", Environment: env };

// ── VPC ───────────────────────────────────────────────────
const vpc = new aws.ec2.Vpc(`${prefix}-vpc`, {
    cidrBlock: vpcCidr,
    enableDnsHostnames: true,
    enableDnsSupport: true,
    tags: { ...commonTags, Name: `${prefix}-vpc` },
});

// ── Internet Gateway ──────────────────────────────────────
const igw = new aws.ec2.InternetGateway(`${prefix}-igw`, {
    vpcId: vpc.id,
    tags: { ...commonTags, Name: `${prefix}-igw` },
});

// ── Public Subnet ─────────────────────────────────────────
const publicSubnet = new aws.ec2.Subnet(`${prefix}-subnet-public`, {
    vpcId: vpc.id,
    cidrBlock: subnetCidr,
    availabilityZone: az,
    mapPublicIpOnLaunch: true,
    tags: { ...commonTags, Name: `${prefix}-subnet-public` },
});

// ── Route Table ───────────────────────────────────────────
const routeTable = new aws.ec2.RouteTable(`${prefix}-rt-public`, {
    vpcId: vpc.id,
    routes: [
        {
            cidrBlock: "0.0.0.0/0",
            gatewayId: igw.id,
        },
    ],
    tags: { ...commonTags, Name: `${prefix}-rt-public` },
});

new aws.ec2.RouteTableAssociation(`${prefix}-rt-assoc-public`, {
    subnetId: publicSubnet.id,
    routeTableId: routeTable.id,
});

// ── Security Group ────────────────────────────────────────
const sg = new aws.ec2.SecurityGroup(`${prefix}-sg`, {
    name: `${prefix}-sg`,
    description: "ReadyGSM production security group",
    vpcId: vpc.id,
    ingress: [
        {
            description: "SSH",
            protocol: "tcp",
            fromPort: 22,
            toPort: 22,
            cidrBlocks: ["0.0.0.0/0"],
            // 보안 강화 시: 배포 담당자 IP로 제한
            // cidrBlocks: ["YOUR_IP/32"],
        },
        {
            // GitHub Actions 호스팅 러너의 아웃바운드가 22번 포트만 필터링하는 네트워크가 있어
            // CD 파이프라인이 22번으로 접속하지 못하는 문제 발견 (2026-09-01). CI는 이 대체 포트로 접속.
            description: "SSH-alt (bypass port22 outbound filtering on some CI networks)",
            protocol: "tcp",
            fromPort: 2222,
            toPort: 2222,
            cidrBlocks: ["0.0.0.0/0"],
        },
        {
            description: "HTTP",
            protocol: "tcp",
            fromPort: 80,
            toPort: 80,
            cidrBlocks: ["0.0.0.0/0"],
        },
        {
            description: "HTTPS",
            protocol: "tcp",
            fromPort: 443,
            toPort: 443,
            cidrBlocks: ["0.0.0.0/0"],
        },
        // 8080(app), 3306(MySQL), 6379(Redis)는 외부 미노출 — Docker 내부 네트워크만 통신
    ],
    egress: [
        {
            description: "All outbound",
            protocol: "-1",
            fromPort: 0,
            toPort: 0,
            cidrBlocks: ["0.0.0.0/0"],
        },
    ],
    tags: { ...commonTags, Name: `${prefix}-sg` },
});

// ── IAM Role (EC2 → ECR pull 권한) ───────────────────────
const ec2Role = new aws.iam.Role(`${prefix}-ec2-role`, {
    name: `${prefix}-ec2-role`,
    assumeRolePolicy: JSON.stringify({
        Version: "2012-10-17",
        Statement: [
            {
                Effect: "Allow",
                Principal: { Service: "ec2.amazonaws.com" },
                Action: "sts:AssumeRole",
            },
        ],
    }),
    tags: { ...commonTags, Name: `${prefix}-ec2-role` },
});

new aws.iam.RolePolicyAttachment(`${prefix}-ec2-ssm-core`, {
    role: ec2Role.name,
    policyArn: "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore",
});

new aws.iam.RolePolicy(`${prefix}-ec2-ecr-policy`, {
    name: `${prefix}-ec2-ecr-policy`,
    role: ec2Role.id,
    policy: JSON.stringify({
        Version: "2012-10-17",
        Statement: [
            {
                Effect: "Allow",
                Action: "ecr:GetAuthorizationToken",
                Resource: "*",
            },
            {
                Effect: "Allow",
                Action: [
                    "ecr:BatchCheckLayerAvailability",
                    "ecr:GetDownloadUrlForLayer",
                    "ecr:BatchGetImage",
                ],
                Resource: `arn:aws:ecr:ap-northeast-2:*:repository/${prefix}-app`,
            },
        ],
    }),
});

// ── IAM Policy (EC2 → certbot DNS-01 인증서 발급/갱신용 Route53 권한) ──
new aws.iam.RolePolicy(`${prefix}-ec2-certbot-policy`, {
    name: `${prefix}-ec2-certbot-policy`,
    role: ec2Role.id,
    policy: JSON.stringify({
        Version: "2012-10-17",
        Statement: [
            {
                Effect: "Allow",
                Action: [
                    "route53:ListHostedZones",
                    "route53:GetChange",
                    "route53:ChangeResourceRecordSets",
                    "route53:ListResourceRecordSets",
                ],
                Resource: "*",
            },
        ],
    }),
});

// ── S3 백업 버킷 ──────────────────────────────────────────
const backupBucket = new aws.s3.BucketV2(`${prefix}-backup`, {
    bucket: `${prefix}-backup`,
    tags: { ...commonTags, Name: `${prefix}-backup` },
});

new aws.s3.BucketPublicAccessBlock(`${prefix}-backup-public-access-block`, {
    bucket: backupBucket.id,
    blockPublicAcls: true,
    blockPublicPolicy: true,
    ignorePublicAcls: true,
    restrictPublicBuckets: true,
});

new aws.s3.BucketLifecycleConfigurationV2(`${prefix}-backup-lifecycle`, {
    bucket: backupBucket.id,
    rules: [{
        id: "expire-old-backups",
        status: "Enabled",
        expiration: { days: 30 },
    }],
});

new aws.iam.RolePolicy(`${prefix}-ec2-s3-backup-policy`, {
    name: `${prefix}-ec2-s3-backup-policy`,
    role: ec2Role.id,
    policy: backupBucket.arn.apply(arn => JSON.stringify({
        Version: "2012-10-17",
        Statement: [{
            Effect: "Allow",
            Action: ["s3:PutObject", "s3:GetObject", "s3:ListBucket"],
            Resource: [arn, `${arn}/*`],
        }],
    })),
});

const ec2InstanceProfile = new aws.iam.InstanceProfile(`${prefix}-ec2-profile`, {
    name: `${prefix}-ec2-profile`,
    role: ec2Role.name,
    tags: { ...commonTags, Name: `${prefix}-ec2-profile` },
});

// ── Amazon Linux 2023 최신 AMI ────────────────────────────
const al2023Ami = aws.ec2.getAmi({
    mostRecent: true,
    owners: ["amazon"],
    filters: [
        { name: "name", values: ["al2023-ami-2023.*-x86_64"] },
        { name: "architecture", values: ["x86_64"] },
        { name: "virtualization-type", values: ["hvm"] },
        { name: "state", values: ["available"] },
    ],
});

// ── EC2 User Data ─────────────────────────────────────────
const userData = `#!/bin/bash
set -e

dnf update -y

# Docker 설치
dnf install -y docker
systemctl enable docker
systemctl start docker
usermod -aG docker ec2-user
chmod 666 /var/run/docker.sock

# cron (MySQL 백업 스케줄링용, AL2023 기본 미설치)
dnf install -y cronie
systemctl enable --now crond

# Docker Compose v2 CLI 플러그인 설치
mkdir -p /usr/local/lib/docker/cli-plugins
curl -SL "https://github.com/docker/compose/releases/download/v2.29.7/docker-compose-linux-x86_64" \\
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# 작업 디렉토리 생성
mkdir -p /home/ec2-user/readygsm/nginx
mkdir -p /home/ec2-user/readygsm/backup
chown -R ec2-user:ec2-user /home/ec2-user/readygsm

echo "ReadyGSM production server bootstrap complete" >> /var/log/readygsm-init.log
`;

// ── EC2 인스턴스 ──────────────────────────────────────────
const ec2 = new aws.ec2.Instance(`${prefix}-ec2`, {
    ami: al2023Ami.then((ami) => ami.id),
    instanceType: instanceType,
    subnetId: publicSubnet.id,
    vpcSecurityGroupIds: [sg.id],
    keyName: keyPairName,
    iamInstanceProfile: ec2InstanceProfile.name,
    userData: userData,
    rootBlockDevice: {
        volumeType: "gp3",
        volumeSize: 30,
        deleteOnTermination: true,
        encrypted: true,
    },
    metadataOptions: {
        httpEndpoint: "enabled",
        httpTokens: "required", // IMDSv2 강제
    },
    tags: { ...commonTags, Name: `${prefix}-ec2` },
}, { ignoreChanges: ["ami", "rootBlockDevice", "userData"] });

// ── Elastic IP ────────────────────────────────────────────
const eip = new aws.ec2.Eip(`${prefix}-eip`, {
    domain: "vpc",
    tags: { ...commonTags, Name: `${prefix}-eip` },
});

new aws.ec2.EipAssociation(`${prefix}-eip-assoc`, {
    instanceId: ec2.id,
    allocationId: eip.id,
});

// ── ECR Repository ────────────────────────────────────────
const ecrRepo = new aws.ecr.Repository(`${prefix}-app`, {
    name: `${prefix}-app`,
    imageTagMutability: "MUTABLE",
    imageScanningConfiguration: {
        scanOnPush: true,
    },
    forceDelete: true,
    tags: { ...commonTags, Name: `${prefix}-app` },
});

new aws.ecr.LifecyclePolicy(`${prefix}-app-lifecycle`, {
    repository: ecrRepo.name,
    policy: JSON.stringify({
        rules: [
            {
                rulePriority: 1,
                description: "Keep last 5 images",
                selection: {
                    tagStatus: "any",
                    countType: "imageCountMoreThan",
                    countNumber: 5,
                },
                action: { type: "expire" },
            },
        ],
    }),
});

// ── Route 53 A 레코드 (api.ready.hellogsm.kr) ────────────────
const hostedZone = aws.route53.getZone({
    name: hostedZoneName,
    privateZone: false,
});

new aws.route53.Record(`${prefix}-dns`, {
    zoneId: hostedZone.then((hz) => hz.zoneId),
    name: appDomainName,
    type: "A",
    ttl: dnsTtl,
    records: [eip.publicIp],
});

// ── Vercel 프론트엔드(client/admin) CNAME 레코드는 Route53에 수동 등록되어
// 상시 유지되므로 Pulumi가 관리하지 않는다.

// ── Outputs ───────────────────────────────────────────────
export const vpcId = vpc.id;
export const ec2InstanceId = ec2.id;
export const ec2PublicIp = eip.publicIp;
export const ecrRepositoryUrl = ecrRepo.repositoryUrl;
export const ecrRepositoryName = ecrRepo.name;
export const appDomain = appDomainName;
export const backupBucketName = backupBucket.id;