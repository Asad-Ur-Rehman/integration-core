#!/bin/bash
set -x
set -e

export INTEGRATION_DB_HOST=127.0.0.1
export INTEGRATION_DB_PORT=5432
export INTEGRATION_DB_NAME=test
export INTEGRATION_DB_USER=signalvine
export INTEGRATION_DB_PASSWORD=signalvine
