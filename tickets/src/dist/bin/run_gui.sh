#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch GUI
#
# parameters
# --endpoint ipc:tsurugi \
# --rowSeat 160 160
# --threadSize 16
# --encoding ipc:tsurugi

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.app.App $@
