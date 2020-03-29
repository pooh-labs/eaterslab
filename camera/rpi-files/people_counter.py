# -*- coding: utf-8 -*-

"""People counting module.

People counter will connect to imaging device and detect people.
"""

import os

FIRST_PERSON_THRESHOLD = 190
SECOND_PERSON_THRESHOLD = 230
LEAVING_PERSON_THRESHOLD = 30


class PeopleCounter(object):
    """Counts people leaving and entering."""

    def __init__(self):
        """Initialize queues."""
        self.enter_times = []
        self.leave_times = []
        self._counter = 0

    def update(self, timestamp):
        """Run updates.

        Args:
            timestamp: Current time
        """
        # Generate random patterns
        repeats = 10
        while repeats > 0:
            random = os.urandom(1)[0]

            if random > FIRST_PERSON_THRESHOLD:
                self.enter_times.append(timestamp)
                self._counter += 1

                if random > SECOND_PERSON_THRESHOLD:
                    self.enter_times.append(timestamp)
                    self._counter += 1

            elif self._counter > 0 and random > LEAVING_PERSON_THRESHOLD:
                self.leave_times.append(timestamp)
                self._counter -= 1

            repeats -= 1

    def get_entering_list(self):
        """Get list of enter times.

        Returns:
            list of enter timestamps.
        """
        to_return = []
        to_return.extend(self.enter_times)
        self.enter_times.clear()
        return to_return

    def get_leaving_list(self):
        """Get list of leave times.

        Returns:
            list of leave timestamps.
        """
        to_return = []
        to_return.extend(self.leave_times)
        self.leave_times.clear()
        return to_return
