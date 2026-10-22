#!/usr/bin/env bash
# Script Rollback / Restore CleanYourTV kembali ke tampilan pabrik
set -euo pipefail

TV_HOST="${TV_HOST:-192.168.1.15}"
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

echo "=== Memulai Rollback ke Tampilan Pabrik di $TV_HOST ==="

# 1. Jalankan restore package dan Home menggunakan recovery record
TV_HOST="$TV_HOST" ./tools/restore.sh "$DIR/debloat-safe.txt" com.google.android.tvlauncher

# 2. Kembalikan Home default ke stock launcher
TV_HOST="$TV_HOST" tools/venv/bin/python tools/tv.py 'cmd package set-home-activity --user 0 com.google.android.tvlauncher/.MainActivity'

echo "=== Rollback selesai! Tampilan kembali ke stock Android TV launcher. ==="
echo "Catatan: Jika ingin menghapus CleanYourTV Launcher sepenuhnya, jalankan:"
echo "TV_HOST=$TV_HOST tools/venv/bin/python tools/tv.py 'pm uninstall com.gathin.tvhome'"
