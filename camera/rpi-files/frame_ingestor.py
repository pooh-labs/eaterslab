# -*- coding: utf-8 -*-

"""Module exposing FrameIngestor.

FrameIngestor provides frames from currently registered source.
"""

from abc import ABC

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

        Args:
            source_num: video stream number
        """
        self.source_num = source_num

    def start(self):
        """Open the source.
        
        """
        self.vs = WebcamVideoStream(src=self.source_num)
        if not self.vs.stream.isOpened():
            msg = 'Could not open webcam stream {0}'.format(self.source_num)
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


class FrameIngestor(object):
    """Image provider."""

    def __init__(self, source=None):
        """Initialize sources.

        Args:
            source: source to read from
        """
        self.source = None
        if source:
            self.register_source(source)

    def __exit__(self, exc_type, exc_value, traceback):
        """Finalize `with` block.

        Args:
            exc_type: exception type
            exc_value: exception value
            traceback: exception traceback

        Returns:
            true if there were no exceptions
        """
        if self.source:
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

        if self.source:
            self.source.stop()

        self.source = source
        self.source.start()

    def release_source(self):
        """Release registered source.

        Raises:
            RuntimeError: when no source registered
        """
        if not self.source:
            raise RuntimeError('No source to release.')
        self.source.stop()
        self.source = None

    def get_frame(self):
        """Fetch single frame from source.

        Returns:
            fetched frame.
        """
        return self.source.get_frame()
