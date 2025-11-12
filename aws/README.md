# AWS Deployment Files for ShowSpace

This directory contains all the necessary files and scripts to deploy ShowSpace to AWS ECS with EC2 (100% free tier for 12 months).

##  Files Overview

| File | Description |
|------|-------------|
| **DEPLOYMENT_GUIDE.md** | Complete step-by-step deployment instructions with all AWS CLI commands |
| **QUICK_REFERENCE.md** | Quick reference guide for common commands and troubleshooting |
| **ecs-task-definition.json** | ECS task definition (container specs, resources, environment) |
| **build-and-push.sh** | Automated script to build Docker image and push to ECR |
| **deploy-ecs.sh** | Automated script to deploy/update ECS service |

##  Quick Start

### First Time Setup

1. **Read the full guide**: [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)
2. **Follow steps 1-13** to set up AWS infrastructure
3. **Run deployment scripts** (steps 11-15)

### Subsequent Deployments

```bash
cd aws
./build-and-push.sh    # Build & push new Docker image
./deploy-ecs.sh        # Deploy to ECS
```

##  Cost Estimate

**100% FREE** for 12 months with new AWS account:
-  EC2 t2.micro (750 hours/month)
-  Application Load Balancer (750 hours/month)
-  500 MB ECR storage
-  5 GB CloudWatch Logs
-  100 GB data transfer

**After 12 months**: ~$15-20/month if you continue using same resources

##  Architecture

```
Internet
   ↓
Application Load Balancer (ALB)
   ↓
Target Group
   ↓
ECS Service (on EC2 t2.micro)
   ↓
Docker Container (ShowSpace)
   ↓
H2 Database (file-based in container)
```

##  Configuration Files Modified

The following project files were updated for cloud deployment:

1. **Dockerfile** - Multi-stage build with optimizations
2. **src/main/resources/application.properties** - Added environment variable support
3. **src/main/java/anthonynguyen/showspace/config/Persistence.java** - Cloud-compatible database path
4. **.dockerignore** - Optimize Docker build speed

##  Environment Variables

The application supports these environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 8080 | Application port |
| `DB_PATH` | /app/data/showspace | H2 database file path |
| `DB_USERNAME` | h2 | Database username |
| `DB_PASSWORD` | spring | Database password |
| `SPRING_PROFILE` | default | Spring Boot profile |
| `JAVA_OPTS` | -Xmx256m -Xms128m | JVM options |

##  What Gets Deployed

- **Spring Boot Application** running in Docker container
- **H2 Database** (file-based, persisted in container volume)
- **Port 8080** exposed via ALB on HTTP
- **Health checks** configured for container restarts
- **CloudWatch Logs** for monitoring

##  Resources

- [Full Deployment Guide](./DEPLOYMENT_GUIDE.md)
- [Quick Reference](./QUICK_REFERENCE.md)
- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS Free Tier](https://aws.amazon.com/free/)

##  Need Help?

Check the troubleshooting section in [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) or [QUICK_REFERENCE.md](./QUICK_REFERENCE.md).

---

**Ready to deploy? Start with [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)!** 
