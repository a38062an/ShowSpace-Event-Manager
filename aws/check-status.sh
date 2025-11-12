#!/bin/bash
# Check ShowSpace deployment status
# Usage: ./check-status.sh

# Configuration - UPDATE THESE VALUES
AWS_REGION="${AWS_REGION:-eu-west-2}"
CLUSTER_NAME="${CLUSTER_NAME:-showspace-cluster}"
SERVICE_NAME="${SERVICE_NAME:-showspace-service}"

echo "========================================="
echo "ShowSpace Deployment Status Check"
echo "========================================="
echo "AWS Region: $AWS_REGION"
echo "ECS Cluster: $CLUSTER_NAME"
echo "ECS Service: $SERVICE_NAME"
echo "========================================="
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed."
    exit 1
fi

# 1. Check ECS Service Status
echo "1. Checking ECS Service Status..."
SERVICE_STATUS=$(aws ecs describe-services \
    --cluster $CLUSTER_NAME \
    --services $SERVICE_NAME \
    --region $AWS_REGION \
    --query 'services[0].{Status:status,Running:runningCount,Desired:desiredCount,Pending:pendingCount}' \
    --output json 2>/dev/null || echo "{}")

if [ "$SERVICE_STATUS" = "{}" ]; then
    echo "   ERROR: Service not found or unable to query"
    echo "   Check if service exists in region $AWS_REGION"
    exit 1
else
    echo "$SERVICE_STATUS" | jq '.'
fi
echo ""

# 2. Check Running Tasks
echo "2. Checking Running Tasks..."
TASKS=$(aws ecs list-tasks \
    --cluster $CLUSTER_NAME \
    --service-name $SERVICE_NAME \
    --region $AWS_REGION \
    --output text --query 'taskArns[]' 2>/dev/null || echo "")

if [ -z "$TASKS" ]; then
    echo "   No tasks running!"
    echo "   Service might be stopped or failed to start"
else
    echo "   Found $(echo $TASKS | wc -w | tr -d ' ') task(s)"
    for TASK in $TASKS; do
        TASK_ID=$(echo $TASK | awk -F'/' '{print $NF}')
        echo "   - Task: $TASK_ID"
        
        # Get task details
        TASK_DETAILS=$(aws ecs describe-tasks \
            --cluster $CLUSTER_NAME \
            --tasks $TASK \
            --region $AWS_REGION \
            --query 'tasks[0].{LastStatus:lastStatus,DesiredStatus:desiredStatus,HealthStatus:healthStatus,StoppedReason:stoppedReason}' \
            --output json 2>/dev/null || echo "{}")
        
        echo "$TASK_DETAILS" | jq '.'
    done
fi
echo ""

# 3. Check ALB Target Health
echo "3. Checking ALB Target Health..."
ALB_ARN=$(aws elbv2 describe-load-balancers \
    --region $AWS_REGION \
    --query "LoadBalancers[?contains(LoadBalancerName, 'showspace')].LoadBalancerArn" \
    --output text 2>/dev/null || echo "")

if [ -z "$ALB_ARN" ]; then
    echo "   WARNING: No ALB found with 'showspace' in name"
else
    echo "   ALB ARN: $ALB_ARN"
    
    # Get target groups
    TG_ARN=$(aws elbv2 describe-target-groups \
        --region $AWS_REGION \
        --query "TargetGroups[?contains(TargetGroupName, 'showspace')].TargetGroupArn" \
        --output text 2>/dev/null | head -1 || echo "")
    
    if [ -z "$TG_ARN" ]; then
        echo "   WARNING: No target group found"
    else
        echo "   Target Group: $TG_ARN"
        echo ""
        echo "   Target Health Status:"
        aws elbv2 describe-target-health \
            --target-group-arn $TG_ARN \
            --region $AWS_REGION \
            --query 'TargetHealthDescriptions[].[Target.Id,TargetHealth.State,TargetHealth.Reason,TargetHealth.Description]' \
            --output table 2>/dev/null || echo "   Unable to query target health"
    fi
fi
echo ""

# 4. Get ALB DNS Name
echo "4. Application URL:"
ALB_DNS=$(aws elbv2 describe-load-balancers \
    --region $AWS_REGION \
    --query "LoadBalancers[?contains(LoadBalancerName, 'showspace')].DNSName" \
    --output text 2>/dev/null || echo "Not found")

if [ "$ALB_DNS" != "Not found" ] && [ ! -z "$ALB_DNS" ]; then
    echo "   http://$ALB_DNS"
else
    echo "   ALB DNS name not found"
fi
echo ""

# 5. Check Recent Logs
echo "5. Recent Log Errors (last 50 lines):"
aws logs tail /ecs/showspace \
    --region $AWS_REGION \
    --since 10m \
    --format short 2>/dev/null | tail -20 || echo "   Unable to retrieve logs"
echo ""

# 6. Summary
echo "========================================="
echo "Summary"
echo "========================================="
echo "If you see 503 errors, likely causes:"
echo "  1. Service is not running (check task count)"
echo "  2. Tasks are failing to start (check task status)"
echo "  3. Health checks are failing (check target health)"
echo "  4. App is still starting up (takes 7-8 minutes)"
echo ""
echo "If you see 404 errors:"
echo "  1. ALB DNS might be wrong"
echo "  2. Target group might not be routing correctly"
echo ""
echo "To view full logs:"
echo "  aws logs tail /ecs/showspace --follow --region $AWS_REGION"
echo "========================================="

