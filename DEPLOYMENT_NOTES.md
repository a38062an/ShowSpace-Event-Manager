# Deployment Notes

## Can This Be Hosted on Vercel?

**Short Answer: No, not directly.**

Vercel is optimized for frontend frameworks (Next.js, React, Vue, etc.) and serverless functions. This project is a traditional Spring Boot Java application with the following requirements:

### Why Vercel Won't Work:

1. **Java Runtime**: Vercel doesn't support Java applications. It's designed for Node.js, Python, Go, and Ruby serverless functions.

2. **Persistent Server**: Spring Boot runs as a long-lived server process, not serverless functions.

3. **Database**: This app uses H2 file-based database which requires persistent storage. Vercel's filesystem is ephemeral.

4. **Build System**: Requires Maven and Java 17+ compilation, which Vercel doesn't support.

## Recommended Hosting Options

### Best Options for This Project:

#### 1. Heroku (Easiest)
- Native Java/Maven support
- Free tier available
- PostgreSQL addon for production database
- Simple deployment: `git push heroku main`
- Configuration: Just add a `Procfile`

#### 2. Railway.app
- Modern Heroku alternative
- Automatic Java detection
- Generous free tier
- PostgreSQL included
- GitHub integration

#### 3. Render
- Free tier for web services
- Native Spring Boot support
- PostgreSQL database included
- Auto-deploy from GitHub

#### 4. AWS Elastic Beanstalk
- Scalable and production-ready
- Pay-as-you-go pricing
- Supports Java applications natively
- RDS for database

#### 5. Google Cloud Platform (App Engine)
- Java Standard Environment
- Scales automatically
- Free tier available
- Cloud SQL for database

#### 6. Docker + Any Cloud
- Dockerize the application
- Deploy to: DigitalOcean, AWS ECS, Google Cloud Run, Azure Container Instances
- Most flexible option

### Quick Start for Heroku:

```bash
# 1. Create Procfile
echo "web: java -jar target/showspace-1.0.0.jar --server.port=\$PORT" > Procfile

# 2. Add PostgreSQL to pom.xml (for production)
# 3. Create application-production.properties for PostgreSQL config
# 4. Deploy
heroku create showspace-app
heroku addons:create heroku-postgresql:mini
git push heroku main
```

### Quick Start for Railway:

```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login and deploy
railway login
railway init
railway up
```

## For Local Development

Continue using:
```bash
mvn spring-boot:run
```

Access at http://localhost:8080

## Production Considerations

Before deploying to any platform:

1. **Switch from H2 to PostgreSQL/MySQL**
   - H2 is for development only
   - Add PostgreSQL driver to pom.xml
   - Update application.properties

2. **Environment Variables**
   - Move API keys to environment variables
   - Don't commit secrets to Git
   - Use platform-specific config management

3. **Security Enhancements**
   - Change default passwords
   - Enable HTTPS only
   - Configure CORS properly
   - Add rate limiting

4. **Monitoring**
   - Add application monitoring (New Relic, Datadog)
   - Configure logging
   - Set up error tracking

## Summary

Vercel is for frontend/serverless. This is a Spring Boot backend application.

**Recommended for portfolio**: Railway.app or Render (both have good free tiers and are easy to set up).
