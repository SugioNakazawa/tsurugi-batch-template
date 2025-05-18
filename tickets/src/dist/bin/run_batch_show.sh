#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 3: show data
# -f show --rowSeat 10 10

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch \
-f show \
--rowSeat 10 10
