# AWS ECS Deployment Guide for ShowSpace

## 100% FREE Tier Deployment (12 Months)

This guide walks you through deploying ShowSpace to AWS ECS using EC2 t2.micro instances (completely free for 12 months).

---

## Prerequisites

1. **AWS Account** (with free tier available)
2. **AWS CLI** installed and configured
3. **Docker Desktop** installed and running
4. **Git** (to clone/manage the repository)

---

## Step-by-Step Deployment Instructions

### Step 1: Install & Configure AWS CLI

#### Install AWS CLI (macOS)

```bash
# Using Homebrew
brew install awscli

# Or download from AWS
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg AWSCLIV2.pkg -target /
```

#### Configure AWS CLI

```bash
aws configure
```

Enter your:

- **AWS Access Key ID** (from IAM Console)
- **AWS Secret Access Key** (from IAM Console)
- **Default region**: `us-east-1` (or your preferred region)
- **Default output format**: `json`

#### Verify Installation

```bash
aws --version
aws sts get-caller-identity  # Should show your account info
```

---

### Step 2: Set Environment Variables

```bash
# Get your AWS Account ID
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export AWS_REGION=us-east-1

# Verify
echo "AWS Account ID: $AWS_ACCOUNT_ID"
echo "AWS Region: $AWS_REGION"
```

---

### Step 3: Create ECR Repository

Amazon ECR (Elastic Container Registry) stores your Docker images.

```bash
# Create the repository
aws ecr create-repository \
    --repository-name showspace \
    --region $AWS_REGION

# Expected output: Repository ARN and URI
```

**Free Tier Limit**: 500 MB storage for 12 months

---

### Step 4: Create CloudWatch Log Group

This is for ECS container logs (free tier: 5GB ingestion, 5GB storage).

```bash
aws logs create-log-group \
    --log-group-name /ecs/showspace \
    --region $AWS_REGION
```

---

### Step 5: Create ECS Cluster (EC2 Type)

```bash
# Create ECS cluster
aws ecs create-cluster \
    --cluster-name showspace-cluster \
    --region $AWS_REGION
```

---

### Step 6: Set Up VPC and Security (If Using Default VPC)

#### Get Default VPC ID

```bash
export VPC_ID=$(aws ec2 describe-vpcs \
    --filters "Name=isDefault,Values=true" \
    --query "Vpcs[0].VpcId" \
    --output text \
    --region $AWS_REGION)

echo "VPC ID: $VPC_ID"
```

#### Get Subnet IDs

```bash
# Get subnet IDs as an array (ALB requires at least 2 subnets in different AZs)
SUBNET_ARRAY=($(aws ec2 describe-subnets \
    --filters "Name=vpc-id,Values=$VPC_ID" \
    --query "Subnets[*].SubnetId" \
    --output text \
    --region $AWS_REGION))

echo "Found ${#SUBNET_ARRAY[@]} subnets: ${SUBNET_ARRAY[@]}"

# Verify you have at least 2 subnets in different availability zones
aws ec2 describe-subnets \
    --filters "Name=vpc-id,Values=$VPC_ID" \
    --query "Subnets[*].[SubnetId,AvailabilityZone]" \
    --output table \
    --region $AWS_REGION
```

#### Create Security Group for ALB

```bash
export ALB_SG_ID=$(aws ec2 create-security-group \
    --group-name showspace-alb-sg \
    --description "Security group for ShowSpace ALB" \
    --vpc-id $VPC_ID \
    --region $AWS_REGION \
    --query 'GroupId' \
    --output text)

echo "ALB Security Group ID: $ALB_SG_ID"

# Allow HTTP traffic to ALB
aws ec2 authorize-security-group-ingress \
    --group-id $ALB_SG_ID \
    --protocol tcp \
    --port 80 \
    --cidr 0.0.0.0/0 \
    --region $AWS_REGION
```

#### Create Security Group for ECS Instances

```bash
export ECS_SG_ID=$(aws ec2 create-security-group \
    --group-name showspace-ecs-sg \
    --description "Security group for ShowSpace ECS instances" \
    --vpc-id $VPC_ID \
    --region $AWS_REGION \
    --query 'GroupId' \
    --output text)

echo "ECS Security Group ID: $ECS_SG_ID"

# Allow traffic from ALB to ECS instances on all ports
aws ec2 authorize-security-group-ingress \
    --group-id $ECS_SG_ID \
    --protocol tcp \
    --port 0-65535 \
    --source-group $ALB_SG_ID \
    --region $AWS_REGION

# Allow SSH (optional, for debugging)
aws ec2 authorize-security-group-ingress \
    --group-id $ECS_SG_ID \
    --protocol tcp \
    --port 22 \
    --cidr 0.0.0.0/0 \
    --region $AWS_REGION
```

---

### Step 7: Create Application Load Balancer (ALB)

```bash
# Create ALB (using array expansion to properly pass multiple subnet IDs)
export ALB_ARN=$(aws elbv2 create-load-balancer \
    --name showspace-alb \
    --subnets ${SUBNET_ARRAY[@]} \
    --security-groups $ALB_SG_ID \
    --region $AWS_REGION \
    --query 'LoadBalancers[0].LoadBalancerArn' \
    --output text)

echo "ALB ARN: $ALB_ARN"

# Get ALB DNS name (this is your application URL)
export ALB_DNS=$(aws elbv2 describe-load-balancers \
    --load-balancer-arns $ALB_ARN \
    --region $AWS_REGION \
    --query 'LoadBalancers[0].DNSName' \
    --output text)

echo " Your application will be available at: http://$ALB_DNS"
```

#### Create Target Group

```bash
export TG_ARN=$(aws elbv2 create-target-group \
    --name showspace-tg \
    --protocol HTTP \
    --port 80 \
    --vpc-id $VPC_ID \
    --target-type instance \
    --health-check-path / \
    --health-check-interval-seconds 30 \
    --health-check-timeout-seconds 5 \
    --healthy-threshold-count 2 \
    --unhealthy-threshold-count 3 \
    --region $AWS_REGION \
    --query 'TargetGroups[0].TargetGroupArn' \
    --output text)

echo "Target Group ARN: $TG_ARN"
```

#### Create ALB Listener

```bash
aws elbv2 create-listener \
    --load-balancer-arn $ALB_ARN \
    --protocol HTTP \
    --port 80 \
    --default-actions Type=forward,TargetGroupArn=$TG_ARN \
    --region $AWS_REGION
```

**Free Tier Limit**: 750 hours/month, 15 GB data processing for 12 months

---

### Step 8: Create IAM Roles

#### ECS Task Execution Role

```bash
# Create trust policy
cat > ecs-task-execution-trust-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create role
aws iam create-role \
    --role-name ecsTaskExecutionRole \
    --assume-role-policy-document file://ecs-task-execution-trust-policy.json

# Attach managed policy
aws iam attach-role-policy \
    --role-name ecsTaskExecutionRole \
    --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

#### EC2 Instance Role for ECS

```bash
# Create trust policy for EC2
cat > ecs-instance-trust-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create role
aws iam create-role \
    --role-name ecsInstanceRole \
    --assume-role-policy-document file://ecs-instance-trust-policy.json

# Attach managed policies
aws iam attach-role-policy \
    --role-name ecsInstanceRole \
    --policy-arn arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role

# Create instance profile
aws iam create-instance-profile \
    --instance-profile-name ecsInstanceRole

# Add role to instance profile
aws iam add-role-to-instance-profile \
    --instance-profile-name ecsInstanceRole \
    --role-name ecsInstanceRole

# Wait for propagation
echo "Waiting 10 seconds for IAM role propagation..."
sleep 10
```

---

### Step 9: Launch EC2 Instance for ECS

```bash
# Get latest ECS-optimized AMI ID
export ECS_AMI=$(aws ssm get-parameters \
    --names /aws/service/ecs/optimized-ami/amazon-linux-2/recommended/image_id \
    --region $AWS_REGION \
    --query "Parameters[0].Value" \
    --output text)

echo "ECS AMI: $ECS_AMI"

# Create user data script
cat > user-data.txt <<EOF
#!/bin/bash
echo ECS_CLUSTER=showspace-cluster >> /etc/ecs/ecs.config
echo ECS_ENABLE_TASK_IAM_ROLE=true >> /etc/ecs/ecs.config
EOF

# Launch t2.micro instance (FREE TIER)
export INSTANCE_ID=$(aws ec2 run-instances \
    --image-id $ECS_AMI \
    --instance-type t2.micro \
    --iam-instance-profile Name=ecsInstanceRole \
    --security-group-ids $ECS_SG_ID \
    --user-data file://user-data.txt \
    --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=showspace-ecs-instance}]" \
    --region $AWS_REGION \
    --query 'Instances[0].InstanceId' \
    --output text)

echo "EC2 Instance ID: $INSTANCE_ID"
echo "Waiting for instance to start..."
aws ec2 wait instance-running --instance-ids $INSTANCE_ID --region $AWS_REGION
echo " Instance is running"
```

**Free Tier Limit**: 750 hours/month of t2.micro for 12 months

---

### Step 10: Update Task Definition with Your Values

```bash
cd aws

# Update task definition with your AWS account ID and region
sed -i.bak "s/<AWS_ACCOUNT_ID>/$AWS_ACCOUNT_ID/g" ecs-task-definition.json
sed -i.bak "s/<AWS_REGION>/$AWS_REGION/g" ecs-task-definition.json

# Verify changes
cat ecs-task-definition.json
```

---

### Step 11: Build and Push Docker Image

```bash
# Make scripts executable
chmod +x build-and-push.sh deploy-ecs.sh

# Build and push to ECR
./build-and-push.sh
```

This will:

1. Build your Docker image
2. Tag it for ECR
3. Login to ECR
4. Push the image

**Build time**: 5-10 minutes (multi-stage build compiles from source)

---

### Step 12: Register Task Definition

```bash
aws ecs register-task-definition \
    --cli-input-json file://ecs-task-definition.json \
    --region $AWS_REGION
```

---

### Step 13: Create ECS Service

```bash
aws ecs create-service \
    --cluster showspace-cluster \
    --service-name showspace-service \
    --task-definition showspace-task \
    --desired-count 1 \
    --launch-type EC2 \
    --load-balancers targetGroupArn=$TG_ARN,containerName=showspace-container,containerPort=8080 \
    --scheduling-strategy REPLICA \
    --region $AWS_REGION
```

---

### Step 14: Wait for Deployment

```bash
echo " Waiting for service to become stable (this takes 2-5 minutes)..."
aws ecs wait services-stable \
    --cluster showspace-cluster \
    --services showspace-service \
    --region $AWS_REGION

echo " Service is stable!"
```

---

### Step 15: Access Your Application

```bash
echo " ShowSpace is now deployed!"
echo " Application URL: http://$ALB_DNS"
echo ""
echo "Default admin credentials:"
echo "  Username: Rob"
echo "  Password: Haines"
```

Open the URL in your browser and test the application!

---

## Making Updates (After Initial Deployment)

### Update Code and Redeploy

```bash
# 1. Make your code changes

# 2. Build and push new image
cd aws
./build-and-push.sh

# 3. Deploy to ECS
./deploy-ecs.sh
```

---

## Monitoring & Troubleshooting

### View Service Status

```bash
aws ecs describe-services \
    --cluster showspace-cluster \
    --services showspace-service \
    --region $AWS_REGION
```

### View Container Logs

```bash
# Tail logs in real-time
aws logs tail /ecs/showspace --follow --region $AWS_REGION

# Get recent logs
aws logs tail /ecs/showspace --since 1h --region $AWS_REGION
```

### Check Task Status

```bash
aws ecs list-tasks \
    --cluster showspace-cluster \
    --service-name showspace-service \
    --region $AWS_REGION
```

### View ALB Target Health

```bash
aws elbv2 describe-target-health \
    --target-group-arn $TG_ARN \
    --region $AWS_REGION
```

---

## Cost Monitoring (Free Tier)

### Check What's Free

- **EC2 t2.micro**: 750 hours/month (1 instance 24/7)
- **ALB**: 750 hours/month + 15 GB processing
- **ECR**: 500 MB storage
- **CloudWatch Logs**: 5 GB ingestion + 5 GB storage
- **Data Transfer**: 100 GB/month outbound

### Monitor Usage

```bash
# Check EC2 running time
aws ec2 describe-instances \
    --instance-ids $INSTANCE_ID \
    --query "Reservations[0].Instances[0].[LaunchTime,State.Name]" \
    --region $AWS_REGION
```

**Set up billing alerts** in AWS Console → Billing → Budgets (to avoid unexpected charges)

---

## Cleanup (Delete All Resources)

If you want to stop everything and delete all resources:

```bash
# Delete ECS service
aws ecs update-service \
    --cluster showspace-cluster \
    --service showspace-service \
    --desired-count 0 \
    --region $AWS_REGION

aws ecs delete-service \
    --cluster showspace-cluster \
    --service showspace-service \
    --force \
    --region $AWS_REGION

# Terminate EC2 instance
aws ec2 terminate-instances \
    --instance-ids $INSTANCE_ID \
    --region $AWS_REGION

# Delete ALB and target group
aws elbv2 delete-load-balancer \
    --load-balancer-arn $ALB_ARN \
    --region $AWS_REGION

# Wait for ALB deletion
sleep 30

aws elbv2 delete-target-group \
    --target-group-arn $TG_ARN \
    --region $AWS_REGION

# Delete security groups
aws ec2 delete-security-group --group-id $ALB_SG_ID --region $AWS_REGION
aws ec2 delete-security-group --group-id $ECS_SG_ID --region $AWS_REGION

# Delete ECS cluster
aws ecs delete-cluster \
    --cluster showspace-cluster \
    --region $AWS_REGION

# Delete ECR repository
aws ecr delete-repository \
    --repository-name showspace \
    --force \
    --region $AWS_REGION

# Delete CloudWatch log group
aws logs delete-log-group \
    --log-group-name /ecs/showspace \
    --region $AWS_REGION

echo " All resources deleted"
```

---

## Common Issues

### Issue: Container keeps restarting

**Solution**: Check logs for errors

```bash
aws logs tail /ecs/showspace --follow --region $AWS_REGION
```

### Issue: ALB shows "unhealthy"

**Solution**:

1. Check security groups allow ALB → ECS traffic
2. Verify health check path is correct
3. Ensure container is listening on port 8080

### Issue: Can't access application

**Solution**:

1. Check ALB DNS: `echo $ALB_DNS`
2. Verify target health: `aws elbv2 describe-target-health --target-group-arn $TG_ARN`
3. Check ECS task is running: `aws ecs list-tasks --cluster showspace-cluster`

### Issue: Docker build fails

**Solution**: Ensure you have Maven and sufficient disk space. Build might take 5-10 minutes.

---

## Additional Resources

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS Free Tier Details](https://aws.amazon.com/free/)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot on AWS](https://spring.io/guides/gs/spring-boot-docker/)

---

## Next Steps

1. Set up custom domain with Route 53
2. Enable HTTPS with AWS Certificate Manager (ACM)
3. Configure auto-scaling
4. Set up CI/CD with GitHub Actions
5. Monitor with CloudWatch dashboards

---

**Happy Deploying!**
