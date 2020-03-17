#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""Main application.

Main application module will start monitoring for people entereing and leaving.
"""

import logging
import signal
import time


class Main(object):
    """Handle application lifecycle."""

    def __init__(self):
        """Initialize signal handlers and run."""
        self.should_close = False

        # Add signal handlers
        signal.signal(signal.SIGINT, self.handle_signals)
        signal.signal(signal.SIGTERM, self.handle_signals)

        self.start()

    def start(self):
        """Start the system."""
        logging.info('System starts')

        while not self.should_close:
            time.sleep(1)
            logging.debug('Loop')

        logging.info('System shutting down...')

    def handle_signals(self, _signum, _frame):
        """Handle interruption events.

        Args:
            _signum: Signal number
            _frame: Current stack frame
        """
        self.should_close = True


if __name__ == '__main__':
    logging.basicConfig(
        format='%(asctime)s [%(levelname)s] %(message)s',
        datefmt='%Y-%m-%dT%H:%M:%S%z',
        filename='app.log',
        filemode='w',
        level=logging.DEBUG,
    )
    Main()
