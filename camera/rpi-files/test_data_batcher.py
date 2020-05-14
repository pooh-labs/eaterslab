# -*- coding: utf-8 -*-

"""DataBatcher tests."""

from datetime import datetime

import pytest
from data_batcher import DataBatcher


def datetime_range(start, end):
    """Create a list of floats from start to end, with step equal 1.

    Args:
        start: First list value
        end: Last list value

    Returns:
        list of floats.
    """
    return [datetime.fromtimestamp(elem) for elem in range(start, end + 1, 1)]


@pytest.fixture
def sample_timestamps():
    """Create a list of sample timestamps.

    Returns:
        a list of 3 sample float timestamps.
    """
    return [
        datetime.fromtimestamp(1588441493.5),
        datetime.fromtimestamp(1588441693.34),
        datetime.fromtimestamp(1589441493.1),
    ]


def test_empty_batch():
    """Test batching on empty DataBatcher."""
    data_batcher = DataBatcher()
    batch = data_batcher.batch()
    assert not batch.entering
    assert not batch.leaving


def test_entered_empty_batcher(sample_timestamps):
    """Test appending to an empty DataBatcher.

    Args:
        sample_timestamps: samples for the correct argument
    """
    data_batcher = DataBatcher()
    data_batcher.entered(sample_timestamps)

    batch = data_batcher.batch()
    assert batch.entering == sample_timestamps


def test_entered_non_empty_batcher():
    """Test appending to a non-empty DataBatcher."""
    data_batcher = DataBatcher()
    data_batcher.entered(datetime_range(1, 3))
    data_batcher.entered(datetime_range(4, 6))

    batch = data_batcher.batch()
    assert batch.entering == datetime_range(1, 6)


def test_entered_batch_entered():
    """Test appending after a batch."""
    data_batcher = DataBatcher()
    data_batcher.entered(datetime_range(1, 3))
    data_batcher.batch()
    data_batcher.entered(datetime_range(4, 6))

    batch = data_batcher.batch()
    assert batch.entering == datetime_range(4, 6)


def test_entered_non_float():
    """Test exception raising on non-list arguments."""
    data_batcher = DataBatcher()

    invalid_arg = 100
    with pytest.raises(TypeError):
        data_batcher.entered(invalid_arg)

    batch = data_batcher.batch()
    assert not batch.entering


def test_entered_list_with_non_float():
    """Test exception raising on invalid list elements."""
    data_batcher = DataBatcher()

    invalid_list = [1, 2, 'a']
    with pytest.raises(TypeError):
        data_batcher.entered(invalid_list)

    batch = data_batcher.batch()
    assert not batch.entering


def test_left_empty_batcher(sample_timestamps):
    """Test appending to an empty DataBatcher.

    Args:
        sample_timestamps: samples for the correct argument
    """
    data_batcher = DataBatcher()
    data_batcher.left(sample_timestamps)

    batch = data_batcher.batch()
    assert batch.leaving == sample_timestamps


def test_left_non_empty_batcher():
    """Test appending to a non-empty DataBatcher."""
    data_batcher = DataBatcher()
    data_batcher.left(datetime_range(1, 3))
    data_batcher.left(datetime_range(4, 6))

    batch = data_batcher.batch()
    assert batch.leaving == datetime_range(1, 6)


def test_left_batch_entered():
    """Test appending after a batch."""
    data_batcher = DataBatcher()
    data_batcher.left(datetime_range(1, 3))
    data_batcher.batch()
    data_batcher.left(datetime_range(4, 6))

    batch = data_batcher.batch()
    assert batch.leaving == datetime_range(4, 6)


def test_left_non_float():
    """Test exception raising on non-list arguments."""
    data_batcher = DataBatcher()

    invalid_arg = 100
    with pytest.raises(TypeError):
        data_batcher.left(invalid_arg)

    batch = data_batcher.batch()
    assert not batch.leaving


def test_left_list_with_non_float():
    """Test exception raising on invalid list elements."""
    data_batcher = DataBatcher()

    invalid_list = [1, 2, 'a']
    with pytest.raises(TypeError):
        data_batcher.left(invalid_list)

    batch = data_batcher.batch()
    assert not batch.leaving
