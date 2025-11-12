#!/bin/bash
# ShowSpace AWS Setup - Interactive Setup Script
# This script helps you prepare for deployment

set -e

echo "========================================="
echo "   ShowSpace AWS Deployment Setup"
echo "========================================="
echo ""

# Check prerequisites
echo "Checking prerequisites..."
echo ""

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed"
    echo ""
    echo "Install it with:"
    echo "  brew install awscli"
    echo ""
    exit 1
else
    echo "AWS CLI is installed ($(aws --version))"
fi

# Check Docker
if ! docker info &> /dev/null; then
    echo "Docker is not running"
    echo ""
    echo "Please start Docker Desktop and try again"
    echo ""
    exit 1
else
    echo "Docker is running"
fi

# Check AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo "AWS credentials not configured"
    echo ""
    echo "Configure with:"
    echo "  aws configure"
    echo ""
    exit 1
else
    echo "AWS credentials configured"
fi

echo ""
echo "========================================="
echo "   All Prerequisites Met!"
echo "========================================="
echo ""

# Get AWS Account Info
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export AWS_REGION=$(aws configure get region || echo "us-east-1")

echo "AWS Account ID: $AWS_ACCOUNT_ID"
echo "AWS Region: $AWS_REGION"
echo ""

# Save to env file
cat > .env.aws <<EOF
export AWS_ACCOUNT_ID=$AWS_ACCOUNT_ID
export AWS_REGION=$AWS_REGION
EOF

echo "Environment variables saved to .env.aws"
echo ""
echo "To use them in your shell:"
echo "  source aws/.env.aws"
echo ""

# Check if ECR repository exists
echo "Checking AWS resources..."
echo ""

ECR_EXISTS=$(aws ecr describe-repositories --repository-names showspace --region $AWS_REGION 2>&1 || true)
if echo "$ECR_EXISTS" | grep -q "RepositoryNotFoundException"; then
    echo "WARNING: ECR repository 'showspace' does not exist"
    echo ""
    read -p "Would you like to create it now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        aws ecr create-repository --repository-name showspace --region $AWS_REGION
    echo "ECR repository created"
    fi
else
    echo "ECR repository 'showspace' exists"
fi

echo ""

# Check ECS cluster
CLUSTER_EXISTS=$(aws ecs describe-clusters --clusters showspace-cluster --region $AWS_REGION --query 'clusters[0].status' --output text 2>&1 || echo "NONE")
if [ "$CLUSTER_EXISTS" = "ACTIVE" ]; then
    echo "ECS cluster 'showspace-cluster' exists"
else
    echo "WARNING: ECS cluster 'showspace-cluster' does not exist"
    echo ""
    read -p "Would you like to create it now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        aws ecs create-cluster --cluster-name showspace-cluster --region $AWS_REGION
    echo "ECS cluster created"
    fi
fi

echo ""

# Check CloudWatch log group
LOG_GROUP_EXISTS=$(aws logs describe-log-groups --log-group-name-prefix /ecs/showspace --region $AWS_REGION --query 'logGroups[0].logGroupName' --output text 2>&1 || echo "None")
if [ "$LOG_GROUP_EXISTS" = "/ecs/showspace" ]; then
    echo "CloudWatch log group exists"
else
    echo "WARNING: CloudWatch log group does not exist"
    echo ""
    read -p "Would you like to create it now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        aws logs create-log-group --log-group-name /ecs/showspace --region $AWS_REGION
    echo "CloudWatch log group created"
    fi
fi

echo ""
echo "========================================="
echo "   Setup Summary"
echo "========================================="
echo ""
echo "Prerequisites are installed and configured"
echo "AWS Account: $AWS_ACCOUNT_ID"
echo "AWS Region: $AWS_REGION"
echo ""
echo "Next Steps:"
echo ""
echo "1. Read the deployment guide:"
echo "   open aws/DEPLOYMENT_GUIDE.md"
echo ""
echo "2. Complete the AWS infrastructure setup (Steps 6-9 in guide):"
echo "   - VPC & Security Groups"
echo "   - Application Load Balancer"
echo "   - IAM Roles"
echo "   - EC2 Instance"
echo ""
echo "3. Update task definition:"
echo "   cd aws"
echo "   sed -i.bak \"s/<AWS_ACCOUNT_ID>/$AWS_ACCOUNT_ID/g\" ecs-task-definition.json"
echo "   sed -i.bak \"s/<AWS_REGION>/$AWS_REGION/g\" ecs-task-definition.json"
echo ""
echo "4. Build and deploy:"
echo "   ./build-and-push.sh"
echo "   ./deploy-ecs.sh"
echo ""
echo "========================================="
echo ""
