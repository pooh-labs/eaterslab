#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Data batching module.

DataBatcher prepares batches for sending.
"""


class Batch(object):
    """Single batch of data."""

    def __init__(self, entering, leaving):
        """Initialize queues.

        Args:
            entering: list of enter timestamps
            leaving: list of leave timestamps
        """
        self.entering = entering
        self.leaving = leaving


class DataBatcher(object):
    """Batches people leaving and entering."""

    def __init__(self):
        """Initialize queues."""
        self.current_batch = Batch([], [])

    def update(self, entering, leaving):
        """Run updates.

        Args:
            entering: list of enter timestamps
            leaving: list of leave timestamps
        """
        self.current_batch.entering.extend(entering)
        self.current_batch.leaving.extend(leaving)

    def batch(self):
        """Return data batch and start a new one.

        Returns:
            Batch with data since last batching.
        """
        # Swap new and current batch
        current = self.current_batch
        (to_return, current) = (current, Batch([], []))  # noqa: WPS414
        self.current_batch = current
        return to_return
