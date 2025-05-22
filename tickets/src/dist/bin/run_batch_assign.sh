#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 1: assign data

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch \
--endpoint ipc:tsurugi \
-f assign \
--rowSeat 160 160  \
--threadSize 16
