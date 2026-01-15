#!/usr/bin/env bash

#
# Copied into .github/workflows/ via update_workflows.sh and executed in the repository to be updated.
#

set -euo pipefail

# clone the workflows repository into a temporary directory
WORKFLOW_REPO_CLONED_PATH=$(mktemp -d)

# cleanup temp directory on exit
trap 'rm -rf "$WORKFLOW_REPO_CLONED_PATH"' EXIT

git clone https://github.com/Hapag-Lloyd/Workflow-Templates.git "$WORKFLOW_REPO_CLONED_PATH"

TOP_LEVEL_DIR=$(cd "$(git rev-parse --show-toplevel)" && pwd)

# do the job and pass all arguments
"$WORKFLOW_REPO_CLONED_PATH/update_workflows.sh" "$TOP_LEVEL_DIR" "$@"
