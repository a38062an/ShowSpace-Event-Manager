#!/bin/bash
# Launch an ECS-optimized EC2 instance for your ECS cluster

AWS_REGION="eu-west-2"
AMI_ID=$(aws ssm get-parameters --names /aws/service/ecs/optimized-ami/amazon-linux-2/recommended/image_id --region $AWS_REGION --query 'Parameters[0].Value' --output text)
INSTANCE_TYPE="t3.micro"
KEY_NAME="a38062an  "         # Replace with your EC2 key pair name
SECURITY_GROUP="sg-07c241576905d2971"         # Replace with your security group ID
SUBNET_ID="subnet-0f9057738216894ae"          # Replace with your subnet ID
IAM_PROFILE="ecsInstanceRole"        # Ensure this IAM role exists

# User data to join the ECS cluster (defaults to defeault cluster)
CLUSTER_NAME="showspace-cluster"
ER_DATA="#!/bin/bash
echo ECS_CLUSTER=showspace-cluster >> /etc/ecs/ecs.config"

aws ec2 run-instances \
  --image-id $AMI_ID \
  --count 1 \
  --instance-type $INSTANCE_TYPE \
  --key-name $KEY_NAME \
  --security-group-ids $SECURITY_GROUP \
  --subnet-id $SUBNET_ID \
  --iam-instance-profile Name=$IAM_PROFILE \
  --user-data "$USER_DATA" \
  --region $AWS_REGION
