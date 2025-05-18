#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 1: assign data
# -f assign --rowSeat 10 10

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch \
-f assign \
--rowSeat 10 10  \
--threadSize 8
