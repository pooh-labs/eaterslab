# -*- coding: utf-8 -*-

"""CsvArchiver tests."""

from datetime import datetime

import pytest
from data_archiver import CsvArchiver, EventType
from data_batcher import Batch

LABEL_TIMESTAMP = 'timestamp'

SAMPLE_PATH = 'archives/example.csv'
SAMPLE_TIMESTAMP = datetime.fromtimestamp(1588761927.234)
SAMPLE_TIMESTAMP2 = datetime.fromtimestamp(1588761929.234)


def test_init():
    """Test initialization with a path."""
    archiver = CsvArchiver(SAMPLE_PATH)
    assert archiver._path == SAMPLE_PATH


def test_init_no_path():
    """Test initialization without path."""
    with pytest.raises(TypeError):
        CsvArchiver()


def test_init_not_string():
    """Test checking that path is a string."""
    with pytest.raises(TypeError):
        CsvArchiver(100)


def test_append_event():
    """Test appending single event."""
    event = EventType.monitoring_started
    archiver = CsvArchiver(SAMPLE_PATH)
    archiver.append_event(SAMPLE_TIMESTAMP, event)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 1,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 0,
        EventType.person_left.name: 0,
    }
    assert entry_actual == entry_expected


def test_append_event_twice():
    """Test multiple appending single event."""
    event = EventType.monitoring_started
    archiver = CsvArchiver(SAMPLE_PATH)
    archiver.append_event(SAMPLE_TIMESTAMP, event)
    archiver.append_event(SAMPLE_TIMESTAMP, event)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 2,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 0,
        EventType.person_left.name: 0,
    }
    assert entry_actual == entry_expected


def test_append_event_multiple_types():
    """Test appending multiple events."""
    event1 = EventType.monitoring_started
    event2 = EventType.person_entered
    archiver = CsvArchiver(SAMPLE_PATH)
    archiver.append_event(SAMPLE_TIMESTAMP, event1)
    archiver.append_event(SAMPLE_TIMESTAMP, event2)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 1,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 1,
        EventType.person_left.name: 0,
    }
    assert entry_actual == entry_expected


def test_append_empty_batch():
    """Test appending empty batch."""
    archiver = CsvArchiver(SAMPLE_PATH)
    batch = Batch([], [])
    archiver.append(batch)
    assert not archiver._entries


def test_append_batch_entered():
    """Test appending batch with enter timestamps."""
    archiver = CsvArchiver(SAMPLE_PATH)
    batch = Batch([SAMPLE_TIMESTAMP], [])
    archiver.append(batch)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 0,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 1,
        EventType.person_left.name: 0,
    }
    assert entry_actual == entry_expected


def test_append_batch_left():
    """Test appending batch with leave timestamps."""
    archiver = CsvArchiver(SAMPLE_PATH)
    batch = Batch([], [SAMPLE_TIMESTAMP])
    archiver.append(batch)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 0,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 0,
        EventType.person_left.name: 1,
    }
    assert entry_actual == entry_expected


def test_append_batch_both_same():
    """Test appending batch with both timestamps."""
    archiver = CsvArchiver(SAMPLE_PATH)
    batch = Batch([SAMPLE_TIMESTAMP], [SAMPLE_TIMESTAMP])
    archiver.append(batch)

    entry_actual = archiver._entries[SAMPLE_TIMESTAMP]
    entry_expected = {
        LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
        EventType.monitoring_started.name: 0,
        EventType.monitoring_ended.name: 0,
        EventType.person_entered.name: 1,
        EventType.person_left.name: 1,
    }
    assert entry_actual == entry_expected


def test_append_batch_both_different():
    """Test appending batch with both timestamps, different times."""
    archiver = CsvArchiver(SAMPLE_PATH)
    batch = Batch([SAMPLE_TIMESTAMP], [SAMPLE_TIMESTAMP2])
    archiver.append(batch)

    data_expected = {
        SAMPLE_TIMESTAMP: {
            LABEL_TIMESTAMP: SAMPLE_TIMESTAMP,
            EventType.monitoring_started.name: 0,
            EventType.monitoring_ended.name: 0,
            EventType.person_entered.name: 1,
            EventType.person_left.name: 0,
        },
        SAMPLE_TIMESTAMP2: {
            LABEL_TIMESTAMP: SAMPLE_TIMESTAMP2,
            EventType.monitoring_started.name: 0,
            EventType.monitoring_ended.name: 0,
            EventType.person_entered.name: 0,
            EventType.person_left.name: 1,
        },
    }
    assert archiver._entries == data_expected
