#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Main application.

Main application module will start monitoring for people entereing and leaving.
"""

import logging
import os
import signal
import time

import openapi_client
from data_batcher import DataBatcher
from dotenv import load_dotenv
from people_counter import PeopleCounter

BATCH_SECONDS = 3


class Main(object):
    """Handle application lifecycle."""

    def __init__(self):
        """Initialize signal handlers, API client and run."""
        self.should_close = False
        self.counter = PeopleCounter()
        self.batcher = DataBatcher()

        # Add signal handlers
        signal.signal(signal.SIGINT, self.handle_signals)
        signal.signal(signal.SIGTERM, self.handle_signals)

        # Set API configuration
        configuration = openapi_client.Configuration()
        configuration.host = os.getenv('API_HOST')
        configuration.api_key['X-DEVICE-ID'] = os.getenv('DEVICE_ID')
        configuration.api_key['X-API-KEY'] = os.getenv('API_KEY')

        # Start
        with openapi_client.ApiClient(configuration) as api_client:
            self.api = openapi_client.DefaultApi(api_client)
            self.start()

    def start(self):
        """Start the system."""
        logging.info('System starts')

        last_batch_time = time.time()
        iteration = 0
        while not self.should_close:
            # Sleep to simulate computations
            time.sleep(1)

            # Update counter
            self.counter.update(time.time())
            self.batcher.update(
                self.counter.get_entering_list(),
                self.counter.get_leaving_list(),
            )

            # Run batching
            current_time = time.time()
            if current_time > last_batch_time + BATCH_SECONDS:
                last_batch_time = current_time

                # Log data
                batch = self.batcher.batch()
                logging.debug('People in: {0}'.format(len(batch.entering)))
                logging.debug('People out: {0}'.format(len(batch.leaving)))

            # Count iterations for demo purposes
            iteration += 1
            if iteration == 10:
                self.should_close = True

        logging.info('System shutting down...')

    def handle_signals(self, _signum, _frame):
        """Handle interruption events.

        Args:
            _signum: Signal number
            _frame: Current stack frame
        """
        self.should_close = True


if __name__ == '__main__':
    logging.basicConfig(
        format='%(asctime)s [%(levelname)s] %(message)s',
        datefmt='%Y-%m-%dT%H:%M:%S%z',
        filename='app.log',
        filemode='w',
        level=logging.DEBUG,
    )
    load_dotenv()
    Main()
