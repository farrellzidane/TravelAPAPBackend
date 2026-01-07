#!/bin/sh

# Convert Render's postgres:// or postgresql:// URL to JDBC format if needed
if [ -n "$DATABASE_URL" ]; then
  # Check if DATABASE_URL starts with postgres:// or postgresql://
  if echo "$DATABASE_URL" | grep -q "^postgres://"; then
    # Convert postgres:// to jdbc:postgresql://
    export JDBC_DATABASE_URL=$(echo "$DATABASE_URL" | sed 's/^postgres:/jdbc:postgresql:/')
    echo "Converted postgres:// to JDBC format"
  elif echo "$DATABASE_URL" | grep -q "^postgresql://"; then
    # Convert postgresql:// to jdbc:postgresql://
    export JDBC_DATABASE_URL=$(echo "$DATABASE_URL" | sed 's/^postgresql:/jdbc:postgresql:/')
    echo "Converted postgresql:// to JDBC format"
  else
    # If already in correct format, use as is
    export JDBC_DATABASE_URL="$DATABASE_URL"
  fi
fi

# Start the application with the configured port
exec java -jar app.jar --server.port=${PORT:-10000}
