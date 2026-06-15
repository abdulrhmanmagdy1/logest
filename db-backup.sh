#!/usr/bin/env bash
# ================================================================
# Edham Logistics — MongoDB Production Backup Script
#
# What it does:
#   1. Streams a compressed mongodump archive from inside the
#      running MongoDB container directly to the host filesystem.
#      (No temp files inside the container; zero extra disk I/O.)
#   2. Verifies the archive is non-empty before declaring success.
#   3. Deletes backups older than RETENTION_DAYS to save disk space.
#   4. Logs every action with timestamps to a persistent log file.
#
# Crontab setup (runs every night at 02:00 AM):
#   sudo crontab -e
#   0 2 * * * /opt/edham/db-backup.sh >> /opt/edham/backups/cron.log 2>&1
#
# Requirements:
#   • Docker running with container named "edham-mongo-prod"
#   • MONGO_ROOT_USER and MONGO_ROOT_PASSWORD exported, OR set in
#     /opt/edham/.env.prod  (this script sources it automatically)
# ================================================================

set -euo pipefail

# ── Configuration ─────────────────────────────────────────────
CONTAINER_NAME="edham-mongo-prod"
DB_NAME="edham_logistics"
BACKUP_DIR="/opt/edham/backups"
LOG_FILE="${BACKUP_DIR}/backup.log"
RETENTION_DAYS=7

# ── Source production env file if credentials not in environment ─
ENV_FILE="/opt/edham/.env.prod"
if [[ -f "${ENV_FILE}" ]]; then
    # Only export the vars we need — avoid polluting the environment
    set -a
    # shellcheck disable=SC1090
    source <(grep -E '^\s*(MONGO_ROOT_USER|MONGO_ROOT_PASSWORD)=' "${ENV_FILE}" | sed 's/\r//')
    set +a
fi

# ── Validate required variables ───────────────────────────────
: "${MONGO_ROOT_USER:?ERROR: MONGO_ROOT_USER is not set. Check /opt/edham/.env.prod}"
: "${MONGO_ROOT_PASSWORD:?ERROR: MONGO_ROOT_PASSWORD is not set. Check /opt/edham/.env.prod}"

# ── Timestamp & target path ───────────────────────────────────
TIMESTAMP="$(date +"%Y%m%d_%H%M%S")"
BACKUP_FILE="${BACKUP_DIR}/edham_mongo_${TIMESTAMP}.archive.gz"

# ── Logging helper ────────────────────────────────────────────
log() {
    local level="${2:-INFO}"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [${level}] $1" | tee -a "${LOG_FILE}"
}

# ── Ensure backup directory exists ────────────────────────────
mkdir -p "${BACKUP_DIR}"

log "========================================================"
log "Backup started  — database: ${DB_NAME}"
log "Target file     — ${BACKUP_FILE}"

# ── Verify container is running ───────────────────────────────
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    log "Container '${CONTAINER_NAME}' is NOT running — aborting." "ERROR"
    exit 1
fi

# ── Stream compressed dump from container to host ─────────────
# --archive  : write dump as a single binary stream to stdout
# --gzip     : compress the stream on the fly (no temp files)
# The output is piped directly to a file on the host.
log "Running mongodump (streaming archive to host)..."
docker exec "${CONTAINER_NAME}" mongodump \
    --username="${MONGO_ROOT_USER}" \
    --password="${MONGO_ROOT_PASSWORD}" \
    --authenticationDatabase=admin \
    --db="${DB_NAME}" \
    --archive \
    --gzip \
    > "${BACKUP_FILE}"

# ── Verify archive is non-empty ───────────────────────────────
if [[ ! -s "${BACKUP_FILE}" ]]; then
    log "Backup file is empty or missing — backup FAILED." "ERROR"
    rm -f "${BACKUP_FILE}"
    exit 1
fi

BACKUP_SIZE="$(du -sh "${BACKUP_FILE}" | cut -f1)"
log "Backup successful — size: ${BACKUP_SIZE}"

# ── Purge backups older than RETENTION_DAYS ───────────────────
log "Purging backups older than ${RETENTION_DAYS} days..."
DELETED_COUNT="$(find "${BACKUP_DIR}" \
    -maxdepth 1 \
    -name "edham_mongo_*.archive.gz" \
    -mtime "+${RETENTION_DAYS}" \
    -print \
    -delete \
    | wc -l)"
log "Deleted ${DELETED_COUNT} old archive(s)."

# ── Report current disk usage ─────────────────────────────────
DISK_USED="$(du -sh "${BACKUP_DIR}" | cut -f1)"
ARCHIVE_COUNT="$(find "${BACKUP_DIR}" -maxdepth 1 -name "*.archive.gz" | wc -l)"
log "Backup directory: ${DISK_USED} used — ${ARCHIVE_COUNT} archive(s) retained."
log "Completed: ${BACKUP_FILE}"
log "========================================================"
