#!/bin/bash

echo "Initializing LocalStack S3 buckets..."

# Wait for LocalStack to be ready with timeout
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
    if curl -f -s http://localhost:4566/_localstack/health > /dev/null 2>&1; then
        echo "LocalStack is ready!"
        break
    fi
    echo "Waiting for LocalStack to be ready... (attempt $((attempt+1))/$max_attempts)"
    sleep 2
    attempt=$((attempt+1))
done

if [ $attempt -eq $max_attempts ]; then
    echo "LocalStack failed to become ready after $max_attempts attempts"
    exit 1
fi

# Check if staging-bucket exists, if not create it
echo "Checking if S3 bucket 'staging-bucket' exists..."
if curl -f -s http://localhost:4566/staging-bucket > /dev/null 2>&1; then
    echo "S3 bucket 'staging-bucket' already exists"
else
    echo "Creating S3 bucket 'staging-bucket'..."
    curl -X PUT http://localhost:4566/staging-bucket > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "S3 bucket 'staging-bucket' created successfully"
    else
        echo "Failed to create S3 bucket 'staging-bucket'"
        exit 1
    fi
fi

# Check if permanent-bucket exists, if not create it
echo "Checking if S3 bucket 'permanent-bucket' exists..."
if curl -f -s http://localhost:4566/permanent-bucket > /dev/null 2>&1; then
    echo "S3 bucket 'permanent-bucket' already exists"
else
    echo "Creating S3 bucket 'permanent-bucket'..."
    curl -X PUT http://localhost:4566/permanent-bucket > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "S3 bucket 'permanent-bucket' created successfully"
    else
        echo "Failed to create S3 bucket 'permanent-bucket'"
        exit 1
    fi
fi

echo "LocalStack initialization complete"