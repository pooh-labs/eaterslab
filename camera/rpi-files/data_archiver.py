# -*- coding: utf-8 -*-

"""Data archiving module.

DataArchiver saves batches in a selected format.
"""

import csv
import os
from abc import ABC
from datetime import datetime

from data_batcher import Batch
from events import EventType


class DataArchiver(ABC):
    """Archives batches of data."""

    def init(self):
        """Initialize the archive if not initialized."""

    def append_event(self, timestamp: datetime, event_type: EventType):
        """Includes single event in the archive. May not flush archive yet.

        Args:
            timestamp: event timestamp
            event_type: event type
        """

    def append(self, batch: Batch):
        """Include batch in the archive. May not flush archive yet.

        Args:
            batch: new data batch to include
        """

    def flush(self):
        """Force flushing the archive."""

    def finalize(self):
        """Finalize the archive. No more data can be saved."""


class CsvArchiver(DataArchiver):
    """Archive batches of data to a file."""

    def __init__(self, path):
        """Save path for writing.

        Raises:
            TypeError: if path is not a string

        Args:
            path: file path
        """
        if not isinstance(path, str):
            msg = 'Path `{0}` is not a string'.format(path)
            raise TypeError(msg)
        self._path = path
        self._archive_file = None
        self._writer = None
        self._entries = {}

    def init(self, force_overwrite=False):
        """Create file for writing.

        Args:
            force_overwrite: force overwriting file

        Raises:
            RuntimeError: if path already exists or could not create file
        """
        if not force_overwrite and os.path.exists(self._path):
            msg = 'Path `{0}` already exists'.format(self._path)
            raise RuntimeError(msg)

        try:
            self._archive_file = open(self._path, 'w+')  # noqa: WPS515
        except OSError as ex1:
            msg = 'Could not open path `{0}` for writing: {1}'.format(
                self._path, ex1,
            )
            raise RuntimeError(msg)

        field_names = [
            'timestamp',
            EventType.monitoring_started.name,
            EventType.monitoring_ended.name,
            EventType.person_entered.name,
            EventType.person_left.name,
        ]
        try:
            self._writer = csv.DictWriter(
                self._archive_file, fieldnames=field_names,
            )
        except csv.Error as ex2:
            msg = 'Error creating csv.writer: {0}'.format(ex2)
            raise RuntimeError(msg)

    def append_event(self, timestamp: datetime, event_type: EventType):
        """Include single event in the archive. May not flush archive yet.

        Args:
            timestamp: event timestamp
            event_type: event type
        """
        if timestamp not in self._entries:
            self._entries[timestamp] = {
                'timestamp': timestamp,
                EventType.monitoring_started.name: 0,
                EventType.monitoring_ended.name: 0,
                EventType.person_entered.name: 0,
                EventType.person_left.name: 0,
            }

        self._entries[timestamp][event_type.name] += 1

    def append(self, batch: Batch):
        """Include batch in the archive. May not flush archive yet.

        Args:
            batch: new data batch to include
        """
        for timestamp1 in batch.entering:
            self.append_event(timestamp1, EventType.person_entered)

        for timestamp2 in batch.leaving:
            self.append_event(timestamp2, EventType.person_left)

    def flush(self):
        """Force flushing the archive.

        Raises:
            RuntimeError: if archive is not opened.
        """
        if not self._writer:
            raise RuntimeError('Archive not opened')

        # Reinit file
        self._reset_archive_file()
        self._writer.writeheader()

        # Write data timestamp by timestamp
        for _, entry in self._entries.items():
            self._writer.writerow(entry)

    def finalize(self):
        """Finalize the archive. No more data can be saved."""
        if self._writer:
            self.flush()
        if self._archive_file:
            self._archive_file.close()

    def _reset_archive_file(self):
        """Close and open file for writing to replace content.

        This action is required because file.truncate(0) adds NULL bytes to
        contents.
        """
        self._writer = None
        self._archive_file.close()
        self.init(force_overwrite=True)
