#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 3: show data
#
# parameters
# -f [ assign | prepare | show ]
# --endpoint --endpoint tcp://localhost:12345
# --rowSeat 160 160
# --threadSize 16
# --encoding ipc:tsurugi


java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch $@