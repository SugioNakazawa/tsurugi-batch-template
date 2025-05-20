#!/bin/bash

CUR_DIR="$(dirname "$0")"

# ReserveTicketsBatch GUI

java -cp "$CUR_DIR/../lib/*" jp.gr.java_conf.nkzw.tbt.tickets.app.App $@
