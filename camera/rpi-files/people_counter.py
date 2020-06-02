# -*- coding: utf-8 -*-

"""People counting module.

People counter will connect to imaging device and detect people.
"""

from datetime import datetime

from cv2 import COLOR_BGR2RGB, cvtColor
from numpy import mean
from people_recognizer import PeopleRecognizer
from pyimagesearch.centroidtracker import CentroidTracker
from pyimagesearch.trackableobject import TrackableObject

MAX_DISAPPEARED = 40
MAX_DISTANCE = 50


class Configuration(object):
    """Configuration for people counter."""

    def __init__(self):
        """Initialize config."""
        self.prototxt_path = None
        self.model_path = None
        self.min_confidence = 0.0


class PeopleCounter(object):
    """Counts people leaving and entering."""

    def __init__(self, config: Configuration):
        """Initialize queues.

        Args:
            config: configuration
        """
        self.enter_times = []
        self.leave_times = []
        self._recognizer = PeopleRecognizer(
            config.prototxt_path, config.model_path, config.min_confidence,
        )
        self._frame = 0
        self._skip_frames = config.skip_frames
        self._ct = CentroidTracker(
            maxDisappeared=MAX_DISAPPEARED, maxDistance=MAX_DISTANCE,
        )
        self._trackers = []
        self._trackable_objects = {}

    def update(self, frame, timestamp: datetime):
        """Run updates.

        Args:
            frame: input frame
            timestamp: Current time
        """
        frame_rgb = cvtColor(frame, COLOR_BGR2RGB)
        rects = []

        self._frame = self._frame + 1
        if self._frame % self._skip_frames == 0:
            # Run recognition and finish
            self._trackers = self._recognizer.recognize_people(
                frame, frame_rgb,
            )
            return

        # Run tracking
        rects = self._update_trackers(frame_rgb)
        agents = self._ct.update(rects)
        self._analyze_movement(frame, agents, timestamp)

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

    def _update_trackers(self, frame_rgb):
        rects = []

        if not self._trackers:
            return rects

        for tracker in self._trackers:
            tracker.update(frame_rgb)
            pos = tracker.get_position()

            rect = (
                int(pos.left()),
                int(pos.top()),
                int(pos.right()),
                int(pos.bottom()),
            )
            rects.append(rect)

        return rects

    def _analyze_movement(self, frame, agents, timestamp):
        """Analyze movement of objects and add to enter/leave queues.

        Args:
            frame: input frame
            agents: dictionary of agents
            timestamp: current timestamp
        """
        (height, _) = frame.shape[:2]
        frame_mid = height // 2
        for (agent_id, centroid) in agents.items():
            self._analyze_agent_movement(
                timestamp, agent_id, centroid, frame_mid,
            )

    def _analyze_agent_movement(
        self, timestamp, agent_id, centroid, frame_mid,
    ):
        to = self._trackable_objects.get(agent_id, None)

        if to is None:
            to = TrackableObject(agent_id, centroid)
            self._trackable_objects[agent_id] = to
            return

        # Compute vertical delta
        mean_y = mean([cent[1] for cent in to.centroids])
        direction = centroid[1] - mean_y
        to.centroids.append(centroid)

        if to.counted:
            return

        # Moved up (entered)
        if direction < 0 and centroid[1] < frame_mid:
            self.enter_times.append(timestamp)
            to.counted = True

        # Moved down (left)
        if direction > 0 and centroid[1] > frame_mid:
            self.leave_times.append(timestamp)
            to.counted = True

        self._trackable_objects[agent_id] = to
