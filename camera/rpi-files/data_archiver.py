# -*- coding: utf-8 -*-

"""Data archiving module.

DataArchiver saves batches in a selected format.
"""

import csv
import os.path
from abc import ABC
from enum import Enum

from data_batcher import Batch


class EventType(Enum):
    """Enum for event types."""

    monitoring_started = 0
    monitoring_ended = 1
    person_entered = 2
    person_left = 3


class DataArchiver(ABC):
    """Archives batches of data."""

    def init(self):
        """Initializes the archive if not initialized."""

    def append_event(self, timestamp: float, event_type: Enum):
        """Includes single event in the archive. May not flush archive yet.

        Args:
            timestamp: event timestamp
            event_type: event type
        """

    def append(self, batch: Batch):
        """Includes batch in the archive. May not flush archive yet.

        Args:
            batch: new data batch to include
        """

    def flush(self):
        """Forces flushing the archive."""

    def finalize(self):
        """Finalizes the archive. No more data can be saved."""


class CsvArchiver(DataArchiver):
    """Archives batches of data to a file."""

    def __init__(self, path):
        """Saves path for writing.

        Raises:
            TypeError: if path is not a string

        Args:
            path: file path
        """
        if not isinstance(path, str):
            msg = 'Path `{0}` is not a string'.format(path)
            raise TypeError(msg)
        self._path = path
        self._file = None
        self._writer = None
        self._data = {}

    def init(self, force_overwrite=True):
        """Creates file for writing.

        Args:
            force_overwrite: force overwriting file

        Raises:
            RuntimeError: if path already exists or could not create file
        """
        if not force_overwrite and os.path.exists(self._path):
            msg = 'Path `{0}` already exists'.format(self._path)
            raise RuntimeError(msg)

        try:
            self._file = open(self._path, 'w+')
        except OSError as ex:
            msg = 'Could not open path `{0}` for writing: {1}'.format(
                self._path, ex
            )
            raise RuntimeError(msg)

        field_names = ['timestamp']
        field_names.extend(EventType._member_names_)
        try:
            self._writer = csv.DictWriter(self._file, fieldnames=field_names)
        except csv.Error as ex:
            msg = "Error creating csv.writer: {0}".format(ex)
            raise RuntimeError(msg)

    def append_event(self, timestamp: float, event_type: EventType):
        """Includes single event in the archive. May not flush archive yet.

        Args:
            timestamp: event timestamp
            event_type: event type
        """
        timestamp_as_int = int(timestamp)
        if timestamp_as_int not in self._data:
            self._data[timestamp_as_int] = {
                'timestamp': int(timestamp),
                EventType.monitoring_started.name: 0,
                EventType.monitoring_ended.name: 0,
                EventType.person_entered.name: 0,
                EventType.person_left.name: 0,
            }

        self._data[timestamp_as_int][event_type.name] += 1

    def append(self, batch: Batch):
        """Includes batch in the archive. May not flush archive yet.

        Args:
            batch: new data batch to include
        """
        for timestamp in batch.entering:
            self.append_event(timestamp, EventType.person_entered)

        for timestamp in batch.leaving:
            self.append_event(timestamp, EventType.person_left)

    def _reset_file(self):
        """Close and open file for writing to replace content. This action is
        required because file.truncate(0) adds NULL bytes to contents.

        Raises:
            RuntimeError: if could not reinit archive.
        """

        self._writer = None
        self._file.close()
        self.init(force_overwrite=True)

    def flush(self):
        """Forces flushing the archive.

        Raises:
            RuntimeError: if archive is not opened.
        """
        if not self._writer:
            raise RuntimeError('Archive not opened')

        # Reinit file
        self._reset_file()
        self._writer.writeheader()

        # Write data timestamp by timestamp
        for timestamp in self._data:
            self._writer.writerow(self._data[timestamp])

    def finalize(self):
        """Finalizes the archive. No more data can be saved."""
        if self._writer:
            self.flush()
        if self._file:
            self._file.close()
