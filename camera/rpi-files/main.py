#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Main application.

Main application module will start monitoring for people entereing and leaving.
"""

import logging
import os
import signal
import time
from datetime import datetime

import cv2
import openapi_client
from data_archiver import CsvArchiver, EventType
from data_batcher import DataBatcher
from dotenv import load_dotenv
from frame_ingestor import (
    FileIngestorSource,
    FrameIngestor,
    WebcamIngestorSource,
)
from people_counter import PeopleCounter

BATCH_SECONDS = 3


def read_int_from_env(name, default=None):
    """Read integer from environment.

        Args:
            name: variable name
            default: variable default value

        Returns:
            Value or None on parsing error
        """
    try:
        return int(os.getenv(name, default))
    except TypeError as ex:
        msg = 'Could not parse {0}: {1}'.format(name, ex)
        logging.error(msg)
        return None


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

        # Extra configuration
        self._display_frame = read_int_from_env('DISPLAY_FRAME', 0) > 0

        # Start client
        with openapi_client.ApiClient(configuration) as api_client:
            self._api = openapi_client.DefaultApi(api_client)
            self._init_archiver()
            if not self._should_close:
                self._start()

    def _init_archiver(self):
        archives_dir = os.getenv('ARCHIVES_DIR', None)
        if not archives_dir:
            logging.error('Missing ARCHIVES_DIR')
            self._should_close = True
            return

        datetime_string = datetime.now().strftime('%Y-%m-%d_%H_%M_%S')
        archive_path = '{0}/{1}.csv'.format(archives_dir, datetime_string)

        logging.info('Opening archive...')
        try:
            self._archiver = CsvArchiver(archive_path)
            self._archiver.init()
        except RuntimeError as ex:
            msg = 'Could not init archive: {0}'.format(ex)
            logging.error(msg)
            self._archiver = None
            self._should_close = True

    def _start(self):
        """Start the system."""
        logging.info('System starts')

        # Start monitoring
        self._archiver.append_event(time.time(), EventType.monitoring_started)
        self._init_ingestion_stream()

        self._last_batch_time = time.time()
        iteration = 0
        while not self._should_close:
            # Execute loop
            self._execute_loop()

            # Get camera frame
            if self._display_frame:
                frame = self._frame_ingestor.get_frame()
                cv2.imshow('Frame', frame)
                cv2.waitKey(1)

            # Count iterations for demo purposes
            iteration += 1
            if iteration == 10:
                self._should_close = True

        self._close()

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

            # Add to archiver
            self._archiver.append(batch)
            self._archiver.flush()

    def _close(self):
        """Close the system."""
        logging.info('System shutting down...')

        self._finish_ingestion_stream()

        if self._archiver:
            logging.info('Closing archive...')
            self._archiver.append_event(
                time.time(), EventType.monitoring_ended
            )
            self._archiver.finalize()

    def _init_ingestion_stream(self):
        """Init ingestion stream."""
        logging.info('Opening camera stream...')

        stream_type = read_int_from_env('FRAME_SOURCE')
        if stream_type is None:
            self._should_close = True
            return

        if stream_type == 0:  # File
            path = os.getenv('SOURCE_FILE_PATH', None)
            source = FileIngestorSource(path)
        elif stream_type == 1:  # Webcam
            stream_num = read_int_from_env('WEBCAM_STREAM_NUM')
            if stream_num is None:
                self._should_close = True
                return
            source = WebcamIngestorSource(stream_num)

        try:
            self._frame_ingestor.register_source(source)
        except RuntimeError as ex:
            msg = 'Could not register_source: {0}'.format(ex)
            logging.error(msg)
            self._should_close = True

    def _finish_ingestion_stream(self):
        if self._display_frame:
            cv2.destroyAllWindows()
        if self._frame_ingestor.has_source():
            logging.info('Closing ingestor source...')
            self._frame_ingestor.release_source()

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
