# -*- coding: utf-8 -*-

"""Module exposing FrameIngestor.

FrameIngestor provides frames from currently registered source.
"""

from abc import ABC

from cv2 import VideoCapture
from imutils.video import WebcamVideoStream


class FrameIngestorSource(ABC):
    """Source for fetching frames."""

    def start(self):
        """Open the source."""

    def get_frame(self):
        """Fetch single frame from source.

        Returns:
            fetched frame.
        """
        return []

    def stop(self):
        """Stop the source and free all allocated resources."""


class WebcamIngestorSource(FrameIngestorSource):
    """Returns frames from the webcam."""

    def __init__(self, source_num=0):
        """Initialize object.

        Raises:
            TypeError: when source_num is not an int

        Args:
            source_num: video stream number
        """
        if not isinstance(source_num, int):
            raise TypeError('`source_num` is not an int')
        self._source_num = source_num

    def start(self):
        """Open the source.

        Raises:
            RuntimeError: when could not open stream.
        """
        self.vs = WebcamVideoStream(src=self._source_num)
        if not self.vs.stream.isOpened():
            msg = 'Could not open webcam stream {0}'.format(self._source_num)
            raise RuntimeError(msg)
        self.vs.start()
        self.initialized = True

    def get_frame(self):
        """Fetch single frame from source.

        Returns:
            fetched frame.

        Raises:
            RuntimeError: if source is not initialized.
        """
        if not self.initialized:
            raise RuntimeError('Source not initialized.')
        return self.vs.read()

    def stop(self):
        """Stop the source and free all allocated resources."""
        self.vs.stop()


class FileIngestorSource(FrameIngestorSource):
    """Returns frames from a file source."""

    def __init__(self, path):
        """Initialize object.

        Args:
            path: input path
        """
        self._path = path

    def start(self):
        """Open the source.

        Raises:
            RuntimeError: when could not open stream.
        """
        self.vs = VideoCapture(self._path)
        if not self.vs.isOpened():
            msg = 'Could not open file stream {0}'.format(self._path)
            raise RuntimeError(msg)
        self.initialized = True

    def get_frame(self):
        """Fetch single frame from source.

        Returns:
            fetched frame.

        Raises:
            RuntimeError: if source is not initialized.
        """
        if not self.initialized:
            raise RuntimeError('Source not initialized.')
        return self.vs.read()[1]

    def stop(self):
        """Stop the source and free all allocated resources."""
        self.vs.release()


class FrameIngestor(object):
    """Image provider."""

    def __init__(self, source=None):
        """Initialize sources.

        Args:
            source: source to read from
        """
        self._source = None
        if source:
            self.register_source(source)

    def __enter__(self):
        """Enter `with` block.

        Returns:
            itself
        """
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        """Finalize `with` block.

        Args:
            exc_type: exception type
            exc_value: exception value
            traceback: exception traceback

        Returns:
            true if there were no exceptions
        """
        if self._source:
            self.release_source()
        return traceback is None

    def register_source(self, source):
        """Register frame source.

        Any previous source is closed and discarded.

        Args:
            source: source to read from

        Raises:
            TypeError: if source is not a `FrameIngestorSource`
        """
        if not isinstance(source, FrameIngestorSource):
            raise TypeError('`source` is not a FrameIngestorSource')

        if self._source:
            self._source.stop()

        self._source = source
        self._source.start()

    def release_source(self):
        """Release registered source.

        Raises:
            RuntimeError: when no source registered
        """
        if not self._source:
            raise RuntimeError('No source to release.')
        self._source.stop()
        self._source = None

    def get_frame(self):
        """Fetch single frame from source.

        Raises:
            RuntimeError: when no source registered

        Returns:
            fetched frame.
        """
        if not self._source:
            raise RuntimeError('No source to get frame from.')
        return self._source.get_frame()

    def has_source(self) -> bool:
        """Say whether there is a registered source.

        Returns:
            true when source is registered.
        """
        return self._source is not None
