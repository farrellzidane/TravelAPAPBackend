#!/bin/sh

# Convert Render's postgres:// or postgresql:// URL to JDBC format
if [ -n "$DATABASE_URL" ]; then
  if echo "$DATABASE_URL" | grep -qE "^(postgres|postgresql)://"; then
    # Extract host, port, and database from URL
    # Remove protocol
    TEMP_URL=$(echo "$DATABASE_URL" | sed -E 's#^(postgres|postgresql)://##')
    
    # Extract credentials if present (user:pass@)
    if echo "$TEMP_URL" | grep -q "@"; then
      # Extract username
      if [ -z "$DATABASE_USERNAME" ]; then
        export DATABASE_USERNAME=$(echo "$TEMP_URL" | cut -d':' -f1)
      fi
      
      # Extract password (between first : and @)
      if [ -z "$DATABASE_PASSWORD" ]; then
        export DATABASE_PASSWORD=$(echo "$TEMP_URL" | sed 's/^[^:]*://' | cut -d'@' -f1)
      fi
      
      # Remove credentials part to get host/port/db
      HOST_PART=$(echo "$TEMP_URL" | sed 's/^.*@//')
    else
      HOST_PART="$TEMP_URL"
    fi
    
    # Add default port if not present
    if ! echo "$HOST_PART" | grep -q ":"; then
      HOST_PART=$(echo "$HOST_PART" | sed 's#/#:5432/#')
    fi
    
    # Construct JDBC URL
    export JDBC_DATABASE_URL="jdbc:postgresql://$HOST_PART"
    echo "Converted to JDBC format: jdbc:postgresql://$HOST_PART"
  else
    # Already in JDBC format
    export JDBC_DATABASE_URL="$DATABASE_URL"
  fi
fi

# Start the application with the configured port
exec java -jar app.jar --server.port=${PORT:-10000}
