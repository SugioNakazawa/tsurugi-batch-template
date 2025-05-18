#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch
# show
# case 2: prepare data
# -f prepare --rowSeat 10 10

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.batch.ReserveTicketsBatch \
-f prepare \
--rowSeat 10 10
