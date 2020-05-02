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

    def entered(self, entering):
        """Append to lists of people entering.

        Args:
            entering: list of enter timestamps
        """
        self._assert_list_of_floats(entering, 'entering')
        self.current_batch.entering.extend(entering)

    def left(self, leaving):
        """Append to lists of people leaving.

        Args:
            leaving: list of leave timestamps
        """
        self._assert_list_of_floats(leaving, 'leaving')
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

    def _assert_list_of_floats(self, arg, argname):
        if not isinstance(arg, list):
            msg = 'Argument `{0}` should be a list'.format(argname)
            raise TypeError(msg)
        for elem in arg:
            if not isinstance(elem, float):
                msg = 'List `{0}` contains non-float element'.format(argname)
                raise TypeError(msg)
