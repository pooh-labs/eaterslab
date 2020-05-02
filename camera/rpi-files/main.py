#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Main application.

Main application module will start monitoring for people entereing and leaving.
"""

import logging
import os
import signal
import time

import cv2
import openapi_client
from data_batcher import DataBatcher
from dotenv import load_dotenv
from frame_ingestor import FrameIngestor, WebcamIngestorSource
from people_counter import PeopleCounter

BATCH_SECONDS = 3


class Main(object):
    """Handle application lifecycle."""

    def __init__(self):
        """Initialize signal handlers, API client and run."""
        self._should_close = False
        self._counter = PeopleCounter()
        self._frame_ingestor = FrameIngestor()
        self._batcher = DataBatcher()

        # Add signal handlers
        signal.signal(signal.SIGINT, self._handle_signals)
        signal.signal(signal.SIGTERM, self._handle_signals)

        # Set API configuration
        configuration = openapi_client.Configuration()
        configuration.host = os.getenv('API_HOST')
        configuration.api_key['X-DEVICE-ID'] = os.getenv('DEVICE_ID')
        configuration.api_key['X-API-KEY'] = os.getenv('API_KEY')

        # Start client
        with openapi_client.ApiClient(configuration) as api_client:
            self._api = openapi_client.CafeteriaApi(api_client)
            self._start()

    def _start(self):
        """Start the system."""
        logging.info('System starts')
        self._init_ingestion_stream()

        self._last_batch_time = time.time()
        iteration = 0
        while not self._should_close:
            # Execute loop
            self._execute_loop()

            # Get camera frame
            frame = self._frame_ingestor.get_frame()
            if os.getenv('DISPLAY_FRAME') != '0':
                cv2.imshow('Frame', frame)
                cv2.waitKey(1)

            # Count iterations for demo purposes
            iteration += 1
            if iteration == 10:
                self._should_close = True

        logging.info('System shutting down...')
        self._finish_ingestion_stream()

    def _init_ingestion_stream(self):
        """Init ingestion stream."""
        logging.info('Opening camera stream...')
        stream_num = int(os.getenv('WEBCAM_STREAM_NUM', 0))
        source = WebcamIngestorSource(stream_num)
        try:
            self._frame_ingestor.register_source(source)
        except RuntimeError as ex:
            msg = 'Could not initialize stream: {0}'.format(ex)
            logging.error(msg)
            self._should_close = True

    def _finish_ingestion_stream(self):
        if os.getenv('DISPLAY_FRAME'):
            cv2.destroyAllWindows()
        logging.info('Closing camera stream...')
        self._frame_ingestor.release_source()

    def _execute_loop(self):
        """Execute main program loop."""
        # Sleep to simulate computations
        time.sleep(1)

        # Update counter
        self._counter.update(time.time())
        self._batcher.entered(self._counter.get_entering_list())
        self._batcher.left(self._counter.get_leaving_list())

        # Run batching
        current_time = time.time()
        if current_time > self._last_batch_time + BATCH_SECONDS:
            self._last_batch_time = current_time

            # Log data
            batch = self._batcher.batch()
            logging.debug('People in: {0}'.format(len(batch.entering)))
            logging.debug('People out: {0}'.format(len(batch.leaving)))

    def _handle_signals(self, _signum, _frame):
        """Handle interruption events.

        Args:
            _signum: Signal number
            _frame: Current stack frame
        """
        self._should_close = True


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
