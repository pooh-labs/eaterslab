# -*- coding: utf-8 -*-

"""Recognition module.

PeopleRecognizer finds people in input frames.
"""

from cv2 import dnn
from dlib import correlation_tracker, rectangle
from numpy import arange, array

# List of class labels MobileNet SSD was trained to detect
CLASSES = (
    'background',
    'aeroplane',
    'bicycle',
    'bird',
    'boat',
    'bottle',
    'bus',
    'car',
    'cat',
    'chair',
    'cow',
    'diningtable',
    'dog',
    'horse',
    'motorbike',
    'person',
    'pottedplant',
    'sheep',
    'sofa',
    'train',
    'tvmonitor',
)

SCALE_FACTOR = 0.007843
MEAN = 127.5


class PeopleRecognizer(object):
    """Recognizes people in input frames."""

    def __init__(
        self, prototxt_path: str, model_path: str, min_confidence: float,
    ):
        """Initialize configuration and load pretrained net.

        Args:
            prototxt_path: Path to net .prototxt file
            model_path: Path to net .caffemodel file
            min_confidence: Minimum confidence for accepting pe
        """
        self._min_confidence = min_confidence
        self._net = dnn.readNetFromCaffe(prototxt_path, model_path)

    def recognize_people(self, frame, frame_rgb):
        """Recognize people in frame.

        Args:
            frame: default OpenCV frame
            frame_rgb: frame converted to RGB

        Returns:
            List of trackers
        """
        # Convert frame and run detections
        detections = self._run_detection(frame)

        # Find detections
        trackers = []
        for index in arange(0, detections.shape[2]):

            # Discard low confidence hits
            if not self._confident_enough(detections[0, 0, index, 2]):
                continue

            # Discard other types
            class_idx = int(detections[0, 0, index, 1])
            if CLASSES.index('person') != class_idx:  # noq: WPS407
                continue

            # Get bounding box
            box = self._scale_box(
                detections[0, 0, index, 3:7], frame.shape[:2],
            )
            trackers.append(self._create_tracker(box, frame_rgb))

        return trackers

    def _confident_enough(self, confidence):
        return confidence >= self._min_confidence

    def _scale_box(self, box, sizes):
        height = sizes[0]
        width = sizes[1]
        return box * array([width, height, width, height])

    def _box_to_rect(self, box):
        (start_x, start_y, end_x, end_y) = box.astype('int')
        return rectangle(start_x, start_y, end_x, end_y)

    def _create_tracker(self, box, frame_rgb):
        tracker = correlation_tracker()
        tracker.start_track(frame_rgb, self._box_to_rect(box))
        return tracker

    def _run_detection(self, frame):
        (height, width) = frame.shape[:2]
        blob = dnn.blobFromImage(frame, SCALE_FACTOR, (width, height), MEAN)
        self._net.setInput(blob)
        return self._net.forward()
