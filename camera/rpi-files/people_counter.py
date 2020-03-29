#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""People counting module.

People counter will connect to imaging device and detect people.
"""

import os

FIRST_PERSON_THRESHOLD = 180
SECOND_PERSON_THRESHOLD = 230


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
        for _iter in range(0, 5):
            random_variable = os.urandom(1)[0]

            if random_variable > FIRST_PERSON_THRESHOLD:
                self.enter_times.append(timestamp)
                self._counter += 1

                if random_variable > SECOND_PERSON_THRESHOLD:
                    self.enter_times.append(timestamp)
                    self._counter += 1

            elif self._counter > 0:
                self.leave_times.append(timestamp)
                self._counter -= 1

    def get_entered_list(self):
        """Get list of enter times.

        Returns:
            list of enter timestamps.
        """
        to_return = []
        to_return.extend(self.enter_times)
        self.enter_times.clear()
        return to_return

    def get_left_list(self):
        """Get list of leave times.

        Returns:
            list of leave timestamps.
        """
        to_return = []
        to_return.extend(self.leave_times)
        self.leave_times.clear()
        return to_return
