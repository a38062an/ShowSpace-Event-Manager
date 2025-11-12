#!/bin/bash
# Build and push ShowSpace Docker image to AWS ECR
# Usage: ./build-and-push.sh

set -e

# Configuration - UPDATE THESE VALUES
AWS_REGION="${AWS_REGION:-us-east-1}"
AWS_ACCOUNT_ID="${AWS_ACCOUNT_ID:-YOUR_AWS_ACCOUNT_ID}"
ECR_REPOSITORY="showspace"
IMAGE_TAG="${IMAGE_TAG:-latest}"

echo "========================================="
echo "ShowSpace Docker Build & Push to ECR"
echo "========================================="
echo "AWS Region: $AWS_REGION"
echo "AWS Account: $AWS_ACCOUNT_ID"
echo "ECR Repository: $ECR_REPOSITORY"
echo "Image Tag: $IMAGE_TAG"
echo "========================================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed. Please install it first."
    echo "Visit: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if Docker is running
if ! docker info &> /dev/null; then
    echo "Error: Docker is not running. Please start Docker Desktop.    aws ecs describe-services --cluster showspace-cluster --services showspace-service --region eu-west-2 --query 'services[0].{Running:runningCount,Pending:pendingCount}' --output table    aws ecs describe-tasks --cluster showspace-cluster --region eu-west-2 --tasks ffd1a63e619f4a38bc06e4961549c637 --query 'tasks[0].{LastStatus:lastStatus,DesiredStatus:desiredStatus,StoppedReason:stoppedReason,Containers:containers[0].{Name:name,Status:lastStatus,Reason:reason}}' --output json"
    exit 1
fi

# Check if AWS_ACCOUNT_ID is set
if [ "$AWS_ACCOUNT_ID" = "YOUR_AWS_ACCOUNT_ID" ]; then
    echo "Error: Please set AWS_ACCOUNT_ID in the script or environment variable"
    echo "Run: export AWS_ACCOUNT_ID=\$(aws sts get-caller-identity --query Account --output text)"
    exit 1
fi

echo "Prerequisites check passed"
echo ""

# Step 1: Build the Docker image
echo "Step 1: Building Docker image..."
docker build -t $ECR_REPOSITORY:$IMAGE_TAG .
if [ $? -ne 0 ]; then
    echo "Error: Docker build failed"
    exit 1
fi
echo "Docker image built successfully"
echo ""

# Step 2: Tag the image for ECR
echo "Step 2: Tagging image for ECR..."
ECR_IMAGE="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG"
docker tag $ECR_REPOSITORY:$IMAGE_TAG $ECR_IMAGE
echo "Image tagged: $ECR_IMAGE"
echo ""

# Step 3: Login to ECR
echo "Step 3: Logging into ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
if [ $? -ne 0 ]; then
    echo "Error: ECR login failed"
    exit 1
fi
echo "Logged into ECR"
echo ""

# Step 4: Push the image
echo "Step 4: Pushing image to ECR..."
docker push $ECR_IMAGE
if [ $? -ne 0 ]; then
    echo "Error: Docker push failed"
    exit 1
fi
echo "Image pushed successfully"
echo ""

echo "========================================="
echo "SUCCESS"
echo "========================================="
echo "Image: $ECR_IMAGE"
echo ""
echo "Next steps:"
echo "1. Update ECS task definition with this image"
echo "2. Deploy to ECS using: ./deploy-ecs.sh"
echo "========================================="
