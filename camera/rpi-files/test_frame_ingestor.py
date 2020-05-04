# -*- coding: utf-8 -*-

"""FrameIngestor tests."""

from unittest.mock import Mock

import pytest
from frame_ingestor import FrameIngestor, FrameIngestorSource


@pytest.fixture
def mock_source():
    """Create a mock FrameIngestorSource.

    Returns:
        a mock FrameIngestorSource
    """
    return Mock(spec=FrameIngestorSource)


mock_source2 = mock_source


def test_init_no_source():
    """Test initializing FrameIngestor without providing source."""
    frame_ingestor = FrameIngestor()
    assert not frame_ingestor._source


def test_init_with_source(mock_source):
    """Test initializing FrameIngestor with a source.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor(mock_source)
    assert frame_ingestor._source == mock_source
    mock_source.start.assert_called_once()


def test_register_source_empty():
    """Test preventing empty sources."""
    frame_ingestor = FrameIngestor()
    with pytest.raises(TypeError):
        frame_ingestor.register_source()


def test_register_source_invalid():
    """Test preventing sources not matching type."""
    frame_ingestor = FrameIngestor()
    invalid_arg = 'string'
    with pytest.raises(TypeError):
        frame_ingestor.register_source(invalid_arg)


def test_register_source(mock_source):
    """Test registering initial source.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor()
    frame_ingestor.register_source(mock_source)
    assert frame_ingestor._source == mock_source
    mock_source.start.assert_called_once()


def test_register_source_swap(mock_source, mock_source2):
    """Test swapping initial source.

    Args:
        mock_source: mock FrameIngestorSource.
        mock_source2: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor(mock_source)
    frame_ingestor.register_source(mock_source2)

    assert frame_ingestor._source == mock_source2
    mock_source.stop.assert_called_once()
    mock_source2.start.assert_called_once()


def test_release_source(mock_source):
    """Test releasing source.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor(mock_source)
    frame_ingestor.release_source()

    assert not frame_ingestor._source
    mock_source.stop.assert_called_once()


def test_release_source_empty(mock_source):
    """Test releasing source.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor()
    with pytest.raises(RuntimeError):
        frame_ingestor.release_source()


def test_with_statement(mock_source):
    """Test releasing source.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    with FrameIngestor(mock_source) as frame_ingestor:
        frame_ingestor.get_frame()
    mock_source.stop.assert_called_once()


def test_with_statement_exception(mock_source):
    """Test passing exceptions from with statement.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    mock_source.get_frame.side_effect = RuntimeError('error')
    with pytest.raises(RuntimeError):
        with FrameIngestor(mock_source) as frame_ingestor:
            frame_ingestor.get_frame()
    mock_source.stop.assert_called_once()


def test_get_frame(mock_source):
    """Test getting a frame.

    Args:
        mock_source: mock FrameIngestorSource.
    """
    frame_ingestor = FrameIngestor(mock_source)
    frame_ingestor.get_frame()

    mock_source.get_frame.assert_called_once()


def test_get_frame_no_source():
    """Test exception when getting frame and no source."""
    frame_ingestor = FrameIngestor()
    with pytest.raises(RuntimeError):
        frame_ingestor.get_frame()
