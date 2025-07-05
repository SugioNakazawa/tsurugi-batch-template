#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 1: assign data

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch \
--endpoint tcp://localhost:12345 \
-f assign \
--rowSeat 160 160  \
--threadSize 16
