
#!/bin/bash
# Deploy ShowSpace to ECS
# Usage: ./deploy-ecs.sh

set -e

# Configuration - UPDATE THESE VALUES
AWS_REGION="${AWS_REGION:-eu-west-2}"
CLUSTER_NAME="${CLUSTER_NAME:-default}"
SERVICE_NAME="${SERVICE_NAME:-showspace-service}"
TASK_FAMILY="showspace-task"

echo "========================================="
echo "ShowSpace ECS Deployment"
echo "========================================="
echo "AWS Region: $AWS_REGION"
echo "ECS Cluster: $CLUSTER_NAME"
echo "ECS Service: $SERVICE_NAME"
echo "Task Family: $TASK_FAMILY"
echo "========================================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    exit 1
fi

# Step 1: Register new task definition
echo "Step 1: Registering new task definition..."
TASK_DEFINITION=$(aws ecs register-task-definition \
    --cli-input-json file://ecs-task-definition.json \
    --region $AWS_REGION \
    --query 'taskDefinition.taskDefinitionArn' \
    --output text)

if [ $? -ne 0 ]; then
    echo "Error: Failed to register task definition"
    exit 1
fi
echo "Task definition registered: $TASK_DEFINITION"
echo ""

# Step 1.5: Create ECS service if it does not exist
echo "Checking if ECS service $SERVICE_NAME exists..."
SERVICE_EXISTS=$(aws ecs describe-services \
    --cluster $CLUSTER_NAME \
    --services $SERVICE_NAME \
    --region $AWS_REGION \
    --query 'services[0].status' \
    --output text 2>/dev/null)

if [ "$SERVICE_EXISTS" = "ACTIVE" ]; then
    echo "ECS service $SERVICE_NAME already exists. Skipping creation."
else
    echo "ECS service $SERVICE_NAME not found. Creating service..."
    aws ecs create-service \
        --cluster $CLUSTER_NAME \
        --service-name $SERVICE_NAME \
        --task-definition $TASK_FAMILY \
        --desired-count 1 \
        --launch-type EC2 \
        --region $AWS_REGION \
        #--network-configuration "awsvpcConfiguration={subnets=[subnet-0e581170aa1002846],securityGroups=[sg-07c241576905d2971],assignPublicIp=ENABLED}" \
        --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:eu-west-2:124878108241:targetgroup/showspace-tg/733189982439039f,containerName=showspace-container,containerPort=8080" \
        > /dev/null
    if [ $? -ne 0 ]; then
        echo "Error: Failed to create ECS service. Please check your subnet, security group, and target group values."
        exit 1
    fi
    echo "ECS service $SERVICE_NAME created."
    echo ""
fi

# Step 2: Update ECS service
echo "Step 2: Updating ECS service..."
aws ecs update-service \
    --cluster $CLUSTER_NAME \
    --service $SERVICE_NAME \
    --task-definition $TASK_FAMILY \
    --force-new-deployment \
    --region $AWS_REGION \
    > /dev/null
if [ $? -ne 0 ]; then
    echo "Error: Failed to update ECS service"
    exit 1
fi
echo "ECS service updated"
echo ""

# Step 3: Wait for service to stabilize
echo "Step 3: Waiting for service to stabilize (this may take a few minutes)..."
aws ecs wait services-stable \
    --cluster $CLUSTER_NAME \
    --services $SERVICE_NAME \
    --region $AWS_REGION

if [ $? -ne 0 ]; then
    echo "Warning: Service deployment may still be in progress. Check ECS console for status."
else
    echo "Service is stable"
fi
echo ""

# Step 4: Get service details
echo "Step 4: Getting service details..."
ALB_DNS=$(aws elbv2 describe-load-balancers \
    --region $AWS_REGION \
    --query "LoadBalancers[?contains(LoadBalancerName, 'showspace')].DNSName" \
    --output text 2>/dev/null || echo "Not found")

echo ""
echo "========================================="
echo "DEPLOYMENT COMPLETE"
echo "========================================="
if [ "$ALB_DNS" != "Not found" ] && [ ! -z "$ALB_DNS" ]; then
    echo "Application URL: http://$ALB_DNS"
else
    echo "Get your ALB URL from AWS Console or run:"
    echo "   aws elbv2 describe-load-balancers --region $AWS_REGION"
fi
echo ""
echo "Check service status:"
echo "   aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION"
echo ""
echo "View logs:"
echo "   aws logs tail /ecs/showspace --follow --region $AWS_REGION"
echo "========================================"
echo "   aws logs tail /ecs/showspace --follow --region $AWS_REGION"
echo "========================================="
