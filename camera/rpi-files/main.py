#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Main application.

Main application module will start monitoring for people entereing and leaving.
"""

import logging
import os
import signal
from datetime import datetime

import cv2
from api_connector import ApiConnector
from data_archiver import CsvArchiver
from data_batcher import DataBatcher
from dotenv import load_dotenv
from events import EventType
from frame_ingestor import (
    FileIngestorSource,
    FrameIngestor,
    WebcamIngestorSource,
)
from imutils import resize
from openapi_client import Configuration
from people_counter import Configuration as PeopleCounterConfiguration
from people_counter import PeopleCounter

BATCH_SECONDS = 3
DEFAULT_FRAME_WIDTH = 500


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


def read_float_from_env(name, default=None):
    """Read float from environment.

    Args:
        name: variable name
        default: variable default value

    Returns:
        Value or None on parsing error
    """
    try:
        return float(os.getenv(name, default))
    except TypeError as ex:
        msg = 'Could not parse {0}: {1}'.format(name, ex)
        logging.error(msg)
        return None


def load_api_configuration() -> Configuration:
    """Load API configuration.

    Raises:
        RuntimeError: when required data missing or could not parse data.

    Returns:
        configuration object
    """
    try:
        device_id = read_int_from_env('DEVICE_ID', None)
    except TypeError as ex:
        msg = 'Could not load device ID: {0}'.format(ex)
        raise RuntimeError(msg)

    configuration = Configuration()
    configuration.host = os.getenv('API_HOST')
    configuration.api_key['X-API-KEY'] = os.getenv('API_KEY')
    configuration.api_key['X-DEVICE-ID'] = device_id
    return configuration


def load_counter_configuration() -> PeopleCounterConfiguration:
    """Load counter configuration.

    Raises:
        RuntimeError: when required data missing or could not parse data.

    Returns:
        configuration object
    """
    try:
        skip_frames = read_int_from_env('SKIP_FRAMES', None)
    except TypeError as ex:
        msg = 'Could not load fram skip count: {0}'.format(ex)
        raise RuntimeError(msg)
    try:
        min_confidence = read_float_from_env('NET_MIN_CONFIDENCE', None)
    except TypeError as ex1:
        msg = 'Could not load minimal confidence: {0}'.format(ex1)
        raise RuntimeError(msg)

    configuration = PeopleCounterConfiguration()
    configuration.skip_frames = skip_frames
    configuration.prototxt_path = os.getenv('NET_PROTOTXT_PATH')
    configuration.model_path = os.getenv('NET_MODEL_PATH')
    configuration.min_confidence = min_confidence
    return configuration


class Main(object):
    """Handle application lifecycle."""

    def __init__(self):
        """Initialize signal handlers, API client and run."""
        self._should_close = False
        self._frame_ingestor = FrameIngestor()
        self._batcher = DataBatcher()

        # Add signal handlers
        signal.signal(signal.SIGINT, self._handle_signals)
        signal.signal(signal.SIGTERM, self._handle_signals)

        # Extra configuration
        self._uses_file = False
        self._display_frame = read_int_from_env('DISPLAY_FRAME', 0) > 0
        self._max_frame_width = read_int_from_env(
            'MAX_FRAME_WIDTH', DEFAULT_FRAME_WIDTH,
        )

        # Start client
        self._start()

    def _init_counter(self):
        """Set up people counter."""
        try:
            configuration = load_counter_configuration()
        except RuntimeError as ex:
            msg = 'Could not load counter settings: {0}'.format(ex)
            logging.error(msg)
            self._should_close = True
            self._close()
            return
        logging.info('Setting up counter...')
        self._counter = PeopleCounter(configuration)

    def _init_archiver(self, start_time: datetime):
        """Set up archiver and save initial message.

        Args:
            start_time: monitoring start timestamp
        """
        archives_dir = os.getenv('ARCHIVES_DIR', None)
        if not archives_dir:
            logging.error('Missing ARCHIVES_DIR')
            self._should_close = True
            return

        start_time_string = start_time.strftime('%Y-%m-%d_%H_%M_%S')
        archive_path = '{0}/{1}.csv'.format(archives_dir, start_time_string)

        logging.info('Opening archive...')

        try:
            self._archiver = CsvArchiver(archive_path)
        except RuntimeError as ex1:
            msg = 'Could not setup archive: {0}'.format(ex1)
            logging.error(msg)
            self._archiver = None
            self._should_close = True

        try:
            self._archiver.init()
        except RuntimeError as ex2:
            msg = 'Could not init archive: {0}'.format(ex2)
            logging.error(msg)
            self._archiver = None
            self._should_close = True

        # Save initial event
        self._archiver.append_event(
            self._last_batch_time, EventType.monitoring_started,
        )

    def _init_api_connector(self, start_time: datetime):
        """Set up uplink and send initial message.

        Args:
            start_time: monitoring start timestamp
        """
        try:
            configuration = load_api_configuration()
        except RuntimeError as ex:
            msg = 'Could not load API settings: {0}'.format(ex)
            logging.error(msg)
            self._should_close = True
            self._close()
            return
        device_id = configuration.api_key['X-DEVICE-ID']
        logging.info('Setting up uplink...')
        self._api_connector = ApiConnector(
            device_id, configuration, start_time,
        )

    def _start(self):
        """Start the system."""
        self._start_monitoring()

        while not self._should_close:
            # Execute loop
            self._execute_loop()

        self._close()

    def _start_monitoring(self):
        """Start monitoring coroutines."""
        # Save start time
        start_time = datetime.now().astimezone()
        self._last_batch_time = start_time
        logging.info('System starts')

        # Start monitoring
        self._init_counter()
        self._init_archiver(start_time)
        self._init_api_connector(start_time)
        self._init_ingestion_stream()

    def _execute_loop(self):
        """Execute main program loop."""
        # Get camera frame
        frame = self._frame_ingestor.get_frame()
        if self._uses_file and frame is None:
            self._should_close = True
            return

        frame = resize(frame, width=self._max_frame_width)

        if self._display_frame:
            cv2.imshow('Frame', frame)
            cv2.waitKey(1)

        # Update counter
        current_time = datetime.now().astimezone()
        self._counter.update(frame, current_time)
        self._batcher.entered(self._counter.get_entering_list())
        self._batcher.left(self._counter.get_leaving_list())

        # Run batching
        delta = current_time - self._last_batch_time
        if delta.total_seconds() > BATCH_SECONDS:
            self._last_batch_time = current_time

            # Log data
            batch = self._batcher.batch()
            logging.debug('People in: {0}'.format(len(batch.entering)))
            logging.debug('People out: {0}'.format(len(batch.leaving)))

            # Add to archiver
            self._archiver.append(batch)
            self._archiver.flush()

            # Send to API endpoint
            if not self._api_connector.send(batch):
                logging.warn('Could not upload events')

    def _close(self):
        """Close the system."""
        # Save start time
        shutdown_time = datetime.now().astimezone()
        logging.info('System shutting down...')

        # Finish ingestion stream
        if self._display_frame:
            cv2.destroyAllWindows()
        if self._frame_ingestor.has_source():
            logging.info('Closing ingestor source...')
            self._frame_ingestor.release_source()

        # Finish archiver
        if self._archiver:
            logging.info('Closing archive...')
            self._archiver.append_event(
                shutdown_time, EventType.monitoring_ended,
            )
            self._archiver.finalize()

        # Finish monitoring
        if self._api_connector:
            logging.info('Closing uplink...')
            self._api_connector.close(shutdown_time)

    def _init_ingestion_stream(self):
        """Init ingestion stream."""
        logging.info('Opening camera stream...')

        stream_type = read_int_from_env('FRAME_SOURCE')
        if stream_type is None:
            self._should_close = True
            return

        if stream_type == 0:  # File
            self._uses_file = True
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
